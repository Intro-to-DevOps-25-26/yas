package com.yas.customer.service;

import com.yas.commonlibrary.exception.AccessDeniedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.customer.model.UserAddress;
import com.yas.customer.repository.UserAddressRepository;
import com.yas.customer.util.SecurityContextUtils;
import com.yas.customer.viewmodel.address.ActiveAddressVm;
import com.yas.customer.viewmodel.address.AddressDetailVm;
import com.yas.customer.viewmodel.address.AddressPostVm;
import com.yas.customer.viewmodel.address.AddressVm;
import com.yas.customer.viewmodel.useraddress.UserAddressVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserAddressServiceTest {

    private UserAddressRepository userAddressRepository;
    private LocationService locationService;
    private UserAddressService userAddressService;

    private static final String USER_ID = "test-user";
    private static final Long ADDRESS_ID_1 = 1L;
    private static final Long ADDRESS_ID_2 = 2L;

    @BeforeEach
    void setUp() {
        userAddressRepository = mock(UserAddressRepository.class);
        locationService = mock(LocationService.class);
        userAddressService = new UserAddressService(userAddressRepository, locationService);
        SecurityContextUtils.setUpSecurityContext(USER_ID);
    }

    @Test
    void testGetUserAddressList_whenNormalCase_returnsSortedByActive() {
        UserAddress ua1 = UserAddress.builder().id(1L).userId(USER_ID).addressId(ADDRESS_ID_1).isActive(false).build();
        UserAddress ua2 = UserAddress.builder().id(2L).userId(USER_ID).addressId(ADDRESS_ID_2).isActive(true).build();
        List<UserAddress> userAddresses = List.of(ua1, ua2);

        AddressDetailVm addr1 = AddressDetailVm.builder().id(ADDRESS_ID_1).contactName("John").phone("123").addressLine1("Line1").city("City").zipCode("12345").districtId(1L).districtName("D1").stateOrProvinceId(1L).stateOrProvinceName("S1").countryId(1L).countryName("C1").build();
        AddressDetailVm addr2 = AddressDetailVm.builder().id(ADDRESS_ID_2).contactName("Jane").phone("456").addressLine1("Line2").city("City2").zipCode("67890").districtId(2L).districtName("D2").stateOrProvinceId(2L).stateOrProvinceName("S2").countryId(2L).countryName("C2").build();

        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(userAddresses);
        when(locationService.getAddressesByIdList(List.of(ADDRESS_ID_1, ADDRESS_ID_2))).thenReturn(List.of(addr1, addr2));

        List<ActiveAddressVm> result = userAddressService.getUserAddressList();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).isActive()).isTrue();
        assertThat(result.get(1).isActive()).isFalse();
    }

    @Test
    void testGetUserAddressList_whenAnonymousUser_throwsAccessDeniedException() {
        SecurityContextHolder.clearContext();
        SecurityContextUtils.setUpSecurityContext("anonymousUser");

        assertThrows(AccessDeniedException.class, () -> userAddressService.getUserAddressList());
    }

    @Test
    void testGetAddressDefault_whenNormalCase_returnsAddressDetailVm() {
        UserAddress ua = UserAddress.builder().id(1L).userId(USER_ID).addressId(ADDRESS_ID_1).isActive(true).build();
        AddressDetailVm addressDetailVm = AddressDetailVm.builder().id(ADDRESS_ID_1).contactName("John").build();

        when(userAddressRepository.findByUserIdAndIsActiveTrue(USER_ID)).thenReturn(Optional.of(ua));
        when(locationService.getAddressById(ADDRESS_ID_1)).thenReturn(addressDetailVm);

        AddressDetailVm result = userAddressService.getAddressDefault();

        assertThat(result.id()).isEqualTo(ADDRESS_ID_1);
    }

    @Test
    void testGetAddressDefault_whenNotFound_throwsNotFoundException() {
        when(userAddressRepository.findByUserIdAndIsActiveTrue(USER_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userAddressService.getAddressDefault());
    }

    @Test
    void testCreateAddress_whenFirstAddress_isActiveTrue() {
        AddressPostVm postVm = new AddressPostVm("John", "123", "Line1", "City", "12345", 1L, 1L, 1L);
        AddressVm addressVm = AddressVm.builder().id(ADDRESS_ID_1).contactName("John").build();
        UserAddress saved = UserAddress.builder().id(10L).userId(USER_ID).addressId(ADDRESS_ID_1).isActive(true).build();

        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(List.of());
        when(locationService.createAddress(postVm)).thenReturn(addressVm);
        when(userAddressRepository.save(any(UserAddress.class))).thenReturn(saved);

        UserAddressVm result = userAddressService.createAddress(postVm);

        assertThat(result.userId()).isEqualTo(USER_ID);
        assertThat(result.isActive()).isTrue();

        ArgumentCaptor<UserAddress> captor = ArgumentCaptor.forClass(UserAddress.class);
        verify(userAddressRepository).save(captor.capture());
        assertThat(captor.getValue().getIsActive()).isTrue();
    }

    @Test
    void testCreateAddress_whenNotFirstAddress_isActiveFalse() {
        AddressPostVm postVm = new AddressPostVm("John", "123", "Line1", "City", "12345", 1L, 1L, 1L);
        AddressVm addressVm = AddressVm.builder().id(ADDRESS_ID_1).contactName("John").build();
        UserAddress existing = UserAddress.builder().id(1L).userId(USER_ID).addressId(ADDRESS_ID_2).isActive(true).build();
        UserAddress saved = UserAddress.builder().id(20L).userId(USER_ID).addressId(ADDRESS_ID_1).isActive(false).build();

        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(List.of(existing));
        when(locationService.createAddress(postVm)).thenReturn(addressVm);
        when(userAddressRepository.save(any(UserAddress.class))).thenReturn(saved);

        UserAddressVm result = userAddressService.createAddress(postVm);

        assertThat(result.isActive()).isFalse();
    }

    @Test
    void testDeleteAddress_whenNormalCase_deletesSuccessfully() {
        UserAddress ua = UserAddress.builder().id(1L).userId(USER_ID).addressId(ADDRESS_ID_1).build();
        when(userAddressRepository.findOneByUserIdAndAddressId(USER_ID, ADDRESS_ID_1)).thenReturn(ua);

        userAddressService.deleteAddress(ADDRESS_ID_1);

        verify(userAddressRepository).delete(ua);
    }

    @Test
    void testDeleteAddress_whenNotFound_throwsNotFoundException() {
        when(userAddressRepository.findOneByUserIdAndAddressId(USER_ID, ADDRESS_ID_1)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> userAddressService.deleteAddress(ADDRESS_ID_1));
    }

    @Test
    void testChooseDefaultAddress_whenNormalCase_updatesActiveStatus() {
        UserAddress ua1 = UserAddress.builder().id(1L).userId(USER_ID).addressId(ADDRESS_ID_1).isActive(false).build();
        UserAddress ua2 = UserAddress.builder().id(2L).userId(USER_ID).addressId(ADDRESS_ID_2).isActive(true).build();
        List<UserAddress> userAddresses = new ArrayList<>(List.of(ua1, ua2));

        when(userAddressRepository.findAllByUserId(USER_ID)).thenReturn(userAddresses);

        userAddressService.chooseDefaultAddress(ADDRESS_ID_1);

        assertThat(ua1.getIsActive()).isTrue();
        assertThat(ua2.getIsActive()).isFalse();
        verify(userAddressRepository).saveAll(userAddresses);
    }
}
