package com.yas.webhook.integration.inbound;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import tools.jackson.databind.JsonNode;
import com.yas.webhook.service.OrderEventService;
import org.junit.jupiter.api.Test;

class OrderEventInboundTest {

    @Test
    void onOrderEvent_ShouldCallService() {
        OrderEventService service = mock(OrderEventService.class);
        OrderEventInbound inbound = new OrderEventInbound(service);
        JsonNode payload = mock(JsonNode.class);

        inbound.onOrderEvent(payload);

        verify(service).onOrderEvent(payload);
    }
}
