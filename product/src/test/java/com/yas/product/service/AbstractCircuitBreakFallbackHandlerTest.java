package com.yas.product.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AbstractCircuitBreakFallbackHandlerTest {

    private final AbstractCircuitBreakFallbackHandler handler = new AbstractCircuitBreakFallbackHandler() {};

    @Test
    void testHandleBodilessFallback_RethrowsException() {
        RuntimeException expectedException = new RuntimeException("Test error");

        Throwable thrown = assertThrows(RuntimeException.class, () -> handler.handleBodilessFallback(expectedException));

        assertEquals("Test error", thrown.getMessage());
    }

    @Test
    void testHandleTypedFallback_ReturnsNullAndRethrowsException() {
        RuntimeException expectedException = new RuntimeException("Test error");

        Throwable thrown = assertThrows(RuntimeException.class, () -> handler.handleTypedFallback(expectedException));

        assertEquals("Test error", thrown.getMessage());
    }

    @Test
    void testHandleError_RethrowsException() {
        RuntimeException expectedException = new RuntimeException("Test error");

        Throwable thrown = assertThrows(RuntimeException.class, () -> handler.handleError(expectedException));

        assertEquals("Test error", thrown.getMessage());
    }
}
