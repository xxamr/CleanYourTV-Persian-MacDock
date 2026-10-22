# نصب APK آماده با ویندوز

برای نصب APK آماده **نیازی به Java، Android Studio، Gradle یا ساخت سورس نیست**. این راهنما از PowerShell و ابزار رسمی ADB استفاده می‌کند؛ تغییر PATH یا نصب Homebrew لازم نیست.

## ۱. دانلود و آماده‌کردن پوشه

- [APK آزمایشی پروژه](https://github.com/xxamr/CleanYourTV-Persian-MacDock/releases/download/experimental-27e5fec-1-1/CleanYourTV-experimental.apk) را دانلود کنید.
- از [صفحه رسمی Android Platform Tools](https://developer.android.com/tools/releases/platform-tools)، گزینه `Download SDK Platform-Tools for Windows` را انتخاب کنید و پس از مطالعه و پذیرش شرایط، ZIP را دانلود کنید.

روی ZIP راست‌کلیک و `Extract All` را بزنید. فایل APK را داخل پوشه‌ای بگذارید که `adb.exe` در آن قرار دارد؛ این پوشه معمولاً `platform-tools` نام دارد. ابزار را از داخل ZIP اجرا نکنید. فایل‌های همراه مانند DLLها را کنار `adb.exe` نگه دارید.

## ۲. بازکردن PowerShell در پوشه درست

پوشه حاوی `adb.exe` را در File Explorer باز کنید. در نوار نشانی `powershell` بنویسید و Enter بزنید. سپس:

```powershell
.\adb.exe version
Get-Item .\CleanYourTV-experimental.apk
```

اگر نسخه ADB و مشخصات APK نمایش داده شد، مسیر درست است. پیشوند `.\` در PowerShell لازم است. اجرای PowerShell با دسترسی Administrator معمولاً برای این روش لازم نیست.

اگر هشدار Windows یا آنتی‌ویروس دیدید، منبع دانلود و اصالت ابزار را بررسی کنید؛ محافظت سیستم را برای نصب این لانچر خاموش نکنید. در اتصال شبکه معمولاً درایور USB لازم نیست؛ اتصال USB ممکن است درایور رسمی سازنده بخواهد.

## آماده‌کردن باکس

۱. باکس و کامپیوتر را به شبکه قابل دسترس وصل کنید؛ شبکه مهمان ممکن است ارتباط دستگاه‌ها را مسدود کند.
۲. در تنظیمات باکس، بخش «درباره دستگاه» یا `About` را باز و روی شماره ساخت یا `Build` چند بار، معمولاً هفت بار، بزنید تا گزینه‌های توسعه‌دهنده فعال شود.
۳. در `Developer options`، اشکال‌زدایی USB یا شبکه را در صورت پشتیبانی دستگاه روشن کنید. در Mi Box ممکن است نام گزینه USB debugging باشد؛ دسترسی روی شبکه به Firmware بستگی دارد.
۴. IP دستگاه را از تنظیمات شبکه یا بخش `Status` پیدا کنید. این IP را عمومی منتشر نکنید.
۵. صفحه تلویزیون و ریموت در دسترس باشد تا درخواست اعتماد به کامپیوتر را تأیید کنید. فقط روی کامپیوتر مورد اعتماد خودتان گزینه اعتماد همیشگی را انتخاب کنید.

اگر دستگاه فقط گزینه جفت‌سازی Wireless debugging با کد دارد، از IP و پورت‌هایی که همان صفحه نشان می‌دهد و روش جفت‌سازی انتهای این راهنما استفاده کنید؛ پورت ۵۵۵۵ را برای آن حدس نزنید. همه باکس‌ها این قابلیت را ندارند.

## ۳. اتصال به باکس

```powershell
$tvIp = Read-Host 'IP دستگاه را وارد کنید'
.\adb.exe connect "${tvIp}:5555"
.\adb.exe devices -l
```

درخواست اعتماد را روی تلویزیون تأیید و دستور آخر را تکرار کنید. وضعیت باید `device` باشد. تا زمانی که `unauthorized` یا `offline` است نصب نکنید. اگر چند دستگاه متصل‌اند، هدف را با `.\adb.exe -s "${tvIp}:5555" ...` مشخص کنید یا اتصال‌های اضافی را قطع کنید.

اگر فایروال درخواست دسترسی نشان داد، فقط برای شبکه خصوصی مورد اعتماد و ابزار رسمی تصمیم بگیرید؛ فایروال را کامل خاموش نکنید. شبکه عمومی یا مهمان برای این کار مناسب نیست.

## ۴. ثبت Home اصلی

```powershell
.\adb.exe shell cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.HOME
```

نام کامل مؤلفه خروجی را برای بازگشت در یادداشت محلی نگه دارید. لازم نیست آن را در GitHub منتشر کنید.

## ۵. نصب و اجرای فایل آماده

```powershell
.\adb.exe install -r .\CleanYourTV-experimental.apk
.\adb.exe shell am start -n com.gathin.tvhome.preview/com.gathin.tvhome.MainActivity
```

`Success` یعنی نصب انجام شده است. برنامه با نام **CleanYourTV Preview** در فهرست برنامه‌های باکس نیز قابل بازکردن است. این APK روی ویندوز اجرا نمی‌شود؛ مقصد نصب، باکس اندرویدی است. شناسه برنامه جداست و نصب آن جای لانچر قبلی را نمی‌گیرد.

## ۶. استفاده و انتخاب Home

ابتدا جهت‌ها، OK و Back، برنامه‌های دلخواه و تلکس را بررسی کنید. برای استفاده روزمره از [راهنمای ریموت](USER_GUIDE_FA.md) کمک بگیرید. سپس از تنظیمات برنامه‌های پیش‌فرض دستگاه، Home را انتخاب کنید. در Firmware پشتیبانی‌شده:

```powershell
.\adb.exe shell cmd package set-home-activity com.gathin.tvhome.preview/com.gathin.tvhome.MainActivity
```

دکمه Home واقعی ریموت را تست کنید. اگر دستگاه انتخاب را نمی‌پذیرد، برنامه کارخانه یا setup wizard را غیرفعال نکنید؛ از محدودیت Firmware عبور اجباری نکنید.

## ۷. بازگشت و حذف نسخه آزمایشی

روش اول انتخاب Home قبلی از تنظیمات است. روش ADB با مؤلفه ثبت‌شده مرحله ۴:

```powershell
$stockHomeComponent = Read-Host 'مؤلفه Home قبلی را وارد کنید'
.\adb.exe shell cmd package set-home-activity $stockHomeComponent
.\adb.exe shell input keyevent KEYCODE_HOME
```

پس از برگشت، در صورت تمایل Preview را از تنظیمات برنامه‌های تلویزیون حذف کنید؛ فقط داده‌های خودش پاک می‌شوند. پیش از بررسی خطای امضا، داده یا برنامه قبلی را حذف نکنید. آپدیت نسخه‌های آزمایشی به دلیل کلید موقت تضمین نشده است.

## ۸. قطع اتصال و پایان کار

```powershell
.\adb.exe disconnect
```

در تنظیمات باکس، debugging را نیز خاموش و مجوزهای اعتماد را لغو کنید؛ سپس دستگاه را راه‌اندازی مجدد کنید. قطع ADB به‌تنهایی پورت اشکال‌زدایی را نمی‌بندد.

## بررسی هش فایل در ویندوز

```powershell
Get-FileHash .\CleanYourTV-experimental.apk -Algorithm SHA256
```

هش را با `SHA256SUMS.txt` در [صفحه انتشار](https://github.com/xxamr/CleanYourTV-Persian-MacDock/releases/tag/experimental-27e5fec-1-1) مقایسه کنید؛ باید یکسان باشد.

## جفت‌سازی Wireless debugging در دستگاه پشتیبانی‌شده

```powershell
.\adb.exe pair <PAIRING_IP>:<PAIRING_PORT>
.\adb.exe connect <DEVICE_IP>:<CONNECT_PORT>
.\adb.exe devices -l
```

جای‌نگهدارها را قبل از اجرا با اطلاعات صفحه باکس جایگزین کنید. پورت اتصال و جفت‌سازی ممکن است متفاوت باشند. این گزینه روی همه باکس‌ها وجود ندارد.

[رفع خطاهای نصب](TROUBLESHOOTING_FA.md) · [محدودیت امضا و نسخه آزمایشی](EXPERIMENTAL_APK.md)
