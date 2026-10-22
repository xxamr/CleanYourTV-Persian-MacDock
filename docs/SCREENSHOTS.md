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

## Published image provenance

- `screenshots/stock-home-reference.webp`: owner-supplied stock Android TV Home reference, original filename Xiaomi-Mi-Box-S-4K-Android-12.webp. It shows a different television from the custom photo. Device/Android version is not independently verified from the filename. Do not describe it as the exact same Mi Box before installation.
- `screenshots/custom-home-owner.jpg`: owner-supplied photograph IMG_9989.jpg of the custom Home, with a single floating Dock, Persian date and RSS ticker. The exact installed source commit/signing certificate cannot be established from a photograph; it is not a fresh install test of the CI preview.

Images are published as supplied without UI edits or generative reconstruction. No account names, network addresses or device identifiers are visible. The earlier development photo with overlapping docks and device-settings photos are not published. The owner supplied these images for this GitHub comparison; no broader license for third-party imagery is asserted.

## فارسی

تصاویر ارسالی مالک در README منتشر شده‌اند. تصویر قبل، نمونه مرجع Android TV و تصویر بعد، عکس Home سفارشی است؛ این دو، تست کنترل‌شده یک تلویزیون و Firmware نیستند. از کامپیوتری که از قبل به ADB باکس وصل و مجاز شده می‌توان با دستورهای بالا تصویر گرفت. لازم نیست لانچر اصلی را حذف یا غیرفعال کنید. نام شبکه، حساب، اعلان و شناسه دستگاه نباید در تصویر عمومی دیده شود. مشخص کنید تصویر نهایی از نسخه مک است یا APK آزمایشی CI.
