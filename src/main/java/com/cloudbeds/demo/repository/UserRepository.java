package com.cloudbeds.demo.repository;

import com.cloudbeds.demo.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findUserByEmail(final String email);
}
