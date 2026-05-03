package com.yas.product.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductOptionValueTest {

    @Test
    void testProductOptionValue_GetterSetter() {
        ProductOptionValue productOptionValue = new ProductOptionValue();
        productOptionValue.setId(1L);
        productOptionValue.setDisplayOrder(1);
        productOptionValue.setDisplayType("dropdown");
        productOptionValue.setValue("Red");

        ProductOption option = new ProductOption();
        option.setId(10L);
        productOptionValue.setProductOption(option);

        Product product = new Product();
        product.setId(100L);
        productOptionValue.setProduct(product);

        assertEquals(1L, productOptionValue.getId());
        assertEquals(1, productOptionValue.getDisplayOrder());
        assertEquals("dropdown", productOptionValue.getDisplayType());
        assertEquals("Red", productOptionValue.getValue());
        assertEquals(10L, productOptionValue.getProductOption().getId());
        assertEquals(100L, productOptionValue.getProduct().getId());
    }

    @Test
    void testProductOptionValue_Builder() {
        ProductOption option = new ProductOption();
        Product product = new Product();

        ProductOptionValue productOptionValue = ProductOptionValue.builder()
            .id(1L)
            .displayOrder(2)
            .displayType("radio")
            .value("Blue")
            .productOption(option)
            .product(product)
            .build();

        assertEquals(1L, productOptionValue.getId());
        assertEquals(2, productOptionValue.getDisplayOrder());
        assertEquals("radio", productOptionValue.getDisplayType());
        assertEquals("Blue", productOptionValue.getValue());
    }

    @Test
    void testProductOptionValue_EqualsAndHashCode() {
        ProductOptionValue value1 = new ProductOptionValue();
        value1.setId(1L);
        value1.setDisplayOrder(1);

        ProductOptionValue value2 = new ProductOptionValue();
        value2.setId(1L);
        value2.setDisplayOrder(1);

        assertEquals(value1, value2);
        assertEquals(value1.hashCode(), value2.hashCode());
    }

    @Test
    void testProductOptionValue_ToString() {
        ProductOptionValue productOptionValue = new ProductOptionValue();
        productOptionValue.setId(1L);

        String result = productOptionValue.toString();

        assertNotNull(result);
        assertTrue(result.contains("ProductOptionValue"));
    }
}
