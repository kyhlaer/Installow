package com.installow.app.model

import androidx.compose.runtime.Immutable
import java.util.Locale
import kotlin.math.abs

enum class RelationCategory(val labelTr: String) {
    MUTUAL("Karşılıklı"),
    NON_FOLLOWER("Takip Etmiyor"),
    FAN("Sen Takip Etmiyorsun"),
    PENDING_REQUEST("Bekleyen İstek")
}

enum class FilterTab(val labelTr: String, val shortLabelTr: String) {
    NON_FOLLOWERS("Beni Takip Etmeyenler", "Takip Etmeyen"),
    FANS("Takip Etmediklerim", "Hayranlar"),
    MUTUALS("Karşılıklı Takip", "Karşılıklı"),
    ALL_FOLLOWERS("Takipçilerim", "Takipçiler"),
    ALL_FOLLOWING("Takip Ettiklerim", "Takip Edilen"),
    UNFOLLOWERS_DIFF("Takipten Çıkanlar", "Çıkanlar"),
    PENDING("Bekleyen İstekler", "İstekler"),
    WHITELIST("Beyaz Liste", "Beyaz Liste")
}

enum class SortOption(val labelTr: String) {
    NEWEST_FIRST("En Yeni Tarih"),
    OLDEST_FIRST("En Eski Tarih"),
    FOLLOWERS_DESC("Takipçi Sayısı (Çok → Az)"),
    FOLLOWERS_ASC("Takipçi Sayısı (Az → Çok)"),
    USERNAME_ASC("Kullanıcı Adı (A → Z)"),
    USERNAME_DESC("Kullanıcı Adı (Z → A)")
}

enum class ConnectionQuickFilter {
    ALL,
    WITH_NOTES,
    CLOSE_FRIENDS
}

enum class DataSourceType(val labelTr: String) {
    DEMO("Demo Veri Seti"),
    PUBLIC_LOOKUP("Herkese Açık Profil"),
    OFFICIAL_EXPORT("Instagram Resmi Arşiv"),
    WEB_SYNC("Canlı Web Senkronizasyonu")
}

enum class AppScreen(val labelTr: String) {
    DASHBOARD("Panel"),
    CONNECTIONS("Kişiler"),
    ANALYTICS("Analiz"),
    DATA("Veri"),
    SETTINGS("Ayarlar")
}

enum class ListAccessStatus {
    FULL_DATA,
    PRIVATE_ACCOUNT,
    LOGIN_REQUIRED_FOR_LISTS,
    PROFILE_NOT_FOUND,
    NETWORK_ERROR
}

enum class ThemePreference(val labelTr: String) {
    DARK("Koyu (Obsidian)"),
    LIGHT("Açık (Gün Işığı)"),
    SYSTEM("Sistem Varsayılanı")
}

enum class LanguagePreference {
    SYSTEM,
    TURKISH,
    ENGLISH
}

enum class AppLanguage {
    TR,
    EN
}

@Immutable
data class InstaAccount(
    val username: String,
    val displayName: String = "",
    val profileUrl: String = "https://www.instagram.com/$username/",
    val followedMeAt: Long? = null,
    val iFollowedAt: Long? = null,
    val pendingRequestAt: Long? = null,
    val isCloseFriend: Boolean = false,
    val isWhitelisted: Boolean = false,
    val customTag: String = "",
    val customNote: String = "",
    val followerCount: Long? = null
) {
    val isFollower: Boolean = followedMeAt != null

    val isFollowing: Boolean = iFollowedAt != null

    val isPending: Boolean = pendingRequestAt != null && iFollowedAt == null

    val category: RelationCategory = when {
        isFollower && isFollowing -> RelationCategory.MUTUAL
        !isFollower && isFollowing -> RelationCategory.NON_FOLLOWER
        isFollower && !isFollowing -> RelationCategory.FAN
        else -> RelationCategory.PENDING_REQUEST
    }

    val relevantTimestamp: Long = maxOf(
        iFollowedAt ?: 0L,
        followedMeAt ?: 0L,
        pendingRequestAt ?: 0L
    )

    val earliestTimestamp: Long = run {
        var minVal = Long.MAX_VALUE
        if (followedMeAt != null && followedMeAt > 0L) minVal = minOf(minVal, followedMeAt)
        if (iFollowedAt != null && iFollowedAt > 0L) minVal = minOf(minVal, iFollowedAt)
        if (pendingRequestAt != null && pendingRequestAt > 0L) minVal = minOf(minVal, pendingRequestAt)
        if (minVal == Long.MAX_VALUE) 0L else minVal
    }

    val initials: String = run {
        val clean = displayName.trim().ifEmpty { username }
        val parts = clean.split(" ", "_", ".").filter { it.isNotEmpty() }
        when {
            parts.size >= 2 -> "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
            clean.length >= 2 -> clean.take(2).uppercase()
            clean.isNotEmpty() -> clean.take(1).uppercase()
            else -> "IG"
        }
    }

    val avatarColors: Pair<Long, Long> =
        AVATAR_GRADIENTS[abs(username.hashCode()) % AVATAR_GRADIENTS.size]

    val searchBlob: String = buildString {
        append(username)
        if (displayName.isNotEmpty()) {
            append('\u0000')
            append(displayName.lowercase(Locale.ROOT))
        }
        if (customTag.isNotEmpty()) {
            append('\u0000')
            append(customTag.lowercase(Locale.ROOT))
        }
        if (customNote.isNotEmpty()) {
            append('\u0000')
            append(customNote.lowercase(Locale.ROOT))
        }
    }

    companion object {
        private val AVATAR_GRADIENTS = listOf(
            0xFF8B5CF6L to 0xFFEC4899L,
            0xFF06B6D4L to 0xFF3B82F6L,
            0xFFF43F5EL to 0xFFF59E0BL,
            0xFF10B981L to 0xFF06B6D4L,
            0xFFA855F7L to 0xFF6366F1L,
            0xFFEC4899L to 0xFFF43F5EL,
            0xFFF59E0BL to 0xFFEF4444L,
            0xFF14B8A6L to 0xFF8B5CF6L
        )
    }
}

