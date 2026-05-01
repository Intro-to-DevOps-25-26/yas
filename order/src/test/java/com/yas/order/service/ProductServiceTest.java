package com.yas.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.utils.AuthenticationUtils;
import com.yas.order.config.ServiceUrlConfig;
import com.yas.order.viewmodel.order.OrderVm;
import com.yas.order.viewmodel.product.ProductCheckoutListVm;
import com.yas.order.viewmodel.product.ProductGetCheckoutListVm;
import com.yas.order.viewmodel.product.ProductVariationVm;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private RestClient restClient;
    @Mock
    private ServiceUrlConfig serviceUrlConfig;

    @InjectMocks
    private ProductService productService;

    @Test
    void getProductVariations_ShouldReturnList() {
        try (MockedStatic<AuthenticationUtils> mockedAuth = mockStatic(AuthenticationUtils.class)) {
            mockedAuth.when(AuthenticationUtils::extractJwt).thenReturn("token");
            when(serviceUrlConfig.product()).thenReturn("http://api/product");

            RestClient.RequestHeadersUriSpec getSpec = mock(RestClient.RequestHeadersUriSpec.class);
            RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
            RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);
            ResponseEntity<List<ProductVariationVm>> responseEntity = ResponseEntity.ok(List.of(new ProductVariationVm(1L, "V1", "S1")));

            when(restClient.get()).thenReturn(getSpec);
            when(getSpec.uri(any(URI.class))).thenReturn(headersSpec);
            when(headersSpec.headers(any())).thenReturn(headersSpec);
            when(headersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.toEntity(any(ParameterizedTypeReference.class))).thenReturn(responseEntity);

            List<ProductVariationVm> result = productService.getProductVariations(1L);

            assertThat(result).hasSize(1);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void subtractProductStockQuantity_ShouldCallApi() {
        try (MockedStatic<AuthenticationUtils> mockedAuth = mockStatic(AuthenticationUtils.class)) {
            mockedAuth.when(AuthenticationUtils::extractJwt).thenReturn("token");
            when(serviceUrlConfig.product()).thenReturn("http://api/product");

            RestClient.RequestBodyUriSpec putSpec = mock(RestClient.RequestBodyUriSpec.class);
            RestClient.RequestBodySpec bodySpec = mock(RestClient.RequestBodySpec.class);
            RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
            RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

            when(restClient.put()).thenReturn(putSpec);
            when(putSpec.uri(any(URI.class))).thenReturn(bodySpec);
            when(bodySpec.headers(any(Consumer.class))).thenReturn(bodySpec);
            // Use doReturn with explicit return type match for the overloaded body method
            doReturn(headersSpec).when(bodySpec).body(any(Object.class));
            when(headersSpec.retrieve()).thenReturn(responseSpec);

            OrderVm orderVm = OrderVm.builder().orderItemVms(Collections.emptySet()).build();
            productService.subtractProductStockQuantity(orderVm);
        }
    }

    @Test
    void getProductInfomation_ShouldReturnMap() {
        try (MockedStatic<AuthenticationUtils> mockedAuth = mockStatic(AuthenticationUtils.class)) {
            mockedAuth.when(AuthenticationUtils::extractJwt).thenReturn("token");
            when(serviceUrlConfig.product()).thenReturn("http://api/product");

            RestClient.RequestHeadersUriSpec getSpec = mock(RestClient.RequestHeadersUriSpec.class);
            RestClient.RequestHeadersSpec headersSpec = mock(RestClient.RequestHeadersSpec.class);
            RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);
            
            ProductCheckoutListVm item = ProductCheckoutListVm.builder().id(1L).build();
            ProductGetCheckoutListVm response = new ProductGetCheckoutListVm(List.of(item), 0, 10, 1, 1, true);
            ResponseEntity<ProductGetCheckoutListVm> responseEntity = ResponseEntity.ok(response);

            when(restClient.get()).thenReturn(getSpec);
            when(getSpec.uri(any(URI.class))).thenReturn(headersSpec);
            when(headersSpec.headers(any())).thenReturn(headersSpec);
            when(headersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.toEntity(any(ParameterizedTypeReference.class))).thenReturn(responseEntity);

            Map<Long, ProductCheckoutListVm> result = productService.getProductInfomation(Set.of(1L), 0, 10);

            assertThat(result).containsKey(1L);
        }
    }
}
