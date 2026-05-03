package com.yas.webhook.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.webhook.integration.api.WebhookApi;
import com.yas.webhook.model.Event;
import com.yas.webhook.model.Webhook;
import com.yas.webhook.model.WebhookEvent;
import com.yas.webhook.model.WebhookEventNotification;
import com.yas.webhook.model.dto.WebhookEventNotificationDto;
import com.yas.webhook.model.enums.NotificationStatus;
import com.yas.webhook.model.mapper.WebhookMapper;
import com.yas.webhook.model.viewmodel.webhook.EventVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookDetailVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookListGetVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookPostVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookVm;
import com.yas.webhook.repository.EventRepository;
import com.yas.webhook.repository.WebhookEventNotificationRepository;
import com.yas.webhook.repository.WebhookEventRepository;
import com.yas.webhook.repository.WebhookRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class WebhookServiceTest {

    @Mock
    private WebhookRepository webhookRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private WebhookEventRepository webhookEventRepository;
    @Mock
    private WebhookEventNotificationRepository webhookEventNotificationRepository;
    @Mock
    private WebhookMapper webhookMapper;
    @Mock
    private WebhookApi webHookApi;

    @InjectMocks
    private WebhookService webhookService;

    @Test
    void getPageableWebhooks_ShouldReturnListGetVm() {
        Page<Webhook> page = mock(Page.class);
        when(webhookRepository.findAll(any(Pageable.class))).thenReturn(page);
        WebhookListGetVm expectedVm = WebhookListGetVm.builder().build();
        when(webhookMapper.toWebhookListGetVm(any(), any(Integer.class), any(Integer.class))).thenReturn(expectedVm);

        WebhookListGetVm result = webhookService.getPageableWebhooks(0, 10);

        assertThat(result).isEqualTo(expectedVm);
    }

    @Test
    void findAllWebhooks_ShouldReturnList() {
        Webhook webhook = new Webhook();
        when(webhookRepository.findAll(any(Sort.class))).thenReturn(List.of(webhook));
        WebhookVm vm = new WebhookVm();
        when(webhookMapper.toWebhookVm(webhook)).thenReturn(vm);

        List<WebhookVm> result = webhookService.findAllWebhooks();

        assertThat(result).hasSize(1);
    }

    @Test
    void findById_WhenExists_ShouldReturnDetailVm() {
        Webhook webhook = new Webhook();
        when(webhookRepository.findById(1L)).thenReturn(Optional.of(webhook));
        WebhookDetailVm detailVm = new WebhookDetailVm();
        detailVm.setId(1L);
        when(webhookMapper.toWebhookDetailVm(webhook)).thenReturn(detailVm);

        WebhookDetailVm result = webhookService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void findById_WhenNotExists_ShouldThrowNotFoundException() {
        when(webhookRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> webhookService.findById(1L));
    }

    @Test
    void create_WithEvents_ShouldSaveEverything() {
        WebhookPostVm postVm = new WebhookPostVm();
        EventVm eventVm = EventVm.builder().id(2L).build();
        postVm.setEvents(List.of(eventVm));
        
        Webhook createdWebhook = new Webhook();
        createdWebhook.setId(1L);
        
        when(webhookMapper.toCreatedWebhook(postVm)).thenReturn(createdWebhook);
        when(webhookRepository.save(createdWebhook)).thenReturn(createdWebhook);
        when(eventRepository.findById(2L)).thenReturn(Optional.of(new Event()));
        
        WebhookDetailVm detailVm = new WebhookDetailVm();
        when(webhookMapper.toWebhookDetailVm(any())).thenReturn(detailVm);

        webhookService.create(postVm);

        verify(webhookRepository).save(createdWebhook);
        verify(webhookEventRepository).saveAll(any());
    }

    @Test
    void create_WhenEventNotFound_ShouldThrowNotFoundException() {
        WebhookPostVm postVm = new WebhookPostVm();
        EventVm eventVm = EventVm.builder().id(2L).build();
        postVm.setEvents(List.of(eventVm));
        
        Webhook createdWebhook = new Webhook();
        createdWebhook.setId(1L);
        
        when(webhookMapper.toCreatedWebhook(postVm)).thenReturn(createdWebhook);
        when(webhookRepository.save(createdWebhook)).thenReturn(createdWebhook);
        when(eventRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> webhookService.create(postVm));
    }

    @Test
    void update_WithEvents_ShouldUpdateAndReplaceEvents() {
        WebhookPostVm postVm = new WebhookPostVm();
        EventVm eventVm = EventVm.builder().id(2L).build();
        postVm.setEvents(List.of(eventVm));
        
        Webhook existedWebhook = new Webhook();
        existedWebhook.setId(1L);
        existedWebhook.setWebhookEvents(List.of(new WebhookEvent()));
        
        when(webhookRepository.findById(1L)).thenReturn(Optional.of(existedWebhook));
        when(webhookMapper.toUpdatedWebhook(any(), any())).thenReturn(existedWebhook);
        when(eventRepository.findById(2L)).thenReturn(Optional.of(new Event()));

        webhookService.update(postVm, 1L);

        verify(webhookRepository).save(existedWebhook);
        verify(webhookEventRepository).deleteAll(any());
        verify(webhookEventRepository).saveAll(any());
    }

    @Test
    void update_WhenNotExists_ShouldThrowNotFoundException() {
        when(webhookRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> webhookService.update(new WebhookPostVm(), 1L));
    }

    @Test
    void delete_WhenExists_ShouldDelete() {
        when(webhookRepository.existsById(1L)).thenReturn(true);
        webhookService.delete(1L);
        verify(webhookEventRepository).deleteByWebhookId(1L);
        verify(webhookRepository).deleteById(1L);
    }

    @Test
    void delete_WhenNotExists_ShouldThrowNotFoundException() {
        when(webhookRepository.existsById(1L)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> webhookService.delete(1L));
    }

    @Test
    void notifyToWebhook_ShouldCallApiAndUpdateStatus() {
        WebhookEventNotificationDto dto = WebhookEventNotificationDto.builder()
            .notificationId(1L).url("url").secret("secret").build();
        WebhookEventNotification notification = new WebhookEventNotification();
        when(webhookEventNotificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        webhookService.notifyToWebhook(dto);

        verify(webHookApi).notify("url", "secret", null);
        assertThat(notification.getNotificationStatus()).isEqualTo(NotificationStatus.NOTIFIED);
        verify(webhookEventNotificationRepository).save(notification);
    }
}
