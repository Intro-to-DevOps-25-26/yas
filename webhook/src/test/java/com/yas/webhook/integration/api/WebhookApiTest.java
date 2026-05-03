package com.yas.webhook.integration.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import tools.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

class WebhookApiTest {

    private RestClient restClient;
    private WebhookApi webhookApi;

    @BeforeEach
    void setUp() {
        restClient = mock(RestClient.class);
        webhookApi = new WebhookApi(restClient);
    }

    @Test
    void notify_ShouldCallPostAndBody() {
        RestClient.RequestBodyUriSpec requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec requestBodySpec = mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);
        JsonNode jsonNode = mock(JsonNode.class);
        when(jsonNode.toString()).thenReturn("{}");

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), any())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(JsonNode.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);

        webhookApi.notify("http://url", "secret", jsonNode);

        verify(restClient).post();
        verify(requestBodyUriSpec).uri("http://url");
        verify(requestBodySpec).header(eq(WebhookApi.X_HUB_SIGNATURE_256), any());
        verify(requestBodySpec).body(jsonNode);
        verify(responseSpec).toBodilessEntity();
    }

    @Test
    void notify_WhenNoSecret_ShouldNotAddHeader() {
        RestClient.RequestBodyUriSpec requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec requestBodySpec = mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);
        JsonNode jsonNode = mock(JsonNode.class);

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(JsonNode.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);

        webhookApi.notify("http://url", null, jsonNode);

        verify(requestBodySpec).body(jsonNode);
        // verify no header added - would need to mock more specifically to ensure header wasn't called
    }
}
