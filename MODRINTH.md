# Minecraft2Shell - Modrinth Description (Türkçe & English)

---

## 📢 Genel Bakış (Türkçe)

Minecraft2Shell, Minecraft içinde doğrudan işletim sistemi komutları çalıştırmanızı sağlayan güçlü bir client-side Fabric modudur!

## ✨ Ana Özellikler

| Özellik | Açıklama |
|---------|----------|
| 🖥️ **Çoklu Platform Desteği** | Windows, Linux ve MacOS'u destekler |
| ⚡ **Tamamen Client-Side** | Hiçbir sunucu moduna ihtiyaç duymaz |
| 📜 **Komut Geçmişi** | Önceden çalıştırdığınız komutları kaydeder ve tekrar kullanmanızı sağlar |
| 🏷️ **Alias Sistemi** | Sık kullandığınız komutlar için özel kısayollar oluşturun |
| 🚫 **Kara Liste** | İstenmeyen veya tehlikeli komutları engelleyin |
| ⚙️ **Gelişmiş Ayarlar** | ModMenu ve YACL ile entegreli konfigürasyon arayüzü |
| 🌍 **Çoklu Dil Desteği** | Türkçe ve İngilizce seçenekleri |

## 📝 Komutlar

Tüm komutlar `/shell` ile başlar:

- `/shell history` - Komut geçmişini listeler
- `/shell history clear` - Komut geçmişini temizler
- `/shell history <num>` - Belirli bir geçmiş komutunu tekrar çalıştırır
- `/shell alias list` - Mevcut aliasları listeler
- `/shell alias add <isim> <komut>` - Yeni bir alias ekler
- `/shell alias remove <isim>` - Bir aliası siler
- `/shell alias run <isim>` - Aliası hemen çalıştırır (yeniden başlatmaya gerek yok)
- `/shell blacklist list` - Kara listedeki komutları gösterir
- `/shell blacklist add <komut>` - Bir komutu kara listeye ekler
- `/shell blacklist remove <komut>` - Bir komutu kara listeden çıkarır
- `/shell config` - Ayarları açar
- `/shell <komut>` - Herhangi bir sistem komutunu çalıştırır

💡 **İpucu**: Oluşturduğunuz aliasları doğrudan `/<aliasadı>` olarak çalıştırabilirsiniz!

## 🛠️ Kurulum

1. Minecraft 26.1.2 veya uyumlu bir sürümünü kullanın
2. Fabric Loader'ı kurun
3. Gerekli bağımlılıkları yükleyin:
   - [Fabric API](https://modrinth.com/mod/fabric-api)
   - [Mod Menu](https://modrinth.com/mod/modmenu)
   - [YetAnotherConfigLib](https://modrinth.com/mod/yacl)
4. `minecraft2shell-x.x.x.jar` dosyasını indirin ve Minecraft dizininizdeki `mods` klasörüne atın
5. Oyunu başlatın!

## ⚠️ Güvenlik Uyarısı

**BU MOD SON DERECE GÜÇLÜDÜR!**
Kötü amaçlı komutlar çalıştırmak bilgisayarınıza kalıcı hasar verebilir. Komutları çalıştırmadan önce ne yaptığınızı iyice anladığınızdan emin olun!
- Başkalarının bu modu kullanmasına izin vermeyin!

## 📚 Lisans

MIT Lisansı

## 👤 Geliştirici

- Yazar: yigitgulyurt
- Kaynak Kodu: [GitHub](https://github.com/yigitgulyurt/minecraft2shell)

---

## 📢 Overview (English)

Minecraft2Shell is a powerful client-side Fabric mod that allows you to run operating system commands directly from within Minecraft!

## ✨ Key Features

| Feature | Description |
|---------|-------------|
| 🖥️ **Multi-Platform Support** | Supports Windows, Linux, and MacOS |
| ⚡ **Completely Client-Side** | No server mod required |
| 📜 **Command History** | Records previously executed commands and lets you re-use them |
| 🏷️ **Alias System** | Create custom shortcuts for frequently used commands |
| 🚫 **Blacklist** | Block unwanted or dangerous commands |
| ⚙️ **Advanced Settings** | Integrated configuration interface with ModMenu and YACL |
| 🌍 **Multi-Language Support** | Turkish and English options |

## 📝 Commands

All commands start with `/shell`:

- `/shell history` - List command history
- `/shell history clear` - Clear command history
- `/shell history <num>` - Re-run a specific command from history
- `/shell alias list` - List current aliases
- `/shell alias add <name> <command>` - Add a new alias
- `/shell alias remove <name>` - Remove an alias
- `/shell alias run <name>` - Run an alias immediately (no restart required)
- `/shell blacklist list` - Show blacklisted commands
- `/shell blacklist add <command>` - Add a command to the blacklist
- `/shell blacklist remove <command>` - Remove a command from the blacklist
- `/shell config` - Open settings
- `/shell <command>` - Run any system command

💡 **Tip**: You can run your aliases directly as `/<aliasname>`!

## 🛠️ Installation

1. Use Minecraft 26.1.2 or a compatible version
2. Install Fabric Loader
3. Install required dependencies:
   - [Fabric API](https://modrinth.com/mod/fabric-api)
   - [Mod Menu](https://modrinth.com/mod/modmenu)
   - [YetAnotherConfigLib](https://modrinth.com/mod/yacl)
4. Download `minecraft2shell-x.x.x.jar` and place it in your Minecraft `mods` folder
5. Launch the game!

## ⚠️ Security Warning

**THIS MOD IS EXTREMELY POWERFUL!**
Running malicious commands can cause permanent damage to your computer. Make sure you understand what you're doing before running any commands!
- Never let anyone else use this mod!

## 📚 License

MIT License

## 👤 Developer

- Author: yigitgulyurt
- Source Code: [GitHub](https://github.com/yigitgulyurt/minecraft2shell)
