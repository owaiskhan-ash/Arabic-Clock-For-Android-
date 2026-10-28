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
        // Bound memory even on unusually large widget hosts.
        int width = Math.max(1, Math.min(1000, Math.round(widthDp * density)));
        int height = Math.max(1, Math.min(600, Math.round(heightDp * density)));
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint digits = new Paint(Paint.ANTI_ALIAS_FLAG);
        digits.setTypeface(c.getResources().getFont(settings.font() == 0 ? R.font.long_arabic : R.font.classic_arabic));
        digits.setTextSize(settings.size() * c.getResources().getDisplayMetrics().scaledDensity);
        digits.setTextScaleX(new float[]{1f, 1.15f, 1.3f}[settings.stretch()]);
        digits.setColor(settings.color());
        Paint dots = new Paint(digits);
        dots.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        dots.setTextScaleX(1f);
        String time = time(now);
        String hours = time.substring(0, 2), minutes = time.substring(3);
        float gap = settings.dots() ? dots.measureText(":") + 6 * density : 6 * density;
        float total = digits.measureText(hours) + gap + digits.measureText(minutes);
        float top = Math.min(digits.ascent(), dots.ascent());
        float bottom = Math.max(digits.descent(), dots.descent());
        float scale = Math.min(1, Math.min(Math.max(1, width - 16 * density) / total,
                Math.max(1, height - 16 * density) / (bottom - top)));
        canvas.translate((width - total * scale) / 2, (height - (bottom-top)*scale) / 2);
        canvas.scale(scale, scale);
        float baseline = -top;
        canvas.drawText(hours, 0, baseline, digits);
        float x = digits.measureText(hours);
        if (settings.dots()) canvas.drawText(":", x + 3*density, baseline, dots);
        canvas.drawText(minutes, x + gap, baseline, digits);
        return bitmap;
    }
}
