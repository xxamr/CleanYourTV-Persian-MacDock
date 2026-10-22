# Build from source

Use JDK 17, SDK platform 34, build tools 34.0.0, and the checked-in Gradle wrapper (8.9, checksum pinned). No SDK is bundled.

## Linux and macOS

Install JDK/SDK from their official vendors. On Catalina, use a JDK 17 distribution explicitly supporting 10.15 and compatible Android command-line tools; current vendor installers may require newer macOS. Catalina compatibility was not exercised here. Homebrew is not required.

```sh
export JAVA_HOME=/absolute/path/to/jdk17
export ANDROID_HOME=/absolute/path/to/android-sdk
export PATH="$JAVA_HOME/bin:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH"
sdkmanager 'platforms;android-34' 'build-tools;34.0.0' 'platform-tools'
cd launcher
chmod +x gradlew
./gradlew --no-daemon testDebugUnitTest assembleDebug
# Local installation only: app/build/outputs/apk/debug/app-debug.apk
./gradlew --no-daemon testReleaseUnitTest lintRelease assembleRelease
# Without private signing variables: app/build/outputs/apk/release/app-release-unsigned.apk
```

## Windows PowerShell

Install JDK 17 and Android command-line tools, then set real paths:

```powershell
$env:JAVA_HOME = 'C:\Java\jdk-17'
$env:ANDROID_HOME = 'C:\Android\sdk'
$env:Path = "$env:JAVA_HOME\bin;$env:ANDROID_HOME\cmdline-tools\latest\bin;$env:ANDROID_HOME\platform-tools;$env:Path"
sdkmanager.bat 'platforms;android-34' 'build-tools;34.0.0' 'platform-tools'
cd launcher
.\gradlew.bat --no-daemon testDebugUnitTest assembleDebug
.\gradlew.bat --no-daemon testReleaseUnitTest lintRelease assembleRelease
```

Read and accept SDK licenses yourself using `sdkmanager --licenses` if prompted. Keep local.properties out of Git. The legacy build-and-install scripts assume upstream release APK naming/signing and should not be used for this source preview; use the explicit debug build and ADB instructions above instead.

CI is build verification only, with read-only repository permissions and no binary uploads. Android checks require network access to Gradle, Google Maven and Maven Central. No successful local Android build is claimed; see [verification](VERIFICATION.md).
