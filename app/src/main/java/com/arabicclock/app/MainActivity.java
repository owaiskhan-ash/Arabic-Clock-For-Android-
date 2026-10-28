package com.arabicclock.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.util.Locale;

/** A single screen: live transparent-widget preview above scrollable settings. */
public class MainActivity extends Activity {
    private final android.os.Handler ticker = new android.os.Handler(android.os.Looper.getMainLooper());
    private TextView precisionStatus;
    private final Runnable previewTick = new Runnable() {
        public void run() {
            renderPreview();
            ticker.postDelayed(this, 60000 - System.currentTimeMillis() % 60000);
        }
    };
    @Override protected void onResume() {
        super.onResume();
        ticker.post(previewTick);
        ClockWidgetProvider.updateAll(this);
        if (precisionStatus != null) precisionStatus.setText(ClockWidgetProvider.preciseUpdatesAllowed(this)
            ? "Precise minute updates enabled. Sleeping/battery-restricted devices may defer updates."
            : "Allow precise updates below; otherwise Android may delay the clock by several minutes.");
    }
    @Override protected void onPause() { ticker.removeCallbacks(previewTick); super.onPause(); }
    private WidgetSettings settings;
    private FrameLayout preview;
    private TextView sizeLabel, colorLabel;
    private final int gold = Color.rgb(220, 193, 138);

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        settings = new WidgetSettings(this);
        LinearLayout root = column();
        root.setBackgroundColor(Color.rgb(16, 28, 32));
        root.setOnApplyWindowInsetsListener((v, insets) -> {
            v.setPadding(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(),
                    insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
            return insets;
        });
        setContentView(root);

        LinearLayout top = column();
        top.setGravity(Gravity.CENTER_HORIZONTAL);
        top.setPadding(dp(20), dp(16), dp(20), dp(8));
        root.addView(top, new LinearLayout.LayoutParams(-1, 0, 1));
        TextView title = label("الساعة العربية  ·  Arabic Clock", 22, gold);
        title.setGravity(Gravity.CENTER);
        top.addView(title);
        TextView caption = label("LIVE WIDGET PREVIEW", 11, Color.LTGRAY);
        caption.setGravity(Gravity.CENTER);
        top.addView(caption);
        preview = new FrameLayout(this);
        top.addView(preview, new LinearLayout.LayoutParams(-1, 0, 1));
        preview.addOnLayoutChangeListener((v,l,t,r,b,ol,ot,or,ob) -> {
            if (r-l != or-ol || b-t != ob-ot) renderPreview();
        });
        TextView transparency = label("100% transparent · checkerboard is preview only", 12, Color.LTGRAY);
        transparency.setGravity(Gravity.CENTER);
        top.addView(transparency);

        ScrollView scroll = new ScrollView(this);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        LinearLayout controls = column();
        controls.setPadding(dp(24), dp(8), dp(24), dp(24));
        scroll.addView(controls);
        controls.addView(label("Customize your widget", 20, gold));
        controls.addView(label("Changes apply to all placed widgets immediately.", 12, Color.LTGRAY));
        addChoices(controls, "Arabic font", new String[]{"Long · Amiri", "Classic · Scheherazade", "Janna LT Bold"}, settings.font(), "font");
        addChoices(controls, "Numeral width (dots stay unchanged)", new String[]{"Natural", "+15%", "+30%"}, settings.stretch(), "stretch");
        sizeLabel = label("Font size · " + settings.size() + " sp", 14, Color.WHITE);
        controls.addView(sizeLabel);
        SeekBar size = new SeekBar(this);
        size.setMax(36); size.setProgress(settings.size() - 28);
        size.setContentDescription("Widget font size");
        controls.addView(size, new LinearLayout.LayoutParams(-1, dp(48)));
        size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar bar) { }
            public void onStopTrackingTouch(SeekBar bar) { ClockWidgetProvider.updateAll(MainActivity.this); }
            public void onProgressChanged(SeekBar bar, int value, boolean user) {
                if (!user) return;
                settings.prefs.edit().putInt("size", value + 28).apply();
                sizeLabel.setText("Font size · " + (value + 28) + " sp");
                renderPreview();
            }
        });
        controls.addView(label("Automatically fits smaller widget spaces.", 12, Color.LTGRAY));
        colorLabel = label("", 14, Color.WHITE);
        controls.addView(colorLabel);
        LinearLayout colors = new LinearLayout(this);
        controls.addView(colors);
        String[] names = {"White", "Gold", "Black", "Custom"};
        int[] values = {Color.WHITE, gold, Color.BLACK};
        for (int i=0; i<names.length; i++) {
            final int index = i;
            Button button = new Button(this);
            button.setText(names[i]); button.setTextSize(11); button.setAllCaps(false);
            button.setPadding(0,0,0,0);
            colors.addView(button, new LinearLayout.LayoutParams(0, dp(48), 1));
            button.setOnClickListener(v -> {
                if (index == 3) customColor();
                else { settings.prefs.edit().putInt("color", values[index]).apply(); changed(); }
            });
        }
        Switch dots = new Switch(this);
        dots.setText("Show separator dots  :"); dots.setTextColor(Color.WHITE);
        dots.setMinHeight(dp(48)); dots.setChecked(settings.dots());
        controls.addView(dots);
        dots.setOnCheckedChangeListener((v, checked) -> {
            settings.prefs.edit().putBoolean("dots", checked).apply(); changed();
        });
        precisionStatus = label("", 13, Color.LTGRAY);
        controls.addView(precisionStatus);
        Button precision = new Button(this);
        precision.setText("Allow precise clock updates");
        controls.addView(precision);
        precision.setOnClickListener(v -> {
            if (android.os.Build.VERSION.SDK_INT >= 31 && !ClockWidgetProvider.preciseUpdatesAllowed(this)) {
                try {
                    startActivity(new android.content.Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                        android.net.Uri.parse("package:" + getPackageName())));
                } catch (android.content.ActivityNotFoundException e) {
                    Toast.makeText(this, "Open system settings → Apps → Special access → Alarms & reminders", Toast.LENGTH_LONG).show();
                }
            } else Toast.makeText(this, "Precise updates are already enabled", Toast.LENGTH_SHORT).show();
        });
        Button add = new Button(this);
        add.setText("Add home-screen widget");
        controls.addView(add);
        add.setOnClickListener(v -> {
            AppWidgetManager manager = AppWidgetManager.getInstance(this);
            if (manager.isRequestPinAppWidgetSupported()) {
                boolean requested = manager.requestPinAppWidget(new ComponentName(this, ClockWidgetProvider.class), null, null);
                if (!requested) widgetHelp();
            } else widgetHelp();
        });
        controls.addView(label("Lock screen: add through your device’s lock-screen widget picker, if available. Android and manufacturer support varies; this app cannot replace the system lock-screen clock.", 13, Color.LTGRAY));
        changed();
    }
    private void widgetHelp() {
        new AlertDialog.Builder(this).setTitle("Add a widget")
            .setMessage("Long-press an empty area on your home screen, choose Widgets, then Arabic Clock. Drag the widget onto your home screen. Resize it by long-pressing it.")
            .setPositiveButton("OK", null).show();
    }
    private void addChoices(LinearLayout parent, String title, String[] options, int selected, String key) {
        parent.addView(label(title, 14, Color.WHITE));
        Spinner spinner = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter); spinner.setSelection(selected);
        spinner.setContentDescription(title);
        parent.addView(spinner, new LinearLayout.LayoutParams(-1, dp(48)));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> p) { }
            public void onItemSelected(AdapterView<?> p, View v, int position, long id) {
                if (settings.prefs.getInt(key, key.equals("stretch") ? 1 : 0) == position) return;
                settings.prefs.edit().putInt(key, position).apply(); changed();
            }
        });
    }
    private void customColor() {
        EditText input = new EditText(this);
        input.setSingleLine(true); input.setHint("#RRGGBB");
        input.setText(String.format(Locale.US, "#%06X", settings.color() & 0xffffff));
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Widget text color")
            .setView(input).setNegativeButton("Cancel", null).setPositiveButton("Apply", null).create();
        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String value = input.getText().toString().trim();
            if (!value.matches("#[0-9a-fA-F]{6}")) { input.setError("Enter a color like #DCC18A"); return; }
            settings.prefs.edit().putInt("color", Color.parseColor(value)).apply();
            changed(); dialog.dismiss();
        }));
        dialog.show();
    }
    private void changed() {
        if (colorLabel != null) colorLabel.setText(String.format(Locale.US, "Text color · #%06X", settings.color() & 0xffffff));
        renderPreview();
        ClockWidgetProvider.updateAll(this);
    }
    private void renderPreview() {
        if (preview == null) return;
        preview.removeAllViews();
        View checkerboard = new View(this) {
            final Paint paint = new Paint();
            @Override protected void onDraw(Canvas c) {
                int cell = dp(16);
                for (int y=0; y<getHeight(); y+=cell) for (int x=0; x<getWidth(); x+=cell) {
                    paint.setColor(((x/cell+y/cell)%2==0) ? 0xff64777a : 0xff718488);
                    c.drawRect(x,y,x+cell,y+cell,paint);
                }
            }
        };
        preview.addView(checkerboard, new FrameLayout.LayoutParams(-1,-1));
        float density = getResources().getDisplayMetrics().density;
        int width = preview.getWidth() > 0 ? (int)(preview.getWidth()/density) : 280;
        int height = preview.getHeight() > 0 ? (int)(preview.getHeight()/density) : 180;
        ImageView face = new ImageView(this);
        face.setScaleType(ImageView.ScaleType.FIT_CENTER);
        long now = System.currentTimeMillis();
        face.setImageBitmap(ClockRenderer.render(this, settings, width, height, now));
        face.setContentDescription(ClockRenderer.time(now));
        preview.addView(face, new FrameLayout.LayoutParams(-1,-1));
    }
    private LinearLayout column() { LinearLayout v = new LinearLayout(this); v.setOrientation(LinearLayout.VERTICAL); return v; }
    private TextView label(String text, int size, int color) {
        TextView v = new TextView(this); v.setText(text); v.setTextSize(size); v.setTextColor(color);
        v.setPadding(0,dp(5),0,dp(5)); return v;
    }
    private int dp(int n) { return Math.round(n * getResources().getDisplayMetrics().density); }
}
