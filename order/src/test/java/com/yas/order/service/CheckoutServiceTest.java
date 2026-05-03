package com.yas.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.order.model.Checkout;
import com.yas.order.model.enumeration.CheckoutState;
import com.yas.order.repository.CheckoutItemRepository;
import com.yas.order.repository.CheckoutRepository;
import com.yas.order.viewmodel.checkout.CheckoutPaymentMethodPutVm;
import com.yas.order.viewmodel.checkout.CheckoutStatusPutVm;
import com.yas.order.viewmodel.checkout.CheckoutVm;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceTest {

    @Mock
    private CheckoutRepository checkoutRepository;
    @Mock
    private CheckoutItemRepository checkoutItemRepository;
    @Mock
    private OrderService orderService;
    @Mock
    private ProductService productService;
    @Mock
    private com.yas.order.mapper.CheckoutMapper checkoutMapper;

    @InjectMocks
    private CheckoutService checkoutService;

    @Test
    void getCheckoutWithItemsById_WhenExists_ShouldReturnVm() {
        Checkout checkout = Checkout.builder().id("123").email("test@test.com").createdBy("user").build();
        when(checkoutRepository.findByIdAndCheckoutState("123", CheckoutState.PENDING)).thenReturn(Optional.of(checkout));
        try (MockedStatic<com.yas.commonlibrary.utils.AuthenticationUtils> mockedAuth = mockStatic(com.yas.commonlibrary.utils.AuthenticationUtils.class)) {
            mockedAuth.when(com.yas.commonlibrary.utils.AuthenticationUtils::extractUserId).thenReturn("user");
            when(checkoutMapper.toVm(any())).thenReturn(CheckoutVm.builder().id("123").email("test@test.com").build());

            CheckoutVm result = checkoutService.getCheckoutPendingStateWithItemsById("123");

            assertThat(result.id()).isEqualTo("123");
            assertThat(result.email()).isEqualTo("test@test.com");
        }
    }

    @Test
    void updateCheckoutStatus_ShouldSave() {
        Checkout checkout = Checkout.builder().id("123").createdBy("user").build();
        when(checkoutRepository.findById("123")).thenReturn(Optional.of(checkout));
        try (MockedStatic<com.yas.commonlibrary.utils.AuthenticationUtils> mockedAuth = mockStatic(com.yas.commonlibrary.utils.AuthenticationUtils.class)) {
            mockedAuth.when(com.yas.commonlibrary.utils.AuthenticationUtils::extractUserId).thenReturn("user");
            when(orderService.findOrderByCheckoutId("123")).thenReturn(com.yas.order.model.Order.builder().id(1L).build());

            CheckoutStatusPutVm statusPutVm = new CheckoutStatusPutVm("123", "COMPLETED");
            checkoutService.updateCheckoutStatus(statusPutVm);

            assertThat(checkout.getCheckoutState()).isEqualTo(CheckoutState.COMPLETED);
            verify(checkoutRepository).save(checkout);
        }
    }

    @Test
    void updateCheckoutPaymentMethod_ShouldSave() {
        Checkout checkout = Checkout.builder().id("123").build();
        when(checkoutRepository.findById("123")).thenReturn(Optional.of(checkout));
        
        CheckoutPaymentMethodPutVm paymentVm = new CheckoutPaymentMethodPutVm("COD");
        checkoutService.updateCheckoutPaymentMethod("123", paymentVm);

        assertThat(checkout.getPaymentMethodId()).isEqualTo("COD");
        verify(checkoutRepository).save(checkout);
    }
    
    @Test
    void updateCheckoutPaymentMethod_WhenNotFound_ShouldThrowException() {
        when(checkoutRepository.findById("invalid")).thenReturn(Optional.empty());
        CheckoutPaymentMethodPutVm paymentVm = new CheckoutPaymentMethodPutVm("COD");
        assertThrows(NotFoundException.class, () -> checkoutService.updateCheckoutPaymentMethod("invalid", paymentVm));
    }
}