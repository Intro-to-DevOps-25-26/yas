package com.yas.tax.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.tax.model.TaxClass;
import com.yas.tax.repository.TaxClassRepository;
import com.yas.tax.viewmodel.taxclass.TaxClassListGetVm;
import com.yas.tax.viewmodel.taxclass.TaxClassPostVm;
import com.yas.tax.viewmodel.taxclass.TaxClassVm;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

class TaxClassServiceTest {

    private TaxClassRepository taxClassRepository;
    private TaxClassService taxClassService;

    @BeforeEach
    void setUp() {
        taxClassRepository = mock(TaxClassRepository.class);
        taxClassService = new TaxClassService(taxClassRepository);
    }

    @Test
    void findAllTaxClasses_ShouldReturnList() {
        TaxClass taxClass = new TaxClass();
        taxClass.setId(1L);
        taxClass.setName("Test Tax Class");
        when(taxClassRepository.findAll(any(Sort.class))).thenReturn(List.of(taxClass));

        List<TaxClassVm> result = taxClassService.findAllTaxClasses();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Test Tax Class");
    }

    @Test
    void findById_WhenExists_ShouldReturnVm() {
        TaxClass taxClass = new TaxClass();
        taxClass.setId(1L);
        taxClass.setName("Test Tax Class");
        when(taxClassRepository.findById(1L)).thenReturn(Optional.of(taxClass));

        TaxClassVm result = taxClassService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Test Tax Class");
    }

    @Test
    void findById_WhenNotExists_ShouldThrowNotFoundException() {
        when(taxClassRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taxClassService.findById(1L));
    }

    @Test
    void create_WhenNameNotDuplicated_ShouldSave() {
        TaxClassPostVm postVm = new TaxClassPostVm("id", "New Tax Class");
        when(taxClassRepository.existsByName("New Tax Class")).thenReturn(false);
        TaxClass taxClass = new TaxClass();
        taxClass.setId(1L);
        taxClass.setName("New Tax Class");
        when(taxClassRepository.save(any(TaxClass.class))).thenReturn(taxClass);

        TaxClass result = taxClassService.create(postVm);

        assertThat(result.getName()).isEqualTo("New Tax Class");
        verify(taxClassRepository).save(any(TaxClass.class));
    }

    @Test
    void create_WhenNameDuplicated_ShouldThrowDuplicatedException() {
        TaxClassPostVm postVm = new TaxClassPostVm("id", "Existing Tax Class");
        when(taxClassRepository.existsByName("Existing Tax Class")).thenReturn(true);

        assertThrows(DuplicatedException.class, () -> taxClassService.create(postVm));
    }

    @Test
    void update_WhenExistsAndNameNotDuplicated_ShouldSave() {
        TaxClassPostVm postVm = new TaxClassPostVm("id", "Updated Tax Class");
        TaxClass taxClass = new TaxClass();
        taxClass.setId(1L);
        taxClass.setName("Old Name");
        when(taxClassRepository.findById(1L)).thenReturn(Optional.of(taxClass));
        when(taxClassRepository.existsByNameNotUpdatingTaxClass("Updated Tax Class", 1L)).thenReturn(false);

        taxClassService.update(postVm, 1L);

        assertThat(taxClass.getName()).isEqualTo("Updated Tax Class");
        verify(taxClassRepository).save(taxClass);
    }

    @Test
    void delete_WhenExists_ShouldDelete() {
        when(taxClassRepository.existsById(1L)).thenReturn(true);

        taxClassService.delete(1L);

        verify(taxClassRepository).deleteById(1L);
    }

    @Test
    void delete_WhenNotExists_ShouldThrowNotFoundException() {
        when(taxClassRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> taxClassService.delete(1L));
    }

    @Test
    void getPageableTaxClasses_ShouldReturnListGetVm() {
        Page<TaxClass> page = mock(Page.class);
        TaxClass taxClass = new TaxClass();
        taxClass.setId(1L);
        taxClass.setName("Test");
        when(page.getContent()).thenReturn(List.of(taxClass));
        when(page.getNumber()).thenReturn(0);
        when(page.getSize()).thenReturn(10);
        when(page.getTotalElements()).thenReturn(1L);
        when(page.getTotalPages()).thenReturn(1);
        when(page.isLast()).thenReturn(true);
        when(taxClassRepository.findAll(any(Pageable.class))).thenReturn(page);

        TaxClassListGetVm result = taxClassService.getPageableTaxClasses(0, 10);

        assertThat(result.taxClassContent()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1);
    }
}
