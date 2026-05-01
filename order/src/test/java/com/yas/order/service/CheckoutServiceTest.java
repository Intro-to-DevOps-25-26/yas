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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

    @InjectMocks
    private CheckoutService checkoutService;

    @Test
    void getCheckoutWithItemsById_WhenExists_ShouldReturnVm() {
        Checkout checkout = Checkout.builder().id("123").email("test@test.com").build();
        when(checkoutRepository.findById("123")).thenReturn(Optional.of(checkout));
        when(checkoutItemRepository.findAllByCheckoutId("123")).thenReturn(Collections.emptyList());

        CheckoutVm result = checkoutService.getCheckoutWithItemsById("123");

        assertThat(result.id()).isEqualTo("123");
        assertThat(result.email()).isEqualTo("test@test.com");
    }

    @Test
    void updateCheckoutStatus_ShouldSave() {
        Checkout checkout = Checkout.builder().id("123").build();
        when(checkoutRepository.findById("123")).thenReturn(Optional.of(checkout));
        
        CheckoutStatusPutVm statusPutVm = new CheckoutStatusPutVm("COMPLETED");
        checkoutService.updateCheckoutStatus("123", statusPutVm);

        assertThat(checkout.getCheckoutState()).isEqualTo(CheckoutState.COMPLETED);
        verify(checkoutRepository).save(checkout);
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