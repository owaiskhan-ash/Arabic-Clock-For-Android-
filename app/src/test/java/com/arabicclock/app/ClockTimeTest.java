package com.arabicclock.app;

import org.junit.Test;
import java.util.Calendar;
import static org.junit.Assert.*;

/** 12/24-hour formatting contract, independent of the device's time zone. */
public class ClockTimeTest {
    /** Local wall-clock time on a fixed day, so tests pass in any time zone. */
    private static long at(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2026, Calendar.OCTOBER, 29, hour, minute, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    @Test public void twentyFourHourKeepsZeroPaddingAndNoMarker() {
        ClockTime time = ClockTime.of(at(14, 5), true, ClockTime.MARKER_ARABIC);
        assertEquals("١٤:٠٥", time.text());
        assertEquals("١٤:٠٥", time.description());
        assertNull(time.marker);
        assertEquals("٠٠:٠٠", ClockTime.of(at(0, 0), true, ClockTime.MARKER_LATIN).text());
        assertEquals("٢٣:٥٩", ClockTime.of(at(23, 59), true, ClockTime.MARKER_NONE).text());
    }

    @Test public void twelveHourUsesNoLeadingZeroAndArabicMarkers() {
        assertEquals("٩:٠٥ ص", ClockTime.of(at(9, 5), false, ClockTime.MARKER_ARABIC).description());
        assertEquals("٢:٠٥ م", ClockTime.of(at(14, 5), false, ClockTime.MARKER_ARABIC).description());
        // Midnight is 12:00 AM and noon is 12:00 PM, never 0:00.
        assertEquals("١٢:٠٠ ص", ClockTime.of(at(0, 0), false, ClockTime.MARKER_ARABIC).description());
        assertEquals("١٢:٠٠ م", ClockTime.of(at(12, 0), false, ClockTime.MARKER_ARABIC).description());
        assertEquals("١١:٥٩ ص", ClockTime.of(at(11, 59), false, ClockTime.MARKER_ARABIC).description());
        assertEquals("١١:٥٩ م", ClockTime.of(at(23, 59), false, ClockTime.MARKER_ARABIC).description());
    }

    @Test public void twelveHourSupportsLatinMarkersAndMarkersOff() {
        assertEquals("٩:٠٥ AM", ClockTime.of(at(9, 5), false, ClockTime.MARKER_LATIN).description());
        assertEquals("٩:٠٥ PM", ClockTime.of(at(21, 5), false, ClockTime.MARKER_LATIN).description());
        ClockTime plain = ClockTime.of(at(21, 5), false, ClockTime.MARKER_NONE);
        assertEquals("٩:٠٥", plain.text());
        assertEquals("٩:٠٥", plain.description());
        assertNull(plain.marker);
    }

    @Test public void minutesAreAlwaysTwoArabicDigits() {
        for (int minute = 0; minute < 60; minute++) {
            ClockTime time = ClockTime.of(at(7, minute), false, ClockTime.MARKER_ARABIC);
            assertEquals(2, time.minutes.length());
            for (char digit : time.minutes.toCharArray()) {
                assertTrue(digit >= '٠' && digit <= '٩');
            }
        }
    }
}
