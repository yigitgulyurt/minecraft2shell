# Minecraft2Shell

Minecraft içinde doğrudan sistem komutları çalıştırmanızı sağlayan Fabric modu!

## Özellikler

- 💻 **Windows, Linux ve MacOS desteği** - Tüm popüler işletim sistemlerini destekler
- ⚡ **Client-side mod** - Sunucuya ihtiyaç duymaz, tek başına çalışır
- 📜 **Komut geçmişi** - Önceden çalıştırdığınız komutları kaydeder ve hızlıca tekrar çalıştırmanızı sağlar
- 🏷️ **Alias desteği** - Sık kullandığınız komutlar için kısayollar oluşturun
- 🚫 **Kara liste** - İstenmeyen komutları engelleyin
- ⚙️ **Konfigürasyon ekranı** - ModMenu ile entegreli, kolayca ayarları yapın

## Komutlar

Tüm komutlar `/shell` ile başlar:

| Komut | Açıklama |
|--------|----------|
| `/shell history` | Komut geçmişini göster |
| `/shell history clear` | Komut geçmişini temizle |
| `/shell history <numara>` | Geçmişten belirli bir komutu tekrar çalıştır |
| `/shell alias list` | Mevcut aliasları listele |
| `/shell alias add <isim> <komut>` | Yeni bir alias oluştur |
| `/shell alias remove <isim>` | Bir aliası sil |
| `/shell blacklist list` | Kara listedeki komutları listele |
| `/shell blacklist add <komut>` | Bir komutu kara listeye ekle |
| `/shell blacklist remove <komut>` | Bir komutu kara listeden çıkar |
| `/shell config` | Konfigürasyon ekranını aç |
| `/shell <komut>` | Sistem komutu çalıştır |

## Bağımsız aliasları doğrudan `/<aliasadı> olarak da kullanabilirsiniz!

## Kurulum

1. Minecraft 26.1.2 veya uyumlu bir sürümünü kullanın
2. Fabric Loader kurulu olsun
3. Gerekli bağımlılıkları yükleyin:
   - [Fabric API](https://modrinth.com/mod/fabric-api)
   - [Mod Menu](https://modrinth.com/mod/modmenu)
   - [YetAnotherConfigLib](https://modrinth.com/mod/yacl)
4. Mod dosyasını (`minecraft2shell-x.x.x.jar) Minecraft dizininin `mods` klasörüne atın
5. Oyunu başlatın!

## Güvenlik Uyarısı

Bu mod **son derece güçlüdür! Kötü amaçlı komutlar çalıştırmak bilgisayarınıza zarar verebilir. Komutları çalıştırmadan önce ne yaptığınızdan emin olun!

- Yetkilendirilmemiş kişilerin bu modu kullanmasına izin vermeyin!

## Geliştirme

### Gereksinimler
- Java 25
- Gradle 9.x
- Minecraft 26.1.2
- Fabric Loom

### Derleme
```bash
./gradlew build
```

### Çalıştırma (Geliştirme Ortamı)
```bash
./gradlew runClient
```

## Lisans

MIT Lisansı - daha fazla bilgi için [LICENSE](LICENSE) dosyasına bakın.

## İletişim

- [Kaynak Kodu (GitHub)](https://github.com/yigitgulyurt/minecraft2shell)
- Yazar: yigitgulyurt
