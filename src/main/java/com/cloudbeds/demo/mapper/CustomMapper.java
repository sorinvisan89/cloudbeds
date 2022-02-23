package com.cloudbeds.demo.mapper;

import com.cloudbeds.demo.entity.AddressEntity;
import com.cloudbeds.demo.entity.UserEntity;
import com.cloudbeds.demo.model.request.CreateAddressRequestDTO;
import com.cloudbeds.demo.model.request.CreateUserRequestDTO;
import com.cloudbeds.demo.model.response.AddressResponseDTO;
import com.cloudbeds.demo.model.response.UserResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CustomMapper {

    @Mappings({
            @Mapping(target = "addressLine1", source = "address1"),
            @Mapping(target = "addressLine2", source = "address2")
    })
    AddressResponseDTO mapResponse(AddressEntity value);

    @Mappings({
            @Mapping(target = "userId", source = "id")
    })
    UserResponseDTO mapResponse(final UserEntity entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "addresses", ignore = true)
    })
    UserEntity mapRequest(final CreateUserRequestDTO requestDTO);

    @Mappings({
            @Mapping(target = "addressId", ignore = true),
            @Mapping(target = "address1", source = "addressLine1"),
            @Mapping(target = "address2", source = "addressLine2")
    })
    AddressEntity mapRequest(final CreateAddressRequestDTO requestDTO);


}
