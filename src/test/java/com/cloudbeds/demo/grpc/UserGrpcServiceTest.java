package com.cloudbeds.demo.grpc;

import com.cloudbeds.demo.generated.grpc.Address;
import com.cloudbeds.demo.generated.grpc.GetUserRequest;
import com.cloudbeds.demo.generated.grpc.GetUserResponse;
import com.cloudbeds.demo.generated.grpc.UserServiceGrpc;
import com.cloudbeds.demo.entity.AddressEntity;
import com.cloudbeds.demo.entity.UserEntity;
import com.cloudbeds.demo.repository.UserRepository;
import com.cloudbeds.demo.service.UserGrpcService;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserGrpcServiceTest {

    private UserServiceGrpc.UserServiceBlockingStub blockingStub;

    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setup() throws Exception {

        MockitoAnnotations.openMocks(this);

        final String serverName = InProcessServerBuilder.generateName();

        grpcCleanup.register(InProcessServerBuilder.forName(serverName)
                .directExecutor()
                .addService(new UserGrpcService(userRepository))
                .build()
                .start());

        blockingStub = UserServiceGrpc.newBlockingStub(grpcCleanup.register(InProcessChannelBuilder.forName(serverName)
                .directExecutor()
                .build()));
    }

    @Test
    public void retrieveUser_whenUserIdExist_shouldReturnExpected() {

        final int userId = 1;

        final UserEntity expected = UserEntity.builder()
                .id(userId)
                .firstName("John")
                .lastName("Snow")
                .email("john_snow@yahoo.com")
                .password("-----==encoded==-----")
                .addresses(Arrays.asList(
                        AddressEntity.builder()
                                .address1("Address 1 Line 1")
                                .state("New York")
                                .country("USA")
                                .city("New York")
                                .addressId(100)
                                .build(),
                        AddressEntity.builder()
                                .address1("Address 2 Line 1")
                                .address2("Address 2 Line 2")
                                .state("Georgia")
                                .country("USA")
                                .city("Atlanta")
                                .addressId(200)
                                .build()
                ))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(expected));

        final GetUserResponse actual = blockingStub.retrieveUser(GetUserRequest.newBuilder()
                .setUserId(userId)
                .build());

        assertEquals(expected.getId(), actual.getUserId());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertNotNull(actual.getAddresses());
        assertAddress(actual.getAddresses().getAddress(0), expected.getAddresses().get(0));
        assertAddress(actual.getAddresses().getAddress(1), expected.getAddresses().get(1));

        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void retrieveUser_whenUserDoesNotExist_shouldThrowException() {

        final StatusRuntimeException exception = assertThrows(
                StatusRuntimeException.class,
                () -> blockingStub.retrieveUser(GetUserRequest.newBuilder().setUserId(4).build())
        );

        assertEquals("INVALID_ARGUMENT: The user with id 4 does not exists!", exception.getMessage());
    }


    private void assertAddress(final Address actual, final AddressEntity expected) {
        assertThat(actual.getAddressId(), equalTo(expected.getAddressId()));
        assertThat(actual.getAddressLine1(), equalTo(expected.getAddress1()));
        assertThat(actual.getAddressLine2(), equalTo(Optional.ofNullable(expected.getAddress2()).orElse("")));
        assertThat(actual.getCity(), equalTo(expected.getCity()));
        assertThat(actual.getCountry(), equalTo(expected.getCountry()));
        assertThat(actual.getState(), equalTo(expected.getState()));
    }
}
