package com.arabicclock.app;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Locale;

public class ArabicDigitsTest {
    @Test public void allDigitsUseActualArabicUnicode() {
        assertEquals("٠١٢٣٤٥٦٧٨٩", ArabicDigits.convert("0123456789"));
        assertEquals("٠٠:٠٠", ArabicDigits.convert("00:00"));
        assertEquals("٢٣:٥٩", ArabicDigits.convert("23:59"));
    }
    @Test public void allMinutesHaveNoWesternDigits() {
        for (int h = 0; h < 24; h++) for (int m = 0; m < 60; m++) {
            String result = ArabicDigits.convert(String.format(Locale.US, "%02d:%02d", h, m));
            assertEquals(5, result.length());
            assertEquals(':', result.charAt(2));
            for (int index : new int[]{0,1,3,4}) {
                assertTrue(result.charAt(index) >= '٠' && result.charAt(index) <= '٩');
            }
        }
    }
    @Test public void existingArabicAndSeparatorArePreserved() {
        assertEquals("١٢:٣٤", ArabicDigits.convert("١٢:٣٤"));
    }
}
