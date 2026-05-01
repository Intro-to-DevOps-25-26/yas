package com.yas.commonlibrary.viewmodel.error;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ErrorVmTest {

    @Test
    void testConstructorWithThreeArgs_setsFieldsCorrectly() {
        ErrorVm errorVm = new ErrorVm("404", "Not Found", "Resource not found");
        assertEquals("404", errorVm.statusCode());
        assertEquals("Not Found", errorVm.title());
        assertEquals("Resource not found", errorVm.detail());
        assertTrue(errorVm.fieldErrors().isEmpty());
    }

    @Test
    void testConstructorWithFourArgs_setsAllFields() {
        List<String> errors = Arrays.asList("field1 error", "field2 error");
        ErrorVm errorVm = new ErrorVm("400", "Bad Request", "Invalid", errors);
        assertEquals("400", errorVm.statusCode());
        assertEquals(errors, errorVm.fieldErrors());
    }
}
