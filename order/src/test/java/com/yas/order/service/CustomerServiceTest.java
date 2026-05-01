package com.yas.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.utils.AuthenticationUtils;
import com.yas.order.config.ServiceUrlConfig;
import com.yas.order.viewmodel.customer.CustomerVm;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private RestClient restClient;
    @Mock
    private ServiceUrlConfig serviceUrlConfig;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void getCustomer_ShouldReturnCustomer() {
        try (MockedStatic<AuthenticationUtils> mockedAuth = mockStatic(AuthenticationUtils.class)) {
            mockedAuth.when(AuthenticationUtils::extractJwt).thenReturn("token");
            when(serviceUrlConfig.customer()).thenReturn("http://api/customer");

            RestClient.RequestHeadersUriSpec getSpec = mock(RestClient.RequestHeadersUriSpec.class);
            RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
            RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);
            CustomerVm customerVm = new CustomerVm("id", "user", "first", "last", "email");

            when(restClient.get()).thenReturn(getSpec);
            when(getSpec.uri(any(URI.class))).thenReturn(headersSpec);
            when(headersSpec.headers(any())).thenReturn(headersSpec);
            when(headersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.body(CustomerVm.class)).thenReturn(customerVm);

            CustomerVm result = customerService.getCustomer();

            assertThat(result.email()).isEqualTo("email");
        }
    }
}