@Immutable
data class AccountAnnotation(
    val isWhitelisted: Boolean = false,
    val customTag: String = "",
    val customNote: String = ""
)

@Immutable
data class ParsedUserEntry(
    val username: String,
    val displayName: String = "",
    val profileUrl: String = "",
    val timestampSec: Long = 0L
)

@Immutable
data class ParsedExportData(
    val followers: Map<String, ParsedUserEntry> = emptyMap(),
    val following: Map<String, ParsedUserEntry> = emptyMap(),
    val pendingRequests: Map<String, ParsedUserEntry> = emptyMap(),
    val closeFriends: Set<String> = emptySet(),
    val recentlyUnfollowed: Map<String, ParsedUserEntry> = emptyMap(),
    val sourceFilesFound: List<String> = emptyList()
)

@Immutable
data class Snapshot(
    val id: String,
    val createdAtMillis: Long,
    val sourceLabel: String,
    val followers: Map<String, Long>,
    val following: Map<String, Long>,
    val pendingRequests: Map<String, Long> = emptyMap(),
    val closeFriends: Set<String> = emptySet(),
    val displayNames: Map<String, String> = emptyMap(),
    val followerCounts: Map<String, Long> = emptyMap(),
    val reportedFollowersCount: Int? = null,
    val reportedFollowingCount: Int? = null,
    val isPrivateAccount: Boolean? = null
) {
    val followerCount: Int get() = reportedFollowersCount ?: followers.size
    val followingCount: Int get() = reportedFollowingCount ?: following.size
}

@Immutable
data class AccountWorkspace(
    val id: String,
    val ownerUsername: String,
    val displayName: String = "",
    val dataSourceType: DataSourceType = DataSourceType.DEMO,
    val snapshots: List<Snapshot> = emptyList(),
    val annotations: Map<String, AccountAnnotation> = emptyMap(),
    val reportedFollowersCount: Int? = null,
    val reportedFollowingCount: Int? = null,
    val isPrivateAccount: Boolean? = null,
    val listAccessStatus: ListAccessStatus = ListAccessStatus.FULL_DATA
) {
    val latestSnapshot: Snapshot? get() = snapshots.lastOrNull()
    val summaryFollowersCount: Int
        get() = reportedFollowersCount ?: latestSnapshot?.followerCount ?: 0
    val summaryFollowingCount: Int
        get() = reportedFollowingCount ?: latestSnapshot?.followingCount ?: 0
}

@Immutable
data class SnapshotDiff(
    val previousSnapshotId: String,
    val currentSnapshotId: String,
    val previousTimeMillis: Long,
    val currentTimeMillis: Long,
    val newFollowers: List<String>,
    val lostFollowers: List<String>,
    val newFollowing: List<String>,
    val unfollowedByMe: List<String>
) {
    val hasAnyChanges: Boolean
        get() = newFollowers.isNotEmpty() || lostFollowers.isNotEmpty() ||
            newFollowing.isNotEmpty() || unfollowedByMe.isNotEmpty()
}

@Immutable
data class MonthlyStat(
    val monthKey: String,
    val label: String,
    val gainedFollowers: Int,
    val newFollowing: Int
)

object PresetTags {
    val ALL = listOf(
        "Ünlü / Marka",
        "Yakın Arkadaş",
        "Aile",
        "İş / Network",
        "İçerik Üretici",
        "Takip Kontrol"
    )
}
