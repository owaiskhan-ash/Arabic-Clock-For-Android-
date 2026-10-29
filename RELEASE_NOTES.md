## Arabic Clock 2.4.0 — 12/24-hour format (test release)

- New **Time format** setting in the settings screen: **24-hour · ١٤:٠٥** or **12-hour · ٢:٠٥ م**.
- 12-hour mode drops the leading zero (٢:٠٥, ١٢:٠٠) like the system clock, and appends an
  **AM/PM marker** you can choose as:
  - **Arabic · ص / م** (default),
  - **Latin · AM / PM**, or
  - **None** for a marker-free 12-hour clock.
- The marker is drawn at 40% of the numeral size, baseline-centered with the minutes, and
  included in the fit/centering maths, so HUGE, stacked, outline and opacity modes all
  keep the full time visible without clipping.
- The marker always uses the bundled Janna LT Bold face, because the Amiri/Scheherazade
  digit subsets contain no ص/م or Latin glyphs — drawing it with the numeral font would
  show blank boxes (tofu) on some devices. Numerals keep using your selected font.
- 12-/24-hour state is shared by the preview and every placed widget, including
  lock-screen hosts, and updates immediately.
- **24-hour remains the default**, so upgrading changes nothing until you switch.
- Widget accessibility descriptions now include the marker (e.g. ٢:٠٥ م).
- New unit tests cover midnight/noon, 11:59/23:59 boundaries, both marker styles,
  markers off, and Arabic-Indic minute padding in any time zone.

### Use
Install `Arabic-Clock-2.4.0.apk`, open the app, and choose **Time format** near the top of
the settings list. Existing settings are retained.

Android 8+ required. On Android 12+ allow Alarms & reminders for precise updates;
Doze/battery restrictions can still delay refreshes. Lock-screen widgets require host support.

This is a debug-signed test release. If Android refuses an update because the previous
build has a different signature, uninstall it first (this removes saved preferences and
placed widgets). A SHA-256 checksum is attached.

Build/lint, numeral and time-format unit tests, and static font/resource checks run in CI.
Visual validation on physical devices/launchers is still required.
