# Publication verification — 2026-10-23

Status: source preview, no public APK, no Stable Release.

- Input: owner-supplied final source ZIP. No APK or original .git history was included; APK/source identity cannot be established.
- Static inspection: custom FloatingDock, DockEditorScreen, LaunchpadScreen, NewsTicker, Shamsi and ClockScene are present and integrated from MainActivity. Dock width 60%, idle collapse 3000 ms, refresh 300000 ms. This establishes implementation, not device performance.
- Java 17.0.20 is available. Local command `./gradlew --no-daemon testReleaseUnitTest lintRelease assembleRelease --console=plain` stopped downloading Gradle 8.9 with `Network is unreachable`. SDK API 34/build tools 34.0.0 were not available locally. No successful Android build, JVM suite or lint run is claimed locally.
- CI requests Java 17, SDK 34/build tools 34.0.0, unit tests, lint and unsigned Release assembly. Consult the Actions run for this commit for the actual result.
- Publication changes: unsigned Release default with optional environment-only signing; no binary workflow; fail-closed RSS redirects and DTD rejection; remove two local backup files; correct installation, signing, locale, permissions and verification statements.
- Secret screening: source filenames, text patterns for credentials/cookies/private keys, personal paths, IPs and local outputs reviewed. Apparent key/IP/path matches in inherited tests/scripts are fixtures/placeholders. This is a practical pre-publication screen, not an exhaustive security audit or guarantee.
- Original MIT and font OFL files match upstream. No new launcher dependency. Current-tree source is from the attachment; inherited upstream history is retained as a public fork.
- Physical TV, firmware Home selection, install/update/rollback, clock locale, RSS publisher availability and remote performance remain unverified for this published commit. Owner reports prior Mi Box 4 use only.

Before Stable: create/back up a dedicated signing key, verify build/certificate/hash, test clean install and same-key updates, stock Home recovery and data behavior on a physical TV. Record actual device/firmware and real screenshots with private fields removed.

## Inherited Python suite

Initial run: 429 tests, 13 failures, 5 errors, 3 skips. Most failures assume the upstream marketing README and workflows/size gate, which this fork intentionally replaces; they are not a passing fork CI claim. A genuine missing Hebrew-layout Clock/News button regression was found and fixed to prevent null lookups. The inherited full Python suite has not been adapted wholesale to the fork's documentation. Android CI is the publication build gate.

CI first attempt failed before compilation because setup-android requested the removed SDK package `tools`. The workflow now supplies explicit platform/build-tools packages and pins Ubuntu 24.04; it uses current checkout/setup-java major versions. Hebrew view-ID parity and Markdown links passed after fixes (6 checks total).

## Successful hosted Android verification

[GitHub Actions run 39852363967](https://github.com/xxamr/CleanYourTV-Persian-MacDock/actions/runs/39852363967) completed successfully on code commit eecfe04386bc9b1d9d74f164647098579f7b58c0: Java 17, API 34/build tools 34.0.0, testReleaseUnitTest, lintRelease and assembleRelease. No APK artifact was uploaded. This supersedes the local network blocker for build verification, but does not establish TV installation/update/rollback. Subsequent documentation-only commits do not change the verified launcher or build configuration.
