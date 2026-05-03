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
import java.util.List;
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

            RestClient.RequestBodyUriSpec postSpec = mock(RestClient.RequestBodyUriSpec.class);
            RestClient.RequestBodySpec bodySpec = mock(RestClient.RequestBodySpec.class);
            RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

            when(restClient.post()).thenReturn(postSpec);
            when(postSpec.uri(any(URI.class))).thenReturn(bodySpec);
            when(bodySpec.headers(any())).thenReturn(bodySpec);
            when(bodySpec.body(any(List.class))).thenReturn(bodySpec);
            when(bodySpec.retrieve()).thenReturn(responseSpec);

            promotionService.updateUsagePromotion(List.of(PromotionUsageVm.builder().build()));

            verify(restClient).post();
        }
    }
}
