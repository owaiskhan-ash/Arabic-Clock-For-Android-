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
    int font() { return prefs.getInt("font", 0) == 0 ? 0 : 1; }
    boolean dots() { return prefs.getBoolean("dots", true); }
    int layout() {
        int[][] layouts = {
            {R.layout.widget_long_0, R.layout.widget_long_1, R.layout.widget_long_2},
            {R.layout.widget_classic_0, R.layout.widget_classic_1, R.layout.widget_classic_2}
        };
        return layouts[font()][stretch()];
    }
}
