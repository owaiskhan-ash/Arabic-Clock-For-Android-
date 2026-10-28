package com.arabicclock.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

final class WidgetSettings {
    final SharedPreferences prefs;
    WidgetSettings(Context c) { prefs = c.getSharedPreferences("widget", Context.MODE_PRIVATE); }
    int color() { return prefs.getInt("color", Color.WHITE); }
    int size() { return Math.max(28, Math.min(64, prefs.getInt("size", 48))); }
    int stretch() { return Math.max(0, Math.min(2, prefs.getInt("stretch", 1))); }
    int font() {
        int selected = prefs.getInt("font", 0);
        return selected >= 0 && selected <= 2 ? selected : 0;
    }
    int fontResource() {
        switch (font()) {
            case 1: return R.font.classic_arabic;
            case 2: return R.font.janna_lt_bold;
            default: return R.font.long_arabic;
        }
    }
    boolean dots() { return prefs.getBoolean("dots", true); }
}
