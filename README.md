# GitHub Repository Viewer

Bu proje, GitHub API kullanarak bir kullanıcının repolarını listeleyen Android uygulamasıdır.

## KURULUM
1. `local.properties irisindeki değerlere kendi api bilgilerinizi ekleyin ve sonra proje çalıştırın.
2. İçindeki örnek değerleri kendi API bilgilerinizle değiştirin:
   - BASE_URL = API_BASE_URL_ORNEK
   - GITHUB_API_TOKEN = API_TOKEN_ORNEK

## Özellikler

- GitHub API entegrasyonu ile repo listeleme
- Pagination desteği 
- Offline kullanım için local veritabanı desteği
- 1, 2 veya 3 sütunlu görünüm seçeneği
- Repository detaylarını Toast mesajı ile gösterme

## Kullanılan Teknolojiler

### Mimari
- Clean Architecture
- MVVM (Model-View-ViewModel)
- Repository Pattern
- Use Case Pattern
- Single Activity

### Android Jetpack
- Compose UI
- ViewModel
- DataStore
- Room Database
- Hilt (Dependency Injection)

### Asenkron İşlemler
- Kotlin Coroutines
- Flow
- StateFlow

### Network
- Retrofit
- OkHttp
- Moshi

### UI
- Material Design 3
- Compose Navigation
- LazyColumn & LazyGrid
- Custom Composables

### Diğer
- BuildConfig yapılandırması
- Gradle Kotlin DSL
- GridLayoutManager
- Material Design Components

## Atlanılan Kısımlar (Test Projesi Olması Nedeniyle)

1. **Loglama**: Detaylı loglama sistemi implement edilmedi.
2. **Birim Testler**: Unit ve UI testleri eklenmedi.

### Offline Kullanım
- Veriler Room veritabanında saklanır
- İnternet bağlantısı olmadığında local veriler gösterilir
- İlk sayfa yüklenirken veritabanı temizlenir
- Her yeni sayfa veritabanına eklenir

### Görünüm Modları
- Tek sütun: Detaylı liste görünümü
- İki sütun: Orta boy grid görünümü
- Üç sütun: Kompakt grid görünümü
- Seçilen görünüm DataStore ile saklanır
- Uygulama yeniden açıldığında son seçilen görünüm kullanılır

## Güvenlik

- API anahtarları BuildConfig üzerinden yönetildi
- Base URL'ler build variantlarına göre yapılandırılabilirdi.(Test projesi oldugu için yapılmadı)
- ProGuard kuralları uygulanabilirdi
- Network security configuration kullandım

## Alınabilecek Önlemler

1. **Veri Güvenliği**:
   - Veritabanı şifrelenmeli
   - Hassas veriler encrypted olarak saklanmalı
   - ProGuard/R8 code obfuscation kullanılmalı

2. **Network Güvenliği**:
   - HTTPS zorunlu kılınmalı
   - Network security configuration dosyası eklenmeli
   - Certificate pinning implementasyonu yapılmalı

3. **Uygulama Güvenliği**:
   - Root detection eklenmeli
   - Debugging devre dışı bırakılmalı
   - Memory dumping engellemeli
