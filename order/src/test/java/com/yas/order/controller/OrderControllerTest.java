package com.yas.order.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.order.service.OrderService;
import com.yas.order.viewmodel.order.OrderListVm;
import com.yas.order.viewmodel.order.OrderVm;
import com.yas.order.viewmodel.order.PaymentOrderStatusVm;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = OrderController.class,
    excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private OrderService orderService;

    @Test
    @Disabled("TODO: Fix @WebMvcTest endpoint routing")
    void getOrderWithItemsById_ShouldReturnOrder() throws Exception {
        when(orderService.getOrderWithItemsById(1L)).thenReturn(OrderVm.builder().id(1L).build());

        mockMvc.perform(get("/backoffice/orders/{id}", 1L))
            .andExpect(status().isOk());
    }

    @Test
    @Disabled("TODO: Fix @WebMvcTest endpoint routing")
    void getAllOrder_ShouldReturnList() throws Exception {
        when(orderService.getAllOrder(any(), any(), any(), any(), any(), any()))
            .thenReturn(new OrderListVm(List.of(), 0, 0));

        mockMvc.perform(get("/backoffice/orders"))
            .andExpect(status().isOk());
    }

    @Test
    @Disabled("TODO: Fix @WebMvcTest endpoint routing")
    void updateOrderPaymentStatus_ShouldReturnStatus() throws Exception {
        PaymentOrderStatusVm request = PaymentOrderStatusVm.builder().orderId(1L).build();
        when(orderService.updateOrderPaymentStatus(any())).thenReturn(request);

        mockMvc.perform(put("/backoffice/orders/payment-status")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
    }

    @Test
    @Disabled("TODO: Fix @WebMvcTest endpoint routing")
    void rejectOrder_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(put("/backoffice/orders/reject/1")
            .param("rejectReason", "Reason"))
            .andExpect(status().isNoContent());
    }
}