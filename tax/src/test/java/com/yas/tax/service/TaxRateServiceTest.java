package com.yas.tax.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.tax.model.TaxClass;
import com.yas.tax.model.TaxRate;
import com.yas.tax.repository.TaxClassRepository;
import com.yas.tax.repository.TaxRateRepository;
import com.yas.tax.viewmodel.location.StateOrProvinceAndCountryGetNameVm;
import com.yas.tax.viewmodel.taxrate.TaxRateListGetVm;
import com.yas.tax.viewmodel.taxrate.TaxRatePostVm;
import com.yas.tax.viewmodel.taxrate.TaxRateVm;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

class TaxRateServiceTest {

    private TaxRateRepository taxRateRepository;
    private TaxClassRepository taxClassRepository;
    private LocationService locationService;
    private TaxRateService taxRateService;

    @BeforeEach
    void setUp() {
        taxRateRepository = mock(TaxRateRepository.class);
        taxClassRepository = mock(TaxClassRepository.class);
        locationService = mock(LocationService.class);
        taxRateService = new TaxRateService(locationService, taxRateRepository, taxClassRepository);
    }

    @Test
    void createTaxRate_WhenTaxClassExists_ShouldSave() {
        TaxRatePostVm postVm = new TaxRatePostVm(10.0, "12345", 1L, 1L, 1L);
        when(taxClassRepository.existsById(1L)).thenReturn(true);
        TaxClass taxClass = new TaxClass();
        when(taxClassRepository.getReferenceById(1L)).thenReturn(taxClass);
        TaxRate taxRate = TaxRate.builder().id(1L).rate(10.0).build();
        when(taxRateRepository.save(any(TaxRate.class))).thenReturn(taxRate);

        TaxRate result = taxRateService.createTaxRate(postVm);

        assertThat(result.getRate()).isEqualTo(10.0);
        verify(taxRateRepository).save(any(TaxRate.class));
    }

    @Test
    void createTaxRate_WhenTaxClassNotExists_ShouldThrowNotFoundException() {
        TaxRatePostVm postVm = new TaxRatePostVm(10.0, "12345", 1L, 1L, 1L);
        when(taxClassRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> taxRateService.createTaxRate(postVm));
    }

    @Test
    void updateTaxRate_WhenExists_ShouldSave() {
        TaxRatePostVm postVm = new TaxRatePostVm(15.0, "54321", 1L, 1L, 1L);
        TaxRate taxRate = TaxRate.builder().id(1L).rate(10.0).build();
        when(taxRateRepository.findById(1L)).thenReturn(Optional.of(taxRate));
        when(taxClassRepository.existsById(1L)).thenReturn(true);

        taxRateService.updateTaxRate(postVm, 1L);

        assertThat(taxRate.getRate()).isEqualTo(15.0);
        verify(taxRateRepository).save(taxRate);
    }

    @Test
    void delete_WhenExists_ShouldDelete() {
        when(taxRateRepository.existsById(1L)).thenReturn(true);

        taxRateService.delete(1L);

        verify(taxRateRepository).deleteById(1L);
    }

    @Test
    void findById_WhenExists_ShouldReturnVm() {
        TaxClass taxClass = new TaxClass();
        taxClass.setName("Test Class");
        TaxRate taxRate = TaxRate.builder().id(1L).rate(10.0).taxClass(taxClass).build();
        when(taxRateRepository.findById(1L)).thenReturn(Optional.of(taxRate));

        TaxRateVm result = taxRateService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.rate()).isEqualTo(10.0);
    }

    @Test
    void getPageableTaxRates_ShouldReturnListGetVm() {
        Page<TaxRate> page = mock(Page.class);
        TaxClass taxClass = new TaxClass();
        taxClass.setName("Test Class");
        TaxRate taxRate = TaxRate.builder().id(1L).rate(10.0).taxClass(taxClass).stateOrProvinceId(1L).build();
        when(page.getContent()).thenReturn(List.of(taxRate));
        when(page.getTotalElements()).thenReturn(1L);
        when(taxRateRepository.findAll(any(Pageable.class))).thenReturn(page);
        
        StateOrProvinceAndCountryGetNameVm nameVm = new StateOrProvinceAndCountryGetNameVm(1L, "State", "Country");
        when(locationService.getStateOrProvinceAndCountryNames(anyList())).thenReturn(List.of(nameVm));

        TaxRateListGetVm result = taxRateService.getPageableTaxRates(0, 10);

        assertThat(result.taxRateGetDetailContent()).hasSize(1);
        assertThat(result.taxRateGetDetailContent().get(0).stateOrProvinceName()).isEqualTo("State");
    }

    @Test
    void getTaxPercent_ShouldReturnRate() {
        when(taxRateRepository.getTaxPercent(1L, 1L, "12345", 1L)).thenReturn(10.0);

        double result = taxRateService.getTaxPercent(1L, 1L, 1L, "12345");

        assertThat(result).isEqualTo(10.0);
    }

    @Test
    void getTaxPercent_WhenNull_ShouldReturnZero() {
        when(taxRateRepository.getTaxPercent(1L, 1L, "12345", 1L)).thenReturn(null);

        double result = taxRateService.getTaxPercent(1L, 1L, 1L, "12345");

        assertThat(result).isEqualTo(0.0);
    }

    @Test
    void getBulkTaxRate_ShouldReturnList() {
        TaxClass taxClass = new TaxClass();
        taxClass.setName("Class");
        TaxRate taxRate = TaxRate.builder().id(1L).taxClass(taxClass).build();
        when(taxRateRepository.getBatchTaxRates(any(), any(), any(), any())).thenReturn(List.of(taxRate));

        List<TaxRateVm> result = taxRateService.getBulkTaxRate(List.of(1L), 1L, 1L, "12345");

        assertThat(result).hasSize(1);
    }
}
