# Hava Kalitesi İzleme Sistemi (Geliştirme Aşamasında)

Bu proje, NodeMCU ve çeşitli sensörler kullanılarak gerçek zamanlı hava kalitesi izleme sistemi oluşturmak için geliştirilmekte olan bir Android uygulamasıdır. Sistem, çevresel verileri (hava kalitesi, sıcaklık, nem ve çeşitli gaz seviyeleri) ölçer ve Firebase Realtime Database üzerinden kullanıcılara sunar.

> ⚠️ **Not**: Bu proje şu anda geliştirme aşamasındadır ve bazı özellikler tam olarak çalışmamaktadır.

## Mevcut Özellikler

✅ Tam Çalışan Özellikler:
- 🌡️ Gerçek zamanlı sıcaklık ve nem takibi
- 💨 Hava kalitesi ölçümü (PPM)
- ⚠️ Zararlı gaz seviyelerinin takibi (LPG, CO, Duman)
- 📊 Günlük, haftalık ve aylık veri grafikleri
- 👤 Temel kullanıcı kimlik doğrulama

🚧 Geliştirme Aşamasındaki Özellikler:
- Özelleştirilebilir bildirim sistemi
- Gelişmiş profil yönetimi
- Veri analizi ve raporlama
- Kullanıcı tercihlerinin yönetimi

## Teknik Detaylar

### Donanım Bileşenleri
- NodeMCU ESP8266
- MQ-135 Hava Kalitesi Sensörü
- DHT11/DHT22 Sıcaklık ve Nem Sensörü
- MQ-2 Gaz Sensörü (LPG, CO, Duman)

### Yazılım Bileşenleri
- Android Studio
- Kotlin programlama dili
- Firebase Realtime Database
- Firebase Authentication
- MPAndroidChart (grafik görselleştirme)
- ViewBinding
- Navigation Component
- Coroutines

## Kurulum

1. Projeyi klonlayın:
```bash
git clone https://github.com/kullaniciadi/air-quality-monitoring.git
```

2. Android Studio'da projeyi açın

3. Firebase Console'dan yeni bir proje oluşturun ve `google-services.json` dosyasını `app` klasörüne ekleyin

4. Uygulamayı derleyin ve çalıştırın

## Mevcut Durum ve Kısıtlamalar

- 🚧 Bildirim sistemi henüz test aşamasındadır ve tam olarak çalışmayabilir
- 🚧 Profil yönetimi özellikleri sınırlıdır ve geliştirme aşamasındadır
- ⚠️ Uygulama şu anda test amaçlı olarak kullanılmaktadır
- 📝 Bazı özellikler eksik veya optimize edilmemiş olabilir

## Gelecek Geliştirmeler

- [ ] Bildirim sisteminin tamamlanması
- [ ] Profil yönetimi özelliklerinin geliştirilmesi
- [ ] Veri analizi ve raporlama özelliklerinin eklenmesi
- [ ] Kullanıcı arayüzünün iyileştirilmesi
- [ ] Performans optimizasyonları
- [ ] Kapsamlı test süreçlerinin tamamlanması

## Katkıda Bulunma

Bu proje geliştirmeye açıktır ve katkılarınızı bekliyoruz. Katkıda bulunmak için:

1. Bu depoyu fork edin
2. Yeni bir branch oluşturun (`git checkout -b feature/yeniOzellik`)
3. Değişikliklerinizi commit edin (`git commit -am 'Yeni özellik eklendi'`)
4. Branch'inizi push edin (`git push origin feature/yeniOzellik`)
5. Pull Request oluşturun

## Lisans

Bu proje MIT lisansı altında lisanslanmıştır. Daha fazla bilgi için `LICENSE` dosyasına bakın.

## İletişim

Fatih Altuntaş - [@github](https://github.com/fatihaltuntas) - fatihaltuntas0@outlook.com

Proje Linki: [https://github.com/fatihaltuntas/air-quality-monitoring](https://github.com/fatihaltuntas/air-quality-monitoring)