package com.yas.webhook.integration.inbound;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import tools.jackson.databind.JsonNode;
import com.yas.webhook.service.ProductEventService;
import org.junit.jupiter.api.Test;

class ProductEventInboundTest {

    @Test
    void onProductEvent_ShouldCallService() {
        ProductEventService service = mock(ProductEventService.class);
        ProductEventInbound inbound = new ProductEventInbound(service);
        JsonNode payload = mock(JsonNode.class);

        inbound.onProductEvent(payload);

        verify(service).onProductEvent(payload);
    }
}
