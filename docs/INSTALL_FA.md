# نصب، اتصال ADB و بازیابی Home

**راهنمای قدم‌به‌قدم:** [نصب با مک](INSTALL_MAC.md) · [نصب با ویندوز](INSTALL_WINDOWS.md)

برای **APK آماده فعلی** از [راهنمای نسخه آزمایشی](EXPERIMENTAL_APK.md) استفاده کنید؛ شناسه و دستور اجرای آن با نسخه معمولی فرق دارد. این صفحه مبانی اتصال و نصب از سورس را توضیح می‌دهد.

## ۱. آماده‌کردن دستگاه

در تنظیمات Android TV به بخش «درباره دستگاه» بروید و چند بار روی شماره ساخت بزنید تا گزینه‌های توسعه‌دهنده فعال شوند. سپس اشکال‌زدایی USB یا شبکه را، در صورت پشتیبانی دستگاه، روشن کنید. نام منوها بسته به سازنده فرق دارد.

روی کامپیوتر Android Platform Tools را از منبع رسمی نصب کنید. باکس و کامپیوتر باید در شبکه قابل دسترس باشند. درخواست اعتماد به کامپیوتر را روی تلویزیون تأیید کنید.

```sh
adb connect <TV_IP>:5555
adb devices -l
```

`<TV_IP>` را با IP باکس خودتان جایگزین کنید؛ آن را عیناً وارد نکنید. وضعیت دستگاه باید `device` باشد. بعضی دستگاه‌ها اتصال شبکه را ارائه نمی‌کنند یا روش جفت‌سازی دیگری دارند؛ در این حالت از روش سازنده استفاده کنید.

## ۲. ثبت Home فعلی

```sh
adb shell cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.HOME
adb shell cmd package query-activities --brief -a android.intent.action.MAIN -c android.intent.category.HOME
```

نام بسته و مؤلفه Home اصلی را نگه دارید. Google TV معمولاً از `com.google.android.apps.tv.launcherx` و برخی Android TVها از `com.google.android.tvlauncher` استفاده می‌کنند؛ نام Activity را از خروجی واقعی دستگاه بگیرید.

## ۳. نصب نسخه معمولی ساخته‌شده از سورس

دستورهای زیر مخصوص شناسه معمولی `com.gathin.tvhome` هستند. مسیر APK را با فایل واقعی خودتان جایگزین کنید:

```sh
adb install -r path/to/app-debug.apk
adb shell am start -n com.gathin.tvhome/.MainActivity
```

برای فایل آماده Preview این دستورها را به کار نبرید؛ [راهنمای درست آن](EXPERIMENTAL_APK.md) را بخوانید.

## ۴. تغییر Home و بازگشت

ابتدا از تنظیمات برنامه‌های پیش‌فرض، Home را انتخاب کنید. دستور زیر برای نسخه معمولی است:

```sh
adb shell cmd package set-home-activity com.gathin.tvhome/.MainActivity
```

با Home فیزیکی ریموت تست کنید. برای بازگشت، Home قبلی را در تنظیمات انتخاب کنید یا مؤلفه ثبت‌شده را در دستور زیر قرار دهید:

```sh
adb shell cmd package set-home-activity <RECORDED_HOME_COMPONENT>
adb shell input keyevent KEYCODE_HOME
```

لانچر کارخانه را حذف یا صرفاً برای نصب این برنامه غیرفعال نکنید. اگر قبلاً غیرفعال شده، با نام بسته واقعی دستگاه و دستور `adb shell pm enable --user 0 <STOCK_HOME_PACKAGE>` آن را فعال کنید؛ جای‌نگهدارها باید پیش از اجرا جایگزین شوند.

## نکات مهم

- Android 8 به بالا حداقل تعیین‌شده در سورس است؛ عملکرد همه مدل‌ها تأیید نشده است.
- خطای `INSTALL_FAILED_UPDATE_INCOMPATIBLE` معمولاً یعنی امضای فایل جدید و نسخه موجود متفاوت است. بدون بررسی و ثبت تنظیمات، برنامه قبلی را پاک نکنید. قابلیت خروجی‌گرفتن از تنظیمات این نسخه تأیید نشده است.
- دسترسی Location، تنظیمات امن یا سرویس دسترس‌پذیری برای نصب پایه لازم نیست.
- پس از پایان کار، اشکال‌زدایی را خاموش، اعتمادهای ADB را لغو و دستگاه را راه‌اندازی مجدد کنید.
