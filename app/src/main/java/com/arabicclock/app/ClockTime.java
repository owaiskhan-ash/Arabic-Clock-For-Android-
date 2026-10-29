package com.arabicclock.app;

import java.util.Calendar;
import java.util.Locale;

/**
 * Pure, Android-free local time formatting for the widget.
 *
 * <p>24-hour mode keeps the original zero-padded look (١٤:٠٥). 12-hour mode drops the
 * leading zero, like the system clock (٢:٠٥), and appends a marker that the user can
 * choose as Arabic (ص/م), Latin (AM/PM), or none.
 */
final class ClockTime {
    /** Marker styles stored in preferences. */
    static final int MARKER_ARABIC = 0;
    static final int MARKER_LATIN = 1;
    static final int MARKER_NONE = 2;

    static final String ARABIC_AM = "ص";
    static final String ARABIC_PM = "م";
    static final String LATIN_AM = "AM";
    static final String LATIN_PM = "PM";

    final String hours;
    final String minutes;
    /** Arabic-Indic or Latin AM/PM text, or null when 24-hour or marker style is none. */
    final String marker;

    private ClockTime(String hours, String minutes, String marker) {
        this.hours = hours;
        this.minutes = minutes;
        this.marker = marker;
    }

    static ClockTime of(long epochMillis, boolean hour24, int markerStyle) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(epochMillis);
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        String marker = null;
        String hours;
        if (hour24) {
            hours = String.format(Locale.US, "%02d", hourOfDay);
        } else {
            int hour = hourOfDay % 12;
            if (hour == 0) hour = 12;
            hours = String.valueOf(hour);
            boolean morning = hourOfDay < 12;
            if (markerStyle == MARKER_ARABIC) marker = morning ? ARABIC_AM : ARABIC_PM;
            else if (markerStyle == MARKER_LATIN) marker = morning ? LATIN_AM : LATIN_PM;
        }
        return new ClockTime(ArabicDigits.convert(hours),
                ArabicDigits.convert(String.format(Locale.US, "%02d", minute)), marker);
    }

    /** Rendered clock text without the AM/PM marker, e.g. ١٤:٠٥. */
    String text() {
        return hours + ":" + minutes;
    }

    /** Full text used for accessibility and widget content descriptions. */
    String description() {
        return marker == null ? text() : text() + " " + marker;
    }
}
