package com.yas.order.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.utils.AuthenticationUtils;
import com.yas.order.config.ServiceUrlConfig;
import com.yas.order.viewmodel.promotion.PromotionUsageVm;
import java.net.URI;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class PromotionServiceTest {

    @Mock
    private RestClient restClient;
    @Mock
    private ServiceUrlConfig serviceUrlConfig;

    @InjectMocks
    private PromotionService promotionService;

    @Test
    void updateUsagePromotion_ShouldCallApi() {
        try (MockedStatic<AuthenticationUtils> mockedAuth = mockStatic(AuthenticationUtils.class)) {
            mockedAuth.when(AuthenticationUtils::extractJwt).thenReturn("token");
            when(serviceUrlConfig.promotion()).thenReturn("http://api/promotion");

            RestClient.RequestBodyUriSpec putSpec = mock(RestClient.RequestBodyUriSpec.class);
            RestClient.RequestBodySpec bodySpec = mock(RestClient.RequestBodySpec.class);
            RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
            RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

            when(restClient.put()).thenReturn(putSpec);
            when(putSpec.uri(any(URI.class))).thenReturn(bodySpec);
            when(bodySpec.headers(any(Consumer.class))).thenReturn(bodySpec);
            when(bodySpec.body(any(Object.class))).thenReturn(headersSpec);
            when(headersSpec.retrieve()).thenReturn(responseSpec);

            promotionService.updateUsagePromotion(PromotionUsageVm.builder().build());

            verify(restClient).put();
        }
    }
}
