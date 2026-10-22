# Stable release signing (not configured yet)

`assembleRelease` no longer falls back to a debug certificate. Without all four environment variables below it produces an unsigned APK, which cannot be installed directly. For local tests use `assembleDebug` and never label its APK Stable.

1. The maintainer creates a dedicated key using JDK `keytool -genkeypair`, with an absolute keystore path outside the checkout. Enter passwords interactively; do not put them in command history, docs or Git. Keep encrypted offline backups. No key has been generated in this publication task.
2. Provide `CTV_KEYSTORE` (absolute path), `CTV_STORE_PASSWORD`, `CTV_KEY_ALIAS`, `CTV_KEY_PASSWORD` through a private local environment or protected secret manager. Do not echo them. Run `./gradlew --no-daemon assembleRelease` in launcher/. A partially configured environment remains unsigned.
3. Verify `apksigner verify --verbose --print-certs app/build/outputs/apk/release/app-release.apk` using build tools 34.0.0 and publish its SHA-256 with the certificate fingerprint when eventually released.
4. Test a clean install, then an update from a prior candidate signed with the same key and a higher versionCode, with favorites/news settings preserved. Current package ID remains `com.gathin.tvhome`; a different existing certificate blocks updates. Never promise seamless migration from the Mac debug build.
5. Test restoring the recorded stock Home before testing failure recovery. APK downgrades may be refused; reverting to stock Home is separate from downgrading the APK. Record a supported data-migration/rollback procedure before Stable.

There is no release-publishing workflow. Adding one later requires protected manual approval and must never expose the keystore in logs/artifacts. The upstream versionCode 7/versionName 2.6 are inherited, not a declared stable fork version; choose an appropriate higher code before public binary release.
