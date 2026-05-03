package com.yas.commonlibrary.kafka.cdc.message;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class OperationTest {

    @Test
    void testRead_getName_returnsR() {
        assertEquals("r", Operation.READ.getName());
    }

    @Test
    void testCreate_getName_returnsC() {
        assertEquals("c", Operation.CREATE.getName());
    }

    @Test
    void testUpdate_getName_returnsU() {
        assertEquals("u", Operation.UPDATE.getName());
    }

    @Test
    void testDelete_getName_returnsD() {
        assertEquals("d", Operation.DELETE.getName());
    }

    @Test
    void testValues_returnsAllOperations() {
        Operation[] operations = Operation.values();
        assertEquals(4, operations.length);
    }

    @Test
    void testValueOf_returnsCorrectOperation() {
        assertEquals(Operation.READ, Operation.valueOf("READ"));
        assertEquals(Operation.CREATE, Operation.valueOf("CREATE"));
        assertEquals(Operation.UPDATE, Operation.valueOf("UPDATE"));
        assertEquals(Operation.DELETE, Operation.valueOf("DELETE"));
    }
}
