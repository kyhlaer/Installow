package com.installow.app.data

import com.installow.app.model.AccountAnnotation
import com.installow.app.model.Snapshot
import java.util.UUID

object DemoDataGenerator {

    private data class SeedProfile(
        val username: String,
        val displayName: String,
        val daysAgoFollowedMe: Int?,
        val daysAgoIFollowed: Int?,
        val daysAgoPending: Int? = null,
        val isCloseFriend: Boolean = false,
        val isWhitelisted: Boolean = false,
        val customTag: String = "",
        val customNote: String = "",
        val followerCount: Long? = null
    )

    private val SEEDS = listOf(
        // Karşılıklı takip (Mutuals)
        SeedProfile("defne.korkmaz", "Defne Korkmaz", 420, 418, isCloseFriend = true, customTag = "Yakın Arkadaş", customNote = "Üniversiteden beri arkadaş"),
        SeedProfile("emre_demir", "Emre Demir", 385, 380, isCloseFriend = true, customTag = "Yakın Arkadaş"),
        SeedProfile("zeynep.aktas", "Zeynep Aktaş", 350, 349, isCloseFriend = true, customTag = "Aile"),
        SeedProfile("mertcan.oz", "Mertcan Öz", 310, 305, customTag = "İş / Network", customNote = "Android ekibinden"),
        SeedProfile("selin.yildiz", "Selin Yıldız", 290, 288),
        SeedProfile("kagan_sahin", "Kağan Şahin", 275, 270, isCloseFriend = true),
        SeedProfile("irem.celik", "İrem Çelik", 260, 255),
        SeedProfile("berk.arslan", "Berk Arslan", 240, 238, customTag = "İş / Network"),
        SeedProfile("elifnur_kaya", "Elif Nur Kaya", 225, 224, customTag = "Aile"),
        SeedProfile("tolga.karahan", "Tolga Karahan", 210, 209),
        SeedProfile("ceren.aydin", "Ceren Aydın", 198, 195),
        SeedProfile("onur_yilmaz", "Onur Yılmaz", 185, 182),
        SeedProfile("melisa.dogan", "Melisa Doğan", 172, 170),
        SeedProfile("batuhan.koc", "Batuhan Koç", 160, 158),
        SeedProfile("aslihan.eren", "Aslıhan Eren", 145, 144, isCloseFriend = true),
        SeedProfile("furkan_tunc", "Furkan Tunç", 132, 130),
        SeedProfile("eylul.kurt", "Eylül Kurt", 118, 115),
        SeedProfile("alp.tekin", "Alp Tekin", 104, 102),
        SeedProfile("damla.sari", "Damla Sarı", 92, 90),
        SeedProfile("yigit.polat", "Yiğit Polat", 78, 76),
        SeedProfile("nazli_demirci", "Nazlı Demirci", 65, 64),
        SeedProfile("doruk.ergun", "Doruk Ergün", 52, 50),
        SeedProfile("ece.bulut", "Ece Bulut", 41, 40),
        SeedProfile("tarik_ozkan", "Tarık Özkan", 29, 28),
        SeedProfile("beyza.guler", "Beyza Güler", 18, 17),
        SeedProfile("arda.sezer", "Arda Sezer", 9, 8),
        SeedProfile("simge.aksoy", "Simge Aksoy", 4, 4),
        SeedProfile("kerem.yalcin", "Kerem Yalçın", 2, 2, customTag = "Yakın Arkadaş"),

        // Beni Takip Etmeyenler (Non-followers: Ben takip ediyorum ama geri takip yok)
        SeedProfile("kotlin.dev", "Kotlin Developers", null, 512, isWhitelisted = true, customTag = "İçerik Üretici", customNote = "Resmi Kotlin hesabı", followerCount = 185_400L),
        SeedProfile("androiddev", "Android Developers", null, 480, isWhitelisted = true, customTag = "Ünlü / Marka", followerCount = 642_000L),
        SeedProfile("nasa", "NASA", null, 455, isWhitelisted = true, customTag = "Ünlü / Marka", followerCount = 97_400_000L),
        SeedProfile("barisozcan", "Barış Özcan", null, 390, isWhitelisted = true, customTag = "İçerik Üretici", followerCount = 3_240_000L),
        SeedProfile("kaantangoz.art", "Kaan Tangöz Studio", null, 340, customTag = "Takip Kontrol", customNote = "Tasarım ilhamı", followerCount = 28_500L),
        SeedProfile("minimal.setup", "Minimal Desk Setup", null, 280, followerCount = 142_800L),
        SeedProfile("caner_uygur", "Caner Uygur", null, 245, customTag = "Takip Kontrol", customNote = "Etkinlikte tanışılmıştı"),
        SeedProfile("duygu.kilic", "Duygu Kılıç", null, 210),
        SeedProfile("ugur_aslan", "Uğur Aslan", null, 180),
        SeedProfile("tugba.keskin", "Tuğba Keskin", null, 152),
        SeedProfile("deniz_akcay", "Deniz Akçay", null, 134),
        SeedProfile("sinan.guven", "Sinan Güven", null, 115),
        SeedProfile("miray_soylu", "Miray Soylu", null, 98),
        SeedProfile("volkan.erdem", "Volkan Erdem", null, 81),
        SeedProfile("ilayda.cetin", "İlayda Çetin", null, 63),
        SeedProfile("koray.akman", "Koray Akman", null, 47),
        SeedProfile("gizem_yildirim", "Gizem Yıldırım", null, 35),
        SeedProfile("burak.turan", "Burak Turan", null, 21),
        SeedProfile("nil.karakus", "Nil Karakuş", null, 11),
        SeedProfile("okan.senol", "Okan Şenol", null, 5),

        // Benim Takip Etmediklerim / Hayranlar (Fans: Beni takip ediyorlar ama ben etmiyorum)
        SeedProfile("ahmet_karaca", "Ahmet Karaca", 540, null, customTag = "İş / Network", customNote = "İlk takipçilerden biri"),
        SeedProfile("sude.ergin", "Sude Ergin", 410, null),
        SeedProfile("ozgur.bayrak", "Özgür Bayrak", 320, null),
        SeedProfile("pelinsu.k", "Pelinsu K.", 265, null),
        SeedProfile("hasan.duman", "Hasan Duman", 195, null),
        SeedProfile("neslisah_t", "Neslişah T.", 165, null),
        SeedProfile("cihan.altun", "Cihan Altun", 122, null),
        SeedProfile("elif.tasdemir", "Elif Taşdemir", 88, null),
        SeedProfile("serhat_bozkurt", "Serhat Bozkurt", 56, null),
        SeedProfile("cansu.pala", "Cansu Pala", 33, null),
        SeedProfile("kadir.unal", "Kadir Ünal", 19, null),
        SeedProfile("melih_gokce", "Melih Gökçe", 7, null),
        SeedProfile("yaren.korkut", "Yaren Korkut", 3, null),
        SeedProfile("tamer.sonmez", "Tamer Sönmez", 1, null),

        // Bekleyen Takip İstekleri (Pending Requests)
        SeedProfile("gizli.hesap.99", "Efe K.", null, null, daysAgoPending = 95),
        SeedProfile("bora_ozdemir", "Bora Özdemir", null, null, daysAgoPending = 42),
        SeedProfile("ipek.caglar", "İpek Çağlar", null, null, daysAgoPending = 14),
        SeedProfile("ruzgar.altay", "Rüzgar Altay", null, null, daysAgoPending = 3)
    )

