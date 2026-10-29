package com.arabicclock.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

final class WidgetSettings {
    final SharedPreferences prefs;
    WidgetSettings(Context c) { prefs = c.getSharedPreferences("widget", Context.MODE_PRIVATE); }
    int color() { return prefs.getInt("color", Color.WHITE); }
    int size() { return Math.max(24, Math.min(240, prefs.getInt("size", 72))); }
    int stretch() { return Math.max(0, Math.min(4, prefs.getInt("stretch", 1))); }
    int font() {
        int selected = prefs.getInt("font", 0);
        return selected >= 0 && selected <= 5 ? selected : 0;
    }
    int fontResource() {
        switch (font()) {
            case 1: return R.font.classic_arabic;
            case 2: return R.font.janna_lt_bold;
            case 3: return R.font.arslan_wessam;
            case 4: return R.font.khebrat_musamim;
            case 5: return R.font.avenir_heavy;
            default: return R.font.long_arabic;
        }
    }
    /** 0 = 24-hour (default, preserves existing installs), 1 = 12-hour. */
    int hourFormat() {
        int selected = prefs.getInt("hourFormat", 0);
        return selected == 1 ? 1 : 0;
    }
    boolean hour24() { return hourFormat() == 0; }
    /** ClockTime.MARKER_ARABIC (default), MARKER_LATIN, or MARKER_NONE. */
    int marker() {
        int selected = prefs.getInt("marker", ClockTime.MARKER_ARABIC);
        return selected >= ClockTime.MARKER_ARABIC && selected <= ClockTime.MARKER_NONE
                ? selected : ClockTime.MARKER_ARABIC;
    }
    boolean fill() { return prefs.getBoolean("fill", false); }
    boolean stacked() { return prefs.getBoolean("stacked", false); }
    int gap() { return Math.max(0, Math.min(40, prefs.getInt("gap", 6))); }
    int margin() { return Math.max(0, Math.min(32, prefs.getInt("margin", 8))); }
    int opacity() { return Math.max(0, Math.min(100, prefs.getInt("opacity", 100))); }
    boolean outline() { return prefs.getBoolean("outline", false); }
    boolean outlineOnly() { return prefs.getBoolean("outlineOnly", false); }
    int outlineWidth() { return Math.max(1, Math.min(8, prefs.getInt("outlineWidth", 2))); }
    int outlineColor() { return prefs.getInt("outlineColor", Color.BLACK); }
    boolean dots() { return prefs.getBoolean("dots", true); }
}
