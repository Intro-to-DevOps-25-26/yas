package com.yas.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.utils.AuthenticationUtils;
import com.yas.order.config.ServiceUrlConfig;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class TaxServiceTest {

    @Mock
    private RestClient restClient;
    @Mock
    private ServiceUrlConfig serviceUrlConfig;

    @InjectMocks
    private TaxService taxService;

    @Test
    void getTaxPercent_ShouldReturnDefaultValue() {
        try (MockedStatic<AuthenticationUtils> mockedAuth = mockStatic(AuthenticationUtils.class)) {
            mockedAuth.when(AuthenticationUtils::extractJwt).thenReturn("token");
            when(serviceUrlConfig.tax()).thenReturn("http://api/tax");

            RestClient.RequestHeadersUriSpec getSpec = mock(RestClient.RequestHeadersUriSpec.class);
            RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
            RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

            when(restClient.get()).thenReturn(getSpec);
            when(getSpec.uri(any(URI.class))).thenReturn(headersSpec);
            when(headersSpec.headers(any())).thenReturn(headersSpec);
            when(headersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.body(Double.class)).thenReturn(10.0);

            double result = taxService.getTaxPercent(1L, 1L);

            assertThat(result).isEqualTo(10.0);
        }
    }
}