    fun createInitialBundle(nowMillis: Long = System.currentTimeMillis()): DemoSeedBundle {
        val nowSec = nowMillis / 1000L
        val daySec = 86_400L

        val currentFollowers = linkedMapOf<String, Long>()
        val currentFollowing = linkedMapOf<String, Long>()
        val currentPending = linkedMapOf<String, Long>()
        val closeFriends = linkedSetOf<String>()
        val displayNames = linkedMapOf<String, String>()
        val followerCounts = linkedMapOf<String, Long>()
        val annotations = linkedMapOf<String, AccountAnnotation>()

        for ((index, seed) in SEEDS.withIndex()) {
            displayNames[seed.username] = seed.displayName
            followerCounts[seed.username] = seed.followerCount ?: generateDemoFollowerCount(seed.username, index)
            seed.daysAgoFollowedMe?.let { currentFollowers[seed.username] = nowSec - it * daySec }
            seed.daysAgoIFollowed?.let { currentFollowing[seed.username] = nowSec - it * daySec }
            seed.daysAgoPending?.let { currentPending[seed.username] = nowSec - it * daySec }
            if (seed.isCloseFriend) closeFriends.add(seed.username)
            if (seed.isWhitelisted || seed.customTag.isNotEmpty() || seed.customNote.isNotEmpty()) {
                annotations[seed.username] = AccountAnnotation(
                    isWhitelisted = seed.isWhitelisted,
                    customTag = seed.customTag,
                    customNote = seed.customNote
                )
            }
        }

        // Önceki snapshot: Son kontrolden beri takipten çıkan 2 kişi + yeni gelen takipçileri göstermek için
        val prevFollowers = LinkedHashMap(currentFollowers).apply {
            remove("tamer.sonmez")
            remove("kerem.yalcin")
            put("caner_uygur", nowSec - 250 * daySec)
            put("ilayda.cetin", nowSec - 70 * daySec)
        }
        val prevFollowing = LinkedHashMap(currentFollowing).apply {
            remove("kerem.yalcin")
        }

        val previousSnapshot = Snapshot(
            id = "demo-prev",
            createdAtMillis = nowMillis - 7L * daySec * 1000L,
            sourceLabel = "Önceki Kontrol (7 gün önce)",
            followers = prevFollowers,
            following = prevFollowing,
            pendingRequests = currentPending,
            closeFriends = closeFriends,
            displayNames = displayNames,
            followerCounts = followerCounts
        )

        val currentSnapshot = Snapshot(
            id = "demo-current",
            createdAtMillis = nowMillis,
            sourceLabel = "Demo Veri Seti",
            followers = currentFollowers,
            following = currentFollowing,
            pendingRequests = currentPending,
            closeFriends = closeFriends,
            displayNames = displayNames,
            followerCounts = followerCounts
        )

        return DemoSeedBundle(
            snapshots = listOf(previousSnapshot, currentSnapshot),
            annotations = annotations
        )
    }

