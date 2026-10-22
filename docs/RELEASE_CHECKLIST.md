# Checklist before a public release

## Source & legal

- [ ] Confirm latest modified project source comes from the actual Mac working directory, **not** only upstream or old patch scripts.
- [ ] Preserve full upstream MIT `LICENSE` and Outfit SIL OFL `launcher/FONT-LICENSE-OFL.txt` and the accompanying font notices.
- [ ] Credit upstream CleanYourTV authors and distinguish community changes from upstream features.
- [ ] Avoid using protected trademarks, copyrighted app icons or third-party article content as new bundled assets without permission.
- [ ] Check third-party dependency licenses.

## Privacy & secrets

- [ ] Search all tracked files for secrets, signing keystores, tokens, cookies, passwords, exact developer addresses, IPs, VPN/proxy configs, local.properties, access logs and device dumps.
- [ ] Exclude Gradle cache, build outputs, local.properties, backups, `*.jks`, `*.keystore`, `.env`, personal screenshots and local logs.
- [ ] Verify RSS feeds are HTTPS, limited by size/timeouts and robust to XML parser hazards.
- [ ] Do not publish the user's Google, Instagram or network credentials.

## Functional QA

- [ ] Fresh clone builds with Java 17 and SDK 34.
- [ ] APK installs with clean settings; no hard-coded developer IP or Mac paths.
- [ ] Test Persian date and 12-hour clock.
- [ ] Dock original icons, gradual magnification, 3-second collapse and D-pad navigation.
- [ ] Change favorite apps and order from TV; verify persistence after process kill and reboot.
- [ ] Launchpad can open apps and Back returns to Dock.
- [ ] Screensaver manual and idle entry/exit work.
- [ ] News sources/topics enable/disable, RSS refresh, offline cache, and Browser opening all work.
- [ ] TV can restore original Home after failed switch.
- [ ] Memory/CPU acceptable on a low-RAM Android TV.

## Signing & releases

- [ ] **Do not ship a debug-signed APK as a stable public Release.** This fork removes upstream debug signing; Release defaults to unsigned.
- [ ] Configure a dedicated **stable release key** and keep the keystore and its passwords outside GitHub; if using GitHub Actions, use protected secrets / manual protected workflow.
- [ ] Archive signing key securely; losing it prevents compatible APK updates.
- [ ] Verify APK signature (`apksigner verify --print-certs`) and SHA-256 hash.
- [ ] Test **updating from previous public Release** without clearing app data.
- [ ] Create a release with changelog, installation notes, rollback and device compatibility disclaimer.
- [ ] Treat CI-produced unsigned output as **test artifact only**.
