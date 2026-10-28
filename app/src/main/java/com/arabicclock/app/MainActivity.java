package com.arabicclock.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends Activity {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final int gold = Color.rgb(220, 193, 138);
    private final int muted = Color.rgb(149, 171, 173);
    private boolean arabic, twentyFour;
    private TextView time, date, zone;
    private ClockFace clock;
    private SharedPreferences prefs;
    private final Runnable tick = new Runnable() {
        @Override public void run() {
            refresh();
            handler.postDelayed(this, 1000 - System.currentTimeMillis() % 1000);
        }
    };

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        prefs = getSharedPreferences("clock", MODE_PRIVATE);
        arabic = prefs.getBoolean("arabic", true);
        twentyFour = prefs.getBoolean("24hour", false);
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(Color.rgb(16, 28, 32));
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(dp(24), dp(28), dp(24), dp(24));
        scroll.addView(root);
        // Respect system bars, including Android 15's enforced edge-to-edge layout.
        scroll.setOnApplyWindowInsetsListener((v, insets) -> {
            v.setPadding(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(),
                    insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
            return insets;
        });
        setContentView(scroll);
        root.addView(label("الساعة العربية", 30, gold));
        TextView subtitle = label("A R A B I C   C L O C K", 12, muted);
        subtitle.setPadding(0, dp(8), 0, dp(20));
        root.addView(subtitle);
        clock = new ClockFace(this);
        root.addView(clock, new LinearLayout.LayoutParams(-1, dp(310)));
        time = label("", 42, Color.WHITE);
        time.setTextDirection(View.TEXT_DIRECTION_LTR);
        time.setPadding(0, dp(20), 0, dp(8));
        root.addView(time);
        date = label("", 16, muted);
        root.addView(date);
        zone = label("", 12, gold);
        zone.setPadding(0, dp(12), 0, dp(28));
        root.addView(zone);
        addToggle(root, "Arabic numerals · ١٢٣", arabic, "arabic");
        addToggle(root, "24-hour time", twentyFour, "24hour");
        addToggle(root, "Keep screen awake", prefs.getBoolean("awake", false), "awake");
        applyAwake(prefs.getBoolean("awake", false));
        refresh();
    }

    private void addToggle(LinearLayout root, String title, boolean checked, String key) {
        Switch toggle = new Switch(this);
        toggle.setText(title);
        toggle.setTextSize(16);
        toggle.setTextColor(Color.WHITE);
        toggle.setPadding(dp(8), dp(12), dp(8), dp(12));
        toggle.setMinHeight(dp(56));
        toggle.setChecked(checked);
        root.addView(toggle, new LinearLayout.LayoutParams(-1, -2));
        toggle.setOnCheckedChangeListener((button, value) -> {
            prefs.edit().putBoolean(key, value).apply();
            if (key.equals("arabic")) arabic = value;
            if (key.equals("24hour")) twentyFour = value;
            if (key.equals("awake")) applyAwake(value);
            refresh();
        });
    }

    private void applyAwake(boolean awake) {
        if (awake) getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        else getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    private TextView label(String value, int size, int color) {
        TextView text = new TextView(this);
        text.setText(value);
        text.setTextSize(size);
        text.setTextColor(color);
        text.setGravity(Gravity.CENTER);
        return text;
    }
    private int dp(int n) { return Math.round(n * getResources().getDisplayMetrics().density); }
    private String digits(String text) {
        if (!arabic) return text;
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) result.append(c >= '0' && c <= '9' ? (char) ('٠' + c - '0') : c);
        return result.toString();
    }
    private void refresh() {
        Date now = new Date();
        time.setText(digits(new SimpleDateFormat(twentyFour ? "HH:mm:ss" : "hh:mm:ss a", Locale.US).format(now)));
        date.setText(digits(new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault()).format(now)));
        TimeZone tz = TimeZone.getDefault();
        zone.setText(tz.getID().replace('_', ' ') + " · " + tz.getDisplayName(tz.inDaylightTime(now), TimeZone.SHORT));
        clock.invalidate();
    }
    @Override protected void onResume() { super.onResume(); handler.post(tick); }
    @Override protected void onPause() { handler.removeCallbacks(tick); super.onPause(); }

    private class ClockFace extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ClockFace(Context context) { super(context); setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO); }
        @Override protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float cx = getWidth() / 2f, cy = getHeight() / 2f;
            float r = Math.min(cx, cy) - dp(8);
            canvas.save();
            canvas.translate(cx, cy);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.rgb(23, 40, 44));
            canvas.drawCircle(0, 0, r, paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(1));
            paint.setColor(gold);
            canvas.drawCircle(0, 0, r, paint);
            paint.setStyle(Paint.Style.FILL);
            for (int i = 0; i < 60; i++) {
                canvas.save(); canvas.rotate(i * 6);
                paint.setColor(i % 5 == 0 ? gold : muted);
                paint.setStrokeWidth(i % 5 == 0 ? dp(2) : dp(1));
                canvas.drawLine(0, -r + dp(9), 0, -r + dp(i % 5 == 0 ? 19 : 13), paint);
                canvas.restore();
            }
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(r * .16f);
            paint.setColor(gold);
            for (int i = 1; i <= 12; i++) {
                double angle = Math.toRadians(i * 30 - 90);
                float x = (float) Math.cos(angle) * r * .72f;
                float y = (float) Math.sin(angle) * r * .72f - (paint.ascent() + paint.descent()) / 2;
                canvas.drawText(digits(String.valueOf(i)), x, y, paint);
            }
            Calendar now = Calendar.getInstance();
            float seconds = now.get(Calendar.SECOND);
            float minutes = now.get(Calendar.MINUTE) + seconds / 60;
            float hours = now.get(Calendar.HOUR) + minutes / 60;
            hand(canvas, hours * 30, r * .44f, dp(5), gold);
            hand(canvas, minutes * 6, r * .61f, dp(3), Color.WHITE);
            hand(canvas, seconds * 6, r * .65f, dp(1), gold);
            paint.setColor(gold);
            canvas.drawCircle(0, 0, dp(5), paint);
            canvas.restore();
        }
        private void hand(Canvas c, float degrees, float length, float width, int color) {
            c.save(); c.rotate(degrees);
            paint.setColor(color); paint.setStrokeWidth(width); paint.setStrokeCap(Paint.Cap.ROUND);
            c.drawLine(0, dp(8), 0, -length, paint);
            c.restore();
        }
    }
}
