package com.yas.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.inventory.model.Warehouse;
import com.yas.inventory.model.enumeration.FilterExistInWhSelection;
import com.yas.inventory.repository.StockRepository;
import com.yas.inventory.repository.WarehouseRepository;
import com.yas.inventory.viewmodel.address.AddressDetailVm;
import com.yas.inventory.viewmodel.address.AddressVm;
import com.yas.inventory.viewmodel.product.ProductInfoVm;
import com.yas.inventory.viewmodel.warehouse.WarehouseDetailVm;
import com.yas.inventory.viewmodel.warehouse.WarehouseGetVm;
import com.yas.inventory.viewmodel.warehouse.WarehouseListGetVm;
import com.yas.inventory.viewmodel.warehouse.WarehousePostVm;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;
    @Mock
    private StockRepository stockRepository;
    @Mock
    private ProductService productService;
    @Mock
    private LocationService locationService;

    @InjectMocks
    private WarehouseService warehouseService;

    @Test
    void findAllWarehouses_ShouldReturnList() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("Warehouse");
        when(warehouseRepository.findAll()).thenReturn(List.of(warehouse));

        List<WarehouseGetVm> result = warehouseService.findAllWarehouses();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1L);
    }

    @Test
    void findById_WhenExists_ShouldReturnDetail() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setAddressId(2L);
        warehouse.setName("Warehouse Name");
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        
        AddressDetailVm address = AddressDetailVm.builder()
            .id(2L)
            .contactName("Contact")
            .phone("Phone")
            .addressLine1("A1")
            .addressLine2("A2")
            .city("City")
            .zipCode("Zip")
            .districtId(1L)
            .stateOrProvinceId(1L)
            .countryId(1L)
            .build();
        when(locationService.getAddressById(2L)).thenReturn(address);

        WarehouseDetailVm result = warehouseService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo(warehouse.getName());
    }

    @Test
    void findById_WhenNotExists_ShouldThrowException() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> warehouseService.findById(1L));
    }

    @Test
    void create_WhenNameExists_ShouldThrowException() {
        WarehousePostVm postVm = WarehousePostVm.builder().name("Exist").build();
        when(warehouseRepository.existsByName("Exist")).thenReturn(true);

        assertThrows(DuplicatedException.class, () -> warehouseService.create(postVm));
    }

    @Test
    void create_WhenSuccess_ShouldSave() {
        WarehousePostVm postVm = WarehousePostVm.builder()
            .name("New")
            .contactName("C")
            .phone("P")
            .addressLine1("A1")
            .city("City")
            .districtId(1L)
            .stateOrProvinceId(1L)
            .countryId(1L)
            .build();
        when(warehouseRepository.existsByName("New")).thenReturn(false);
        when(locationService.createAddress(any())).thenReturn(AddressVm.builder().id(10L).build());
        
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        when(warehouseRepository.save(any())).thenReturn(warehouse);

        Warehouse result = warehouseService.create(postVm);

        assertThat(result.getId()).isEqualTo(1L);
        verify(warehouseRepository).save(any());
    }

    @Test
    void update_WhenSuccess_ShouldSave() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setAddressId(10L);
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(warehouseRepository.existsByNameWithDifferentId("Update", 1L)).thenReturn(false);

        WarehousePostVm postVm = WarehousePostVm.builder().name("Update").build();
        warehouseService.update(postVm, 1L);

        verify(warehouseRepository).save(warehouse);
        verify(locationService).updateAddress(eq(10L), any());
    }

    @Test
    void delete_WhenExists_ShouldDelete() {
        Warehouse warehouse = new Warehouse();
        warehouse.setAddressId(10L);
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));

        warehouseService.delete(1L);

        verify(warehouseRepository).deleteById(1L);
        verify(locationService).deleteAddress(10L);
    }

    @Test
    void getPageableWarehouses_ShouldReturnPage() {
        Page<Warehouse> page = mock(Page.class);
        when(warehouseRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(page.getContent()).thenReturn(List.of());

        WarehouseListGetVm result = warehouseService.getPageableWarehouses(0, 10);

        assertThat(result.warehouseContent()).isEmpty();
    }
    
    @Test
    void getProductWarehouse_ShouldReturnList() {
        when(stockRepository.getProductIdsInWarehouse(1L)).thenReturn(List.of(10L, 11L));
        ProductInfoVm p1 = new ProductInfoVm(10L, "P1", "SKU1", false);
        when(productService.filterProducts(anyString(), anyString(), anyList(), any())).thenReturn(List.of(p1));
        
        List<ProductInfoVm> result = warehouseService.getProductWarehouse(1L, "name", "sku", FilterExistInWhSelection.YES);
        
        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(10L);
    }
}
