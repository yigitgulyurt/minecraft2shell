# Minecraft2Shell (Türkçe & English)

---

## Türkçe

Minecraft içinde doğrudan sistem komutları çalıştırmanızı sağlayan Fabric modu!

## Özellikler

- 💻 **Windows, Linux ve MacOS desteği** - Tüm popüler işletim sistemlerini destekler
- ⚡ **Client-side mod** - Sunucuya ihtiyaç duymaz, tek başına çalışır
- 📜 **Komut geçmişi** - Önceden çalıştırdığınız komutları kaydeder ve hızlıca tekrar çalıştırmanızı sağlar
- 🏷️ **Alias desteği** - Sık kullandığınız komutlar için kısayollar oluşturun
- 🚫 **Kara liste** - İstenmeyen komutları engelleyin
- ⚙️ **Konfigürasyon ekranı** - ModMenu ile entegreli, kolayca ayarları yapın
- 🌍 **Çoklu Dil Desteği** - Türkçe ve İngilizce seçenekleri

## Komutlar

Tüm komutlar `/m2s` ile başlar:

| Komut | Açıklama |
|--------|----------|
| `/m2s history` | Komut geçmişini göster |
| `/m2s history clear` | Komut geçmişini temizle |
| `/m2s history <numara>` | Geçmişten belirli bir komutu tekrar çalıştır |
| `/m2s alias list` | Mevcut aliasları listele |
| `/m2s alias add <isim> <komut>` | Yeni bir alias oluştur |
| `/m2s alias remove <isim>` | Bir aliası sil |
| `/m2s alias run <isim>` | Aliası hemen çalıştır (yeniden başlatmaya gerek yok) |
| `/m2s blacklist list` | Kara listedeki komutları listele |
| `/m2s blacklist add <komut>` | Bir komutu kara listeye ekle |
| `/m2s blacklist remove <komut>` | Bir komutu kara listeden çıkar |
| `/m2s config` | Konfigürasyon ekranını aç |
| `/m2s <komut>` | Sistem komutu çalıştır |

Bağımsız aliasları doğrudan `/<aliasadı>` olarak da kullanabilirsiniz!

## Kurulum

1. Minecraft 26.1.2 veya uyumlu bir sürümünü kullanın
2. Fabric Loader kurulu olsun
3. Gerekli bağımlılıkları yükleyin:
   - [Fabric API](https://modrinth.com/mod/fabric-api)
   - [Mod Menu](https://modrinth.com/mod/modmenu)
   - [YetAnotherConfigLib](https://modrinth.com/mod/yacl)
4. Mod dosyasını (`minecraft2shell-x.x.x.jar`) Minecraft dizininin `mods` klasörüne atın
5. Oyunu başlatın!

## Güvenlik Uyarısı

Bu mod **son derece güçlüdür!** Kötü amaçlı komutlar çalıştırmak bilgisayarınıza zarar verebilir. Komutları çalıştırmadan önce ne yaptığınızdan emin olun!

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

---

## English

Fabric mod that lets you run system commands directly from within Minecraft!

## Features

- 💻 **Windows, Linux and MacOS support** - Supports all popular operating systems
- ⚡ **Client-side mod** - Requires no server, works standalone
- 📜 **Command history** - Records previously executed commands and quickly lets you re-run them
- 🏷️ **Alias support** - Create shortcuts for frequently used commands
- 🚫 **Blacklist** - Block unwanted commands
- ⚙️ **Configuration screen** - Integrated with ModMenu, easy to configure settings
- 🌍 **Multi-Language Support** - Turkish and English options

## Commands

All commands start with `/m2s`:

| Command | Description |
|---------|-------------|
| `/m2s history` | Show command history |
| `/m2s history clear` | Clear command history |
| `/m2s history <number>` | Re-run a specific command from history |
| `/m2s alias list` | List existing aliases |
| `/m2s alias add <name> <command>` | Create a new alias |
| `/m2s alias remove <name>` | Remove an alias |
| `/m2s alias run <name>` | Run an alias immediately (no restart required) |
| `/m2s blacklist list` | List blacklisted commands |
| `/m2s blacklist add <command>` | Add a command to the blacklist |
| `/m2s blacklist remove <command>` | Remove a command from the blacklist |
| `/m2s config` | Open configuration screen |
| `/m2s <command>` | Run a system command |

You can also use standalone aliases directly as `/<aliasname>`!

## Installation

1. Use Minecraft 26.1.2 or a compatible version
2. Fabric Loader must be installed
3. Install required dependencies:
   - [Fabric API](https://modrinth.com/mod/fabric-api)
   - [Mod Menu](https://modrinth.com/mod/modmenu)
   - [YetAnotherConfigLib](https://modrinth.com/mod/yacl)
4. Place the mod file (`minecraft2shell-x.x.x.jar`) in your Minecraft `mods` folder
5. Launch the game!

## Security Warning

This mod is **extremely powerful!** Running malicious commands can damage your computer. Make sure you know what you're doing before running commands!

- Do not let unauthorized people use this mod!

## Development

### Requirements
- Java 25
- Gradle 9.x
- Minecraft 26.1.2
- Fabric Loom

### Building
```bash
./gradlew build
```

### Running (Development Environment)
```bash
./gradlew runClient
```

## License

MIT License - see [LICENSE](LICENSE) file for more details.

## Contact

- [Source Code (GitHub)](https://github.com/yigitgulyurt/minecraft2shell)
- Author: yigitgulyurt
