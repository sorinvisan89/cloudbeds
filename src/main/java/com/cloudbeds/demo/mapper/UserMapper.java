package com.cloudbeds.demo.mapper;

import com.cloudbeds.demo.entity.UserEntity;
import com.cloudbeds.demo.model.CreateUserRequestDTO;
import com.cloudbeds.demo.model.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserMapper {

    @Mappings({
            @Mapping(target = "userId", source = "id")
    })
    UserDTO mapResponse(final UserEntity entity);

    @Mappings({
            @Mapping(target = "id", ignore = true)
    })
    UserEntity mapRequest(final CreateUserRequestDTO requestDTO);
}
