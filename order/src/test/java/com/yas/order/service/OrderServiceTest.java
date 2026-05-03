package com.yas.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.commonlibrary.utils.AuthenticationUtils;
import com.yas.order.mapper.OrderMapper;
import com.yas.order.model.Order;
import com.yas.order.model.OrderAddress;
import com.yas.order.model.OrderItem;
import com.yas.order.model.csv.OrderItemCsv;
import com.yas.order.model.enumeration.DeliveryMethod;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentMethod;
import com.yas.order.model.enumeration.PaymentStatus;
import com.yas.order.model.request.OrderRequest;
import com.yas.order.repository.OrderItemRepository;
import com.yas.order.repository.OrderRepository;
import com.yas.order.viewmodel.order.OrderExistsByProductAndUserGetVm;
import com.yas.order.viewmodel.order.OrderItemPostVm;
import com.yas.order.viewmodel.order.OrderListVm;
import com.yas.order.viewmodel.order.OrderPostVm;
import com.yas.order.viewmodel.order.OrderVm;
import com.yas.order.viewmodel.order.PaymentOrderStatusVm;
import com.yas.order.viewmodel.orderaddress.OrderAddressPostVm;
import com.yas.order.viewmodel.promotion.PromotionUsageVm;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private ProductService productService;
    @Mock
    private CartService cartService;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private PromotionService promotionService;

    @InjectMocks
    private OrderService orderService;

    private OrderAddress createMockAddress() {
        OrderAddress address = new OrderAddress();
        address.setId(1L);
        return address;
    }

    @Test
    void createOrder_WhenSuccess_ShouldSaveAndCallServices() {
        try (MockedStatic<AuthenticationUtils> mockedAuth = mockStatic(AuthenticationUtils.class)) {
            mockedAuth.when(AuthenticationUtils::extractUserId).thenReturn("user");
            
            OrderAddressPostVm addressVm = OrderAddressPostVm.builder()
                .contactName("C").phone("P").addressLine1("A1").city("City").zipCode("Zip")
                .districtId(1L).districtName("D").stateOrProvinceId(1L).stateOrProvinceName("S").countryId(1L).countryName("C")
                .build();
            
            OrderItemPostVm item = OrderItemPostVm.builder().productId(1L).quantity(2).productPrice(BigDecimal.TEN).productName("P").build();
            
            OrderPostVm postVm = OrderPostVm.builder()
                .checkoutId("check")
                .email("e@e.com")
                .shippingAddressPostVm(addressVm)
                .billingAddressPostVm(addressVm)
                .totalPrice(BigDecimal.valueOf(20))
                .deliveryMethod(DeliveryMethod.VIETTEL_POST)
                .paymentMethod(PaymentMethod.COD)
                .paymentStatus(PaymentStatus.PENDING)
                .orderItemPostVms(List.of(item))
                .build();

            doAnswer(invocation -> {
                Order o = invocation.getArgument(0);
                o.setId(1L);
                return o;
            }).when(orderRepository).save(any(Order.class));

            Order orderWithId = Order.builder()
                .id(1L)
                .billingAddressId(createMockAddress())
                .shippingAddressId(createMockAddress())
                .build();
            when(orderRepository.findById(1L)).thenReturn(Optional.of(orderWithId));

            OrderVm result = orderService.createOrder(postVm);

            assertThat(result).isNotNull();
            verify(orderRepository, times(2)).save(any());
        }
    }

    @Test
    void getOrderWithItemsById_WhenExists_ShouldReturnOrder() {
        Order order = Order.builder()
                .id(1L)
                .billingAddressId(createMockAddress())
                .shippingAddressId(createMockAddress())
                .build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findAllByOrderId(1L)).thenReturn(List.of());

        OrderVm result = orderService.getOrderWithItemsById(1L);

        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void getAllOrder_ShouldReturnList() {
        Page<Order> page = mock(Page.class);
        when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(page.isEmpty()).thenReturn(false);
        Order order = Order.builder()
                .billingAddressId(createMockAddress())
                .shippingAddressId(createMockAddress())
                .build();
        when(page.getContent()).thenReturn(List.of(order));

        OrderListVm result = orderService.getAllOrder(
            Pair.of(ZonedDateTime.now(), ZonedDateTime.now()),
            "product",
            List.of(OrderStatus.PENDING),
            Pair.of("country", "phone"),
            "email",
            Pair.of(0, 10)
        );

        assertThat(result.orderList()).hasSize(1);
    }

    @Test
    void getAllOrder_WhenEmpty_ShouldReturnEmptyList() {
        Page<Order> page = mock(Page.class);
        when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(page.isEmpty()).thenReturn(true);

        OrderListVm result = orderService.getAllOrder(
            Pair.of(ZonedDateTime.now(), ZonedDateTime.now()),
            "product",
            List.of(OrderStatus.PENDING),
            Pair.of("country", "phone"),
            "email",
            Pair.of(0, 10)
        );

        assertThat(result.orderList()).isEmpty();
    }

    @Test
    void updateOrderPaymentStatus_WhenNotFound_ShouldThrowException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        PaymentOrderStatusVm request = PaymentOrderStatusVm.builder().orderId(1L).build();
        assertThrows(NotFoundException.class, () -> orderService.updateOrderPaymentStatus(request));
    }

    @Test
    void exportCsv_WhenOrdersExist_ShouldReturnCsv() throws Exception {
        OrderRequest request = OrderRequest.builder()
            .pageNo(0).pageSize(10)
            .createdFrom(ZonedDateTime.now())
            .createdTo(ZonedDateTime.now())
            .build();
        Page<Order> page = mock(Page.class);
        when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(page.isEmpty()).thenReturn(false);
        
        Order order = Order.builder()
                .id(1L)
                .billingAddressId(createMockAddress())
                .shippingAddressId(createMockAddress())
                .build();
        when(page.getContent()).thenReturn(List.of(order));
        when(orderMapper.toCsv(any())).thenReturn(mock(OrderItemCsv.class));

        byte[] result = orderService.exportCsv(request);

        assertThat(result).isNotNull();
    }

    @Test
    void rejectOrder_WhenExists_ShouldSave() {
        Order order = new Order();
        order.setId(1L);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        
        orderService.rejectOrder(1L, "Reason");

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.REJECT);
        assertThat(order.getRejectReason()).isEqualTo("Reason");
        verify(orderRepository).save(order);
    }

    @Test
    void isOrderCompletedWithUserIdAndProductId_ShouldReturnVm() {
        try (MockedStatic<AuthenticationUtils> mockedAuth = mockStatic(AuthenticationUtils.class)) {
            mockedAuth.when(AuthenticationUtils::extractUserId).thenReturn("user");
            when(productService.getProductVariations(1L)).thenReturn(List.of());
            when(orderRepository.findOne(any(org.springframework.data.jpa.domain.Specification.class))).thenReturn(Optional.of(new Order()));

            var result = orderService.isOrderCompletedWithUserIdAndProductId(1L);

            assertThat(result.isPresent()).isTrue();
        }
    }
}
