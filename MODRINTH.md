# Minecraft2Shell - Modrinth Açıklaması

## 📢 Genel Bakış

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

## 📝 Komutlar

Tüm komutlar `/shell` ile başlar:

- `/shell history` - Komut geçmişini listeler
- `/shell history clear` - Komut geçmişini temizler
- `/shell history <num>` - Belirli bir geçmiş komutunu tekrar çalıştırır
- `/shell alias list` - Mevcut aliasları listeler
- `/shell alias add <isim> <komut>` - Yeni bir alias ekler
- `/shell alias remove <isim>` - Bir aliası siler
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
