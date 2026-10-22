# Experimental APK / نسخه آزمایشی آماده

This channel is explicitly authorized for testing, not a Stable Release. The separate package is `com.gathin.tvhome.preview`, labeled **CleanYourTV Preview**. It installs alongside `com.gathin.tvhome` and does not import its favorites/news preferences.

**Signing:** Android's temporary CI debug key, not a protected stable release key. Each run may have a different certificate; future previews or Stable may not install as an update. No keystore/password is published. APK is debuggable. Signature verification and SHA-256 prove build integrity, not safety or physical-TV compatibility.

## Install safely

Keep the existing launcher enabled. First record the current Home component:

```sh
adb shell cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.HOME
adb install -r CleanYourTV-experimental.apk
adb shell am start -n com.gathin.tvhome.preview/com.gathin.tvhome.MainActivity
```

Open Preview from the existing Home app first. To make it Home later, use the TV's default-app chooser; if supported:

```sh
adb shell cmd package set-home-activity com.gathin.tvhome.preview/com.gathin.tvhome.MainActivity
```

To return, select the prior Home in TV settings, or replace the placeholder with the exact component recorded earlier:

```sh
adb shell cmd package set-home-activity <RECORDED_HOME_COMPONENT>
adb shell input keyevent KEYCODE_HOME
```

Do not disable/uninstall the stock launcher. An incompatible signature is not fixed by repeated `adb install -r`; return to the prior Home before removing an old preview using TV Settings. Removing Preview erases its local settings. Never clear the original launcher's data to test this package. After testing, turn debugging off and revoke ADB authorization.

## فارسی

این APK با نام **CleanYourTV Preview** کنار لانچر قبلی نصب می‌شود و تنظیماتش جداست. اول فقط بازش کنید؛ لانچر اصلی را غیرفعال نکنید. امضا موقت و Debug است؛ نصب نسخه بعدی روی همین نسخه تضمین نشده و حذف نسخه آزمایشی تنظیمات خودش را پاک می‌کند. نصب/آپدیت/بازیابی روی TV واقعی هنوز تأیید نشده است. برای Stable کلید ثابت و تست واقعی لازم است.

Download only the APK attached to an **Experimental** Pre-release, verify its SHA-256 against SHA256SUMS.txt, and read BUILD-INFO.txt for exact source commit, application ID and certificate fingerprint. Existing source-preview releases contain no APK.
