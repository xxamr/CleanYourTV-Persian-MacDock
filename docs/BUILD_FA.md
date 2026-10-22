# ساخت از سورس در مک، لینوکس و ویندوز

## نیازمندی‌ها

- JDK 17.
- Android SDK با سکوی API 34 و ابزار ساخت 34.0.0.
- ابزارهای خط فرمان Android SDK و دسترسی به مخازن Gradle، Google Maven و Maven Central.
- ابزار `adb` فقط برای نصب و آزمایش روی دستگاه.

پروژه از Gradle Wrapper نسخه 8.9 با هش مشخص‌شده در فایل تنظیمات استفاده می‌کند. SDK داخل مخزن قرار ندارد. مسیرهای نمونه را با مسیر واقعی سیستم خودتان جایگزین کنید. فایل `local.properties` و کلید امضا را وارد Git نکنید.

## مک و لینوکس

```sh
export JAVA_HOME=/absolute/path/to/jdk17
export ANDROID_HOME=/absolute/path/to/android-sdk
export PATH="$JAVA_HOME/bin:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH"
sdkmanager 'platforms;android-34' 'build-tools;34.0.0' 'platform-tools'
cd launcher
chmod +x gradlew
./gradlew --no-daemon testPreviewUnitTest lintPreview assemblePreview
```

خروجی Preview: `launcher/app/build/outputs/apk/preview/app-preview.apk`؛ شناسه آن جداگانه است. [نصب Preview](EXPERIMENTAL_APK.md)

برای نسخه معمولی آزمایشی یا خروجی Release:

```sh
./gradlew --no-daemon testDebugUnitTest assembleDebug
./gradlew --no-daemon testReleaseUnitTest lintRelease assembleRelease
```

خروجی Debug معمولی: `launcher/app/build/outputs/apk/debug/app-debug.apk`.
خروجی Release بدون کلید: `launcher/app/build/outputs/apk/release/app-release-unsigned.apk`؛ این فایل مستقیماً نصب نمی‌شود. [امضای انتشار](SIGNING.md)

## ویندوز با PowerShell

پس از نصب JDK و ابزارهای Android، مسیر واقعی را قرار دهید:

```powershell
$env:JAVA_HOME = 'C:\Java\jdk-17'
$env:ANDROID_HOME = 'C:\Android\sdk'
$env:Path = "$env:JAVA_HOME\bin;$env:ANDROID_HOME\cmdline-tools\latest\bin;$env:ANDROID_HOME\platform-tools;$env:Path"
sdkmanager.bat 'platforms;android-34' 'build-tools;34.0.0' 'platform-tools'
cd launcher
.\gradlew.bat --no-daemon testPreviewUnitTest lintPreview assemblePreview
.\gradlew.bat --no-daemon testReleaseUnitTest lintRelease assembleRelease
```

اگر پذیرش مجوزهای SDK درخواست شد، با `sdkmanager --licenses` متن را بخوانید و خودتان تصمیم بگیرید.

## نکته درباره macOS Catalina

Homebrew برای این سیستم قدیمی ممکن است قابل استفاده نباشد. نصب دستی JDK و ابزار خط فرمان SDK ممکن است؛ نسخه‌ای انتخاب کنید که سازنده صریحاً از macOS 10.15 پشتیبانی کند. سازگاری ابزارهای جدید با Catalina در این کار آزمایش نشده است. صرف وجود JDK 17، سازگاری تمام نصب‌کننده‌های فعلی را تضمین نمی‌کند.

## نصب محلی

از ریشه مخزن اجرا کنید؛ اگر داخل `launcher` هستید ابتدا یک پوشه بالا بیایید:

```sh
adb install -r launcher/app/build/outputs/apk/preview/app-preview.apk
adb shell am start -n com.gathin.tvhome.preview/com.gathin.tvhome.MainActivity
```

اسکریپت‌های قدیمی `build-and-install` پروژه اصلی مسیر و امضای Release قدیمی را فرض می‌کنند؛ برای این پیش‌نمایش از نصب دستی بالا استفاده کنید.

## ساخت خودکار در GitHub

فایل `build.yml` آزمون JVM، lint و ساخت Release را بررسی می‌کند و باینری منتشر نمی‌کند. فایل `preview-apk.yml` فقط با اجرای دستی روی شاخه اصلی همین مخزن، Preview را آزمایش، امضا را بررسی و به‌صورت انتشار آزمایشی منتشر می‌کند. کلید Debug آن موقت است و برای آپدیت عمومی پایدار مناسب نیست. [نتایج بررسی](VERIFICATION.md)
