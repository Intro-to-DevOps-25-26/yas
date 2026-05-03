package com.yas.webhook.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    void getMessage_WhenKeyExists_ShouldReturnFormattedMessage() {
        String result = MessagesUtils.getMessage("non.existent.key", "arg1");
        assertThat(result).isEqualTo("non.existent.key");
    }

    @Test
    void getMessage_WhenKeyHasArguments_ShouldFormat() {
        String result = MessagesUtils.getMessage("Key with {}", "argument");
        assertThat(result).isEqualTo("Key with argument");
    }
}
