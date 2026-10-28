## Arabic Clock 2.1.1 — Arabic numeral fix (test release)

Fixes home-screen widgets showing Western/English digits on launchers that ignore
bundled fonts in RemoteViews. Both preview and widget now render actual Arabic-Indic
Unicode numerals into a transparent bitmap inside the app. The launcher receives
pixels rather than text, preserving the selected Arabic font, color and width.
Separator dots remain unstretched; the background remains fully transparent.

### Install and enable minute updates
Download `Arabic-Clock-2.1.1.apk` below (Android 8.0+). Open the app and, on Android
12+, tap **Allow precise clock updates**, then grant **Alarms & reminders** access.
Without this optional permission, Android can delay updates by several minutes.
Sleeping devices, Doze, and manufacturer battery restrictions may defer updates;
this app deliberately does not wake the phone every minute. Opening settings
refreshes all placed widgets immediately.

This is a debug-signed test APK. If Android rejects updating a previous test build,
uninstall it first (saved settings and placed widgets will be removed), then install
and add the widget again. The SHA-256 checksum is attached.

Lock-screen widgets still require device/host support; they cannot replace the
system lock-screen clock. Build, lint, numeral-conversion unit tests and resource/font
checks are automated. Physical-device validation is still required.
