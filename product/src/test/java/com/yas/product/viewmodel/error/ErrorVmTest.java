package com.yas.product.viewmodel.error;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorVmTest {

    @Test
    void testErrorVm_Constructor() {
        ErrorVm errorVm = new ErrorVm("404", "Not Found", "Resource not found");

        assertEquals("404", errorVm.statusCode());
        assertEquals("Not Found", errorVm.title());
        assertEquals("Resource not found", errorVm.detail());
        assertNotNull(errorVm.fieldErrors());
        assertTrue(errorVm.fieldErrors().isEmpty());
    }

    @Test
    void testErrorVm_Getters() {
        ErrorVm errorVm = new ErrorVm("500", "Error", "Something broke");

        assertEquals("500", errorVm.statusCode());
        assertEquals("Error", errorVm.title());
        assertEquals("Something broke", errorVm.detail());
    }

    @Test
    void testErrorVm_NullValues() {
        ErrorVm errorVm = new ErrorVm(null, null, null);

        assertNull(errorVm.statusCode());
        assertNull(errorVm.title());
        assertNull(errorVm.detail());
    }
}
