package com.yas.product.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductOptionCombinationTest {

    @Test
    void testProductOptionCombination_GetterSetter() {
        ProductOptionCombination combination = new ProductOptionCombination();
        combination.setId(1L);
        combination.setValue("Red");
        combination.setDisplayOrder(1);

        ProductOption option = new ProductOption();
        option.setId(10L);
        combination.setProductOption(option);

        Product product = new Product();
        product.setId(100L);
        combination.setProduct(product);

        assertEquals(1L, combination.getId());
        assertEquals("Red", combination.getValue());
        assertEquals(1, combination.getDisplayOrder());
        assertEquals(10L, combination.getProductOption().getId());
        assertEquals(100L, combination.getProduct().getId());
    }

    @Test
    void testProductOptionCombination_Builder() {
        ProductOption option = new ProductOption();
        Product product = new Product();

        ProductOptionCombination combination = ProductOptionCombination.builder()
            .id(1L)
            .value("Blue")
            .displayOrder(2)
            .productOption(option)
            .product(product)
            .build();

        assertEquals(1L, combination.getId());
        assertEquals("Blue", combination.getValue());
        assertEquals(2, combination.getDisplayOrder());
    }

    @Test
    void testProductOptionCombination_EqualsAndHashCode() {
        ProductOptionCombination combo1 = new ProductOptionCombination();
        combo1.setId(1L);
        combo1.setValue("Red");

        ProductOptionCombination combo2 = new ProductOptionCombination();
        combo2.setId(1L);
        combo2.setValue("Red");

        assertEquals(combo1, combo2);
        assertEquals(combo1.hashCode(), combo2.hashCode());
    }

    @Test
    void testProductOptionCombination_ToString() {
        ProductOptionCombination combination = new ProductOptionCombination();
        combination.setId(1L);

        String result = combination.toString();

        assertNotNull(result);
        assertTrue(result.contains("ProductOptionCombination"));
    }
}
