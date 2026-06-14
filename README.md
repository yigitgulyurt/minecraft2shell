# Minecraft2Shell (Türkçe & English)

---

## ⚠️ HEMEN İLK UYARI! (Türkçe)
**LÜTFEN OKUYUN!** Bu mod, bilgisayarınıza tam erişim sağlar. Ne yaptığınızı bilmediğiniz komutları çalıştırmayın! Her komutlar ve dosyalarınızı kalıcı olarak silebilir, sisteminizi bozabilirsiniz!

---

### ✨ Güvenli Kullanım için Bilgilendirme (Türkçe)
1. **Modu doğru ve uygun şekilde kullanırsanız, bilgisayarınıza herhangi bir zarar gelmeyecektir.**
2. **Ben (yigitgulyurt), sizin bilgisayarlarınıza hiçbir erişim imkanım yoktur.** Mod yalnızca sizin cihazınızda çalışır.
3. **Tarafımdan kesinlikle hiçbir zararlı eylem gerçekleştirilmeyecektir.** Modun kaynak kodu herkes tarafından incelenebilir.
4. **Kullanıcıların çalıştıracakları tüm komutlardan doğan sorumluluk tamamen kendilerine aittir.** Bu konuda tarafımca hiçbir sorumluluk kabul edilmemektedir.
5. **Bu mod yalnızca belirli, konu hakkında bilgi sahibi olan bir kullanıcı kesimine hitap eder.** Genel son kullanıcılar için önerilmez ancak doğru ve uygun kullanım koşullarında herhangi bir sakınca yaratmaz.

Lütfen dikkatli olun!

---

## ⚠️ FIRST WARNING! (English)
**PLEASE READ!** This mod gives full access to your computer. Do NOT run commands you don't understand! It can permanently delete your files or damage your system!

---

### ✨ Safe Usage Information (English)
1. **If you use this mod correctly and appropriately, your computer will NOT be harmed.**
2. **I (yigitgulyurt) have NO access to your computer.** This mod only runs locally on your device.
3. **NO malicious actions will ever be performed by me.** The source code is available for anyone to inspect.
4. **Users are solely responsible for ALL commands they run.** I accept NO responsibility in this matter.
5. **This mod is only intended for a specific group of informed users.** It is NOT recommended for general end-users, but causes no harm when used correctly and appropriately.

Please be careful!

---

## Türkçe

Minecraft içinde doğrudan sistem komutları çalıştırmanızı sağlayan Fabric modu!

## Özellikler

- 💻 **Tam Çoklu Platform Desteği** - Windows, Linux ve MacOS'u destekler, istediğiniz kabuk (shell) ile çalışır!
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

**BU MOD SON DERECE GÜÇLÜDÜR VE DİKKATSİZ KULLANILMASI HALİNDE ZARAR VEREBİLİR!**

⚠️ **Ne yapacağınızdan %100 emin olmadığınız komutları ÇALIŞTIRMAYIN!**
⚠️ **Önemli dosyalarınızı yedekleyin!**
⚠️ **Başkalarının bu modu kullanmasına ASLA izin vermeyin!**

Kötü amaçlı komutlar şunlara neden olabilir:
- Tüm dosyalarınızın kalıcı olarak silinmesi
- Sistem dosyalarının bozulması
- Bilgisayarınızın virüs veya kötü amaçlı yazılım ile enfekte olması
- Kişisel bilgilerinizin çalınması

Komutları çalıştırmadan önce ne yaptığınızı tamamen anladığınızdan emin olun!

⚠️ **Unutmayın**: Yukarıdaki "Güvenli Kullanım için Bilgilendirme" kısmını da tekrar okuyun!

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

- 💻 **Full Multi-Platform Support** - Supports Windows, Linux, and MacOS, and works with your preferred shell!
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

**THIS MOD IS EXTREMELY POWERFUL AND CAN CAUSE SERIOUS DAMAGE IF USED CARELESSLY!**

⚠️ **NEVER run commands you don't 100% understand!**
⚠️ **Always backup your important files!**
⚠️ **NEVER let anyone else use this mod!**

Malicious commands can cause:
- Permanent deletion of all your files
- Corruption of system files
- Infection of your computer with viruses or malware
- Theft of personal information

Make sure you fully understand what you're doing before running any commands!

⚠️ **Remember**: Please re-read the "Safe Usage Information" section above!

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
