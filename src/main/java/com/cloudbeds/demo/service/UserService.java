package com.cloudbeds.demo.service;

import com.cloudbeds.demo.entity.UserEntity;
import com.cloudbeds.demo.exception.EmailAlreadyRegisteredException;
import com.cloudbeds.demo.mapper.UserMapper;
import com.cloudbeds.demo.model.CreateUserRequestDTO;
import com.cloudbeds.demo.model.UserDTO;
import com.cloudbeds.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Autowired
    public UserService(final UserMapper userMapper, final UserRepository userRepository) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
    }

    public UserDTO createUser(final CreateUserRequestDTO createUserDTO) {
        final UserEntity toPersist = userMapper.mapRequest(createUserDTO);

        userRepository.findUserByEmail(toPersist.getEmail())
                .ifPresent(registeredUser -> {
                    throw new EmailAlreadyRegisteredException(registeredUser.getEmail());
                });

        final UserEntity saved = userRepository.save(toPersist);

        return userMapper.mapResponse(saved);
    }
}
