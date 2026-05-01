package com.yas.product.service;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.ProductCategory;
import com.yas.product.model.ProductImage;
import com.yas.product.model.ProductOption;
import com.yas.product.model.ProductOptionCombination;
import com.yas.product.model.attribute.ProductAttributeValue;
import com.yas.product.model.attribute.ProductAttribute;
import com.yas.product.repository.ProductOptionCombinationRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.ImageVm;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.viewmodel.product.ProductDetailInfoVm;
import com.yas.product.viewmodel.product.ProductVariationGetVm;
import com.yas.product.viewmodel.productattribute.ProductAttributeValueGetVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductDetailServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MediaService mediaService;

    @Mock
    private ProductOptionCombinationRepository productOptionCombinationRepository;

    @InjectMocks
    private ProductDetailService productDetailService;

    private Product sampleProduct;
    private Brand sampleBrand;
    private Category sampleCategory;

    @BeforeEach
    void setUp() {
        sampleBrand = new Brand();
        sampleBrand.setId(1L);
        sampleBrand.setName("Test Brand");

        sampleCategory = new Category();
        sampleCategory.setId(1L);
        sampleCategory.setName("Test Category");

        sampleProduct = Product.builder()
            .id(1L)
            .name("Test Product")
            .slug("test-product")
            .sku("SKU001")
            .gtin("GTIN001")
            .price(100.0)
            .isPublished(true)
            .isAllowedToOrder(true)
            .isFeatured(true)
            .isVisibleIndividually(true)
            .stockTrackingEnabled(true)
            .thumbnailMediaId(100L)
            .brand(sampleBrand)
            .shortDescription("Short description")
            .description("Full description")
            .specification("Specification")
            .metaTitle("Meta Title")
            .metaKeyword("meta, keywords")
            .metaDescription("Meta description")
            .taxClassId(1L)
            .build();
    }

    @Test
    void testGetProductDetailById_Successfully() {
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategory(sampleCategory);
        sampleProduct.setProductCategories(List.of(productCategory));
        sampleProduct.setAttributeValues(Collections.emptyList());

        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(mediaService.getMedia(100L)).thenReturn(new NoFileMediaVm(100L, "url", "type", "fileName", null));

        ProductDetailInfoVm result = productDetailService.getProductDetailById(1L);

        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        assertEquals("SKU001", result.getSku());
        assertEquals(1L, result.getBrandId());
        assertEquals("Test Brand", result.getBrandName());
    }

    @Test
    void testGetProductDetailById_NotPublished_ThrowsNotFoundException() {
        Product unpublishedProduct = Product.builder()
            .id(1L)
            .name("Unpublished")
            .isPublished(false)
            .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(unpublishedProduct));

        assertThrows(NotFoundException.class, () -> productDetailService.getProductDetailById(1L));
    }

    @Test
    void testGetProductDetailById_NotFound_ThrowsNotFoundException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productDetailService.getProductDetailById(1L));
    }

    @Test
    void testGetProductDetailById_WithNullBrand() {
        sampleProduct.setBrand(null);
        sampleProduct.setProductCategories(Collections.emptyList());
        sampleProduct.setAttributeValues(Collections.emptyList());

        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(mediaService.getMedia(100L)).thenReturn(new NoFileMediaVm(100L, "url", "type", "fileName", null));

        ProductDetailInfoVm result = productDetailService.getProductDetailById(1L);

        assertNotNull(result);
        assertNull(result.getBrandId());
        assertNull(result.getBrandName());
    }

    @Test
    void testGetProductDetailById_WithNullProductCategories() {
        sampleProduct.setProductCategories(null);
        sampleProduct.setAttributeValues(Collections.emptyList());

        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(mediaService.getMedia(100L)).thenReturn(new NoFileMediaVm(100L, "url", "type", "fileName", null));

        ProductDetailInfoVm result = productDetailService.getProductDetailById(1L);

        assertNotNull(result);
    }

    @Test
    void testGetProductDetailById_WithNullThumbnailMediaId() {
        sampleProduct.setThumbnailMediaId(null);
        sampleProduct.setProductCategories(Collections.emptyList());
        sampleProduct.setAttributeValues(Collections.emptyList());

        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        ProductDetailInfoVm result = productDetailService.getProductDetailById(1L);

        assertNotNull(result);
        assertNull(result.getThumbnail());
    }

    @Test
    void testGetProductDetailById_WithProductImages() {
        ProductImage image = new ProductImage();
        image.setImageId(200L);
        sampleProduct.setProductImages(List.of(image));
        sampleProduct.setProductCategories(Collections.emptyList());
        sampleProduct.setAttributeValues(Collections.emptyList());

        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(mediaService.getMedia(100L)).thenReturn(new NoFileMediaVm(100L, "url", "type", "fileName", null));
        when(mediaService.getMedia(200L)).thenReturn(new NoFileMediaVm(200L, "url2", "type", "fileName", null));

        ProductDetailInfoVm result = productDetailService.getProductDetailById(1L);

        assertNotNull(result);
        assertFalse(result.getProductImages().isEmpty());
    }

    @Test
    void testGetProductDetailById_WithNullProductImages() {
        sampleProduct.setProductImages(null);
        sampleProduct.setProductCategories(Collections.emptyList());
        sampleProduct.setAttributeValues(Collections.emptyList());

        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(mediaService.getMedia(100L)).thenReturn(new NoFileMediaVm(100L, "url", "type", "fileName", null));

        ProductDetailInfoVm result = productDetailService.getProductDetailById(1L);

        assertNotNull(result);
    }

    @Test
    void testGetProductDetailById_WithAttributes() {
        ProductAttribute attribute = new ProductAttribute();
        attribute.setId(1L);
        attribute.setName("Color");

        ProductAttributeValue attributeValue = new ProductAttributeValue();
        attributeValue.setId(1L);
        attributeValue.setProductAttribute(attribute);
        attributeValue.setValue("Red");

        sampleProduct.setAttributeValues(List.of(attributeValue));
        sampleProduct.setProductCategories(Collections.emptyList());

        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(mediaService.getMedia(100L)).thenReturn(new NoFileMediaVm(100L, "url", "type", "fileName", null));

        ProductDetailInfoVm result = productDetailService.getProductDetailById(1L);

        assertNotNull(result);
        assertFalse(result.getAttributeValues().isEmpty());
    }

    @Test
    void testGetProductDetailById_WithVariations() {
        Product childProduct = Product.builder()
            .id(2L)
            .name("Child Product")
            .slug("child-product")
            .sku("SKU002")
            .gtin("GTIN002")
            .price(50.0)
            .isPublished(true)
            .parent(sampleProduct)
            .thumbnailMediaId(200L)
            .build();
        childProduct.setProductImages(Collections.emptyList());

        sampleProduct.setHasOptions(true);
        sampleProduct.setProducts(List.of(childProduct));
        sampleProduct.setProductCategories(Collections.emptyList());
        sampleProduct.setAttributeValues(Collections.emptyList());

        ProductOption option = new ProductOption();
        option.setId(1L);
        option.setName("Color");

        ProductOptionCombination combination = new ProductOptionCombination();
        combination.setProductOption(option);
        combination.setValue("Red");
        combination.setProduct(childProduct);

        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(mediaService.getMedia(100L)).thenReturn(new NoFileMediaVm(100L, "url", "type", "fileName", null));
        when(mediaService.getMedia(200L)).thenReturn(new NoFileMediaVm(200L, "url2", "type", "fileName", null));
        when(productOptionCombinationRepository.findAllByProduct(childProduct)).thenReturn(List.of(combination));

        ProductDetailInfoVm result = productDetailService.getProductDetailById(1L);

        assertNotNull(result);
        assertFalse(result.getVariations().isEmpty());
        assertEquals(1, result.getVariations().size());
    }

    @Test
    void testGetProductDetailById_WithUnpublishedVariations() {
        Product unpublishedChild = Product.builder()
            .id(2L)
            .name("Unpublished Child")
            .slug("unpublished-child")
            .isPublished(false)
            .parent(sampleProduct)
            .build();

        sampleProduct.setHasOptions(true);
        sampleProduct.setProducts(List.of(unpublishedChild));
        sampleProduct.setProductCategories(Collections.emptyList());
        sampleProduct.setAttributeValues(Collections.emptyList());

        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(mediaService.getMedia(100L)).thenReturn(new NoFileMediaVm(100L, "url", "type", "fileName", null));

        ProductDetailInfoVm result = productDetailService.getProductDetailById(1L);

        assertNotNull(result);
        assertTrue(result.getVariations().isEmpty());
    }

    @Test
    void testGetProductDetailById_WithoutOptions() {
        sampleProduct.setHasOptions(false);
        sampleProduct.setProductCategories(Collections.emptyList());
        sampleProduct.setAttributeValues(Collections.emptyList());

        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(mediaService.getMedia(100L)).thenReturn(new NoFileMediaVm(100L, "url", "type", "fileName", null));

        ProductDetailInfoVm result = productDetailService.getProductDetailById(1L);

        assertNotNull(result);
        assertTrue(result.getVariations().isEmpty());
    }
}
