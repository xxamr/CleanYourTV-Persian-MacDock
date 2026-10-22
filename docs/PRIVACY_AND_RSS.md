# Privacy, network and RSS disclosures / حریم خصوصی

## مجوز اینترنت

تلکس خبری برای خواندن فیدهای RSS از اینترنت به `android.permission.INTERNET` نیاز دارد. این مجوز به خودی خود به حساب‌های کاربر، پیام‌های خصوصی، دوربین یا میکروفون دسترسی نمی‌دهد. دیگر دسترسی‌های موجود در CleanYourTV را در `launcher/app/src/main/AndroidManifest.xml` و مستندات بالادستی بررسی کنید.

## منابع خبری و لینک مقاله

فهرست اولیه کد تلکس (ممکن است با نسخه نهایی تغییر کند):

- BBC Persian — https://feeds.bbci.co.uk/persian/rss.xml
- DW Persian — https://rss.dw.com/syndication/feeds/dwworld.per.xml
- ISNA — https://www.isna.ir/rss
- Khabar Online — https://www.khabaronline.ir/rss
- Zoomit — https://www.zoomit.ir/feed/
- Khodrobank — https://www.khodrobank.com/RSS/RSS.ashx

لینک‌های RSS شخص ثالث ممکن است تغییر کنند یا بدون اطلاع از دسترس خارج شوند. نام منابع برای انتساب است، نه تأیید/همکاری آن‌ها با این پروژه. این برنامه تیترهای فیدها را نمایش می‌دهد و با OK به وب‌سایت اصلی منبع می‌رود؛ مقاله‌های کامل در مخزن ذخیره نمی‌شوند.

## اطلاعات ذخیره‌شده روی دستگاه

انتخاب‌های موضوع و منبع، وضعیت روشن/خاموش تلکس و کش محدود تیترها، به‌صورت محلی با Android SharedPreferences ذخیره می‌شوند. تنظیمات Favorites نیز روی خود دستگاه نگهداری می‌شوند.

با استفاده از RSS، درخواست HTTPS از IP شبکه TV به سرور منبع خبری ارسال می‌شود. با بازکردن خبر در Browser، مقررات حریم خصوصی وب‌سایت منبع اعمال می‌شود. پروژه نباید شامل کلیدهای شخصی، کوکی‌های Instagram یا URLهای خصوصی باشد.

## محدودیت صحت فیلتر موضوعات

دسته‌بندی اولیه به فید یا واژه‌های تیتر متکی است، نه مدل محتوایی تأییدشده. به‌خصوص موضوع اقتصاد با کلیدواژه فیلتر می‌شود؛ ممکن است بعضی تیترها غلط دسته‌بندی یا حذف شوند. «بروزرسانی هر ۵ دقیقه» فقط زمان بررسی فید است، نه وعده تحویل خبر تازه.

## امنیت

- فقط لینک‌های HTTPS را باز کنید و تغییر مسیر HTTPS را بررسی کنید.
- برای منابع ناشناس حد اندازه پاسخ، timeout و جلوگیری از XML entity expansion را حفظ کنید.
- رمز، token، SSH key، keystore امضای APK و IP/آدرس خصوصی را به Git اضافه نکنید.
- **این پروژه ابزار شنود محیط یا ضبط مخفیانه صدا نیست.**

## Verified implementation details

RSS follows no HTTP redirects (including HTTPS redirects); a redirecting feed may therefore show no new headlines. Each feed has a 512 KiB limit, 5.5 s connect timeout and 6.5 s read timeout, at most nine items; the merged cache holds at most 24. Document type declarations are rejected. HTTPS uses platform certificate validation. There is no whole-refresh wall-clock deadline; disabling pauses future refresh scheduling, but an in-flight request can finish. Browser redirects after opening an article are controlled by the browser.

The ticker is enabled by default and defaults to all six sources/topics; disable it from News settings before future polling if unwanted. Cached titles have no expiry and can be stale offline. No publisher availability was verified here.

Inherited permissions include network/Wi-Fi state, fine location (for Wi-Fi name), WRITE_SECURE_SETTINGS (optional remote helper repair) and a vendor TV-source permission. Accessibility services can handle remote keys if explicitly enabled. Basic installation does not require granting location, secure-settings or accessibility access. No RECORD_AUDIO or CAMERA permission is present. Favorites include installed application package names and are local; Android backup is disabled in the manifest. Optional add-ons are separate applications with separate privacy behavior.
