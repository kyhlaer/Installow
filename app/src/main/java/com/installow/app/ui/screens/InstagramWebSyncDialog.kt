package com.installow.app.ui.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.http.SslError
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.installow.app.ui.theme.AccentAmber
import com.installow.app.ui.theme.AccentCoral
import com.installow.app.ui.theme.AccentEmerald
import com.installow.app.ui.theme.BrandRose
import com.installow.app.ui.theme.LocalInstallowPalette
import com.installow.app.util.SecurityManager
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.ceil

private const val INSTAGRAM_LOGIN_URL = "https://www.instagram.com/accounts/login/"
private const val INSTAGRAM_COOKIE_URL = "https://www.instagram.com"
private const val PREFS_SAFETY = "installow_safety_prefs"
private const val KEY_LAST_SYNC_MS = "last_web_sync_ms"
private const val KEY_RATE_LOCK_UNTIL_MS = "rate_limit_until_ms"
private const val SYNC_COOLDOWN_MS = 15 * 60 * 1000L
private const val RATE_LOCK_COOLDOWN_MS = 60 * 60 * 1000L

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun InstagramWebSyncDialog(
    onDismiss: () -> Unit,
    onSyncComplete: (
        ownerUsername: String,
        followers: Map<String, Pair<String, Long>>,
        following: Map<String, Pair<String, Long>>
    ) -> Unit
) {
    val palette = LocalInstallowPalette.current
    val context = LocalContext.current
    val mainHandler = remember { Handler(Looper.getMainLooper()) }
    val bridgeToken = remember { SecurityManager.generateSessionToken() }
    val safetyPrefs = remember {
        context.getSharedPreferences(PREFS_SAFETY, Context.MODE_PRIVATE)
    }

    DisposableEffect(Unit) {
        val window = (context as? Activity)?.window
        val hadSecureFlag = window != null &&
            (window.attributes.flags and WindowManager.LayoutParams.FLAG_SECURE) != 0
        window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        onDispose {
            if (!hadSecureFlag) {
                window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
            }
        }
    }

    var isFetching by remember { mutableStateOf(false) }
    var isLoggedIn by remember { mutableStateOf(false) }
    var pageProgress by remember { mutableIntStateOf(0) }
    var cooldownTick by remember { mutableIntStateOf(0) }

    val nowMs = System.currentTimeMillis()
    val rateLockUntil = remember(cooldownTick) { safetyPrefs.getLong(KEY_RATE_LOCK_UNTIL_MS, 0L) }
    val lastSyncMs = remember(cooldownTick) { safetyPrefs.getLong(KEY_LAST_SYNC_MS, 0L) }
    val remainingLockMinutes = when {
        rateLockUntil > nowMs -> ceil((rateLockUntil - nowMs) / 60_000.0).toInt().coerceAtLeast(1)
        lastSyncMs > 0L && (nowMs - lastSyncMs) < SYNC_COOLDOWN_MS ->
            ceil((SYNC_COOLDOWN_MS - (nowMs - lastSyncMs)) / 60_000.0).toInt().coerceAtLeast(1)
        else -> 0
    }
    val isInCooldown = remainingLockMinutes > 0

    var statusText by remember {
        mutableStateOf(
            if (remainingLockMinutes > 0) {
                "Hesap koruma soğuma süresi aktif: $remainingLockMinutes dk bekleyin veya sıfır riskli Resmi ZIP yöntemini kullanın."
            } else {
                "Instagram hesabınıza giriş yapın, ardından aşağıdaki butona dokunun."
            }
        )
    }
    var isError by remember { mutableStateOf(false) }

    val webView = remember {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(android.graphics.Color.WHITE)

            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccess = false
            settings.allowContentAccess = false
            @Suppress("DEPRECATION")
            settings.allowFileAccessFromFileURLs = false
            @Suppress("DEPRECATION")
            settings.allowUniversalAccessFromFileURLs = false
            @Suppress("DEPRECATION")
            settings.saveFormData = false
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.javaScriptCanOpenWindowsAutomatically = false
            settings.setSupportMultipleWindows(false)
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                settings.safeBrowsingEnabled = true
            }
            settings.userAgentString =
                "Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Mobile Safari/537.36"

            val cookieManager = CookieManager.getInstance()
            cookieManager.setAcceptCookie(true)
            cookieManager.setAcceptThirdPartyCookies(this, true)
        }
    }

    BackHandler {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            onDismiss()
        }
    }

    DisposableEffect(webView, bridgeToken) {
        val bridge = object {
            @JavascriptInterface
            fun onProgress(token: String, msg: String) {
                if (!SecurityManager.constantTimeEquals(token, bridgeToken)) return
                mainHandler.post {
                    isError = false
                    statusText = msg.take(220)
                }
            }

            @JavascriptInterface
            fun onError(token: String, msg: String) {
                if (!SecurityManager.constantTimeEquals(token, bridgeToken)) return
                mainHandler.post {
                    isFetching = false
                    isError = true
                    statusText = msg.take(260)
                }
            }

            @JavascriptInterface
            fun onRateLimited(token: String, msg: String) {
                if (!SecurityManager.constantTimeEquals(token, bridgeToken)) return
                mainHandler.post {
                    safetyPrefs.edit()
                        .putLong(KEY_RATE_LOCK_UNTIL_MS, System.currentTimeMillis() + RATE_LOCK_COOLDOWN_MS)
                        .apply()
                    cooldownTick++
                    isFetching = false
                    isError = true
                    statusText = msg.take(260)
                }
            }

            @JavascriptInterface
            fun onComplete(token: String, jsonPayload: String) {
                if (!SecurityManager.constantTimeEquals(token, bridgeToken)) return
                mainHandler.post {
                    try {
                        val root = JSONObject(jsonPayload)
                        val owner = root.optString("ownerUsername", "instagram_hesabim")
                        val followersMap = jsonArrayToUserMap(root.optJSONArray("followers"))
                        val followingMap = jsonArrayToUserMap(root.optJSONArray("following"))
                        safetyPrefs.edit()
                            .putLong(KEY_LAST_SYNC_MS, System.currentTimeMillis())
                            .apply()
                        cooldownTick++
                        isFetching = false
                        onSyncComplete(owner, followersMap, followingMap)
                    } catch (e: Exception) {
                        isFetching = false
                        isError = true
                        statusText = "Veri işlenirken hata oluştu: ${e.localizedMessage}"
                    }
                }
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                pageProgress = newProgress
            }
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString().orEmpty()
                val scheme = request?.url?.scheme?.lowercase().orEmpty()
                if (scheme == "https") {
                    return !SecurityManager.isAllowedWebSyncUrl(url)
                }
                if (scheme == "intent") {
                    val safeFallback = SecurityManager.extractSafeIntentFallbackUrl(url)
                    if (safeFallback != null) {
                        view?.loadUrl(safeFallback)
                    }
                    return true
                }
                return true
            }

            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                handler?.cancel()
                mainHandler.post {
                    isFetching = false
                    isError = true
                    statusText = "SSL/TLS sertifikası doğrulanamadı. Bağlantı güvenlik gerekçesiyle durduruldu."
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                runCatching { CookieManager.getInstance().flush() }
                val userId = readInstagramCookie("ds_user_id")
                if (!userId.isNullOrBlank()) {
                    isLoggedIn = true
                    if (!isFetching && !isError && !isInCooldown) {
                        statusText = "Oturum algılandı. Takipçi ve takip edilenleri çekebilirsiniz."
                    }
                }
            }
        }

        webView.addJavascriptInterface(bridge, "InstallowBridge")
        webView.loadUrl(INSTAGRAM_LOGIN_URL)

        onDispose {
            webView.removeJavascriptInterface("InstallowBridge")
            webView.stopLoading()
            webView.destroy()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = palette.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(palette.surface)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = null,
                        tint = if (isLoggedIn) AccentEmerald else palette.textSecondary,
                        modifier = Modifier.size(17.dp)
                    )
                    Column {
                        Text(
                            text = "Instagram Web Bağlantısı",
                            style = MaterialTheme.typography.titleMedium,
                            color = palette.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Güvenli yavaş mod · Şifre kaydedilmez",
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.textMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            if (!isFetching) {
                                SecurityManager.clearWebViewSessionData(context)
                                webView.clearCache(true)
                                webView.clearHistory()
                                isLoggedIn = false
                                isError = false
                                statusText = "Oturum ve çerezler sıfırlandı. Yeniden giriş yapabilirsiniz."
                                webView.loadUrl(INSTAGRAM_LOGIN_URL)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.DeleteSweep,
                            contentDescription = "Çerezleri Temizle",
                            tint = palette.textSecondary
                        )
                    }
                    IconButton(onClick = { webView.reload() }) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Yenile",
                            tint = palette.textPrimary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Kapat",
                            tint = palette.textPrimary
                        )
                    }
                }
            }

            if (pageProgress in 1..99) {
                LinearProgressIndicator(
                    progress = { pageProgress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.5.dp),
                    color = BrandRose,
                    trackColor = palette.surfaceVariant
                )
            } else {
                HorizontalDivider(thickness = 0.5.dp, color = palette.border)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.White)
            ) {
                AndroidView(
                    factory = { webView },
                    modifier = Modifier.fillMaxSize()
                )
            }

            HorizontalDivider(thickness = 0.5.dp, color = palette.border)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(palette.surface)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(palette.surfaceElevated)
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Shield,
                        contentDescription = null,
                        tint = AccentEmerald,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "Koruma aktif: Sayfalar arası 2-3 sn bekleme, 15 dk soğuma sınırı ve uyarıda otomatik durdurma.",
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.textSecondary
                    )
                }

                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = when {
                        isError -> AccentCoral
                        isInCooldown -> AccentAmber
                        isLoggedIn && !isFetching -> AccentEmerald
                        else -> palette.textSecondary
                    }
                )

                Button(
                    onClick = {
                        if (!isFetching && !isInCooldown) {
                            isFetching = true
                            isError = false
                            statusText = "Oturum doğrulanıyor..."
                            val cookieUserId = readInstagramCookie("ds_user_id").orEmpty()
                            val cookieCsrf = readInstagramCookie("csrftoken").orEmpty()
                            val script = buildFetchInstagramScript(cookieUserId, cookieCsrf, bridgeToken)
                            webView.evaluateJavascript(script, null)
                        }
                    },
                    enabled = !isFetching && !isInCooldown,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.accentContrastBg,
                        contentColor = palette.accentContrastFg
                    )
                ) {
                    if (isFetching) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(17.dp),
                            strokeWidth = 2.dp,
                            color = palette.accentContrastFg
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Güvenli Modda Çekiliyor...", fontWeight = FontWeight.SemiBold)
                    } else if (isInCooldown) {
                        Icon(
                            imageVector = Icons.Rounded.Shield,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Koruma Beklemesi ($remainingLockMinutes dk)", fontWeight = FontWeight.SemiBold)
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.CloudDownload,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Takipçi & Takip Edilenleri Çek", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

private fun readInstagramCookie(name: String): String? {
    val rawCookies = runCatching {
        CookieManager.getInstance().getCookie(INSTAGRAM_COOKIE_URL)
    }.getOrNull() ?: return null

    for (part in rawCookies.split(";")) {
        val trimmed = part.trim()
        val eqIdx = trimmed.indexOf('=')
        if (eqIdx > 0) {
            val key = trimmed.substring(0, eqIdx).trim()
            if (key == name) {
                return trimmed.substring(eqIdx + 1).trim()
            }
        }
    }
    return null
}

private fun jsonArrayToUserMap(arr: JSONArray?): Map<String, Pair<String, Long>> {
    if (arr == null) return emptyMap()
    val result = linkedMapOf<String, Pair<String, Long>>()
    val nowSec = System.currentTimeMillis() / 1000L
    for (i in 0 until arr.length()) {
        val item = arr.optJSONObject(i) ?: continue
        val username = item.optString("username", "").trim()
        if (username.isNotEmpty()) {
            val fullName = item.optString("full_name", "").trim()
            result[username] = fullName to nowSec
        }
    }
    return result
}

private fun buildFetchInstagramScript(
    cookieUserId: String,
    cookieCsrf: String,
    bridgeToken: String
): String {
    val quotedUserId = JSONObject.quote(cookieUserId)
    val quotedCsrf = JSONObject.quote(cookieCsrf)
    val quotedToken = JSONObject.quote(bridgeToken)
    return """
(async function() {
    const TOKEN = $quotedToken;
    const sleep = (ms) => new Promise(r => setTimeout(r, ms));
    const humanDelay = () => sleep(1800 + Math.floor(Math.random() * 1300));
    const restPause = () => sleep(4500 + Math.floor(Math.random() * 2000));

    try {
        let userId = $quotedUserId;
        if (!userId) {
            const cookieMatch = document.cookie.match(/(?:^|;\s*)ds_user_id=(\d+)/);
            if (cookieMatch) userId = cookieMatch[1];
        }
        if (!userId) {
            InstallowBridge.onError(TOKEN, "Aktif oturum bulunamadı. Lütfen önce yukarıdan Instagram hesabınıza giriş yapın.");
            return;
        }

        let csrfToken = $quotedCsrf;
        if (!csrfToken) {
            const csrfMatch = document.cookie.match(/(?:^|;\s*)csrftoken=([^;]+)/);
            if (csrfMatch) csrfToken = csrfMatch[1];
        }

        const headers = {
            "x-ig-app-id": "936619743392459",
            "x-requested-with": "XMLHttpRequest"
        };
        if (csrfToken) {
            headers["x-csrftoken"] = csrfToken;
        }

        let ownerUsername = "instagram_hesabim";
        try {
            const meRes = await fetch("https://www.instagram.com/api/v1/users/" + userId + "/info/", {
                headers: headers,
                credentials: "include"
            });
            if (meRes.ok) {
                const meData = await meRes.json();
                if (meData && meData.user && meData.user.username) {
                    ownerUsername = meData.user.username;
                }
            }
        } catch (e) {}

        async function fetchAll(kind, labelTr) {
            const collected = [];
            let maxId = null;
            let page = 0;
            const maxSafePages = 40;
            while (true) {
                page++;
                InstallowBridge.onProgress(TOKEN, labelTr + " güvenli modda çekiliyor (" + collected.length + " kişi)...");
                let url = "https://www.instagram.com/api/v1/friendships/" + userId + "/" + kind + "/?count=50";
                if (maxId) url += "&max_id=" + encodeURIComponent(maxId);

                const res = await fetch(url, { headers: headers, credentials: "include" });
                let data = null;
                try {
                    data = await res.json();
                } catch (parseErr) {}

                const msgText = (data && data.message ? String(data.message) : "").toLowerCase();
                const isChallenge = res.status === 429 || res.status === 403 || res.status === 401 ||
                    (data && (data.challenge || data.require_login || data.feedback_required ||
                    msgText.includes("wait") || msgText.includes("limit") || msgText.includes("challenge") || msgText.includes("checkpoint")));

                if (isChallenge) {
                    InstallowBridge.onRateLimited(
                        TOKEN,
                        "Instagram hız uyarısı algılandı (HTTP " + res.status + "). Hesabınızı korumak için işlem durduruldu ve 60 dk kilitlendi. Sıfır risk için Resmi ZIP yöntemini kullanın."
                    );
                    return null;
                }

                if (!res.ok || !data) {
                    throw new Error("Instagram HTTP " + res.status + " döndürdü. İşlem güvenli şekilde durduruldu.");
                }

                const users = data.users || [];
                for (const u of users) {
                    if (u && u.username) {
                        collected.push({
                            username: u.username,
                            full_name: u.full_name || ""
                        });
                    }
                }
                if (!data.next_max_id || page >= maxSafePages) break;
                maxId = data.next_max_id;

                if (page % 8 === 0) {
                    InstallowBridge.onProgress(TOKEN, labelTr + " (" + collected.length + " kişi) · Kısa mola veriliyor...");
                    await restPause();
                } else {
                    await humanDelay();
                }
            }
            return collected;
        }

        const followers = await fetchAll("followers", "Takipçiler");
        if (followers === null) return;

        InstallowBridge.onProgress(TOKEN, "Takipçiler tamam (" + followers.length + "). Kısa mola...");
        await restPause();

        const following = await fetchAll("following", "Takip edilenler");
        if (following === null) return;

        InstallowBridge.onProgress(TOKEN, "Tamamlandı, kaydediliyor...");
        InstallowBridge.onComplete(TOKEN, JSON.stringify({
            ownerUsername: ownerUsername,
            followers: followers,
            following: following
        }));
    } catch (err) {
        InstallowBridge.onError(TOKEN, err && err.message ? err.message : "Senkronizasyon güvenli şekilde durduruldu.");
    }
})();
""".trimIndent()
}
