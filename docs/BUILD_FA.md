# Build از سورس

## نیازمندی‌ها

- JDK **17** (نه Java 18 برای این پروژه)
- Android SDK Platform **34** و Build Tools **34.0.0**
- Android SDK Platform Tools (`adb`)
- Git و اتصال اینترنت برای دریافت وابستگی‌های Gradle
- macOS / Linux / Windows (برای Windows از `gradlew.bat` استفاده کنید)

## ساخت

```bash
export JAVA_HOME=/absolute/path/to/jdk17
export ANDROID_HOME=/absolute/path/to/Android/sdk
export ANDROID_SDK_ROOT="$ANDROID_HOME"

cd launcher
printf 'sdk.dir=%s\n' "$ANDROID_HOME" > local.properties
./gradlew clean assembleDebug --no-daemon --console=plain
```

مسیر APK: `launcher/app/build/outputs/apk/debug/app-debug.apk`.

**امضا:** Debug فقط برای تست محلی است. Release به‌صورت پیش‌فرض unsigned است؛ راهنمای [SIGNING](SIGNING.md) را بخوانید.

## نصب آزمایشی روی TV

```bash
adb connect <TV_IP>:5555
adb devices -l
adb install -r launcher/app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.gathin.tvhome/.MainActivity
```

اگر `INSTALL_FAILED_UPDATE_INCOMPATIBLE` مشاهده شد، کلید امضای نسخه موجود و نسخه تازه متفاوت است. **بدون پشتیبان‌گیری، اطلاعات برنامه را حذف نکنید.**

## CI

Workflow `Build verification` در `.github/workflows/build.yml` فقط صحت ساخت و تست‌های پروژه را بررسی می‌کند. خروجی CI برای نصب عمومی منتشر نمی‌شود، و Release بدون متغیرهای خصوصی امضا نمی‌شود. پس از تعریف امن Release signing می‌توان مرحله انتشار را اضافه کرد.

## درباره macOS Catalina

Homebrew ممکن است از macOS قدیمی پشتیبانی نکند. می‌توان Android SDK Command-line Tools و JDK 17 را به‌صورت دستی نصب کرد. مقدارهای `JAVA_HOME` و `ANDROID_HOME` برای هر سیستم باید با مسیر واقعی همان سیستم جایگزین شوند؛ مسیر کامپیوتر توسعه‌دهنده نباید داخل مخزن ذخیره شود.

برای دستورهای کامل Windows PowerShell و جزئیات سازگاری Catalina، [BUILD_EN](BUILD_EN.md) را ببینید. دستورات نصب را از ریشه مخزن اجرا کنید؛ اگر داخل launcher هستید ابتدا به پوشه والد برگردید. installer قدیمی upstream به مسیر Release امضاشده متکی است؛ در این پیش‌نمایش از روش دستی Debug و ADB استفاده کنید.
