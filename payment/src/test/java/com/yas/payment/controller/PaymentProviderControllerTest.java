package com.yas.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.payment.service.PaymentProviderService;
import com.yas.payment.viewmodel.paymentprovider.CreatePaymentVm;
import com.yas.payment.viewmodel.paymentprovider.PaymentProviderVm;
import com.yas.payment.viewmodel.paymentprovider.UpdatePaymentVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PaymentProviderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentProviderService paymentProviderService;

    @InjectMocks
    private PaymentProviderController paymentProviderController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentProviderController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void create_Success() throws Exception {
        CreatePaymentVm createRequest = new CreatePaymentVm();
        createRequest.setId("PAYPAL");
        createRequest.setName("Paypal");
        createRequest.setConfigureUrl("url");
        PaymentProviderVm response = new PaymentProviderVm("PAYPAL", "Paypal", "url", 1, null, null);

        when(paymentProviderService.create(any(CreatePaymentVm.class))).thenReturn(response);

        mockMvc.perform(post("/backoffice/payment-providers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("PAYPAL"))
                .andExpect(jsonPath("$.name").value("Paypal"));
    }

    @Test
    void update_Success() throws Exception {
        UpdatePaymentVm updateRequest = new UpdatePaymentVm();
        updateRequest.setId("PAYPAL");
        updateRequest.setName("Paypal Updated");
        updateRequest.setConfigureUrl("url");
        PaymentProviderVm response = new PaymentProviderVm("PAYPAL", "Paypal Updated", "url", 1, null, null);

        when(paymentProviderService.update(any(UpdatePaymentVm.class))).thenReturn(response);

        mockMvc.perform(put("/backoffice/payment-providers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Paypal Updated"));
    }

    @Test
    void getAll_Success() throws Exception {
        PaymentProviderVm response = new PaymentProviderVm("PAYPAL", "Paypal", "url", 1, null, null);

        when(paymentProviderService.getEnabledPaymentProviders(any(Pageable.class)))
                .thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/storefront/payment-providers")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("PAYPAL"));
    }
}
