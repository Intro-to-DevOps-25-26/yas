package com.yas.recommendation.vector.common.document;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.yas.recommendation.vector.common.formatter.DefaultDocumentFormatter;
import com.yas.recommendation.vector.product.formatter.ProductDocumentFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

class DocumentFormatterTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testDefaultDocumentFormatter_shouldReplaceTemplate() {
        var formatter = new DefaultDocumentFormatter();
        Map<String, Object> entityMap = new LinkedHashMap<>();
        entityMap.put("name", "Test Product");
        entityMap.put("description", "A great product");

        String result = formatter.format(entityMap, "{name}: {description}", objectMapper);

        assertNotNull(result);
        assertTrue(result.contains("Test Product"));
        assertTrue(result.contains("A great product"));
    }

    @Test
    void testDefaultDocumentFormatter_shouldRemoveHtmlTags() {
        var formatter = new DefaultDocumentFormatter();
        Map<String, Object> entityMap = new LinkedHashMap<>();
        entityMap.put("name", "Test <b>Product</b>");

        String result = formatter.format(entityMap, "{name}", objectMapper);

        assertTrue(result.contains("Test Product"));
    }

    @Test
    void testProductDocumentFormatter_shouldFormatAttributes() {
        var formatter = new ProductDocumentFormatter();
        Map<String, Object> entityMap = new LinkedHashMap<>();
        entityMap.put("name", "Product A");
        entityMap.put("attributeValues", List.of(
            Map.of("id", 1, "nameProductAttribute", "Color", "value", "Red"),
            Map.of("id", 2, "nameProductAttribute", "Size", "value", "M")
        ));
        entityMap.put("categories", List.of(
            Map.of("name", "Electronics"),
            Map.of("name", "Gadgets")
        ));

        String result = formatter.format(entityMap, "{name} - {attributeValues} - {categories}", objectMapper);

        assertTrue(result.contains("Product A"));
        assertTrue(result.contains("Color: Red"));
        assertTrue(result.contains("Size: M"));
        assertTrue(result.contains("Electronics"));
        assertTrue(result.contains("Gadgets"));
    }

    @Test
    void testProductDocumentFormatter_whenAttributesNull_shouldReturnEmptyArray() {
        var formatter = new ProductDocumentFormatter();
        Map<String, Object> entityMap = new LinkedHashMap<>();
        entityMap.put("name", "Product B");
        entityMap.put("attributeValues", null);
        entityMap.put("categories", null);

        String result = formatter.format(entityMap, "{name} - {attributeValues} - {categories}", objectMapper);

        assertTrue(result.contains("[]"));
    }
}