package com.installow.app.ui.components

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.installow.app.data.InstagramExportParser
import com.installow.app.model.InstaAccount
import com.installow.app.model.MonthlyStat
import com.installow.app.model.RelationCategory
import com.installow.app.ui.theme.AccentAmber
import com.installow.app.ui.theme.AccentBlue
import com.installow.app.ui.theme.AccentCoral
import com.installow.app.ui.theme.AccentEmerald
import com.installow.app.ui.theme.AccentIndigo
import com.installow.app.ui.theme.BrandRose
import com.installow.app.ui.theme.LocalInstallowPalette
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    borderColor: Color? = null,
    containerColor: Color? = null,
    padding: Dp = 16.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val palette = LocalInstallowPalette.current
    val shape = RoundedCornerShape(18.dp)
    val baseModifier = modifier
        .clip(shape)
        .let { if (onClick != null) it.clickable(onClick = onClick) else it }

    Surface(
        modifier = baseModifier,
        shape = shape,
        color = containerColor ?: palette.surface,
        border = BorderStroke(0.75.dp, borderColor ?: palette.border)
    ) {
        Column(
            modifier = Modifier.padding(padding),
            content = content
        )
    }
}

private val AVATAR_TONES_DARK = listOf(
    Color(0xFF282436),
    Color(0xFF212936),
    Color(0xFF2E2229),
    Color(0xFF1F2D29),
    Color(0xFF2D271E),
    Color(0xFF24242E)
)

private val AVATAR_TONES_LIGHT = listOf(
    Color(0xFFEDE9F6),
    Color(0xFFE7EFF8),
    Color(0xFFF6E9EE),
    Color(0xFFE6F3EE),
    Color(0xFFF5EFE4),
    Color(0xFFECECF2)
)

@Composable
fun GradientAvatar(
    account: InstaAccount,
    size: Dp = 46.dp,
    modifier: Modifier = Modifier
) {
    val palette = LocalInstallowPalette.current
    val tones = if (palette.isDark) AVATAR_TONES_DARK else AVATAR_TONES_LIGHT
    val bgTone = tones[abs(account.username.hashCode()) % tones.size]

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(bgTone)
                .then(
                    if (account.isCloseFriend) {
                        Modifier.border(1.75.dp, AccentEmerald, CircleShape)
                    } else {
                        Modifier.border(0.75.dp, palette.border, CircleShape)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = account.initials,
                color = palette.textPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = (size.value * 0.34f).sp,
                letterSpacing = 0.2.sp
            )
        }

        if (account.isWhitelisted) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(AccentIndigo)
                    .border(1.5.dp, palette.background, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Shield,
                    contentDescription = "Beyaz Liste",
                    tint = Color.White,
                    modifier = Modifier.size(9.dp)
                )
            }
        } else if (account.isCloseFriend) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(AccentEmerald)
                    .border(1.5.dp, palette.background, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Star,
                    contentDescription = "Yakın Arkadaş",
                    tint = Color.White,
                    modifier = Modifier.size(9.dp)
                )
            }
        }
    }
}

@Composable
fun CategoryBadge(
    category: RelationCategory,
    modifier: Modifier = Modifier
) {
    val (label, accent) = when (category) {
        RelationCategory.NON_FOLLOWER -> "Takip etmiyor" to AccentCoral
        RelationCategory.FAN -> "Seni takip ediyor" to AccentBlue
        RelationCategory.MUTUAL -> "Karşılıklı" to AccentEmerald
        RelationCategory.PENDING_REQUEST -> "İstek bekliyor" to AccentAmber
    }
    PillBadge(text = label, color = accent, modifier = modifier)
}

