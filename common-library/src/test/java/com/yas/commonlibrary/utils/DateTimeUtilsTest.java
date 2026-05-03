package com.yas.commonlibrary.utils;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DateTimeUtilsTest {

    @Test
    void testFormat_withDefaultPattern_returnsFormattedString() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 5, 2, 6, 30, 45);
        String result = DateTimeUtils.format(dateTime);
        assertTrue(result.contains("02-05-2026") || result.contains("02-05-2026"));
    }

    @Test
    void testFormat_withCustomPattern_returnsFormattedString() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 5, 2, 6, 30, 45);
        String result = DateTimeUtils.format(dateTime, "yyyy-MM-dd HH:mm:ss");
        assertTrue(result.startsWith("2026-05-02"));
    }

    @Test
    void testFormat_withNullDateTime_throwsException() {
        assertThrows(NullPointerException.class, () -> DateTimeUtils.format(null));
    }
}
