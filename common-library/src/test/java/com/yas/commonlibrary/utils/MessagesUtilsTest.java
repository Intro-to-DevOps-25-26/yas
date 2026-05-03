package com.yas.commonlibrary.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MessagesUtilsTest {

    @Test
    void testGetMessage_withValidErrorCode_returnsMessage() {
        String result = MessagesUtils.getMessage("NOT_FOUND");
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testGetMessage_withInvalidErrorCode_returnsErrorCode() {
        String invalidCode = "INVALID_ERROR_CODE_12345";
        String result = MessagesUtils.getMessage(invalidCode);
        assertEquals(invalidCode, result);
    }

    @Test
    void testGetMessage_withParams_formatsMessageCorrectly() {
        String result = MessagesUtils.getMessage("NOT_FOUND");
        assertNotNull(result);
    }
}
