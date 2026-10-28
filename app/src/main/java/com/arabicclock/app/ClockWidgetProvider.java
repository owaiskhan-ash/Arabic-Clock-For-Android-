package com.arabicclock.app;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;

/** Native TextClock views tick inside the launcher; no polling, service, or alarms. */
public class ClockWidgetProvider extends AppWidgetProvider {
    static void updateAll(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        int[] ids = manager.getAppWidgetIds(new ComponentName(context, ClockWidgetProvider.class));
        for (int id : ids) update(context, manager, id);
    }
    @Override public void onUpdate(Context context, AppWidgetManager manager, int[] ids) {
        for (int id : ids) update(context, manager, id);
    }
    @Override public void onAppWidgetOptionsChanged(Context c, AppWidgetManager m, int id, Bundle options) {
        update(c, m, id);
    }
    static float fittingSize(Context c, WidgetSettings settings, int widthDp, int heightDp) {
        // Conservative bounds prevent clipping at smaller launcher sizes / large system fonts.
        float fontScale = c.getResources().getConfiguration().fontScale;
        float widthBound = Math.max(1, widthDp - 16) / ((settings.dots() ? 3.8f : 3.5f) * fontScale);
        float heightBound = Math.max(1, heightDp - 16) / (2.3f * fontScale);
        return Math.max(8, Math.min(settings.size(), Math.min(widthBound, heightBound)));
    }
    private static void update(Context c, AppWidgetManager manager, int id) {
        WidgetSettings settings = new WidgetSettings(c);
        RemoteViews views = new RemoteViews(c.getPackageName(), settings.layout());
        Bundle options = manager.getAppWidgetOptions(id);
        int width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 280);
        int height = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 140);
        float size = fittingSize(c, settings, width, height);
        for (int viewId : new int[]{R.id.hours, R.id.minutes, R.id.separator}) {
            views.setTextColor(viewId, settings.color());
            views.setTextViewTextSize(viewId, TypedValue.COMPLEX_UNIT_SP, size);
        }
        views.setViewVisibility(R.id.separator, settings.dots() ? View.VISIBLE : View.GONE);
        Intent intent = new Intent(c, MainActivity.class);
        PendingIntent open = PendingIntent.getActivity(c, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_root, open);
        manager.updateAppWidget(id, views);
    }
}
