package com.yas.webhook.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.webhook.model.enums.EventName;
import com.yas.webhook.model.viewmodel.webhook.EventVm;
import com.yas.webhook.service.EventService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class EventControllerTest {

    private EventService eventService;
    private EventController eventController;

    @BeforeEach
    void setUp() {
        eventService = mock(EventService.class);
        eventController = new EventController(eventService);
    }

    @Test
    void listEvents_ShouldReturnOk() {
        EventVm vm = EventVm.builder()
            .id(1L)
            .name(EventName.ON_ORDER_CREATED)
            .build();
        List<EventVm> list = List.of(vm);
        when(eventService.findAllEvents()).thenReturn(list);

        ResponseEntity<List<EventVm>> response = eventController.listWebhooks();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(list);
    }
}
