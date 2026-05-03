package com.yas.order.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.csv.CsvExporter;
import com.yas.order.model.csv.OrderItemCsv;
import com.yas.order.model.enumeration.DeliveryMethod;
import com.yas.order.model.enumeration.DeliveryStatus;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentMethod;
import com.yas.order.model.enumeration.PaymentStatus;
import com.yas.order.model.request.OrderRequest;
import com.yas.order.service.OrderService;
import com.yas.order.viewmodel.order.OrderBriefVm;
import com.yas.order.viewmodel.order.OrderExistsByProductAndUserGetVm;
import com.yas.order.viewmodel.order.OrderGetVm;
import com.yas.order.viewmodel.order.OrderItemGetVm;
import com.yas.order.viewmodel.order.OrderListVm;
import com.yas.order.viewmodel.order.OrderPostVm;
import com.yas.order.viewmodel.order.OrderVm;
import com.yas.order.viewmodel.order.PaymentOrderStatusVm;
import com.yas.order.viewmodel.orderaddress.OrderAddressPostVm;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderControllerCoverageTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @Test
    void createOrder_ShouldReturnOrderVm() {
        OrderPostVm request = createOrderPostVm();
        OrderVm response = OrderVm.builder().id(1L).email("test@test.com").build();
        when(orderService.createOrder(request)).thenReturn(response);

        var result = orderController.createOrder(request);

        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void updateOrderPaymentStatus_ShouldReturnResponse() {
        PaymentOrderStatusVm request = PaymentOrderStatusVm.builder()
            .orderId(1L)
            .orderStatus("PAID")
            .paymentId(2L)
            .paymentStatus("COMPLETED")
            .build();
        when(orderService.updateOrderPaymentStatus(request)).thenReturn(request);

        var result = orderController.updateOrderPaymentStatus(request);

        assertThat(result.getBody()).isEqualTo(request);
    }

    @Test
    void checkOrderExistsByProductIdAndUserIdWithStatus_ShouldReturnPresent() {
        var response = new OrderExistsByProductAndUserGetVm(true);
        when(orderService.isOrderCompletedWithUserIdAndProductId(1L)).thenReturn(response);

        var result = orderController.checkOrderExistsByProductIdAndUserIdWithStatus(1L);

        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void getMyOrders_ShouldReturnList() {
        List<OrderGetVm> response = List.of(
            new OrderGetVm(1L, OrderStatus.PENDING, BigDecimal.TEN, DeliveryStatus.PREPARING,
                DeliveryMethod.VIETTEL_POST, List.<OrderItemGetVm>of(), ZonedDateTime.now())
        );
        when(orderService.getMyOrders("product", OrderStatus.PENDING)).thenReturn(response);

        var result = orderController.getMyOrders("product", OrderStatus.PENDING);

        assertThat(result.getBody()).hasSize(1);
    }

    @Test
    void getOrderWithItemsById_ShouldReturnOrder() {
        OrderVm response = OrderVm.builder().id(1L).build();
        when(orderService.getOrderWithItemsById(1L)).thenReturn(response);

        var result = orderController.getOrderWithItemsById(1L);

        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void getOrderWithCheckoutId_ShouldReturnOrder() {
        OrderGetVm response = new OrderGetVm(1L, OrderStatus.PENDING, BigDecimal.TEN, DeliveryStatus.PREPARING,
            DeliveryMethod.VIETTEL_POST, List.<OrderItemGetVm>of(), ZonedDateTime.now());
        when(orderService.findOrderVmByCheckoutId("checkout-1")).thenReturn(response);

        var result = orderController.getOrderWithCheckoutId("checkout-1");

        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void getOrders_ShouldReturnListVm() {
        OrderListVm response = OrderListVm.builder().orderList(List.of()).totalElements(0).totalPages(0).build();
        when(orderService.getAllOrder(any(), anyString(), any(), any(), anyString(), any())).thenReturn(response);

        var result = orderController.getOrders(ZonedDateTime.now().minusDays(1), ZonedDateTime.now(), "", List.<OrderStatus>of(), "", "", "", 0, 10);

        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void getLatestOrders_ShouldReturnBriefList() {
        List<OrderBriefVm> response = List.of(OrderBriefVm.builder().id(1L).email("test@test.com").build());
        when(orderService.getLatestOrders(5)).thenReturn(response);

        var result = orderController.getLatestOrders(5);

        assertThat(result.getBody()).hasSize(1);
    }

    @Test
    void exportCsv_ShouldReturnCsvBytesAndHeaders() throws Exception {
        byte[] csvBytes = "csv-data".getBytes();
        when(orderService.exportCsv(any(OrderRequest.class))).thenReturn(csvBytes);

        try (MockedStatic<CsvExporter> mockedCsv = mockStatic(CsvExporter.class)) {
            mockedCsv.when(() -> CsvExporter.createFileName(OrderItemCsv.class)).thenReturn("orders.csv");

            var result = orderController.exportCsv(OrderRequest.builder().pageNo(0).pageSize(10).build());

            assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
            assertThat(result.getHeaders().getFirst("Content-Disposition")).contains("orders.csv");
            assertThat(result.getBody()).isEqualTo(csvBytes);
        }
    }

    private OrderPostVm createOrderPostVm() {
        OrderAddressPostVm address = OrderAddressPostVm.builder()
            .contactName("C")
            .phone("P")
            .addressLine1("A1")
            .city("City")
            .zipCode("Zip")
            .districtId(1L)
            .districtName("D")
            .stateOrProvinceId(1L)
            .stateOrProvinceName("S")
            .countryId(1L)
            .countryName("Country")
            .build();

        return OrderPostVm.builder()
            .checkoutId("checkout-1")
            .email("test@test.com")
            .shippingAddressPostVm(address)
            .billingAddressPostVm(address)
            .totalPrice(BigDecimal.TEN)
            .deliveryMethod(DeliveryMethod.VIETTEL_POST)
            .paymentMethod(PaymentMethod.COD)
            .paymentStatus(PaymentStatus.PENDING)
            .orderItemPostVms(List.of())
            .build();
    }
}
