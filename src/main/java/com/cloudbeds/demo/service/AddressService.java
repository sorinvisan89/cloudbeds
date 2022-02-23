package com.cloudbeds.demo.service;

import com.cloudbeds.demo.entity.AddressEntity;
import com.cloudbeds.demo.exception.custom.DuplicateAddressException;
import com.cloudbeds.demo.mapper.CustomMapper;
import com.cloudbeds.demo.model.request.CreateAddressRequestDTO;
import com.cloudbeds.demo.model.response.AddressResponseDTO;
import com.cloudbeds.demo.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AddressService {

    private final AddressRepository addressRepository;
    private final CustomMapper customMapper;

    @Autowired
    public AddressService(final AddressRepository addressRepository, final CustomMapper customMapper) {
        this.addressRepository = addressRepository;
        this.customMapper = customMapper;
    }

    public AddressResponseDTO createAddress(final CreateAddressRequestDTO createAddressRequestDTO) {
        final AddressEntity toSave = customMapper.mapRequest(createAddressRequestDTO);

        addressRepository.findAddressByZip(toSave.getZip())
                .ifPresent(existingAddress -> {
                    throw new DuplicateAddressException(existingAddress.getZip());
                });

        final AddressEntity persisted = addressRepository.save(toSave);

        return customMapper.mapResponse(persisted);
    }
}
