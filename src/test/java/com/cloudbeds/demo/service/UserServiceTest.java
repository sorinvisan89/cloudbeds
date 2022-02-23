package com.cloudbeds.demo.service;

import com.cloudbeds.demo.entity.AddressEntity;
import com.cloudbeds.demo.entity.UserEntity;
import com.cloudbeds.demo.exception.custom.EmailAlreadyRegisteredException;
import com.cloudbeds.demo.exception.custom.UserNotFoundException;
import com.cloudbeds.demo.mapper.CustomMapper;
import com.cloudbeds.demo.mapper.CustomMapperImpl;
import com.cloudbeds.demo.model.request.AddUserAddressRequestDTO;
import com.cloudbeds.demo.model.request.CreateUserRequestDTO;
import com.cloudbeds.demo.model.response.AddressResponseDTO;
import com.cloudbeds.demo.model.response.UserResponseDTO;
import com.cloudbeds.demo.repository.AddressRepository;
import com.cloudbeds.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AddressRepository addressRepository;

    private final CustomMapper customMapper = new CustomMapperImpl();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(customMapper, userRepository, addressRepository);
    }

    @Test
    public void createUser_whenEmailAlreadyRegistered_shouldReturnError() {
        final String alreadyRegistered = "one_mail@yahoo.com";

        when(userRepository.findUserByEmail(alreadyRegistered))
                .thenReturn(
                        Optional.of(
                                UserEntity.builder()
                                        .id(200)
                                        .email(alreadyRegistered)
                                        .build()
                        )
                );

        final CreateUserRequestDTO createUserRequestDTO = CreateUserRequestDTO.builder()
                .email(alreadyRegistered)
                .build();

        final EmailAlreadyRegisteredException exception = assertThrows
                (EmailAlreadyRegisteredException.class,
                        () -> userService.createUser(createUserRequestDTO)
                );

        assertEquals("Email 'one_mail@yahoo.com' is already registered to another user!", exception.getMessage());


        verify(userRepository).findUserByEmail(alreadyRegistered);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(addressRepository);
    }

    @Test
    public void addAddress_whenUserDoesNotExist_shouldReturnError() {
        final Integer doesNotExist = 111;

        when(userRepository.findById(doesNotExist))
                .thenReturn(Optional.empty());

        final AddUserAddressRequestDTO addUserAddressRequestDTO = AddUserAddressRequestDTO.builder()
                .build();

        final UserNotFoundException exception = assertThrows
                (UserNotFoundException.class,
                        () -> userService.addUserAddress(doesNotExist, addUserAddressRequestDTO)
                );

        assertEquals("User Id '111' not found!", exception.getMessage());

        verify(userRepository).findById(doesNotExist);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(addressRepository);
    }

    @Test
    public void addAddress_whenAddressAlreadyExists_shouldReturnExpected() {
        final Integer userId = 99;
        final Integer addressId = 300;

        final List<AddressEntity> existingAddresses = List.of(
                AddressEntity.builder()
                        .addressId(addressId)
                        .build(),
                AddressEntity.builder()
                        .addressId(300)
                        .build()
        );

        when(userRepository.findById(userId))
                .thenReturn(
                        Optional.of(
                                UserEntity.builder()
                                        .id(userId)
                                        .addresses(existingAddresses)
                                        .build()
                        )
                );

        final AddUserAddressRequestDTO addUserAddressRequestDTO = AddUserAddressRequestDTO.builder()
                .addressId(addressId)
                .build();

        final UserResponseDTO actual = userService.addUserAddress(userId, addUserAddressRequestDTO);

        assertThat(actual.getAddresses(), hasSize(2));

        final List<Integer> actualIds = actual.getAddresses()
                .stream()
                .map(AddressResponseDTO::getAddressId)
                .collect(Collectors.toList());

        final List<Integer> expectedIds = existingAddresses
                .stream()
                .map(AddressEntity::getAddressId)
                .collect(Collectors.toList());

        assertThat(actualIds, containsInAnyOrder(expectedIds.toArray()));

        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(addressRepository);
    }
}
