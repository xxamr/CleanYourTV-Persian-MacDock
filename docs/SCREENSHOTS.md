# Real before/after images / تصاویر واقعی قبل و بعد

Use actual photographs or ADB captures of the same Mi Box. Do not use generated mockups, upstream showcase images, or an intermediate development screen as final release evidence.

## Capture on the computer already authorized by the TV

Open the original Home using TV settings or its existing Home chooser; keep it enabled. Capture from that computer's terminal:

```sh
adb exec-out screencap -p > home-before.png
```

Open the final custom Home, without changing or disabling other packages, then capture:

```sh
adb exec-out screencap -p > home-after.png
```

These redirection commands are for macOS/Linux; on Windows use Command Prompt rather than older PowerShell binary redirection. They assume exactly one authorized ADB device. If multiple devices are listed, use `adb -s <DEVICE_SERIAL> ...` locally; never publish the serial.

Before uploading publicly, inspect Wi-Fi names, account/profile names, notification text, private headlines and device identifiers. A privacy crop/redaction must be disclosed; do not reconstruct the UI with image generation. Prefer 16:9 full Home views with no settings panels. Record whether the final image is the owner's Mac build or the separately packaged CI preview; a photo of one is not installation evidence for the other.

Final reviewed files will go in `docs/screenshots/home-before.png` and `docs/screenshots/home-after.png`, displayed together in README with accurate captions. Neither final file has been supplied yet. The earlier owner photo with overlapping docks is intentionally not used.

## فارسی

یک تصویر از Home قبلی و یک تصویر از Home نهایی همان باکس بفرستید. از کامپیوتری که از قبل به ADB باکس وصل و مجاز شده می‌توان با دستورهای بالا تصویر گرفت. لازم نیست لانچر اصلی را حذف یا غیرفعال کنید. نام شبکه، حساب، اعلان و شناسه دستگاه نباید در تصویر عمومی دیده شود. مشخص کنید تصویر نهایی از نسخه مک است یا APK آزمایشی CI.
