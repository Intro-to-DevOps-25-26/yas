package com.yas.product.service;

import com.yas.commonlibrary.config.ServiceUrlConfig;
import com.yas.product.viewmodel.NoFileMediaVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private ServiceUrlConfig serviceUrlConfig;

    @InjectMocks
    private MediaService mediaService;

    @Test
    void testGetMedia_WithNullId_ReturnsEmptyVm() {
        NoFileMediaVm result = mediaService.getMedia(null);

        assertNotNull(result);
        assertNull(result.id());
        assertEquals("", result.url());
    }

    @Test
    void testHandleBodilessFallback_ThrowsException() {
        AbstractCircuitBreakFallbackHandler handler = new AbstractCircuitBreakFallbackHandler() {};

        Exception exception = new RuntimeException("Test exception");

        assertThrows(RuntimeException.class, () -> handler.handleBodilessFallback(exception));
    }

    @Test
    void testHandleError_ThrowsException() {
        AbstractCircuitBreakFallbackHandler handler = new AbstractCircuitBreakFallbackHandler() {};

        Exception exception = new RuntimeException("Test exception");

        assertThrows(RuntimeException.class, () -> handler.handleError(exception));
    }
}
