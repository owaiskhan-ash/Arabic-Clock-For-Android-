package com.arabicclock.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;

public class ClockWidgetProvider extends AppWidgetProvider {
    private static final String TICK = "com.arabicclock.app.MINUTE_TICK";
    static void updateAll(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        int[] ids = manager.getAppWidgetIds(new ComponentName(context, ClockWidgetProvider.class));
        for (int id : ids) update(context, manager, id);
        if (ids.length > 0) schedule(context);
        else cancel(context);
    }
    @Override public void onReceive(Context c, Intent intent) {
        super.onReceive(c, intent);
        String action = intent.getAction();
        if (TICK.equals(action) || Intent.ACTION_TIME_CHANGED.equals(action)
                || Intent.ACTION_TIMEZONE_CHANGED.equals(action) || Intent.ACTION_BOOT_COMPLETED.equals(action)
                || Intent.ACTION_MY_PACKAGE_REPLACED.equals(action) || Intent.ACTION_LOCALE_CHANGED.equals(action)
                || Intent.ACTION_USER_PRESENT.equals(action)
                || AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED.equals(action)) updateAll(c);
    }
    @Override public void onUpdate(Context c, AppWidgetManager manager, int[] ids) { updateAll(c); }
    @Override public void onAppWidgetOptionsChanged(Context c, AppWidgetManager m, int id, Bundle options) { updateAll(c); }
    @Override public void onDisabled(Context c) { cancel(c); }
    private static PendingIntent alarmIntent(Context c) {
        return PendingIntent.getBroadcast(c, 1, new Intent(c, ClockWidgetProvider.class).setAction(TICK),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }
    static boolean preciseUpdatesAllowed(Context c) {
        AlarmManager alarms = c.getSystemService(AlarmManager.class);
        return Build.VERSION.SDK_INT < 31 || alarms.canScheduleExactAlarms();
    }
    private static void schedule(Context c) {
        AlarmManager alarms = c.getSystemService(AlarmManager.class);
        long next = (System.currentTimeMillis() / 60000 + 1) * 60000;
        // Non-wakeup: don't wake a sleeping phone every minute. Refresh on unlock/resume.
        try {
            if (preciseUpdatesAllowed(c)) alarms.setExact(AlarmManager.RTC, next, alarmIntent(c));
            else alarms.set(AlarmManager.RTC, next, alarmIntent(c));
        } catch (SecurityException revokedDuringUpdate) {
            alarms.set(AlarmManager.RTC, next, alarmIntent(c));
        }
    }
    private static void cancel(Context c) { c.getSystemService(AlarmManager.class).cancel(alarmIntent(c)); }
    private static void update(Context c, AppWidgetManager manager, int id) {
        WidgetSettings settings = new WidgetSettings(c);
        RemoteViews views = new RemoteViews(c.getPackageName(), R.layout.widget_bitmap);
        Bundle options = manager.getAppWidgetOptions(id);
        int width = Math.max(1, options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 280));
        int height = Math.max(1, options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 140));
        long now = System.currentTimeMillis();
        views.setImageViewBitmap(R.id.clock_image, ClockRenderer.render(c, settings, width, height, now));
        views.setContentDescription(R.id.clock_image, ClockRenderer.time(settings, now));
        PendingIntent open = PendingIntent.getActivity(c, 0, new Intent(c, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_root, open);
        manager.updateAppWidget(id, views);
    }
}
