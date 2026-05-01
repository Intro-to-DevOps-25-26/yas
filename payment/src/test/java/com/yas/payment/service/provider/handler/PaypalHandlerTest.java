package com.yas.payment.service.provider.handler;

import com.yas.payment.model.CapturedPayment;
import com.yas.payment.model.InitiatedPayment;
import com.yas.payment.model.enumeration.PaymentMethod;
import com.yas.payment.model.enumeration.PaymentStatus;
import com.yas.payment.paypal.service.PaypalService;
import com.yas.payment.paypal.viewmodel.PaypalCapturePaymentRequest;
import com.yas.payment.paypal.viewmodel.PaypalCapturePaymentResponse;
import com.yas.payment.paypal.viewmodel.PaypalCreatePaymentRequest;
import com.yas.payment.paypal.viewmodel.PaypalCreatePaymentResponse;
import com.yas.payment.service.PaymentProviderService;
import com.yas.payment.viewmodel.CapturePaymentRequestVm;
import com.yas.payment.viewmodel.InitPaymentRequestVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaypalHandlerTest {

    @Mock
    private PaymentProviderService paymentProviderService;

    @Mock
    private PaypalService paypalService;

    private PaypalHandler paypalHandler;

    @BeforeEach
    void setUp() {
        paypalHandler = new PaypalHandler(paymentProviderService, paypalService);
    }

    @Test
    void testGetProviderId_ReturnPaypal() {
        // Act
        String providerId = paypalHandler.getProviderId();

        // Assert
        assertThat(providerId).isEqualTo(PaymentMethod.PAYPAL.name());
    }

    @Test
    void testInitPayment_Success() {
        // Arrange
        InitPaymentRequestVm initRequest = InitPaymentRequestVm.builder()
                .paymentMethod(PaymentMethod.PAYPAL.name())
                .totalPrice(new BigDecimal("100.00"))
                .checkoutId("checkout-123")
                .build();

        String paymentSettings = "{\"apiKey\": \"test-key\"}";
        when(paymentProviderService.getAdditionalSettingsByPaymentProviderId(PaymentMethod.PAYPAL.name()))
                .thenReturn(paymentSettings);

        PaypalCreatePaymentResponse paypalResponse = PaypalCreatePaymentResponse.builder()
                .status("PENDING")
                .paymentId("paypal-payment-123")
                .redirectUrl("https://paypal.com/redirect")
                .build();

        when(paypalService.createPayment(any(PaypalCreatePaymentRequest.class)))
                .thenReturn(paypalResponse);

        // Act
        InitiatedPayment result = paypalHandler.initPayment(initRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("PENDING");
        assertThat(result.getPaymentId()).isEqualTo("paypal-payment-123");
        assertThat(result.getRedirectUrl()).contains("paypal.com");

        verify(paymentProviderService, times(1))
                .getAdditionalSettingsByPaymentProviderId(PaymentMethod.PAYPAL.name());
        verify(paypalService, times(1)).createPayment(any(PaypalCreatePaymentRequest.class));
    }

    @Test
    void testCapturePayment_Success() {
        // Arrange
        CapturePaymentRequestVm captureRequest = CapturePaymentRequestVm.builder()
                .paymentMethod(PaymentMethod.PAYPAL.name())
                .token("token-123")
                .build();

        String paymentSettings = "{\"apiKey\": \"test-key\"}";
        when(paymentProviderService.getAdditionalSettingsByPaymentProviderId(PaymentMethod.PAYPAL.name()))
                .thenReturn(paymentSettings);

        PaypalCapturePaymentResponse paypalResponse = PaypalCapturePaymentResponse.builder()
                .checkoutId("checkout-123")
                .amount(new BigDecimal("100.00"))
                .paymentFee(new BigDecimal("5.00"))
                .paymentStatus(PaymentStatus.COMPLETED.name())
                .paymentMethod(PaymentMethod.PAYPAL.name())
                .gatewayTransactionId("txn-123456")
                .build();

        when(paypalService.capturePayment(any(PaypalCapturePaymentRequest.class)))
                .thenReturn(paypalResponse);

        // Act
        CapturedPayment result = paypalHandler.capturePayment(captureRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getCheckoutId()).isEqualTo("checkout-123");
        assertThat(result.getAmount()).isEqualTo(new BigDecimal("100.00"));
        assertThat(result.getPaymentFee()).isEqualTo(new BigDecimal("5.00"));
        assertThat(result.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(result.getPaymentMethod()).isEqualTo(PaymentMethod.PAYPAL);

        verify(paymentProviderService, times(1))
                .getAdditionalSettingsByPaymentProviderId(PaymentMethod.PAYPAL.name());
        verify(paypalService, times(1)).capturePayment(any(PaypalCapturePaymentRequest.class));
    }

    @Test
    void testCapturePayment_WithError() {
        // Arrange
        CapturePaymentRequestVm captureRequest = CapturePaymentRequestVm.builder()
                .paymentMethod(PaymentMethod.PAYPAL.name())
                .token("invalid-token")
                .build();

        String paymentSettings = "{\"apiKey\": \"test-key\"}";
        when(paymentProviderService.getAdditionalSettingsByPaymentProviderId(PaymentMethod.PAYPAL.name()))
                .thenReturn(paymentSettings);

        PaypalCapturePaymentResponse paypalResponse = PaypalCapturePaymentResponse.builder()
                .checkoutId("checkout-123")
                .amount(new BigDecimal("100.00"))
                .paymentStatus(PaymentStatus.CANCELLED.name())
                .paymentMethod(PaymentMethod.PAYPAL.name())
                .failureMessage("Invalid token")
                .build();

        when(paypalService.capturePayment(any(PaypalCapturePaymentRequest.class)))
                .thenReturn(paypalResponse);

        // Act
        CapturedPayment result = paypalHandler.capturePayment(captureRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getPaymentStatus()).isEqualTo(PaymentStatus.CANCELLED);
        assertThat(result.getFailureMessage()).isEqualTo("Invalid token");

        verify(paypalService, times(1)).capturePayment(any(PaypalCapturePaymentRequest.class));
    }

    @Test
    void testInitPayment_WithLargeAmount() {
        // Arrange
        InitPaymentRequestVm initRequest = InitPaymentRequestVm.builder()
                .paymentMethod(PaymentMethod.PAYPAL.name())
                .totalPrice(new BigDecimal("50000.00"))
                .checkoutId("checkout-large")
                .build();

        String paymentSettings = "{\"apiKey\": \"test-key\"}";
        when(paymentProviderService.getAdditionalSettingsByPaymentProviderId(PaymentMethod.PAYPAL.name()))
                .thenReturn(paymentSettings);

        PaypalCreatePaymentResponse paypalResponse = PaypalCreatePaymentResponse.builder()
                .status("PENDING")
                .paymentId("paypal-large-payment")
                .redirectUrl("https://paypal.com/redirect?large=true")
                .build();

        when(paypalService.createPayment(any(PaypalCreatePaymentRequest.class)))
                .thenReturn(paypalResponse);

        // Act
        InitiatedPayment result = paypalHandler.initPayment(initRequest);

        // Assert
        assertThat(result.getPaymentId()).isEqualTo("paypal-large-payment");
        verify(paypalService, times(1)).createPayment(any(PaypalCreatePaymentRequest.class));
    }
}
