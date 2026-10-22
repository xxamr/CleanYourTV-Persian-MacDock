# نصب CleanYourTV Persian MacDock — فارسی

این نسخه یک لانچر سفارشی Android TV است. برای نصب نیازی به Root نیست. روش راه‌اندازی Home در Firmwareهای مختلف فرق دارد.

## روش ساده: APK منتشرشده

1. از بخش **Releases** همین مخزن، یک APK امضاشده و تست‌شده دانلود کنید. اگر هنوز Release قابل‌اعتماد منتشر نشده است، از روش «ساخت از سورس» استفاده کنید.
2. در Android TV به `Settings → Device Preferences → About` بروید و چند بار روی Build بزنید تا Developer Options فعال شود؛ نام گزینه ممکن است فرق کند.
3. ADB/USB debugging را روشن کنید. کامپیوتر و TV باید در شبکه قابل دسترس باشند. بعضی دستگاه‌ها USB debugging را فقط از کابل ارائه می‌کنند.
4. روی کامپیوتر، Android Platform Tools (`adb`) نصب باشد. روی تلویزیون درخواست تأیید Debugging را قبول کنید.
5. نمونه نصب (IP را با آدرس تلویزیون **خودتان** جایگزین کنید):

```bash
adb connect <TV_IP>:5555
adb devices -l
adb install -r path/to/app-debug.apk
adb shell am start -n com.gathin.tvhome/.MainActivity
```

`<TV_IP>` یک جای‌نگهدار است؛ آن را عیناً تایپ نکنید. اگر ارتباط Wireless ADB در دستگاه غیرفعال است، مطابق راهنمای سازنده عمل کنید. اگر امضای APK نسخه جدید با قبلی فرق کند، `INSTALL_FAILED_UPDATE_INCOMPATIBLE` رخ می‌دهد؛ **پاک‌کردن برنامه می‌تواند تنظیمات Favorites و تلکس را حذف کند**. ابتدا از تنظیمات خود نسخه پشتیبان بگیرید.

## تنظیم Home پیش‌فرض، با احتیاط

اول در Android TV به `Settings → Apps → Default apps → Home app` یا صفحه انتخاب Home بروید. CleanYourTV را انتخاب کنید. نام منو در همه TVها یکسان نیست.

در صورت لزوم از ADB استفاده کنید:

```bash
adb shell cmd package query-activities --brief -a android.intent.action.MAIN -c android.intent.category.HOME
adb shell cmd package set-home-activity com.gathin.tvhome/.MainActivity
adb shell cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.HOME
```

**حتماً با دکمه فیزیکی Home ریموت تست کنید.** پیام `Success` به‌تنهایی تضمین نیست. بعضی Xiaomi/Google TVها همچنان لانچر اصلی را باز می‌کنند.

**هیچ بسته سیستمی را صرفاً برای نصب این لانچر غیرفعال نکنید.** اگر پس از بررسی دقیق Firmware تصمیم به غیرفعال‌کردن لانچر کارخانه گرفته‌اید، روش بازگشت را از قبل آماده کنید. مخزن اصلی خطرات مدل‌های مختلف را در `docs/TROUBLESHOOTING.md` تشریح کرده است.

## بازیابی Google TV

اگر Android TV اصلی شما از `com.google.android.tvlauncher` استفاده می‌کند:

```bash
adb shell pm enable --user 0 com.google.android.tvlauncher
adb shell cmd package set-home-activity <RECORDED_STOCK_HOME_COMPONENT>
adb shell input keyevent KEYCODE_HOME
```

در بعضی Firmwareها نام بسته کارخانه متفاوت است؛ پیش از هر اقدامی نام واقعی آن را ثبت کنید. لانچر کارخانه را هرگز Uninstall نکنید. اگر صفحه Home باز نمی‌شود ولی ADB وصل است، این دستور معمولاً راه بازگشت است.

## هشدارهای سازگاری

- استفاده قبلی روی **Mi Box 4 / Android TV 9** توسط مالک گزارش شده؛ این commit تست دستگاه نشده؛ عملکرد سایر مدل‌ها هنوز تأیید نشده است.
- برای اتصال به منابع RSS، اینترنت مستقیم یا مسیری که با قوانین و تنظیمات شبکه خودتان سازگار است لازم است.
- دکمه‌های ریموت با ماوس فرق دارند؛ کنترل اصلی با جهت‌ها، OK و Back انجام می‌شود.
- VPN یا برنامه‌های کاربر نباید به‌صورت خودکار حذف یا متوقف شوند.

Record the actual stock HOME component before switching: `adb shell cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.HOME`. Google TV often uses `com.google.android.apps.tv.launcherx`; do not guess its activity class. Replace placeholders before running commands. After testing, turn off debugging, revoke authorizations, and reboot. Local debug keys differ between computers; they cannot necessarily update the existing Mac installation. No built-in settings export is verified; record preferences manually.
