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

Tüm komutlar `/shell` ile başlar:

| Komut | Açıklama |
|--------|----------|
| `/shell history` | Komut geçmişini göster |
| `/shell history clear` | Komut geçmişini temizle |
| `/shell history <numara>` | Geçmişten belirli bir komutu tekrar çalıştır |
| `/shell alias list` | Mevcut aliasları listele |
| `/shell alias add <isim> <komut>` | Yeni bir alias oluştur |
| `/shell alias remove <isim>` | Bir aliası sil |
| `/shell alias run <isim>` | Aliası hemen çalıştır (yeniden başlatmaya gerek yok) |
| `/shell blacklist list` | Kara listedeki komutları listele |
| `/shell blacklist add <komut>` | Bir komutu kara listeye ekle |
| `/shell blacklist remove <komut>` | Bir komutu kara listeden çıkar |
| `/shell config` | Konfigürasyon ekranını aç |
| `/shell <komut>` | Sistem komutu çalıştır |

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

All commands start with `/shell`:

| Command | Description |
|---------|-------------|
| `/shell history` | Show command history |
| `/shell history clear` | Clear command history |
| `/shell history <number>` | Re-run a specific command from history |
| `/shell alias list` | List existing aliases |
| `/shell alias add <name> <command>` | Create a new alias |
| `/shell alias remove <name>` | Remove an alias |
| `/shell alias run <name>` | Run an alias immediately (no restart required) |
| `/shell blacklist list` | List blacklisted commands |
| `/shell blacklist add <command>` | Add a command to the blacklist |
| `/shell blacklist remove <command>` | Remove a command from the blacklist |
| `/shell config` | Open configuration screen |
| `/shell <command>` | Run a system command |

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
