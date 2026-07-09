package com.yas.recommendation.vector.common.document;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class DefaultIdGeneratorTest {

    @Test
    void testGenerateId_shouldReturnUuidBasedOnPrefixAndIdentity() {
        var generator = new DefaultIdGenerator("product_", 42L);

        var result = generator.generateId();

        assertNotNull(result);
        assertEquals(UUID.nameUUIDFromBytes("product_-42".getBytes()).toString(), result);
    }

    @Test
    void testGenerateId_withDifferentIdentity_shouldReturnDifferentUuid() {
        var generator1 = new DefaultIdGenerator("product_", 1L);
        var generator2 = new DefaultIdGenerator("product_", 2L);

        var result1 = generator1.generateId();
        var result2 = generator2.generateId();

        assertEquals(UUID.nameUUIDFromBytes("product_-1".getBytes()).toString(), result1);
        assertEquals(UUID.nameUUIDFromBytes("product_-2".getBytes()).toString(), result2);
    }
}