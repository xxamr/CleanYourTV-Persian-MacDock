# CleanYourTV Persian MacDock (Community Edition)

**A Persian-friendly, macOS-inspired Android TV home screen, built on [CleanYourTV](https://github.com/Gathin23/cleanyourtv).**  
**لانچر فارسی با Dock شناور، Launchpad، تاریخ شمسی و تلکس خبری برای Android TV**

> **Community modification, not the official CleanYourTV project and not affiliated with Apple, Google, YouTube, Instagram, or any news publisher.**  
> نسخه سفارشی و مستقل از توسعه‌دهنده اصلی CleanYourTV است. هیچ ارتباط رسمی با Apple یا سرویس‌های نام‌برده ندارد.

## Status / وضعیت انتشار

**Experimental channel available; no Stable Release.** See [experimental APK installation and signing limits](docs/EXPERIMENTAL_APK.md) and the [Releases page](https://github.com/xxamr/CleanYourTV-Persian-MacDock/releases). The original source-preview release has no APK. The attached source snapshot is authoritative; no APK was included, so APK/source correspondence cannot be verified. Local Android build was blocked by network access to Gradle; see [verification](docs/VERIFICATION.md). Device behavior remains unverified in this publication session. Prior Mi Box 4 use is owner-reported, not a test of this exact published commit.

**APK آزمایشی از بخش Releases؛ نسخه Stable موجود نیست.** نصب، آپدیت و بازگشت این commit روی TV هنوز تأیید نشده است.

## Features / امکانات

- **Persian Jalali date / تاریخ شمسی فارسی** with Persian day/month names and Persian digits.
- **12-hour clock (AM/PM on an English system locale)** on Home and a large, drifting clock screensaver with Persian date.
- **Floating dock** centered near the bottom of the screen, roughly 60% of screen width.
- **Remote-controlled macOS-inspired magnification:** focused app scales up, neighbors scale progressively; scales collapse after several seconds of inactivity. Uses the apps' original icons.
- **Editable favorites on TV:** choose/reorder apps using the remote. No Mac/ADB needed after setup.
- **Fullscreen Launchpad:** browse all installed TV apps using directional buttons and Back.
- **Persian RSS news ticker:** source/topic selection, on/off toggle, configurable by remote, refresh attempted every five minutes while Home is active; left/right to switch items, OK to open the source article in a browser.
- **Small native Android launcher** that does not require root. Its performance depends on your TV, network and installed apps.

## Before / after — تصاویر قبل و بعد

The comparison awaits genuine images of this Mi Box's original Home and final Persian MacDock Home. The available owner photo shows an intermediate development state with overlapping docks, so it is not presented as the final UI. Upstream screenshots are not screenshots of this fork.

تصاویر واقعی Home قبلی و Home نهایی پس از دریافت و بررسی حریم خصوصی در همین بخش نمایش داده می‌شوند. [روش ثبت تصاویر](docs/SCREENSHOTS.md)

## Install / نصب آسان

**[Download experimental APK / دانلود APK آزمایشی (1.94 MB)](https://github.com/xxamr/CleanYourTV-Persian-MacDock/releases/download/experimental-27e5fec-1-1/CleanYourTV-experimental.apk)** · [Checksums and build information](https://github.com/xxamr/CleanYourTV-Persian-MacDock/releases/tag/experimental-27e5fec-1-1)

This APK installs as **CleanYourTV Preview** alongside the original launcher. Temporary debug signing; no seamless update or physical-TV validation claim. Use the [preview installation guide](docs/EXPERIMENTAL_APK.md), which has the correct package/activity commands.

**Experimental APK:** read [EXPERIMENTAL_APK](docs/EXPERIMENTAL_APK.md) first; its separate package and temporary signing have different commands and update limits.

**Future Stable installation:** open [Releases](https://github.com/xxamr/CleanYourTV-Persian-MacDock/releases), download a **verified, properly signed APK** if available, and follow [راهنمای نصب فارسی](docs/INSTALL_FA.md). **If no release is published yet, build from source** using [BUILD_FA](docs/BUILD_FA.md).

**پیش از نصب:** از تنظیمات و لانچر فعلی پشتیبان بگیرید. لانچر سیستمی را حذف نکنید. روی همه برندها و مدل‌ها تست نشده است.

**Compatibility:** Source project specifies Android **8.0+ (API 26+)**; owner reports prior use on Xiaomi Mi Box 4 / Android TV 9; this exact commit has not been device-tested. Other firmware can prevent changing the default Home, even when the command returns `Success`.

## Documentation / مستندات

| راهنما | توضیح |
|---|---|
| [INSTALL_FA](docs/INSTALL_FA.md) | نصب APK، اتصال ADB و تغییر Home با روش بازگشت |
| [USER_GUIDE_FA](docs/USER_GUIDE_FA.md) | کنترل Dock، Launchpad، ساعت و تلکس خبری |
| [BUILD_FA](docs/BUILD_FA.md) | Build از سورس با Java 17 و Android SDK |
| [TROUBLESHOOTING_FA](docs/TROUBLESHOOTING_FA.md) | اشکال‌های رایج، کرش و بازیابی Home |
| [PRIVACY_AND_RSS](docs/PRIVACY_AND_RSS.md) | مجوز اینترنت، RSS، منابع خبری و حریم خصوصی |
| [RELEASE_CHECKLIST](docs/RELEASE_CHECKLIST.md) | کنترل کیفیت و نکات امنیتی پیش از انتشار APK |
| [CONTRIBUTING](CONTRIBUTING.md) | مشارکت در توسعه |
| [CREDITS](CREDITS.md) | منبع اصلی، مجوزها و اعتبار توسعه‌دهندگان |

## Quick developer build

```bash
# Requirements: Java 17, Android SDK API 34 and Build Tools 34.0.0
cd launcher
printf 'sdk.dir=%s\n' "$ANDROID_HOME" > local.properties
./gradlew testDebugUnitTest assembleDebug --no-daemon
# Local test only: launcher/app/build/outputs/apk/debug/app-debug.apk
```

**Signing:** This fork removes upstream debug signing from Release. `assembleRelease` is unsigned unless all four private signing environment variables are supplied. Debug builds are local test builds. See [SIGNING](docs/SIGNING.md) and [RELEASE_CHECKLIST](docs/RELEASE_CHECKLIST.md).

English: [Build](docs/BUILD_EN.md) · [Controls](docs/USER_GUIDE_EN.md) · [Verification](docs/VERIFICATION.md) · [Upstream](docs/UPSTREAM.md).

## Copyright & attribution

Based on **CleanYourTV**, © 2026 Gathin and contributors, licensed under **MIT**. Retain the upstream `LICENSE` and the Outfit font's `launcher/FONT-LICENSE-OFL.txt` (SIL OFL 1.1). All modifications must retain the applicable notices. See [CREDITS.md](CREDITS.md).

---

**English quick start:** [INSTALL_EN](docs/INSTALL_EN.md) · **فارسی:** [INSTALL_FA](docs/INSTALL_FA.md)

Agent guidance: [AGENTS.md](AGENTS.md). Inherited device lists and historical docs are described in [UPSTREAM](docs/UPSTREAM.md).
