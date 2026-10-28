## Arabic Clock 2.2.0 — More fonts & huge widgets (test release)

- Adds Arslan Wessam A, 18 Khebrat Musamim, and Avenir Heavy alongside Janna LT Bold, Amiri, and Scheherazade: six selectable fonts total.
- Font size range expanded to **24–240 sp**, with a larger 72 sp default for new installations. Existing preferences remain unchanged.
- **Fill available widget space · HUGE** scales the clock to the available area, overriding the size slider.
- **Stack hours above minutes** for large, tall widgets (separator dots hidden in this mode).
- Numeral width up to **+75%**; dots are never horizontally stretched.
- Adjustable spacing (0–40 dp), inner margins (0–32 dp), and text opacity (20–100%).
- Visible digit bounds replace oversized font line metrics to make better use of space with calligraphic fonts.
- Widget background stays completely transparent. Preview and widgets use the same bitmap renderer.

### Make your clock huge
Install `Arabic-Clock-2.2.0.apk` (Android 8+). Open the app, choose a font, and enable
**Fill available widget space · HUGE**. Long-press your home-screen widget and drag
its resize handles to enlarge it. For a tall clock enable **Stack hours above minutes**.
The app cannot enlarge the launcher grid allocation itself; available space is controlled by your launcher.

On Android 12+, grant **Allow precise clock updates** → Alarms & reminders.
Sleep, Doze and manufacturer restrictions may delay updates. Lock-screen widget
availability depends on the device.

This APK is debug-signed for testing. If installation conflicts with an earlier
build, uninstall that build first (settings and widgets will be removed).
A SHA-256 checksum is attached.

Uploaded fonts are bundled unmodified with their embedded notices, and are not
covered by the app MIT license. Reuse requires the applicable font permissions.
Build/lint, numeral unit tests and font/resource checks are automated. Physical-device
and launcher-specific validation is still required.
