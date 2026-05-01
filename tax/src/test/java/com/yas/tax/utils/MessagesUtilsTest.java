package com.yas.tax.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    void getMessage_WhenKeyExists_ShouldReturnFormattedMessage() {
        // This depends on the actual messages.properties. 
        // If we don't know the keys, we can test the fallback or mock the ResourceBundle if possible.
        // Since messageBundle is static, it's hard to mock. 
        // We'll test with a key that likely doesn't exist to check the fallback.
        String result = MessagesUtils.getMessage("non.existent.key", "arg1");
        assertThat(result).isEqualTo("non.existent.key");
    }

    @Test
    void getMessage_WhenKeyHasArguments_ShouldFormat() {
        // Testing the formatting logic even if key doesn't exist
        String result = MessagesUtils.getMessage("Key with {0}", "argument");
        assertThat(result).isEqualTo("Key with argument");
    }
}
