package com.yas.product.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModelClassesTest {

    @Test
    void testBrand_GettersAndSetters() {
        Brand brand = new Brand();
        brand.setId(1L);
        brand.setName("Test Brand");
        brand.setSlug("test-brand");
        brand.setPublished(true);
        brand.setProducts(List.of());

        assertEquals(1L, brand.getId());
        assertEquals("Test Brand", brand.getName());
        assertEquals("test-brand", brand.getSlug());
        assertTrue(brand.isPublished());
        assertTrue(brand.getProducts().isEmpty());
    }

    @Test
    void testBrand_EqualsAndHashCode() {
        Brand brand1 = new Brand();
        brand1.setId(1L);

        Brand brand2 = new Brand();
        brand2.setId(1L);

        assertEquals(brand1, brand2);
        assertEquals(brand1.hashCode(), brand2.hashCode());
    }

    @Test
    void testBrand_Equals_SameInstance() {
        Brand brand = new Brand();
        assertEquals(brand, brand);
    }

    @Test
    void testBrand_Equals_NullId() {
        Brand brand1 = new Brand();
        Brand brand2 = new Brand();

        assertNotEquals(brand1, brand2);
    }

    @Test
    void testBrand_Equals_DifferentClass() {
        Brand brand = new Brand();
        assertNotEquals(brand, "not a brand");
    }

    @Test
    void testCategory_GettersAndSetters() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");
        category.setSlug("test-category");
        category.setDescription("Description");
        category.setDisplayOrder((short) 1);
        category.setMetaKeyword("meta");
        category.setMetaDescription("meta desc");
        category.setIsPublished(true);
        category.setImageId(100L);

        assertEquals(1L, category.getId());
        assertEquals("Test Category", category.getName());
        assertEquals("test-category", category.getSlug());
        assertEquals("Description", category.getDescription());
        assertEquals((short) 1, category.getDisplayOrder());
        assertEquals("meta", category.getMetaKeyword());
        assertEquals("meta desc", category.getMetaDescription());
        assertTrue(category.getIsPublished());
        assertEquals(100L, category.getImageId());
    }

    @Test
    void testCategory_WithParent() {
        Category parent = new Category();
        parent.setId(1L);
        parent.setName("Parent");

        Category child = new Category();
        child.setId(2L);
        child.setName("Child");
        child.setParent(parent);

        assertEquals(parent, child.getParent());
    }

    @Test
    void testProductOption_GettersAndSetters() {
        ProductOption option = new ProductOption();
        option.setId(1L);
        option.setName("Color");

        assertEquals(1L, option.getId());
        assertEquals("Color", option.getName());
    }

    @Test
    void testProductOption_EqualsAndHashCode() {
        ProductOption option1 = new ProductOption();
        option1.setId(1L);

        ProductOption option2 = new ProductOption();
        option2.setId(1L);

        assertEquals(option1, option2);
        assertEquals(option1.hashCode(), option2.hashCode());
    }

    @Test
    void testProductOptionCombination_GettersAndSetters() {
        ProductOption option = new ProductOption();
        option.setId(1L);

        Product product = new Product();
        product.setId(1L);

        ProductOptionCombination combination = new ProductOptionCombination();
        combination.setId(1L);
        combination.setProductOption(option);
        combination.setProduct(product);
        combination.setValue("Red");
        combination.setDisplayOrder(1);

        assertEquals(1L, combination.getId());
        assertEquals("Red", combination.getValue());
        assertEquals(1, combination.getDisplayOrder());
        assertEquals(option, combination.getProductOption());
        assertEquals(product, combination.getProduct());
    }

    @Test
    void testProductOptionValue_GettersAndSetters() {
        ProductOption option = new ProductOption();
        option.setId(1L);

        Product product = new Product();
        product.setId(1L);

        ProductOptionValue optionValue = new ProductOptionValue();
        optionValue.setId(1L);
        optionValue.setProductOption(option);
        optionValue.setProduct(product);
        optionValue.setValue("Red");
        optionValue.setDisplayOrder(1);
        optionValue.setDisplayType("TEXT");

        assertEquals(1L, optionValue.getId());
        assertEquals("Red", optionValue.getValue());
        assertEquals("TEXT", optionValue.getDisplayType());
        assertEquals(1, optionValue.getDisplayOrder());
    }

    @Test
    void testProductRelated_GettersAndSetters() {
        Product product = new Product();
        product.setId(1L);

        Product relatedProduct = new Product();
        relatedProduct.setId(2L);

        ProductRelated productRelated = new ProductRelated();
        productRelated.setProduct(product);
        productRelated.setRelatedProduct(relatedProduct);

        assertEquals(product, productRelated.getProduct());
        assertEquals(relatedProduct, productRelated.getRelatedProduct());
    }
}
