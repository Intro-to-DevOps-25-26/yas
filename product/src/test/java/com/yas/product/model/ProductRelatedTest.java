package com.yas.product.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductRelatedTest {

    @Test
    void testProductRelated_GetterSetter() {
        ProductRelated productRelated = new ProductRelated();
        productRelated.setId(1L);

        Product product = new Product();
        product.setId(10L);
        productRelated.setProduct(product);

        Product relatedProduct = new Product();
        relatedProduct.setId(20L);
        productRelated.setRelatedProduct(relatedProduct);

        assertEquals(1L, productRelated.getId());
        assertEquals(10L, productRelated.getProduct().getId());
        assertEquals(20L, productRelated.getRelatedProduct().getId());
    }

    @Test
    void testProductRelated_Builder() {
        Product product = new Product();
        Product relatedProduct = new Product();

        ProductRelated productRelated = ProductRelated.builder()
            .id(1L)
            .product(product)
            .relatedProduct(relatedProduct)
            .build();

        assertEquals(1L, productRelated.getId());
        assertNotNull(productRelated.getProduct());
        assertNotNull(productRelated.getRelatedProduct());
    }

    @Test
    void testProductRelated_EqualsAndHashCode() {
        ProductRelated related1 = new ProductRelated();
        related1.setId(1L);

        ProductRelated related2 = new ProductRelated();
        related2.setId(1L);

        assertEquals(related1, related2);
        assertEquals(related1.hashCode(), related2.hashCode());
    }

    @Test
    void testProductRelated_ToString() {
        ProductRelated productRelated = new ProductRelated();
        productRelated.setId(1L);

        String result = productRelated.toString();

        assertNotNull(result);
        assertTrue(result.contains("ProductRelated"));
    }
}
