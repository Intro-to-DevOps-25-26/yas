package com.yas.product.model.attribute;

import com.yas.product.model.Product;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ModelAttributeClassesTest {

    @Test
    void testProductAttributeGroup_GettersAndSetters() {
        ProductAttributeGroup group = new ProductAttributeGroup();
        group.setId(1L);
        group.setName("Test Group");

        assertEquals(1L, group.getId());
        assertEquals("Test Group", group.getName());
    }

    @Test
    void testProductAttributeGroup_EqualsAndHashCode() {
        ProductAttributeGroup group1 = new ProductAttributeGroup();
        group1.setId(1L);

        ProductAttributeGroup group2 = new ProductAttributeGroup();
        group2.setId(1L);

        assertEquals(group1, group2);
        assertEquals(group1.hashCode(), group2.hashCode());
    }

    @Test
    void testProductAttributeGroup_Equals_SameInstance() {
        ProductAttributeGroup group = new ProductAttributeGroup();
        assertEquals(group, group);
    }

    @Test
    void testProductAttributeGroup_Equals_DifferentClass() {
        ProductAttributeGroup group = new ProductAttributeGroup();
        assertNotEquals(group, "not a group");
    }

    @Test
    void testProductAttribute_GettersAndSetters() {
        ProductAttributeGroup group = new ProductAttributeGroup();
        group.setId(1L);
        group.setName("Group");

        ProductAttribute attribute = new ProductAttribute();
        attribute.setId(1L);
        attribute.setName("Color");
        attribute.setProductAttributeGroup(group);
        attribute.setProductAttributeTemplates(new ArrayList<>());
        attribute.setAttributeValues(new ArrayList<>());

        assertEquals(1L, attribute.getId());
        assertEquals("Color", attribute.getName());
        assertEquals(group, attribute.getProductAttributeGroup());
    }

    @Test
    void testProductAttribute_WithBuilder() {
        ProductAttribute attribute = ProductAttribute.builder()
            .id(1L)
            .name("Size")
            .build();

        assertEquals(1L, attribute.getId());
        assertEquals("Size", attribute.getName());
    }

    @Test
    void testProductAttribute_WithAllArgsConstructor() {
        ProductAttribute attribute = new ProductAttribute(1L, "Color", null, null, null);

        assertEquals(1L, attribute.getId());
        assertEquals("Color", attribute.getName());
    }

    @Test
    void testProductAttribute_EqualsAndHashCode() {
        ProductAttribute attr1 = new ProductAttribute();
        attr1.setId(1L);

        ProductAttribute attr2 = new ProductAttribute();
        attr2.setId(1L);

        assertEquals(attr1, attr2);
        assertEquals(attr1.hashCode(), attr2.hashCode());
    }

    @Test
    void testProductAttribute_Equals_DifferentClass() {
        ProductAttribute attribute = new ProductAttribute();
        assertNotEquals(attribute, "not an attribute");
    }

    @Test
    void testProductAttributeValue_GettersAndSetters() {
        ProductAttribute attribute = new ProductAttribute();
        attribute.setId(1L);
        attribute.setName("Color");

        Product product = new Product();
        product.setId(1L);

        ProductAttributeValue attributeValue = new ProductAttributeValue();
        attributeValue.setId(1L);
        attributeValue.setProductAttribute(attribute);
        attributeValue.setProduct(product);
        attributeValue.setValue("Red");

        assertEquals(1L, attributeValue.getId());
        assertEquals("Red", attributeValue.getValue());
        assertEquals(attribute, attributeValue.getProductAttribute());
        assertEquals(product, attributeValue.getProduct());
    }

    @Test
    void testProductTemplate_GettersAndSetters() {
        ProductTemplate template = new ProductTemplate();
        template.setId(1L);
        template.setName("Default Template");

        assertEquals(1L, template.getId());
        assertEquals("Default Template", template.getName());
    }
}
