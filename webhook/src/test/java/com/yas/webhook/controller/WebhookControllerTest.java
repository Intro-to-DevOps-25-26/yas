package com.yas.webhook.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.webhook.model.viewmodel.webhook.WebhookDetailVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookListGetVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookPostVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookVm;
import com.yas.webhook.service.WebhookService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

class WebhookControllerTest {

    private WebhookService webhookService;
    private WebhookController webhookController;

    @BeforeEach
    void setUp() {
        webhookService = mock(WebhookService.class);
        webhookController = new WebhookController(webhookService);
    }

    @Test
    void getPageableWebhooks_ShouldReturnOk() {
        WebhookListGetVm vm = WebhookListGetVm.builder()
            .webhooks(List.of())
            .pageNo(0)
            .pageSize(10)
            .totalElements(0)
            .totalPages(0)
            .isLast(true)
            .build();
        when(webhookService.getPageableWebhooks(anyInt(), anyInt())).thenReturn(vm);

        ResponseEntity<WebhookListGetVm> response = webhookController.getPageableWebhooks(0, 10);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(vm);
    }

    @Test
    void listWebhooks_ShouldReturnOk() {
        WebhookVm vm = new WebhookVm();
        vm.setId(1L);
        List<WebhookVm> list = List.of(vm);
        when(webhookService.findAllWebhooks()).thenReturn(list);

        ResponseEntity<List<WebhookVm>> response = webhookController.listWebhooks();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(list);
    }

    @Test
    void getWebhook_ShouldReturnOk() {
        WebhookDetailVm detailVm = new WebhookDetailVm();
        detailVm.setId(1L);
        when(webhookService.findById(1L)).thenReturn(detailVm);

        ResponseEntity<WebhookDetailVm> response = webhookController.getWebhook(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(detailVm);
    }

    @Test
    void createWebhook_ShouldReturnCreated() {
        WebhookPostVm postVm = new WebhookPostVm();
        WebhookDetailVm detailVm = new WebhookDetailVm();
        detailVm.setId(1L);
        when(webhookService.create(any())).thenReturn(detailVm);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();

        ResponseEntity<WebhookDetailVm> response = webhookController.createWebhook(postVm, uriBuilder);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(detailVm);
    }

    @Test
    void updateWebhook_ShouldReturnNoContent() {
        WebhookPostVm postVm = new WebhookPostVm();

        ResponseEntity<Void> response = webhookController.updateWebhook(1L, postVm);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(webhookService).update(postVm, 1L);
    }

    @Test
    void deleteWebhook_ShouldReturnNoContent() {
        ResponseEntity<Void> response = webhookController.deleteWebhook(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(webhookService).delete(1L);
    }
}
