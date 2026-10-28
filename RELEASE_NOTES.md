## Arabic Clock 2.1.2 — Janna LT Bold (test release)

- Adds **Janna LT Bold** as a third option under **Arabic font**.
- Preserves Amiri, Scheherazade, existing font selections, colors, and width settings.
- Uses the same transparent bitmap rendering for preview and placed widgets,
  so launchers cannot substitute Western digits. Dots remain unstretched.
- Includes the original, unmodified Janna font with proprietary font notices,
  based on the repository owner's confirmed embedding/redistribution permission.
  Janna is not covered by the app MIT license or the other fonts' OFL licenses.

### Install
Download `Arabic-Clock-2.1.2.apk` (Android 8.0+), open the app, and choose
**Arabic font → Janna LT Bold**. Existing widget instances update automatically.
On Android 12+, grant **Allow precise clock updates** → Alarms & reminders.
Without that access Android can delay refreshes. Sleep/Doze and battery restrictions
may also defer updates; this app deliberately does not wake the phone every minute.

This is a debug-signed test APK. If a previous test installation has a different
signing key, uninstall it before installing (this removes settings and widgets).
A SHA-256 checksum is attached. Lock-screen widgets require a compatible device.

Automated checks cover Arabic glyph availability, unchanged Janna font bytes,
resource structure, numeral conversion, APK compilation and lint.
Physical-device/launcher validation is still required.
