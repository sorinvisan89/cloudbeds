package com.cloudbeds.demo.service;

import com.cloudbeds.demo.entity.AddressEntity;
import com.cloudbeds.demo.exception.custom.DuplicateAddressException;
import com.cloudbeds.demo.mapper.CustomMapper;
import com.cloudbeds.demo.mapper.CustomMapperImpl;
import com.cloudbeds.demo.model.request.CreateAddressRequestDTO;
import com.cloudbeds.demo.repository.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class AddressServiceTest {

    private AddressService addressService;

    @Mock
    private AddressRepository addressRepository;

    private final CustomMapper customMapper = new CustomMapperImpl();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        addressService = new AddressService(addressRepository, customMapper);
    }

    @Test
    public void createAddress_whenZipAlreadyExists_shouldReturnError() {
        final String existingZip = "an-existing-zip";

        when(addressRepository.findAddressByZip(existingZip))
                .thenReturn(
                        Optional.of(
                                AddressEntity.builder()
                                        .addressId(400)
                                        .zip(existingZip)
                                        .build()
                        )
                );

        final CreateAddressRequestDTO createAddressRequestDTO = CreateAddressRequestDTO.builder()
                .zip(existingZip)
                .build();

        final DuplicateAddressException exception = assertThrows
                (DuplicateAddressException.class,
                        () -> addressService.createAddress(createAddressRequestDTO)
                );

        assertEquals("An address already exists for this zip code: 'an-existing-zip'!", exception.getMessage());

        verify(addressRepository).findAddressByZip(existingZip);
        verifyNoMoreInteractions(addressRepository);
    }
}
