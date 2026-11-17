# Changelog / Sürüm Notları

## v1.33
- **Ayrılmış 5 Sekmeli Gezinme (`Panel · Kişiler · Analiz · Veri · Ayarlar`):**
  - Veri aktarımı/hesap işlemleri (**Veri**) ile tema/dil/siber güvenlik/hakkında seçenekleri (**Ayarlar**) iki ayrı alt sekmeye bölündü.
- **Çoklu Hesap Yönetimi & Kullanıcı Adından Doğrudan Veri Çekme:**
  - Panel üst kısmındaki `@kullanıcı` alanına dokunarak açılan **Hesaplar & Profil Ekle** penceresi eklendi; farklı Instagram hesapları eklenip tek dokunuşla aralarında geçiş yapılabiliyor.
  - Hiçbir ZIP/JSON verisi yüklenmeden yalnızca kullanıcı adı girilerek HTTPS üzerinden profil bilgisi ve takipçi/takip edilen sayıları çekiliyor; herkese açık profillerde Instagram'ın izin verdiği ölçüde listeler doğrudan gösteriliyor, gizli veya kısıtlı hesaplarda ise dürüst durum bildirimiyle Resmi Arşiv veya Web Oturum Açma yönlendirmesi sunuluyor.
- **Görsel Düzenlemeler & Ek Özellikler:**
  - Panel ve Kişiler ekranlarındaki üst yuvarlak butonların (`12.dp` aralık, `40.dp` boyut) birbirine yapışma sorunu giderildi; veri yükleme/çekme ikonu indirme ikonuyla (`Download`) yenilendi.
  - Panel üst başlığına küçük **`by kyhlaer`** imzası eklendi; Son Değişimler kartına **Senin Bıraktıkların** satırı eklendi.
  - Kişiler ekranına **Tümü / Notlular / Yakın Arkadaşlar** hızlı filtre çipleri ve kişi detay kartına tek dokunuşla `@kullanıcı_adı` kopyalama düğmesi eklendi.
- **İngilizce Dil Desteği (Bilingual TR / EN / System):**
  - Uygulama genelindeki tüm ekranlar, diyaloglar, hata/durum mesajları, etiketler, sıralama seçenekleri ve güvenlik denetimi sonuçları için tam **İngilizce** ve **Türkçe** desteği eklendi.
  - Varsayılan olarak cihaz diline uyum sağlayan `SYSTEM` seçeneği ile birlikte **Ayarlar** sekmesine anlık **`Dil / Language`** kontrolü eklendi (`Türkçe` · `English` · `Sistem`).
  - Takipçi sayısı kısaltmaları (`B` / `K`), tarih formatları, son 6 ay grafik etiketleri ve CSV dışa aktarma başlıkları seçilen dile göre güncellenir.
- **Uygulama İçi Sürüm Notları:** Ayarlar ekranına **Sürüm Notları (v1.33)** seçeneği eklendi.

## v1.32
- **Açılış Ekranı & İmza:** Uygulama açılışına **Installow** / **by kyhlaer** açılış ekranı eklendi.
- **Siber Güvenlik Sertleştirmesi:**
  - Android Keystore destekli yerel **AES-256-GCM** şifreli veri depolama (`allowBackup="false"`, `usesCleartextTraffic="false"`).
  - Sıkı HTTPS & Meta/Instagram alan adı whitelist, `onReceivedSslError` iptali, `FLAG_SECURE` ekran koruması ve 256-bit Nonce JS köprüsü doğrulaması.
  - Akışlı arşiv okuma (`PushbackInputStream`), Zip Slip koruması, ZIP bombası boyut/girdi sınırları ve CSV formül enjeksiyonu nötralizasyonu.
  - Uygulama içi 6 adımlı **Güvenlik Testini Çalıştır** öz-denetimi ve WebView çerez sıfırlama aracı.
- **Performans & Optimizasyon:** `@Immutable` Compose modelleri, ön-hesaplanmış arama/kategori alanları, `DateTimeFormatter` ve R8 / kaynak sıkıştırma optimizasyonu.