@Composable
fun PillBadge(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 7.dp, vertical = 2.5.dp)
    ) {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Immutable
data class DonutSegment(
    val label: String,
    val count: Int,
    val color: Color
)

@Composable
fun RelationshipDonutChart(
    segments: List<DonutSegment>,
    centerPercent: Int,
    centerLabel: String,
    modifier: Modifier = Modifier
) {
    val palette = LocalInstallowPalette.current
    var animateTrigger by remember { mutableStateOf(false) }
    LaunchedEffect(segments) { animateTrigger = true }

    val progress by animateFloatAsState(
        targetValue = if (animateTrigger) 1f else 0f,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "donutAnim"
    )

    val total = max(1, segments.sumOf { it.count })

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Box(
            modifier = Modifier.size(104.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 9.dp.toPx()
                val inset = strokeWidth / 2f
                val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
                val topLeft = Offset(inset, inset)

                drawArc(
                    color = palette.surfaceVariant,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth)
                )

                var startAngle = -90f
                val gapDegrees = if (segments.count { it.count > 0 } > 1) 3f else 0f
                segments.filter { it.count > 0 }.forEach { seg ->
                    val rawSweep = (seg.count.toFloat() / total.toFloat()) * 360f
                    val sweep = ((rawSweep - gapDegrees).coerceAtLeast(2f)) * progress
                    drawArc(
                        color = seg.color,
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    startAngle += rawSweep
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "%$centerPercent",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary
                )
                Text(
                    text = centerLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textMuted
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            segments.forEach { seg ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(seg.color)
                        )
                        Text(
                            text = seg.label,
                            style = MaterialTheme.typography.bodyMedium,
                            color = palette.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = seg.count.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun MonthlyActivityBarChart(
    stats: List<MonthlyStat>,
    modifier: Modifier = Modifier
) {
    val palette = LocalInstallowPalette.current
    val maxValue = max(1, stats.maxOfOrNull { max(it.gainedFollowers, it.newFollowing) } ?: 1)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendDot(color = palette.textPrimary, text = "Yeni Takipçi")
            LegendDot(color = BrandRose, text = "Yeni Takip Edilen")
        }

        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(128.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                if (stats.isEmpty()) return@Canvas
                val groupWidth = size.width / stats.size
                val barWidth = (groupWidth * 0.22f).coerceIn(7.dp.toPx(), 13.dp.toPx())
                val gap = 4.dp.toPx()
                val chartHeight = size.height - 8.dp.toPx()

                for (i in 0..2) {
                    val y = chartHeight * (i / 2f) + 4.dp.toPx()
                    drawLine(
                        color = palette.border,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 0.75.dp.toPx()
                    )
                }

                stats.forEachIndexed { index, item ->
                    val centerX = groupWidth * index + groupWidth / 2f

                    val followerRatio = item.gainedFollowers.toFloat() / maxValue.toFloat()
                    val followerH = (chartHeight * followerRatio).coerceAtLeast(
                        if (item.gainedFollowers > 0) 5.dp.toPx() else 2.dp.toPx()
                    )
                    drawRoundRect(
                        color = palette.textPrimary,
                        topLeft = Offset(centerX - barWidth - gap / 2f, chartHeight - followerH + 4.dp.toPx()),
                        size = Size(barWidth, followerH),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )

                    val followingRatio = item.newFollowing.toFloat() / maxValue.toFloat()
                    val followingH = (chartHeight * followingRatio).coerceAtLeast(
                        if (item.newFollowing > 0) 5.dp.toPx() else 2.dp.toPx()
                    )
                    drawRoundRect(
                        color = BrandRose,
                        topLeft = Offset(centerX + gap / 2f, chartHeight - followingH + 4.dp.toPx()),
                        size = Size(barWidth, followingH),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            stats.forEach { item ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = palette.textSecondary
                    )
                    Text(
                        text = "${item.gainedFollowers} / ${item.newFollowing}",
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.textMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, text: String) {
    val palette = LocalInstallowPalette.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = palette.textSecondary
        )
    }
}

private val TR_LOCALE = Locale("tr", "TR")
private val DATE_FORMATTER_TR: DateTimeFormatter = DateTimeFormatter
    .ofPattern("d MMM yyyy", TR_LOCALE)
    .withZone(ZoneId.systemDefault())

fun formatFollowerCountTr(count: Long): String {
    return when {
        count >= 1_000_000L -> String.format(TR_LOCALE, "%.1fM", count / 1_000_000.0).replace(",0M", "M")
        count >= 10_000L -> String.format(TR_LOCALE, "%.1fB", count / 1_000.0).replace(",0B", "B")
        count >= 1_000L -> String.format(TR_LOCALE, "%,d", count)
        else -> count.toString()
    }
}

fun formatEpochSecondsTr(epochSec: Long?): String {
    if (epochSec == null || epochSec <= 0L) return "Tarih bilinmiyor"
    val formatted = DATE_FORMATTER_TR.format(Instant.ofEpochSecond(epochSec))
    val rel = formatRelativeTimeTr(epochSec * 1000L)
    return "$formatted · $rel"
}

fun formatRelativeTimeTr(timeMillis: Long): String {
    if (timeMillis <= 0L) return ""
    val diffMs = (System.currentTimeMillis() - timeMillis).coerceAtLeast(0L)
    val minutes = diffMs / 60_000L
    val hours = minutes / 60L
    val days = hours / 24L
    return when {
        minutes < 2 -> "Az önce"
        minutes < 60 -> "$minutes dk önce"
        hours < 24 -> "$hours sa önce"
        days < 30 -> "$days gün önce"
        days < 365 -> "${days / 30} ay önce"
        else -> "${days / 365} yıl önce"
    }
}

fun openInstagramProfile(context: Context, username: String) {
    val clean = InstagramExportParser.normalizeUsername(username) ?: return
    val appUri = Uri.parse("https://instagram.com/_u/$clean")
    val appIntent = Intent(Intent.ACTION_VIEW, appUri).apply {
        setPackage("com.instagram.android")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    try {
        context.startActivity(appIntent)
    } catch (_: ActivityNotFoundException) {
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/$clean/")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching { context.startActivity(webIntent) }
    }
}

fun shareTextReport(context: Context, title: String, content: String) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, content)
    }
    val chooser = Intent.createChooser(sendIntent, "Raporu Paylaş / Dışa Aktar").apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    runCatching { context.startActivity(chooser) }
}
