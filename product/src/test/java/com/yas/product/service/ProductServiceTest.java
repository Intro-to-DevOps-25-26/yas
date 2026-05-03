package com.yas.product.service;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.ProductCategory;
import com.yas.product.model.ProductImage;
import com.yas.product.model.ProductOption;
import com.yas.product.model.ProductOptionCombination;
import com.yas.product.model.ProductOptionValue;
import com.yas.product.model.ProductOptionValueSaveVm;
import com.yas.product.model.ProductRelated;
import com.yas.product.model.enumeration.FilterExistInWhSelection;
import com.yas.product.repository.BrandRepository;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.repository.ProductCategoryRepository;
import com.yas.product.repository.ProductImageRepository;
import com.yas.product.repository.ProductOptionCombinationRepository;
import com.yas.product.repository.ProductOptionRepository;
import com.yas.product.repository.ProductOptionValueRepository;
import com.yas.product.repository.ProductRelatedRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.viewmodel.product.ProductListGetFromCategoryVm;
import com.yas.product.viewmodel.product.ProductListVm;
import com.yas.product.viewmodel.product.ProductOptionValueDisplay;
import com.yas.product.viewmodel.product.ProductPostVm;
import com.yas.product.viewmodel.product.ProductProperties;
import com.yas.product.viewmodel.product.ProductPutVm;
import com.yas.product.viewmodel.product.ProductQuantityPostVm;
import com.yas.product.viewmodel.product.ProductQuantityPutVm;
import com.yas.product.viewmodel.product.ProductSaveVm;
import com.yas.product.viewmodel.product.ProductSlugGetVm;
import com.yas.product.viewmodel.product.ProductThumbnailGetVm;
import com.yas.product.viewmodel.product.ProductThumbnailVm;
import com.yas.product.viewmodel.product.ProductVariationPostVm;
import com.yas.product.viewmodel.product.ProductVariationPutVm;
import com.yas.product.viewmodel.product.ProductsGetVm;
import com.yas.product.viewmodel.productoption.ProductOptionValuePostVm;
import com.yas.product.viewmodel.productoption.ProductOptionValuePutVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MediaService mediaService;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @Mock
    private ProductImageRepository productImageRepository;

    @Mock
    private ProductOptionRepository productOptionRepository;

    @Mock
    private ProductOptionValueRepository productOptionValueRepository;

    @Mock
    private ProductOptionCombinationRepository productOptionCombinationRepository;

    @Mock
    private ProductRelatedRepository productRelatedRepository;

    @InjectMocks
    private ProductService productService;

    private Product sampleProduct;
    private Brand sampleBrand;
    private Category sampleCategory;
    private NoFileMediaVm sampleMedia;

    @BeforeEach
    void setUp() {
        sampleBrand = new Brand();
        sampleBrand.setId(1L);
        sampleBrand.setName("Test Brand");

        sampleCategory = new Category();
        sampleCategory.setId(1L);
        sampleCategory.setName("Test Category");
        sampleCategory.setSlug("test-category");

        sampleMedia = new NoFileMediaVm(100L, "url", "type", "fileName", null);

        sampleProduct = Product.builder()
            .id(1L)
            .name("Test Product")
            .slug("test-product")
            .sku("SKU001")
            .gtin("GTIN001")
            .price(100.0)
            .isPublished(true)
            .isAllowedToOrder(true)
            .isFeatured(false)
            .isVisibleIndividually(true)
            .stockTrackingEnabled(true)
            .stockQuantity(10L)
            .thumbnailMediaId(100L)
            .brand(sampleBrand)
            .productCategories(Collections.emptyList())
            .attributeValues(Collections.emptyList())
            .build();
    }

    @Test
    void testGetProducts_ReturnsPaginatedResults() {
        List<Product> products = List.of(sampleProduct);
        Page<Product> productPage = new PageImpl<>(products);

        when(productRepository.getProductsWithFilter(anyString(), anyString(), any(Pageable.class))).thenReturn(productPage);

        com.yas.product.viewmodel.product.ProductListGetVm result = productService.getProductsWithFilter(0, 10, "", "");

        assertNotNull(result);
        assertEquals(1, result.productContent().size());
    }

    @Test
    void testGetProductDetail_NotFound_ThrowsNotFoundException() {
        when(productRepository.findBySlugAndIsPublishedTrue("nonexistent")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.getProductDetail("nonexistent"));
    }

    @Test
    void testGetLatestProducts_ReturnsProducts() {
        when(productRepository.getLatestProducts(any(Pageable.class))).thenReturn(List.of(sampleProduct));

        List<ProductListVm> result = productService.getLatestProducts(5);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetLatestProducts_EmptyResults() {
        when(productRepository.getLatestProducts(any(Pageable.class))).thenReturn(Collections.emptyList());

        List<ProductListVm> result = productService.getLatestProducts(5);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetProductsByBrand_Successfully() {
        when(brandRepository.findBySlug("test-brand")).thenReturn(Optional.of(sampleBrand));
        when(productRepository.findAllByBrandAndIsPublishedTrueOrderByIdAsc(sampleBrand)).thenReturn(List.of(sampleProduct));
        when(mediaService.getMedia(100L)).thenReturn(sampleMedia);

        List<ProductThumbnailVm> result = productService.getProductsByBrand("test-brand");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetProductsByBrand_BrandNotFound_ThrowsNotFoundException() {
        when(brandRepository.findBySlug("nonexistent")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.getProductsByBrand("nonexistent"));
    }

    @Test
    void testGetProductsFromCategory_Successfully() {
        ProductCategory productCategory = new ProductCategory();
        productCategory.setProduct(sampleProduct);
        productCategory.setCategory(sampleCategory);

        Page<ProductCategory> categoryPage = new PageImpl<>(List.of(productCategory));

        when(categoryRepository.findBySlug("test-category")).thenReturn(Optional.of(sampleCategory));
        when(productCategoryRepository.findAllByCategory(any(Pageable.class), any(Category.class))).thenReturn(categoryPage);
        when(mediaService.getMedia(100L)).thenReturn(sampleMedia);

        ProductListGetFromCategoryVm result = productService.getProductsFromCategory(0, 10, "test-category");

        assertNotNull(result);
        assertEquals(1, result.productContent().size());
    }

    @Test
    void testGetProductsFromCategory_CategoryNotFound_ThrowsNotFoundException() {
        when(categoryRepository.findBySlug("nonexistent")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.getProductsFromCategory(0, 10, "nonexistent"));
    }

    @Test
    void testGetFeaturedProductsById_Successfully() {
        when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(sampleProduct));
        when(mediaService.getMedia(100L)).thenReturn(sampleMedia);

        List<ProductThumbnailGetVm> result = productService.getFeaturedProductsById(List.of(1L));

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetFeaturedProductsById_EmptyResults() {
        when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(Collections.emptyList());

        List<ProductThumbnailGetVm> result = productService.getFeaturedProductsById(List.of(1L));

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetListFeaturedProducts_Successfully() {
        Page<Product> productPage = new PageImpl<>(List.of(sampleProduct));

        when(productRepository.getFeaturedProduct(any(Pageable.class))).thenReturn(productPage);
        when(mediaService.getMedia(100L)).thenReturn(sampleMedia);

        var result = productService.getListFeaturedProducts(0, 10);

        assertNotNull(result);
        assertEquals(1, result.totalPage());
    }

    @Test
    void testDeleteProduct_Successfully() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

        productService.deleteProduct(1L);

        assertFalse(sampleProduct.isPublished());
        verify(productRepository).save(sampleProduct);
    }

    @Test
    void testDeleteProduct_NotFound_ThrowsNotFoundException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.deleteProduct(1L));
    }

    @Test
    void testDeleteProduct_WithVariations() {
        Product parentProduct = new Product();
        parentProduct.setId(2L);
        parentProduct.setName("Parent");
        parentProduct.setPublished(true);

        Product childProduct = Product.builder()
            .id(1L)
            .name("Child")
            .slug("child")
            .parent(parentProduct)
            .build();

        ProductOptionCombination combination = new ProductOptionCombination();
        when(productRepository.findById(1L)).thenReturn(Optional.of(childProduct));
        when(productOptionCombinationRepository.findAllByProduct(childProduct)).thenReturn(List.of(combination));
        when(productRepository.save(any(Product.class))).thenReturn(childProduct);

        productService.deleteProduct(1L);

        assertFalse(childProduct.isPublished());
        verify(productOptionCombinationRepository).deleteAll(List.of(combination));
    }

    @Test
    void testGetProductsByMultiQuery_Successfully() {
        Page<Product> productPage = new PageImpl<>(List.of(sampleProduct));

        when(productRepository.findByProductNameAndCategorySlugAndPriceBetween(
            anyString(), anyString(), anyDouble(), anyDouble(), any(Pageable.class))).thenReturn(productPage);
        when(mediaService.getMedia(100L)).thenReturn(sampleMedia);

        ProductsGetVm result = productService.getProductsByMultiQuery(0, 10, "test", "cat", 10.0, 200.0);

        assertNotNull(result);
        assertEquals(1, result.productContent().size());
    }

    @Test
    void testGetProductVariationsByParentId_NotFound_ThrowsNotFoundException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.getProductVariationsByParentId(1L));
    }

    @Test
    void testGetProductVariationsByParentId_NoOptions_ReturnsEmptyList() {
        Product product = new Product();
        product.setId(1L);
        product.setHasOptions(false);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        var result = productService.getProductVariationsByParentId(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testExportProducts_Successfully() {
        when(productRepository.getExportingProducts(anyString(), anyString())).thenReturn(List.of(sampleProduct));

        var result = productService.exportProducts("test", "brand");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetProductSlug_WithoutParent() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        ProductSlugGetVm result = productService.getProductSlug(1L);

        assertNotNull(result);
        assertEquals("test-product", result.slug());
        assertNull(result.productVariantId());
    }

    @Test
    void testGetProductSlug_WithParent() {
        Product parentProduct = new Product();
        parentProduct.setId(2L);
        parentProduct.setSlug("parent-product");

        Product childProduct = Product.builder()
            .id(1L)
            .name("Child")
            .slug("child")
            .parent(parentProduct)
            .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(childProduct));

        ProductSlugGetVm result = productService.getProductSlug(1L);

        assertNotNull(result);
        assertEquals("parent-product", result.slug());
        assertEquals(1L, result.productVariantId());
    }

    @Test
    void testGetProductSlug_NotFound_ThrowsNotFoundException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.getProductSlug(1L));
    }

    @Test
    void testGetProductEsDetailById_Successfully() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        var result = productService.getProductEsDetailById(1L);

        assertNotNull(result);
        assertEquals("Test Product", result.name());
    }

    @Test
    void testGetProductEsDetailById_NotFound_ThrowsNotFoundException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.getProductEsDetailById(1L));
    }

    @Test
    void testGetRelatedProductsBackoffice_Successfully() {
        Product relatedProduct = Product.builder()
            .id(2L)
            .name("Related Product")
            .slug("related-product")
            .isAllowedToOrder(true)
            .isPublished(true)
            .isFeatured(false)
            .isVisibleIndividually(true)
            .price(50.0)
            .build();

        ProductRelated productRelated = new ProductRelated();
        productRelated.setProduct(sampleProduct);
        productRelated.setRelatedProduct(relatedProduct);

        sampleProduct.setRelatedProducts(List.of(productRelated));

        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        List<ProductListVm> result = productService.getRelatedProductsBackoffice(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Related Product", result.get(0).name());
    }

    @Test
    void testGetRelatedProductsBackoffice_NotFound_ThrowsNotFoundException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.getRelatedProductsBackoffice(1L));
    }

    @Test
    void testGetRelatedProductsStorefront_Successfully() {
        Product relatedProduct = Product.builder()
            .id(2L)
            .name("Related Product")
            .slug("related-product")
            .isPublished(true)
            .price(50.0)
            .thumbnailMediaId(200L)
            .build();

        ProductRelated productRelated = new ProductRelated();
        productRelated.setProduct(sampleProduct);
        productRelated.setRelatedProduct(relatedProduct);

        Page<ProductRelated> relatedPage = new PageImpl<>(List.of(productRelated));

        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(productRelatedRepository.findAllByProduct(any(Product.class), any(Pageable.class))).thenReturn(relatedPage);
        when(mediaService.getMedia(200L)).thenReturn(new NoFileMediaVm(200L, "url", "type", "fileName", null));

        ProductsGetVm result = productService.getRelatedProductsStorefront(1L, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.productContent().size());
    }

    @Test
    void testGetProductCheckoutList_Successfully() {
        Page<Product> productPage = new PageImpl<>(List.of(sampleProduct));

        when(productRepository.findAllPublishedProductsByIds(anyList(), any(Pageable.class))).thenReturn(productPage);
        when(mediaService.getMedia(100L)).thenReturn(sampleMedia);

        var result = productService.getProductCheckoutList(0, 10, List.of(1L));

        assertNotNull(result);
        assertEquals(1, result.productCheckoutListVms().size());
    }

    @Test
    void testUpdateProductQuantity_Successfully() {
        ProductQuantityPostVm quantityVm = new ProductQuantityPostVm(1L, 50L);

        when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(sampleProduct));
        when(productRepository.saveAll(anyList())).thenReturn(List.of(sampleProduct));

        productService.updateProductQuantity(List.of(quantityVm));

        assertEquals(50L, sampleProduct.getStockQuantity());
        verify(productRepository).saveAll(anyList());
    }

    @Test
    void testSubtractStockQuantity_Successfully() {
        ProductQuantityPutVm quantityVm = new ProductQuantityPutVm(1L, 5L);

        when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(sampleProduct));
        when(productRepository.saveAll(anyList())).thenReturn(List.of(sampleProduct));

        productService.subtractStockQuantity(List.of(quantityVm));

        assertEquals(5L, sampleProduct.getStockQuantity());
    }

    @Test
    void testSubtractStockQuantity_BelowZero_SetsToZero() {
        ProductQuantityPutVm quantityVm = new ProductQuantityPutVm(1L, 100L);

        when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(sampleProduct));
        when(productRepository.saveAll(anyList())).thenReturn(List.of(sampleProduct));

        productService.subtractStockQuantity(List.of(quantityVm));

        assertEquals(0L, sampleProduct.getStockQuantity());
    }

    @Test
    void testRestoreStockQuantity_Successfully() {
        ProductQuantityPutVm quantityVm = new ProductQuantityPutVm(1L, 5L);

        when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(sampleProduct));
        when(productRepository.saveAll(anyList())).thenReturn(List.of(sampleProduct));

        productService.restoreStockQuantity(List.of(quantityVm));

        assertEquals(15L, sampleProduct.getStockQuantity());
    }

    @Test
    void testGetProductByIds_Successfully() {
        when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(sampleProduct));

        List<ProductListVm> result = productService.getProductByIds(List.of(1L));

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetProductByCategoryIds_Successfully() {
        when(productRepository.findByCategoryIdsIn(List.of(1L))).thenReturn(List.of(sampleProduct));

        List<ProductListVm> result = productService.getProductByCategoryIds(List.of(1L));

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetProductByBrandIds_Successfully() {
        when(productRepository.findByBrandIdsIn(List.of(1L))).thenReturn(List.of(sampleProduct));

        List<ProductListVm> result = productService.getProductByBrandIds(List.of(1L));

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetProductsForWarehouse_Successfully() {
        when(productRepository.findProductForWarehouse(anyString(), anyString(), anyList(), anyString()))
            .thenReturn(List.of(sampleProduct));

        var result = productService.getProductsForWarehouse("test", "SKU", List.of(1L), FilterExistInWhSelection.ALL);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetProductsForWarehouse_WithNullProductIds() {
        when(productRepository.findProductForWarehouse(anyString(), anyString(), nullable(List.class), anyString()))
            .thenReturn(List.of(sampleProduct));

        var result = productService.getProductsForWarehouse("test", "SKU", null, FilterExistInWhSelection.ALL);

        assertNotNull(result);
    }

    @Test
    void testGetProductById_NotFound_ThrowsNotFoundException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.getProductById(1L));
    }

    @Test
    void testGetProductById_Successfully() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(mediaService.getMedia(100L)).thenReturn(sampleMedia);

        var result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals("Test Product", result.name());
        assertEquals("SKU001", result.sku());
        assertEquals(1L, result.brandId());
    }

    @Test
    void testGetProductById_WithBrandAndCategories() {
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategory(sampleCategory);
        sampleProduct.setProductCategories(List.of(productCategory));

        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(mediaService.getMedia(100L)).thenReturn(sampleMedia);

        var result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals(1L, result.brandId());
        assertFalse(result.categories().isEmpty());
    }

    @Test
    void testGetProductById_NoBrand() {
        Product productNoBrand = Product.builder()
            .id(1L)
            .name("Test")
            .slug("test")
            .sku("SKU")
            .thumbnailMediaId(null)
            .productCategories(Collections.emptyList())
            .attributeValues(Collections.emptyList())
            .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(productNoBrand));

        var result = productService.getProductById(1L);

        assertNotNull(result);
        assertNull(result.brandId());
        assertNull(result.thumbnailMedia());
    }

    @Test
    void testGetProductById_WithProductImages() {
        ProductImage productImage = new ProductImage();
        productImage.setImageId(200L);
        productImage.setProduct(sampleProduct);
        sampleProduct.setProductImages(List.of(productImage));

        NoFileMediaVm media2 = new NoFileMediaVm(200L, "url2", "type", "fileName", null);
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(mediaService.getMedia(100L)).thenReturn(sampleMedia);
        when(mediaService.getMedia(200L)).thenReturn(media2);

        var result = productService.getProductById(1L);

        assertNotNull(result);
        assertFalse(result.productImageMedias().isEmpty());
    }

    @Test
    void testGetProductById_WithParentProduct() {
        Product parent = Product.builder()
            .id(2L)
            .name("Parent")
            .slug("parent")
            .sku("SKU-PARENT")
            .thumbnailMediaId(300L)
            .productCategories(Collections.emptyList())
            .attributeValues(Collections.emptyList())
            .build();

        sampleProduct.setParent(parent);

        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(mediaService.getMedia(100L)).thenReturn(sampleMedia);

        var result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals(2L, result.parentId());
    }

    @Test
    void testDeleteProduct_WithNoVariations() {
        Product productWithNoParent = Product.builder()
            .id(1L)
            .name("No Parent")
            .slug("no-parent")
            .isPublished(true)
            .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(productWithNoParent));
        when(productRepository.save(any(Product.class))).thenReturn(productWithNoParent);

        productService.deleteProduct(1L);

        assertFalse(productWithNoParent.isPublished());
        verify(productOptionCombinationRepository, never()).findAllByProduct(any());
    }

    @Test
    void testDeleteProduct_WithParentButNoCombinations() {
        Product parentProduct = new Product();
        parentProduct.setId(2L);
        parentProduct.setName("Parent");
        parentProduct.setPublished(true);

        Product childProduct = Product.builder()
            .id(1L)
            .name("Child")
            .slug("child")
            .parent(parentProduct)
            .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(childProduct));
        when(productOptionCombinationRepository.findAllByProduct(childProduct)).thenReturn(Collections.emptyList());
        when(productRepository.save(any(Product.class))).thenReturn(childProduct);

        productService.deleteProduct(1L);

        assertFalse(childProduct.isPublished());
        verify(productOptionCombinationRepository, never()).deleteAll(any());
    }

    @Test
    void testGetFeaturedProductsById_WithEmptyThumbnailAndParent() {
        Product product = Product.builder()
            .id(1L)
            .name("Test")
            .slug("test")
            .thumbnailMediaId(null)
            .parent(null)
            .productCategories(Collections.emptyList())
            .attributeValues(Collections.emptyList())
            .build();

        NoFileMediaVm emptyMedia = new NoFileMediaVm(null, "", "", "", "");

        when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(product));
        when(mediaService.getMedia(null)).thenReturn(emptyMedia);

        List<ProductThumbnailGetVm> result = productService.getFeaturedProductsById(List.of(1L));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("", result.get(0).thumbnailUrl());
    }

    @Test
    void testGetFeaturedProductsById_WithEmptyThumbnailUrl() {
        Product product = Product.builder()
            .id(1L)
            .name("Test")
            .slug("test")
            .thumbnailMediaId(100L)
            .parent(null)
            .productCategories(Collections.emptyList())
            .attributeValues(Collections.emptyList())
            .build();

        NoFileMediaVm emptyMedia = new NoFileMediaVm(100L, "", "type", "fileName", null);

        when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(product));
        when(mediaService.getMedia(100L)).thenReturn(emptyMedia);

        List<ProductThumbnailGetVm> result = productService.getFeaturedProductsById(List.of(1L));

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetFeaturedProductsById_ParentNotFound_UsesEmptyUrl() {
        Product parent = new Product();
        parent.setId(2L);

        Product childProduct = Product.builder()
            .id(1L)
            .name("Child")
            .slug("child")
            .thumbnailMediaId(null)
            .parent(parent)
            .price(50.0)
            .build();

        NoFileMediaVm emptyMedia = new NoFileMediaVm(null, "", "", "", "");

        when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(childProduct));
        when(mediaService.getMedia(null)).thenReturn(emptyMedia);
        when(productRepository.findById(2L)).thenReturn(Optional.empty());

        List<ProductThumbnailGetVm> result = productService.getFeaturedProductsById(List.of(1L));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("", result.get(0).thumbnailUrl());
    }

    @Test
    void testGetLatestProducts_ZeroCount_ReturnsEmpty() {
        List<ProductListVm> result = productService.getLatestProducts(0);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetLatestProducts_NegativeCount_ReturnsEmpty() {
        List<ProductListVm> result = productService.getLatestProducts(-5);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetListFeaturedProducts_EmptyPage() {
        Page<Product> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);

        when(productRepository.getFeaturedProduct(any(Pageable.class))).thenReturn(emptyPage);

        var result = productService.getListFeaturedProducts(0, 10);

        assertNotNull(result);
        assertEquals(0, result.totalPage());
    }

    @Test
    void testGetProductDetail_WithAttributeGroups() {
        com.yas.product.model.attribute.ProductAttributeGroup group = new com.yas.product.model.attribute.ProductAttributeGroup();
        group.setId(1L);
        group.setName("Test Group");

        com.yas.product.model.attribute.ProductAttribute attribute = new com.yas.product.model.attribute.ProductAttribute();
        attribute.setId(1L);
        attribute.setName("Test Attribute");
        attribute.setProductAttributeGroup(group);

        com.yas.product.model.attribute.ProductAttributeValue attributeValue = new com.yas.product.model.attribute.ProductAttributeValue();
        attributeValue.setProductAttribute(attribute);
        attributeValue.setValue("Test Value");

        sampleProduct.setAttributeValues(List.of(attributeValue));

        when(productRepository.findBySlugAndIsPublishedTrue("test-product")).thenReturn(Optional.of(sampleProduct));
        when(mediaService.getMedia(100L)).thenReturn(sampleMedia);

        var result = productService.getProductDetail("test-product");

        assertNotNull(result);
        assertEquals("Test Product", result.name());
    }

    @Test
    void testGetProductDetail_WithNullAttributeGroup() {
        com.yas.product.model.attribute.ProductAttribute attribute = new com.yas.product.model.attribute.ProductAttribute();
        attribute.setId(1L);
        attribute.setName("Test Attribute");
        attribute.setProductAttributeGroup(null);

        com.yas.product.model.attribute.ProductAttributeValue attributeValue = new com.yas.product.model.attribute.ProductAttributeValue();
        attributeValue.setProductAttribute(attribute);
        attributeValue.setValue("Test Value");

        sampleProduct.setAttributeValues(List.of(attributeValue));

        when(productRepository.findBySlugAndIsPublishedTrue("test-product")).thenReturn(Optional.of(sampleProduct));
        when(mediaService.getMedia(100L)).thenReturn(sampleMedia);

        var result = productService.getProductDetail("test-product");

        assertNotNull(result);
    }

    @Test
    void testGetProductDetail_WithProductImages() {
        ProductImage productImage = new ProductImage();
        productImage.setImageId(200L);
        productImage.setProduct(sampleProduct);
        sampleProduct.setProductImages(List.of(productImage));

        NoFileMediaVm media2 = new NoFileMediaVm(200L, "url2", "type", "fileName", null);

        when(productRepository.findBySlugAndIsPublishedTrue("test-product")).thenReturn(Optional.of(sampleProduct));
        when(mediaService.getMedia(100L)).thenReturn(sampleMedia);
        when(mediaService.getMedia(200L)).thenReturn(media2);

        var result = productService.getProductDetail("test-product");

        assertNotNull(result);
        assertFalse(result.productImageMediaUrls().isEmpty());
    }

    @Test
    void testGetProductDetail_WithNullBrand() {
        Product productNoBrand = Product.builder()
            .id(1L)
            .name("Test")
            .slug("test")
            .thumbnailMediaId(100L)
            .productCategories(Collections.emptyList())
            .attributeValues(Collections.emptyList())
            .build();

        when(productRepository.findBySlugAndIsPublishedTrue("test")).thenReturn(Optional.of(productNoBrand));
        when(mediaService.getMedia(100L)).thenReturn(sampleMedia);

        var result = productService.getProductDetail("test");

        assertNotNull(result);
        assertNull(result.brandName());
    }

    @Test
    void testGetProductsByMultiQuery_EmptyResults() {
        Page<Product> emptyPage = new PageImpl<>(Collections.emptyList());

        when(productRepository.findByProductNameAndCategorySlugAndPriceBetween(
            anyString(), anyString(), anyDouble(), anyDouble(), any(Pageable.class))).thenReturn(emptyPage);

        ProductsGetVm result = productService.getProductsByMultiQuery(0, 10, "test", "cat", 10.0, 200.0);

        assertNotNull(result);
        assertTrue(result.productContent().isEmpty());
    }

    @Test
    void testGetProductEsDetailById_WithThumbnailMediaId() {
        Product product = Product.builder()
            .id(1L)
            .name("Test")
            .slug("test")
            .price(100.0)
            .isPublished(true)
            .isVisibleIndividually(true)
            .isAllowedToOrder(true)
            .isFeatured(true)
            .thumbnailMediaId(100L)
            .productCategories(Collections.emptyList())
            .attributeValues(Collections.emptyList())
            .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        var result = productService.getProductEsDetailById(1L);

        assertNotNull(result);
        assertEquals(100L, result.thumbnailMediaId());
    }

    @Test
    void testGetProductEsDetailById_WithNullThumbnailMediaId() {
        Product product = Product.builder()
            .id(1L)
            .name("Test")
            .slug("test")
            .price(100.0)
            .isPublished(true)
            .isVisibleIndividually(true)
            .isAllowedToOrder(true)
            .isFeatured(true)
            .thumbnailMediaId(null)
            .productCategories(Collections.emptyList())
            .attributeValues(Collections.emptyList())
            .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        var result = productService.getProductEsDetailById(1L);

        assertNotNull(result);
        assertNull(result.thumbnailMediaId());
    }

    @Test
    void testGetProductEsDetailById_WithBrand() {
        sampleProduct.setAttributeValues(Collections.emptyList());
        sampleProduct.setProductCategories(Collections.emptyList());

        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        var result = productService.getProductEsDetailById(1L);

        assertNotNull(result);
        assertEquals("Test Brand", result.brand());
    }

    @Test
    void testGetProductEsDetailById_WithNullBrand() {
        Product productNoBrand = Product.builder()
            .id(1L)
            .name("Test")
            .slug("test")
            .price(100.0)
            .isPublished(true)
            .isVisibleIndividually(true)
            .isAllowedToOrder(true)
            .isFeatured(true)
            .thumbnailMediaId(null)
            .productCategories(Collections.emptyList())
            .attributeValues(Collections.emptyList())
            .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(productNoBrand));

        var result = productService.getProductEsDetailById(1L);

        assertNotNull(result);
        assertNull(result.brand());
    }

    @Test
    void testGetRelatedProductsStorefront_FiltersUnpublishedProducts() {
        Product publishedRelated = Product.builder()
            .id(2L)
            .name("Published")
            .slug("published")
            .isPublished(true)
            .price(50.0)
            .thumbnailMediaId(200L)
            .build();

        Product unpublishedRelated = Product.builder()
            .id(3L)
            .name("Unpublished")
            .slug("unpublished")
            .isPublished(false)
            .price(30.0)
            .thumbnailMediaId(300L)
            .build();

        ProductRelated publishedRelation = new ProductRelated();
        publishedRelation.setProduct(sampleProduct);
        publishedRelation.setRelatedProduct(publishedRelated);

        ProductRelated unpublishedRelation = new ProductRelated();
        unpublishedRelation.setProduct(sampleProduct);
        unpublishedRelation.setRelatedProduct(unpublishedRelated);

        Page<ProductRelated> relatedPage = new PageImpl<>(List.of(publishedRelation, unpublishedRelation));

        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(productRelatedRepository.findAllByProduct(any(Product.class), any(Pageable.class))).thenReturn(relatedPage);
        when(mediaService.getMedia(200L)).thenReturn(new NoFileMediaVm(200L, "url", "type", "fileName", null));

        ProductsGetVm result = productService.getRelatedProductsStorefront(1L, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.productContent().size());
    }

    @Test
    void testGetProductCheckoutList_EmptyThumbnailUrl() {
        Product product = Product.builder()
            .id(1L)
            .name("Test")
            .slug("test")
            .thumbnailMediaId(100L)
            .brand(sampleBrand)
            .productCategories(Collections.emptyList())
            .attributeValues(Collections.emptyList())
            .build();

        NoFileMediaVm emptyMedia = new NoFileMediaVm(100L, "", "type", "fileName", null);

        Page<Product> productPage = new PageImpl<>(List.of(product));

        when(productRepository.findAllPublishedProductsByIds(anyList(), any(Pageable.class))).thenReturn(productPage);
        when(mediaService.getMedia(100L)).thenReturn(emptyMedia);

        var result = productService.getProductCheckoutList(0, 10, List.of(1L));

        assertNotNull(result);
        assertEquals(1, result.productCheckoutListVms().size());
    }

    @Test
    void testSubtractStockQuantity_ProductNotStockTrackingEnabled() {
        Product product = Product.builder()
            .id(1L)
            .name("Test")
            .slug("test")
            .stockTrackingEnabled(false)
            .stockQuantity(10L)
            .build();

        ProductQuantityPutVm quantityVm = new ProductQuantityPutVm(1L, 5L);

        when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(product));
        when(productRepository.saveAll(anyList())).thenReturn(List.of(product));

        productService.subtractStockQuantity(List.of(quantityVm));

        assertEquals(10L, product.getStockQuantity());
    }

    @Test
    void testRestoreStockQuantity_ProductNotStockTrackingEnabled() {
        Product product = Product.builder()
            .id(1L)
            .name("Test")
            .slug("test")
            .stockTrackingEnabled(false)
            .stockQuantity(10L)
            .build();

        ProductQuantityPutVm quantityVm = new ProductQuantityPutVm(1L, 5L);

        when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(product));
        when(productRepository.saveAll(anyList())).thenReturn(List.of(product));

        productService.restoreStockQuantity(List.of(quantityVm));

        assertEquals(10L, product.getStockQuantity());
    }

    @Test
    void testGetProductVariationsByParentId_WithThumbnail() {
        Product parent = Product.builder()
            .id(1L)
            .name("Parent")
            .slug("parent")
            .isPublished(true)
            .hasOptions(true)
            .build();

        Product variation = Product.builder()
            .id(2L)
            .name("Variation")
            .slug("variation")
            .sku("VAR-SKU")
            .gtin("VAR-GTIN")
            .price(50.0)
            .isPublished(true)
            .thumbnailMediaId(200L)
            .parent(parent)
            .productImages(Collections.emptyList())
            .build();

        parent.setProducts(List.of(variation));

        ProductOptionCombination combination = new ProductOptionCombination();
        ProductOption option = new ProductOption();
        option.setId(1L);
        combination.setProductOption(option);
        combination.setValue("Red");
        combination.setProduct(variation);

        NoFileMediaVm variationMedia = new NoFileMediaVm(200L, "variation-url", "type", "fileName", null);

        when(productRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(productOptionCombinationRepository.findAllByProduct(variation)).thenReturn(List.of(combination));
        when(mediaService.getMedia(200L)).thenReturn(variationMedia);

        var result = productService.getProductVariationsByParentId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.get(0).thumbnail());
    }

    @Test
    void testGetProductVariationsByParentId_WithNullThumbnail() {
        Product parent = Product.builder()
            .id(1L)
            .name("Parent")
            .slug("parent")
            .isPublished(true)
            .hasOptions(true)
            .build();

        Product variation = Product.builder()
            .id(2L)
            .name("Variation")
            .slug("variation")
            .sku("VAR-SKU")
            .gtin("VAR-GTIN")
            .price(50.0)
            .isPublished(true)
            .thumbnailMediaId(null)
            .parent(parent)
            .productImages(Collections.emptyList())
            .build();

        parent.setProducts(List.of(variation));

        when(productRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(productOptionCombinationRepository.findAllByProduct(variation)).thenReturn(Collections.emptyList());

        var result = productService.getProductVariationsByParentId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.get(0).thumbnail());
    }

    @Test
    void testGetProductVariationsByParentId_FiltersUnpublishedVariations() {
        Product parent = Product.builder()
            .id(1L)
            .name("Parent")
            .slug("parent")
            .isPublished(true)
            .hasOptions(true)
            .build();

        Product publishedVariation = Product.builder()
            .id(2L)
            .name("Published")
            .slug("published")
            .isPublished(true)
            .thumbnailMediaId(null)
            .parent(parent)
            .productImages(Collections.emptyList())
            .build();

        Product unpublishedVariation = Product.builder()
            .id(3L)
            .name("Unpublished")
            .slug("unpublished")
            .isPublished(false)
            .thumbnailMediaId(null)
            .parent(parent)
            .productImages(Collections.emptyList())
            .build();

        parent.setProducts(List.of(publishedVariation, unpublishedVariation));

        when(productRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(productOptionCombinationRepository.findAllByProduct(publishedVariation)).thenReturn(Collections.emptyList());

        var result = productService.getProductVariationsByParentId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Published", result.get(0).name());
    }

    @Test
    void testGetProductByIds_EmptyResults() {
        when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(Collections.emptyList());

        List<ProductListVm> result = productService.getProductByIds(List.of(1L));

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetProductByCategoryIds_EmptyResults() {
        when(productRepository.findByCategoryIdsIn(List.of(1L))).thenReturn(Collections.emptyList());

        List<ProductListVm> result = productService.getProductByCategoryIds(List.of(1L));

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetProductByBrandIds_EmptyResults() {
        when(productRepository.findByBrandIdsIn(List.of(1L))).thenReturn(Collections.emptyList());

        List<ProductListVm> result = productService.getProductByBrandIds(List.of(1L));

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}

