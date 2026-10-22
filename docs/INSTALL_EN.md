# Installation (English)

**This is a community launcher, not the stock TV firmware.** Android 8+ is the project's minimum API level; the custom version has been exercised on Xiaomi Mi Box 4 / Android TV 9, not every TV.

1. Download a **verified, consistently release-signed APK** from this repository's Releases. If no verified APK exists, [build from source](BUILD_FA.md).
2. Enable Developer options and ADB debugging in the TV settings. Connect your computer and TV over a reachable network, and accept the ADB authorization prompt on the TV.
3. Install with Android SDK Platform Tools:

```bash
adb connect <TV_IP>:5555
adb devices -l
adb install -r path/to/app-debug.apk
adb shell am start -n com.gathin.tvhome/.MainActivity
```

To set Home, prefer **TV Settings → Default Apps → Home**. On Android TV devices supporting it:

```bash
adb shell cmd package set-home-activity com.gathin.tvhome/.MainActivity
adb shell cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.HOME
```

Test the physical Home remote key. `Success` does not mean the OS actually switched. **Do not disable or uninstall the stock launcher** without a separately validated recovery procedure. Some Xiaomi firmware blocks third-party Home replacements.

**Rollback** (example for devices where Google TV package is `com.google.android.tvlauncher`):

```bash
adb shell pm enable --user 0 com.google.android.tvlauncher
adb shell cmd package set-home-activity <RECORDED_STOCK_HOME_COMPONENT>
adb shell input keyevent KEYCODE_HOME
```

For controls and news settings, see [USER_GUIDE_FA.md](USER_GUIDE_FA.md). For troubleshooting, see [TROUBLESHOOTING_FA.md](TROUBLESHOOTING_FA.md).

Record the actual stock HOME component before switching: `adb shell cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.HOME`. Google TV often uses `com.google.android.apps.tv.launcherx`; do not guess its activity class. Replace placeholders before running commands. After testing, turn off debugging, revoke authorizations, and reboot. Local debug keys differ between computers; they cannot necessarily update the existing Mac installation. No built-in settings export is verified; record preferences manually.
