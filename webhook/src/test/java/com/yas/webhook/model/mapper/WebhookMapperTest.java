package com.yas.webhook.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.webhook.model.Webhook;
import com.yas.webhook.model.WebhookEvent;
import com.yas.webhook.model.viewmodel.webhook.EventVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookListGetVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookVm;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

class WebhookMapperTest {

    private final WebhookMapper webhookMapper = Mappers.getMapper(WebhookMapper.class);

    @Test
    void toWebhookEventVms_WhenEmpty_ShouldReturnEmptyList() {
        List<EventVm> result = webhookMapper.toWebhookEventVms(null);
        assertThat(result).isEmpty();
    }

    @Test
    void toWebhookEventVms_WhenNotEmpty_ShouldReturnList() {
        WebhookEvent event = new WebhookEvent();
        event.setEventId(1L);
        List<EventVm> result = webhookMapper.toWebhookEventVms(List.of(event));
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void toWebhookListGetVm_ShouldMapCorrectly() {
        Page<Webhook> page = mock(Page.class);
        Webhook webhook = new Webhook();
        webhook.setId(1L);
        when(page.stream()).thenReturn(List.of(webhook).stream());
        when(page.getTotalPages()).thenReturn(1);
        when(page.getTotalElements()).thenReturn(1L);
        when(page.isLast()).thenReturn(true);

        WebhookListGetVm result = webhookMapper.toWebhookListGetVm(page, 0, 10);

        assertThat(result.getPageNo()).isEqualTo(0);
        assertThat(result.getPageSize()).isEqualTo(10);
        assertThat(result.getTotalPages()).isEqualTo(1);
    }
}
