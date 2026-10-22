# نصب APK آزمایشی

**راهنمای قدم‌به‌قدم:** [نصب با مک](INSTALL_MAC.md) · [نصب با ویندوز](INSTALL_WINDOWS.md)

این فایل برای آزمایش است؛ نسخه پایدار نیست. نام برنامه **CleanYourTV Preview** و شناسه آن `com.gathin.tvhome.preview` است. کنار نسخه اصلی `com.gathin.tvhome` نصب می‌شود و تنظیمات برنامه‌های دلخواه و اخبار را از آن وارد نمی‌کند.

## دریافت فایل

[دانلود APK](https://github.com/xxamr/CleanYourTV-Persian-MacDock/releases/download/experimental-27e5fec-1-1/CleanYourTV-experimental.apk) · [صفحه انتشار و فایل SHA256SUMS.txt](https://github.com/xxamr/CleanYourTV-Persian-MacDock/releases/tag/experimental-27e5fec-1-1)

فایل `BUILD-INFO.txt` شامل commit سورس، شناسه برنامه و اثرانگشت گواهی امضاست. برای بررسی فایل در مک از `shasum -a 256 CleanYourTV-experimental.apk` و در لینوکس از `sha256sum CleanYourTV-experimental.apk` استفاده کنید؛ نتیجه باید با `SHA256SUMS.txt` یکسان باشد.

## پیش از نصب

لانچر قبلی را فعال نگه دارید. [اتصال ADB](INSTALL_FA.md) را برقرار کنید. دستورهای زیر برای یک دستگاه مجازشده‌اند؛ اگر چند دستگاه وصل‌اند، دستگاه درست را با گزینه `-s` انتخاب کنید. این فرمان، Home فعلی را نشان می‌دهد؛ خروجی آن را برای بازگشت نگه دارید:

```sh
adb shell cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.HOME
```

## نصب و اجرای اولیه

در پوشه‌ای که APK را دانلود کرده‌اید اجرا کنید:

```sh
adb install -r CleanYourTV-experimental.apk
adb shell am start -n com.gathin.tvhome.preview/com.gathin.tvhome.MainActivity
```

ابتدا جهت‌ها، OK، Back، بازکردن برنامه‌ها و تنظیمات اخبار را امتحان کنید. برای این مرحله نیازی به تغییر Home نیست.

## انتخاب به‌عنوان Home

روش ترجیحی، تنظیمات دستگاه ← برنامه‌های پیش‌فرض ← برنامه Home است. در دستگاه‌های پشتیبانی‌شده می‌توانید از این دستور نیز استفاده کنید:

```sh
adb shell cmd package set-home-activity com.gathin.tvhome.preview/com.gathin.tvhome.MainActivity
```

با **دکمه فیزیکی Home ریموت** امتحان کنید. پاسخ `Success` به‌تنهایی کافی نیست.

## بازگشت به لانچر قبلی

در تنظیمات دستگاه، Home قبلی را انتخاب کنید. یا جای‌نگهدار زیر را با مؤلفه دقیقی که پیش از نصب ثبت کرده‌اید جایگزین کنید:

```sh
adb shell cmd package set-home-activity <RECORDED_HOME_COMPONENT>
adb shell input keyevent KEYCODE_HOME
```

نام مؤلفه را حدس نزنید. برای حذف نسخه آزمایشی، ابتدا به Home قبلی برگردید و سپس از تنظیمات برنامه‌های تلویزیون اقدام کنید؛ حذف Preview تنظیمات خودش را پاک می‌کند. اطلاعات نسخه اصلی را برای این آزمایش پاک نکنید.

## امضا و محدودیت به‌روزرسانی

این APK با کلید موقت Debug محیط CI امضا شده و حالت اشکال‌زدایی آن فعال است. کلید آن با نسخه نصب‌شده روی مک یکسان نیست. اجرای بعدی CI ممکن است کلید دیگری بسازد؛ بنابراین نصب نسخه بعدی به‌صورت آپدیت تضمین نمی‌شود. خطای امضای ناسازگار با تکرار نصب حل نمی‌شود.

تأیید امضا و هش، تمامیت فایل را بررسی می‌کند؛ جای تست واقعی نصب، آپدیت و بازگشت را نمی‌گیرد. پس از آزمایش، اشکال‌زدایی را خاموش و مجوزهای ADB را لغو کنید.
