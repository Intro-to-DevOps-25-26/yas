package com.yas.search.service;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.search.config.ServiceUrlConfig;
import com.yas.search.constant.MessageCode;
import com.yas.search.model.Product;
import com.yas.search.repository.ProductRepository;
import com.yas.search.viewmodel.ProductEsDetailVm;
import java.net.URI;
import java.util.logging.Logger;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ProductSyncDataService {

    private static final Logger LOGGER = Logger.getLogger(ProductSyncDataService.class.getName());

    private final RestClient restClient;
    private final ServiceUrlConfig serviceUrlConfig;
    private final ObjectProvider<ProductRepository> productRepositoryProvider;

    public ProductSyncDataService(
            RestClient restClient,
            ServiceUrlConfig serviceUrlConfig,
            ObjectProvider<ProductRepository> productRepositoryProvider
    ) {
        this.restClient = restClient;
        this.serviceUrlConfig = serviceUrlConfig;
        this.productRepositoryProvider = productRepositoryProvider;
    }

    public ProductEsDetailVm getProductEsDetailById(Long id) {
        final URI url = UriComponentsBuilder.fromUriString(
                serviceUrlConfig.product()).path("/storefront/products-es/{id}").buildAndExpand(id).toUri();
        return restClient.get()
                .uri(url)
                .retrieve()
                .body(ProductEsDetailVm.class);
    }

    public void updateProduct(Long id) {
        ProductEsDetailVm productEsDetailVm = getProductEsDetailById(id);
        ProductRepository productRepository = productRepositoryProvider.getObject();
        Product product = productRepository.findById(id).orElseThrow(()
                -> new NotFoundException(MessageCode.PRODUCT_NOT_FOUND, id));

        if (!productEsDetailVm.isPublished()) {
            productRepository.deleteById(id);
            return;
        }

        product.setName(productEsDetailVm.name());
        product.setSlug(productEsDetailVm.slug());
        product.setPrice(productEsDetailVm.price());
        product.setIsPublished(true);
        product.setIsVisibleIndividually(productEsDetailVm.isVisibleIndividually());
        product.setIsAllowedToOrder(productEsDetailVm.isAllowedToOrder());
        product.setIsFeatured(productEsDetailVm.isFeatured());
        product.setThumbnailMediaId(productEsDetailVm.thumbnailMediaId());
        product.setBrand(productEsDetailVm.brand());
        product.setCategories(productEsDetailVm.categories());
        product.setAttributes(productEsDetailVm.attributes());
        productRepository.save(product);
    }

    public void createProduct(Long id) {
        ProductEsDetailVm productEsDetailVm = getProductEsDetailById(id);
        ProductRepository productRepository = productRepositoryProvider.getObject();

        Product product = Product.builder()
                .id(id)
                .name(productEsDetailVm.name())
                .slug(productEsDetailVm.slug())
                .price(productEsDetailVm.price())
                .isPublished(productEsDetailVm.isPublished())
                .isVisibleIndividually(productEsDetailVm.isVisibleIndividually())
                .isAllowedToOrder(productEsDetailVm.isAllowedToOrder())
                .isFeatured(productEsDetailVm.isFeatured())
                .thumbnailMediaId(productEsDetailVm.thumbnailMediaId())
                .brand(productEsDetailVm.brand())
                .categories(productEsDetailVm.categories())
                .attributes(productEsDetailVm.attributes())
                .build();

        productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        ProductRepository productRepository = productRepositoryProvider.getObject();
        final boolean isProductExisted = productRepository.existsById(id);
        if (isProductExisted) {
            productRepository.deleteById(id);
        } else {
            LOGGER.warning("Product " + id + " doesn't exist in Elasticsearch.");
        }
    }
}
