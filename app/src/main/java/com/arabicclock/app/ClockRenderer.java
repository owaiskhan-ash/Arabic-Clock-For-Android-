package com.arabicclock.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** Rasterizes inside our unrestricted app context; launcher font loading is never used. */
final class ClockRenderer {
    static String time(long now) {
        return ArabicDigits.convert(new SimpleDateFormat("HH:mm", Locale.US).format(new Date(now)));
    }
    static Bitmap render(Context c, WidgetSettings settings, int widthDp, int heightDp, long now) {
        float density = c.getResources().getDisplayMetrics().density;
        // Preserve host aspect ratio while bounding bitmap memory to ~2.4 MB.
        float reduction = Math.min(1f, Math.min(1000f / Math.max(1, widthDp * density),
                600f / Math.max(1, heightDp * density)));
        density *= reduction;
        int width = Math.max(1, Math.round(widthDp * density));
        int height = Math.max(1, Math.round(heightDp * density));
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint digits = new Paint(Paint.ANTI_ALIAS_FLAG);
        digits.setTypeface(c.getResources().getFont(settings.fontResource()));
        digits.setTextSize(settings.size() * c.getResources().getDisplayMetrics().scaledDensity * reduction);
        digits.setTextScaleX(new float[]{1f, 1.15f, 1.3f, 1.5f, 1.75f}[settings.stretch()]);
        digits.setColor(settings.color());
        digits.setAlpha(Math.round(settings.opacity() * 2.55f));
        Paint dots = new Paint(digits);
        dots.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        dots.setTextScaleX(1f);
        String time = time(now);
        Run hours = new Run(time.substring(0, 2), digits);
        Run minutes = new Run(time.substring(3), digits);
        Run separator = new Run(":", dots);
        android.graphics.Rect numeralBounds = new android.graphics.Rect();
        String allDigits = "٠١٢٣٤٥٦٧٨٩";
        digits.getTextBounds(allDigits, 0, allDigits.length(), numeralBounds);
        // Use visible digit bounds, not oversized calligraphic font line metrics.
        float top = numeralBounds.top - 1;
        float bottom = numeralBounds.bottom + 1;
        boolean showDots = settings.dots() && !settings.stacked();
        if (showDots) {
            top = Math.min(top, separator.top);
            bottom = Math.max(bottom, separator.bottom);
        }
        float rowHeight = Math.max(1, bottom-top);
        float gap = settings.gap() * density;
        float contentWidth = settings.stacked() ? Math.max(hours.width, minutes.width)
            : hours.width + gap + (showDots ? separator.width : 0) + minutes.width;
        float contentHeight = settings.stacked() ? rowHeight * 2 + gap : rowHeight;
        float margin = settings.margin() * density;
        float fit = Math.min(Math.max(1, width - 2*margin) / Math.max(1, contentWidth),
                Math.max(1, height - 2*margin) / contentHeight);
        float scale = settings.fill() ? fit : Math.min(1, fit);
        canvas.translate((width-contentWidth*scale)/2, (height-contentHeight*scale)/2);
        canvas.scale(scale, scale);
        if (settings.stacked()) {
            hours.draw(canvas, (contentWidth-hours.width)/2, -top, digits);
            minutes.draw(canvas, (contentWidth-minutes.width)/2, rowHeight+gap-top, digits);
        } else {
            hours.draw(canvas, 0, -top, digits);
            if (showDots) separator.draw(canvas, hours.width+gap/2, -top, dots);
            minutes.draw(canvas, hours.width+gap+(showDots ? separator.width : 0), -top, digits);
        }
        return bitmap;
    }
    private static final class Run {
        final String text;
        final float left, width, top, bottom;
        Run(String text, Paint paint) {
            this.text = text;
            android.graphics.Rect bounds = new android.graphics.Rect();
            paint.getTextBounds(text, 0, text.length(), bounds);
            left = Math.min(0, bounds.left);
            width = Math.max(paint.measureText(text), bounds.right) - left;
            top = bounds.top; bottom = bounds.bottom;
        }
        void draw(Canvas canvas, float x, float baseline, Paint paint) {
            canvas.drawText(text, x-left, baseline, paint);
        }
    }
}
