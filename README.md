# Arabic Clock for Android

An offline, native Android clock with a gold-on-dark analog face and live digital time.

- Arabic-Indic or Western numerals
- 12/24-hour digital time, local date and time zone
- Optional keep-screen-awake mode
- Preferences saved between launches
- No internet permission, ads, accounts, or third-party runtime dependencies
- Android 6.0 (API 23) or newer

## APK

The **Build Android APK** GitHub Actions workflow builds and lints the app on this working branch. Download the **Arabic-Clock-APK** artifact from a successful run and extract `app-debug.apk`. Transfer it to your Android phone, open it, and allow installation from that source when Android prompts you. This is a debug-signed test APK, not a Play Store release. Only install APKs from sources you trust.

## Local build

Install JDK 17, Android SDK (platform 35), and Gradle 8.10.2. Set `ANDROID_HOME` or configure `sdk.dir` in `local.properties`, then run:

```sh
gradle assembleDebug lintDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`

This app displays the device's local time; it does not provide alarms, background timers, or a home-screen widget.
