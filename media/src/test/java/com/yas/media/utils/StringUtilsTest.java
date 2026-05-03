package com.yas.media.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class StringUtilsTest {

    @Test
    void hasText_WhenNull_ShouldReturnFalse() {
        assertFalse(StringUtils.hasText(null));
    }

    @Test
    void hasText_WhenEmpty_ShouldReturnFalse() {
        assertFalse(StringUtils.hasText(""));
    }

    @Test
    void hasText_WhenBlank_ShouldReturnFalse() {
        assertFalse(StringUtils.hasText("   "));
    }

    @Test
    void hasText_WhenValidText_ShouldReturnTrue() {
        assertTrue(StringUtils.hasText("text"));
        assertTrue(StringUtils.hasText(" text "));
    }
}
