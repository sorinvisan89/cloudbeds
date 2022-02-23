package com.cloudbeds.demo.repository;

import com.cloudbeds.demo.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<AddressEntity, Integer> {

    Optional<AddressEntity> findAddressByZip(final String zip);
}
