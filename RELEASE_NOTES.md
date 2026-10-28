## Arabic Clock 2.3.0 — Font transparency & outlines (test release)

- Font opacity now supports **0–100%**, with explicit transparent/solid labels.
- New **Enable font outline** switch (off by default, preserving existing appearance).
- **Outline thickness:** 1–8 dp, scaled together with the clock.
- **Outline color:** black, white, or a custom `#RRGGBB` value, independent of fill color.
- **Outline only** for hollow numerals when the outline switch is enabled.
- Preview and all placed widgets use the same renderer. Opacity applies to the combined
  outline and fill without double-darkening overlapping passes. Layout reserves space
  for the stroke, including in HUGE and stacked modes.
- Background remains completely transparent. Separator dots remain unstretched.

### Use
Install `Arabic-Clock-2.3.0.apk`, open the app, and scroll through settings to
**Font opacity** and **Enable font outline**. At 0% the clock is invisible but the
widget area remains tappable so you can reopen settings. Existing settings are retained.

Android 8+ required. On Android 12+ allow Alarms & reminders for precise updates;
Doze/battery restrictions can still delay refreshes. Lock-screen widgets require host support.

This is a debug-signed test release. If Android refuses an update because the previous
build has a different signature, uninstall it first (this removes saved preferences and
placed widgets). A SHA-256 checksum is attached.

Build/lint, numeral unit tests, and static font/resource checks run in CI.
Visual validation on physical devices/launchers is still required.
