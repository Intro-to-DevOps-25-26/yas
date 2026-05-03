package com.yas.webhook.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import org.junit.jupiter.api.Test;

class HmacUtilsTest {

    @Test
    void hash_ShouldReturnCorrectHash() throws NoSuchAlgorithmException, InvalidKeyException {
        String data = "test-data";
        String key = "test-key";
        
        String hash1 = HmacUtils.hash(data, key);
        String hash2 = HmacUtils.hash(data, key);
        
        assertThat(hash1).isEqualTo(hash2);
        assertThat(hash1).isNotNull();
    }
}
