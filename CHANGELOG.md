# Changelog / Sürüm Notları

## v1.33
- **İngilizce Dil Desteği (Bilingual TR / EN / System):**
  - Uygulama genelindeki tüm ekranlar, diyaloglar, hata/durum mesajları, etiketler, sıralama seçenekleri ve güvenlik denetimi sonuçları için tam **İngilizce** ve **Türkçe** desteği eklendi.
  - Varsayılan olarak cihaz diline uyum sağlayan `SYSTEM` seçeneği ile birlikte **Veri & Ayarlar** sekmesine anlık **`Dil / Language`** kontrolü eklendi (`Türkçe` · `English` · `Sistem`).
  - Takipçi sayısı kısaltmaları (`B` / `K`), tarih formatları, son 6 ay grafik etiketleri ve CSV dışa aktarma başlıkları seçilen dile göre güncellenir.
- **Uygulama İçi Sürüm Notları:** Veri & Ayarlar ekranına **Sürüm Notları (v1.33)** seçeneği eklendi.

## v1.32
- **Açılış Ekranı & İmza:** Uygulama açılışına **Installow** / **by kyhlaer** açılış ekranı eklendi.
- **Siber Güvenlik Sertleştirmesi:**
  - Android Keystore destekli yerel **AES-256-GCM** şifreli veri depolama (`allowBackup="false"`, `usesCleartextTraffic="false"`).
  - Sıkı HTTPS & Meta/Instagram alan adı whitelist, `onReceivedSslError` iptali, `FLAG_SECURE` ekran koruması ve 256-bit Nonce JS köprüsü doğrulaması.
  - Akışlı arşiv okuma (`PushbackInputStream`), Zip Slip koruması, ZIP bombası boyut/girdi sınırları ve CSV formül enjeksiyonu nötralizasyonu.
  - Uygulama içi 6 adımlı **Güvenlik Testini Çalıştır** öz-denetimi ve WebView çerez sıfırlama aracı.
- **Performans & Optimizasyon:** `@Immutable` Compose modelleri, ön-hesaplanmış arama/kategori alanları, `DateTimeFormatter` ve R8 / kaynak sıkıştırma optimizasyonu.
