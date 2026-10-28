# Arabic Clock — transparent Android widget

A single-screen widget customizer: **live preview in the top half**, **scrollable
settings in the bottom half**. The actual clock lives on your home screen,
not in a full-screen clock app.

## Features

- Completely transparent widget background: no panel, border, or background image.
- Arabic-Indic numerals (`٠١٢٣٤٥٦٧٨٩`), including on English-language phones.
- Long-style **Amiri** and classic **Scheherazade New** numeral fonts, bundled offline.
- Natural, 15%, or 30% wider numerals. Separator dots are a separate, unstretched view.
- White, gold, black, or any custom `#RRGGBB` text color; optional separator dots.
- Adjustable text size (28–64 sp), automatically reduced for smaller widget spaces.
- Settings saved locally and applied to **all** placed instances, including supported lock-screen hosts.
- Tap a placed widget to return to settings; resize via your launcher.
- Native Android `TextClock`: local 24-hour time, automatic minute/time-zone changes,
  without a background service, minute alarms, network access, or battery permissions.
- No ads, accounts, tracking, or internet permission.

The preview checkerboard illustrates transparency. **It is never part of the widget.**
The live preview uses the same layout and fitting logic as the widget; actual size
depends on the space your launcher gives it.

## Requirements and lock-screen support

**Android 8.0 (API 26) or newer.** Version 2 raises the minimum version to support
bundled fonts reliably in launcher-hosted views.

Home-screen widgets require a launcher that supports Android app widgets.

The widget declares both `home_screen` and `keyguard` categories. **This does not
unlock lock-screen widgets on devices that don't offer them.** Many Android versions
and manufacturers do not provide a third-party lock-screen widget picker. Where a
compatible picker exists, add Arabic Clock there; it uses the same global settings.
The app cannot replace or style the built-in system lock-screen clock. It does not
use lock-screen overlays or accessibility-service workarounds. Launcher-provided
editing outlines, selection highlights, or host backgrounds are outside app control.

## Install the APK

1. Open [GitHub Actions](https://github.com/owaiskhan-ash/Arabic-Clock-For-Android-/actions/workflows/build-apk.yml).
2. Choose the latest successful **Build Android APK** run for the working branch.
3. Download **Arabic-Clock-APK** under Artifacts (GitHub sign-in may be required).
4. Extract the ZIP and open `app-debug.apk` on your Android device.
5. Allow installation from that source when prompted; only install APKs you trust.

This is a debug-signed test APK, not a Play Store release. CI debug signing keys
can differ between builds. If Android refuses an update due to a signature mismatch,
uninstall the earlier test version before installing the new APK (this removes
saved preferences and placed widgets). Production distribution needs a stable,
privately stored release-signing key.

## Add and customize

1. Open Arabic Clock and choose a font, numeral width, size, color, and separator style.
2. Tap **Add home-screen widget**, then confirm in your launcher.
3. If pinning isn't supported, long-press an empty home-screen area → Widgets → Arabic Clock.
4. Long-press the widget to resize. Tap it to reopen settings.
5. For supported lock screens, use the **device's** lock-screen widget picker separately.

There are no alarms, stopwatches, weather, date widgets, seconds, or per-widget
profiles. Time is always local, in 24-hour format, with Arabic-Indic digits.

## Build and checks

Install JDK 17, Android SDK platform 35, and Gradle 8.10.2. Set `ANDROID_HOME`
or `sdk.dir` in `local.properties`:

```sh
gradle --no-daemon assembleDebug lintDebug
# Optional source/font regression checks (Python + fonttools):
python -m unittest discover -s tests -v
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`.
The workflow builds, lints, checks the widget resources/fonts, and uploads the APK.

### Device validation checklist

Build/lint and static font checks are not a substitute for device testing:

- Add two home-screen instances; change every setting and verify both update.
- Close the activity; verify the widget advances across minute/hour/midnight boundaries.
- Change device time zone and language; check local time and Arabic-Indic digits.
- Resize, rotate, increase system font size, reboot, and verify no clipping/stale time.
- Test on light and dark wallpapers; confirm no widget background or stretched dots.
- Test launcher pinning and manual addition, including cancellation.
- Where supported, add a lock-screen instance and check settings/time updates.

## License

Application code: [MIT](LICENSE). Bundled modified fonts: **SIL Open Font License 1.1**.
See [font attribution and reproduction instructions](licenses/README.md).
Full font license texts are included in the repository and APK assets.
