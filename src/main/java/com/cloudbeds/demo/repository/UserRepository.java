package com.cloudbeds.demo.repository;

import com.cloudbeds.demo.entity.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findUserByEmail(final String email);

    @Query("SELECT DISTINCT u FROM UserEntity u INNER JOIN u.addresses ad WHERE ad.country = :country")
    List<UserEntity> findUsersByCountry(final @Param("country") String country, final Pageable pageable);
}
