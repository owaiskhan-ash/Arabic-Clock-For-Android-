package com.arabicclock.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

/** Rasterizes inside our unrestricted app context; launcher font loading is never used. */
final class ClockRenderer {
    /** AM/PM marker size relative to the numeral size; small enough to stay secondary. */
    private static final float MARKER_SCALE = 0.40f;
    static String time(WidgetSettings settings, long now) {
        return ClockTime.of(now, settings.hour24(), settings.marker()).description();
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
        // Apply opacity once to the composited fill + outline, not each overlapping pass.
        digits.setAlpha(255);
        float stroke = settings.outline() ? settings.outlineWidth() * density : 0;
        float extra = stroke / 2;
        Paint dots = new Paint(digits);
        dots.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        dots.setTextScaleX(1f);
        ClockTime time = ClockTime.of(now, settings.hour24(), settings.marker());
        // The AM/PM marker never uses the numeral font: the Amiri subsets contain only
        // digits, so a missing ص/م glyph would render as tofu on some devices.
        Paint marks = new Paint(digits);
        marks.setTypeface(c.getResources().getFont(R.font.janna_lt_bold));
        marks.setTextSize(digits.getTextSize() * MARKER_SCALE);
        marks.setTextScaleX(1f);
        Run hours = new Run(time.hours, digits, extra);
        Run minutes = new Run(time.minutes, digits, extra);
        Run separator = new Run(":", dots, extra);
        Run marker = time.marker == null ? null : new Run(time.marker, marks, extra);
        boolean showMarker = marker != null;
        android.graphics.Rect numeralBounds = new android.graphics.Rect();
        String allDigits = "٠١٢٣٤٥٦٧٨٩";
        digits.getTextBounds(allDigits, 0, allDigits.length(), numeralBounds);
        // Use visible digit bounds, not oversized calligraphic font line metrics.
        float top = numeralBounds.top - 1 - extra;
        float bottom = numeralBounds.bottom + 1 + extra;
        boolean showDots = settings.dots() && !settings.stacked();
        if (showDots) {
            top = Math.min(top, separator.top);
            bottom = Math.max(bottom, separator.bottom);
        }
        if (showMarker) {
            top = Math.min(top, marker.top);
            bottom = Math.max(bottom, marker.bottom);
        }
        float rowHeight = Math.max(1, bottom-top);
        float gap = settings.gap() * density;
        float markerGap = digits.getTextSize() * 0.16f;
        float markerWidth = showMarker ? markerGap + marker.width : 0;
        float minutesBlock = minutes.width + markerWidth;
        float contentWidth = settings.stacked() ? Math.max(hours.width, minutesBlock)
            : hours.width + gap + (showDots ? separator.width : 0) + minutesBlock;
        float contentHeight = settings.stacked() ? rowHeight * 2 + gap : rowHeight;
        float margin = settings.margin() * density;
        float fit = Math.min(Math.max(1, width - 2*margin) / Math.max(1, contentWidth),
                Math.max(1, height - 2*margin) / contentHeight);
        float scale = settings.fill() ? fit : Math.min(1, fit);
        int layer = canvas.saveLayerAlpha(0, 0, width, height, Math.round(settings.opacity() * 255f / 100f));
        canvas.translate((width-contentWidth*scale)/2, (height-contentHeight*scale)/2);
        canvas.scale(scale, scale);
        if (settings.stacked()) {
            float minutesX = (contentWidth-minutesBlock)/2;
            hours.draw(canvas, (contentWidth-hours.width)/2, -top, digits, settings, stroke);
            minutes.draw(canvas, minutesX, rowHeight+gap-top, digits, settings, stroke);
            if (showMarker) marker.draw(canvas, minutesX+minutes.width+markerGap,
                    rowHeight+gap-top + (rowHeight-(marker.bottom-marker.top))/2 - marker.top,
                    marks, settings, stroke * MARKER_SCALE);
        } else {
            float minutesX = hours.width+gap+(showDots ? separator.width : 0);
            hours.draw(canvas, 0, -top, digits, settings, stroke);
            if (showDots) separator.draw(canvas, hours.width+gap/2, -top, dots, settings, stroke);
            minutes.draw(canvas, minutesX, -top, digits, settings, stroke);
            if (showMarker) marker.draw(canvas, minutesX+minutes.width+markerGap,
                    -top + (rowHeight-(marker.bottom-marker.top))/2 - marker.top,
                    marks, settings, stroke * MARKER_SCALE);
        }
        canvas.restoreToCount(layer);
        return bitmap;
    }
    private static final class Run {
        final String text;
        final float left, width, top, bottom;
        Run(String text, Paint paint, float extra) {
            this.text = text;
            android.graphics.Rect bounds = new android.graphics.Rect();
            paint.getTextBounds(text, 0, text.length(), bounds);
            left = Math.min(0, bounds.left) - extra;
            width = Math.max(paint.measureText(text), bounds.right) + extra - left;
            top = bounds.top - extra; bottom = bounds.bottom + extra;
        }
        void draw(Canvas canvas, float x, float baseline, Paint paint, WidgetSettings settings, float stroke) {
            if (settings.outline()) {
                Paint edge = new Paint(paint);
                edge.setStyle(Paint.Style.STROKE);
                edge.setStrokeJoin(Paint.Join.ROUND);
                edge.setStrokeWidth(stroke);
                edge.setColor(settings.outlineColor());
                edge.setAlpha(255);
                canvas.drawText(text, x-left, baseline, edge);
            }
            if (!settings.outline() || !settings.outlineOnly()) {
                canvas.drawText(text, x-left, baseline, paint);
            }
        }
    }
}
