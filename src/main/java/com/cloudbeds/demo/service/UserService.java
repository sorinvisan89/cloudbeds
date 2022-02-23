package com.cloudbeds.demo.service;

import com.cloudbeds.demo.entity.AddressEntity;
import com.cloudbeds.demo.entity.UserEntity;
import com.cloudbeds.demo.exception.custom.AddressNotFoundException;
import com.cloudbeds.demo.exception.custom.EmailAlreadyRegisteredException;
import com.cloudbeds.demo.exception.custom.UserNotFoundException;
import com.cloudbeds.demo.mapper.CustomMapper;
import com.cloudbeds.demo.model.request.AddUserAddressRequestDTO;
import com.cloudbeds.demo.model.request.CreateUserRequestDTO;
import com.cloudbeds.demo.model.response.UserResponseDTO;
import com.cloudbeds.demo.repository.AddressRepository;
import com.cloudbeds.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final CustomMapper customMapper;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    @Autowired
    public UserService(final CustomMapper customMapper, final UserRepository userRepository, final AddressRepository addressRepository) {
        this.customMapper = customMapper;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }

    public UserResponseDTO createUser(final CreateUserRequestDTO createUserDTO) {
        final UserEntity toPersist = customMapper.mapRequest(createUserDTO);

        userRepository.findUserByEmail(toPersist.getEmail())
                .ifPresent(registeredUser -> {
                    throw new EmailAlreadyRegisteredException(registeredUser.getEmail());
                });

        final UserEntity saved = userRepository.save(toPersist);

        return customMapper.mapResponse(saved);
    }

    public UserResponseDTO addUserAddress(final Integer userId, final AddUserAddressRequestDTO userAddressRequestDTO) {
        final UserEntity existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        final List<Integer> existingAddresses = existingUser.getAddresses()
                .stream()
                .map(AddressEntity::getAddressId)
                .collect(Collectors.toList());

        final Integer addressIdToAdd = userAddressRequestDTO.getAddressId();

        if (existingAddresses.contains(addressIdToAdd)) {
            return customMapper.mapResponse(existingUser);
        }

        final AddressEntity existingAddress = addressRepository.findById(addressIdToAdd)
                .orElseThrow(() -> new AddressNotFoundException(addressIdToAdd));

        existingUser.getAddresses().add(existingAddress);
        final UserEntity saved = userRepository.save(existingUser);

        return customMapper.mapResponse(saved);
    }

    public List<UserResponseDTO> getUsersByCountry(final String country, final int page, final int limit) {
        final PageRequest pageRequest = PageRequest.of(Math.max(page, 0), limit);
        final List<UserEntity> results = userRepository.findUsersByCountry(country, pageRequest);

        return results.stream()
                .map(customMapper::mapResponse)
                .collect(Collectors.toList());
    }

}
