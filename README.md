# 🕵🏻 Installow - by Kyhlaer

**by kyhlaer** · Sürüm `1.32`

Instagram takipçi ve takip edilen analizini gizlilik odaklı, modern ve güvenli bir arayüzle cihaz üzerinde gerçekleştiren Android (Jetpack Compose) uygulaması.

## ✨ Özellikler

- **Detaylı Takip Analizi:** Seni takip etmeyenler, senin takip etmediklerin (hayranlar), karşılıklı takipleştiklerin, bekleyen istekler ve beyaz liste kategorileri.
- **Zaman İçindeki Değişimler:** Farklı tarihlerdeki veri kayıtlarını karşılaştırarak takipten çıkanları ve yeni takipçileri tespit eder.
- **Arama ve Gelişmiş Sıralama:** Kullanıcı adı, görünen ad, etiket ve notlarda arama; en yeni/eski tarih, takipçi sayısı (çoktan aza / azdan çoğa) ve A-Z / Z-A sıralama.
- **Sıralı İnceleme Kuyruğu:** Beyaz liste dışı hesapları sırayla inceleme, Instagram profilini açma, beyaz listeye ekleme veya yerel olarak işaretleme.
- **Çift Veri Kaynağı:**
  - **Resmi Instagram Arşivi (Sıfır Risk):** Meta veri indirme merkezinden alınan ZIP / JSON / HTML dosyalarını tamamen çevrimdışı analiz eder.
  - **Canlı Web Senkronizasyonu (Yavaş Mod):** İstekler arası gecikme, mola, 15 dk soğuma koruması ve hız uyarısında otomatik 60 dk kilitleme ile salt-okunur senkronizasyon.
- **Kişiselleştirilebilir Tema:** Koyu, Açık ve Sistem varsayılanı tema seçenekleri; sekmeler arası yatay kaydırma desteği.

## Siber Güvenlik ve Gizlilik Önlemleri

- **Yerel AES-256-GCM Şifreleme:** Uygulama verileri Android Keystore donanım destekli `AES/GCM/NoPadding` anahtarıyla şifrelenerek saklanır (`allowBackup="false"`).
- **Ağ ve WebView Sertleştirmesi:** Uygulama genelinde şifresiz HTTP (`cleartext`) trafiği kapalıdır. WebView yalnızca `https://` üzerinden `instagram.com`, `facebook.com` ve `meta.com` alan adlarına izin verir; SSL hatalarında bağlantıyı iptal eder ve giriş sırasında `FLAG_SECURE` ekran korumasını etkinleştirir.
- **JS Köprüsü Koruması:** `@JavascriptInterface` çağrıları oturumluk 256-bit `SecureRandom` token ve sabit zamanlı doğrulama ile korunur.
- **Arşiv & CSV Koruması:** Akışlı (`PushbackInputStream`) okuma, Zip Slip yol geçişi engelleme, ZIP boyutu/girdi sınırları ve CSV formül enjeksiyonu (`=`, `+`, `-`, `@`, `|`, `%`) nötralizasyonu içerir.

## Derleme ve Test

- Minimum SDK: `26` (Android 8.0) · Target/Compile SDK: `35`
- Dil & Arayüz: Kotlin `2.0.21`, Jetpack Compose, Material 3

```bash
# Birim güvenlik testlerini çalıştır
./gradlew testDebugUnitTest

# Optimize edilmiş (R8 + kaynak sıkıştırmalı) Release APK derle
./gradlew assembleRelease
```
