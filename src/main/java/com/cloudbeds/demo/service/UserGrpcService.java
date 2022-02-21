package com.cloudbeds.demo.service;

import com.cloudbeds.demo.generated.grpc.UserServiceGrpc.UserServiceImplBase;
import com.cloudbeds.demo.generated.grpc.*;
import com.cloudbeds.demo.entity.AddressEntity;
import com.cloudbeds.demo.entity.UserEntity;
import com.cloudbeds.demo.repository.UserRepository;
import io.grpc.Metadata;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserGrpcService extends UserServiceImplBase {

    private final UserRepository userRepository;

    @Autowired
    public UserGrpcService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void retrieveUser(
            final GetUserRequest request,
            final StreamObserver<GetUserResponse> responseObserver) {

        log.info("Request received from client: " + request);

        final int userId = request.getUserId();
        final Optional<UserEntity> existingUser = userRepository.findById(userId);

        if (existingUser.isPresent()) {
            final GetUserResponse response = buildResponse(existingUser.get());
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            final Metadata.Key<ErrorResponse> errorResponseKey = ProtoUtils.keyForProto(ErrorResponse.getDefaultInstance());
            final ErrorResponse errorResponse = ErrorResponse.newBuilder()
                    .build();
            final Metadata metadata = new Metadata();
            metadata.put(errorResponseKey, errorResponse);
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription(String.format("The user with id %d does not exists!", userId))
                    .asRuntimeException(metadata));
        }

    }

    private GetUserResponse buildResponse(final UserEntity entity) {
        final GetUserResponse.Builder builder = GetUserResponse.newBuilder();
        Optional.ofNullable(entity)
                .ifPresent(
                        (foundUser) -> {
                            builder.setFirstName(entity.getFirstName());
                            builder.setLastName(entity.getLastName());
                            builder.setEmail(entity.getEmail());
                            builder.setUserId(entity.getId());
                            builder.setAddresses(buildAddressList(entity.getAddresses()));
                        }
                );
        return builder.build();
    }

    private AddressList buildAddressList(final List<AddressEntity> addressEntityList) {
        final AddressList.Builder builder = AddressList.newBuilder();
        Optional.ofNullable(addressEntityList)
                .orElse(Collections.emptyList())
                .forEach(addressEntity -> builder.addAddress(buildAddress(addressEntity)));
        return builder.build();
    }

    private Address buildAddress(final AddressEntity entity) {
        final Address.Builder builder = Address.newBuilder()
                .setAddressId(entity.getAddressId())
                .setAddressLine1(entity.getAddress1())
                .setCity(entity.getCity())
                .setCountry(entity.getCountry())
                .setState(entity.getState());
        Optional.ofNullable(entity.getAddress2()).ifPresent(builder::setAddressLine2);
        return builder.build();
    }
}