    fun simulateNextSnapshot(latest: Snapshot, nowMillis: Long = System.currentTimeMillis()): Snapshot {
        val nowSec = nowMillis / 1000L
        val nextFollowers = LinkedHashMap(latest.followers)
        val nextFollowing = LinkedHashMap(latest.following)
        val nextNames = LinkedHashMap(latest.displayNames)
        val nextCounts = LinkedHashMap(latest.followerCounts)

        // Karşılıklı bir takipçiyi takipten çıkmış gibi simüle et
        val mutualCandidate = latest.following.keys.firstOrNull { it in latest.followers && it !in listOf("defne.korkmaz", "emre_demir") }
        if (mutualCandidate != null) {
            nextFollowers.remove(mutualCandidate)
        }

        // Yeni takipçi ekle
        val suffix = ((nowMillis / 1000L) % 900 + 100).toInt()
        val newHandle = "yeni.takipci.$suffix"
        nextFollowers[newHandle] = nowSec
        nextNames[newHandle] = "Yeni Takipçi $suffix"
        nextCounts[newHandle] = (suffix * 7L) + 120L

        return Snapshot(
            id = UUID.randomUUID().toString(),
            createdAtMillis = nowMillis,
            sourceLabel = "Değişim Simülasyonu",
            followers = nextFollowers,
            following = nextFollowing,
            pendingRequests = latest.pendingRequests,
            closeFriends = latest.closeFriends,
            displayNames = nextNames,
            followerCounts = nextCounts
        )
    }

    private fun generateDemoFollowerCount(username: String, index: Int): Long {
        val hash = kotlin.math.abs(username.hashCode())
        return when (index % 5) {
            0 -> 4_200L + (hash % 14_500L)
            1 -> 1_100L + (hash % 3_400L)
            2 -> 420L + (hash % 750L)
            3 -> 165L + (hash % 310L)
            else -> 780L + (hash % 2_100L)
        }
    }
}

data class DemoSeedBundle(
    val snapshots: List<Snapshot>,
    val annotations: Map<String, AccountAnnotation>
)
