# 🕵🏻 Installow - by Kyhlaer

**Sürüm `1.33`** · Gizlilik odaklı, modern ve güvenli Instagram takip analiz Android uygulaması.

> 📲 **İndir:** Hazır APK dosyası için [Releases (v1.33)](https://github.com/kyhlaer/Installow/releases/tag/v1.33) sayfasını ziyaret edebilirsiniz.

---

## ✨ Özellikler

- 👥 **Kapsamlı Bağlantı Analizi:** Seni takip etmeyenler, senin takip etmediklerin (hayranlar), karşılıklı takipler, bekleyen istekler ve beyaz liste tek ekranda.
- 📈 **Zaman İçindeki Değişimler:** Kaydedilen verileri karşılaştırarak **takipten çıkanları** ve **yeni takipçileri** otomatik listeler; son 6 aylık aktivite grafiği ve öne çıkan tarihleri sunar.
- 🔍 **Arama, Sıralama & Notlar:** Kullanıcı adı, isim, özel etiket veya notlarda anlık arama; tarihe, takipçi sayısına ve alfabetik (A-Z / Z-A) sıraya göre filtreleme.
- 🎯 **Sıralı İnceleme Kuyruğu:** Beyaz liste dışındaki hesapları sırayla gözden geçirin, tek dokunuşla Instagram profilini açın, beyaz listeye alın veya yerel olarak işaretleyin.
- 🗂️ **Çift Veri Kaynağı:**
  - 📦 **Resmi Instagram Arşivi (Sıfır Risk):** Meta'dan indirilen ZIP / JSON / HTML dosyalarını tamamen cihaz içinde ve çevrimdışı analiz eder.
  - 🌐 **Canlı Web Senkronizasyonu (Yavaş Mod):** Uygulama içi tarayıcı üzerinden beklemeli, molalı ve 15 dk soğuma korumalı salt-okunur senkronizasyon yapar.
- 🎨 **Tema, Dil & Akıcı Arayüz:** **Koyu / Açık / Sistem** teması, **Türkçe / English / Sistem** dil seçeneği, sekmeler arası yatay kaydırma, CSV rapor paylaşımı ve uygulama içi sürüm notları.

---

## 🛡️ Siber Güvenlik & Gizlilik

- 🔐 **AES-256-GCM Şifreleme:** Tüm yerel kayıtlar Android Keystore donanım anahtarıyla şifrelenerek saklanır (`allowBackup="false"`).
- 🔒 **Sertleştirilmiş WebView & Ağ:** Şifresiz HTTP (`cleartext`) kapalıdır; yalnızca `https://` üzerinden doğrulanmış Instagram / Meta alan adlarına izin verilir.
- 📵 **Ekran & Köprü Koruması:** Giriş sırasında `FLAG_SECURE` ekran görüntüsü kilidi ve 256-bit Nonce doğrulamalı JS köprüsü aktiftir; SSL hatalarında bağlantı anında kesilir.
- 🧱 **Arşiv & Veri Koruması:** Akışlı okuma, Zip Slip engeli, ZIP bombası limitleri, CSV formül enjeksiyonu nötralizasyonu ve uygulama içi **Güvenlik Testini Çalıştır** öz-denetimi bulunur.

---

## 🛠️ Teknik Bilgiler & Derleme

- **Platform:** Android `8.0+` (`minSdk 26` · `targetSdk 35`)
- **Teknoloji:** Kotlin `2.0.21` · Jetpack Compose · Material 3 · R8 Optimizasyonu

```bash
# Birim güvenlik testlerini çalıştır
./gradlew testDebugUnitTest

# Optimize edilmiş Release APK derle
./gradlew assembleRelease
```

---

## 📝 Sürüm Notları

Tüm değişikliklerin ayrıntıları için [CHANGELOG.md](CHANGELOG.md) dosyasına göz atabilirsiniz.

- 🆕 **v1.33:** Tam **İngilizce / Türkçe / Sistem** dil seçeneği, yerel formata uyumlu sayı/tarih/grafik/CSV çıktıları ve uygulama içi Sürüm Notları penceresi.
- 🚀 **v1.32:** **Installow by kyhlaer** açılış ekranı, AES-256-GCM Keystore şifreleme, WebView & Zip/CSV güvenlik kalkanı, canlı güvenlik testi ve R8 APK küçültme.
