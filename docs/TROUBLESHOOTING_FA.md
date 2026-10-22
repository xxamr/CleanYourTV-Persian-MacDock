# رفع مشکلات متداول

## ADB وصل نمی‌شود / unauthorized

- تلویزیون روشن و در همان شبکه قابل دسترس باشد؛ `adb connect <TV_IP>:5555`.
- درخواست Debugging روی TV را تأیید کنید.
- در `adb devices -l` وضعیت باید `device` باشد، نه `unauthorized` یا `offline`.
- برخی TVها پس از ری‌استارت Wireless debugging را غیرفعال می‌کنند.

## `set-home-activity` می‌گوید Success، اما Google TV باز می‌شود

این رفتار روی بعضی Firmwareهای Xiaomi/Google TV مستند شده است. صفحه انتخاب Home را در Settings بررسی کنید و با **دکمه فیزیکی Home** تست کنید؛ صرفاً به جواب Shell تکیه نکنید. **لانچر سیستمی را حذف نکنید.** پیش از هر غیرفعال‌سازی، راه بازگشت و دسترسی ADB را تأیید کنید.

## بازیابی لانچر کارخانه

مثال Google TV:

```bash
adb shell pm enable --user 0 com.google.android.tvlauncher
adb shell cmd package set-home-activity com.google.android.tvlauncher/.MainActivity
adb shell input keyevent KEYCODE_HOME
```

اگر اسم بسته کارخانه متفاوت است، از لیست `cmd package query-activities` همان دستگاه استفاده کنید.

## صفحه اصلی سیاه، Crash یا کندشدن

```bash
adb shell am start -n com.gathin.tvhome/.MainActivity
adb shell dumpsys meminfo com.gathin.tvhome
adb logcat -d -s AndroidRuntime:E
```

قبل از ثبت Issue، اطلاعات خصوصی را از Log پاک کنید. Build آخرین نسخه را با `./gradlew assembleRelease` بازتولید کنید. اگر Home بالا نمی‌آید ابتدا لانچر کارخانه را بازیابی کنید.

## تیترهای خبری نمایش داده نمی‌شوند

- تلکس از منوی بالا فعال باشد و دست‌کم یک **منبع** و یک **موضوع** انتخاب شده باشد.
- اینترنت روی خود TV قابل استفاده باشد، نه فقط روی کامپیوتر.
- RSSهای بعضی سایت‌ها ممکن است مسدود، کند یا تغییر یافته باشند؛ آخرین تیترهای ذخیره‌شده می‌توانند نمایش داده شوند.
- ساعت و منطقه زمانی TV درست باشند؛ اتصال HTTPS به ساعت صحیح نیاز دارد.
- برخی دسته‌ها از فیلتر کلیدواژه استفاده می‌کنند و ممکن است اخبار مرتبط را از دست بدهند.

## تاریخ اشتباه است

ابتدا تاریخ/منطقه زمانی خود Android TV را اصلاح کنید. تبدیل شمسی از تاریخ تقویم دستگاه انجام می‌شود. تغییر منطقه زمانی دستگاه با تنظیمات سایت خبری متفاوت است.

## YouTube/Browser یا سایر اپ‌ها باز نمی‌شوند

باید خود برنامه نصب و فعال باشد؛ Dock صرفاً Shortcut است. برخی APKهای موبایل از D-pad پشتیبانی کامل ندارند.

## فایل‌های راهنمای اصلی

برای خطرات وابسته به Firmware، مستندات پروژه مادر در `docs/TROUBLESHOOTING.md` را هم بخوانید: https://github.com/Gathin23/cleanyourtv/blob/main/docs/TROUBLESHOOTING.md
