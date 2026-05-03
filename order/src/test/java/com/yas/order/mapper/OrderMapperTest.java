package com.yas.order.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.yas.order.model.csv.OrderItemCsv;
import com.yas.order.viewmodel.order.OrderBriefVm;
import com.yas.order.viewmodel.orderaddress.OrderAddressVm;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class OrderMapperTest {

    private final OrderMapper orderMapper = Mappers.getMapper(OrderMapper.class);

    @Test
    void toCsv_ShouldMapCorrectly() {
        OrderAddressVm address = OrderAddressVm.builder().phone("123456").build();
        OrderBriefVm briefVm = OrderBriefVm.builder()
            .id(1L)
            .billingAddressVm(address)
            .email("e@e.com")
            .build();

        OrderItemCsv result = orderMapper.toCsv(briefVm);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getPhone()).isEqualTo("123456");
    }
}
