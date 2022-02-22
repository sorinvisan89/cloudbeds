package com.cloudbeds.demo.repository;

import com.cloudbeds.demo.entity.AddressEntity;
import com.cloudbeds.demo.entity.UserEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Test
    public void findUserByEmail_whenExistingUser_shouldReturnExpected() {
        final UserEntity expected = UserEntity.builder()
                .lastName("visan")
                .firstName("sorin")
                .email("my_email@yahoo.com")
                .password("a-password-123")
                .build();

        final UserEntity saved = userRepository.save(expected);

        final Optional<UserEntity> retrieved = userRepository.findUserByEmail(saved.getEmail());
        assertTrue(retrieved.isPresent());
        final UserEntity actual = retrieved.get();
        assertThat(actual.getEmail(), equalTo(expected.getEmail()));
    }

    @Test
    public void findUsersByCountry_whenMultipleUsers_shouldReturnExpected() {
        final UserEntity userEntity1 = UserEntity.builder()
                .lastName("family1")
                .firstName("user1")
                .email("my_email_1@yahoo.com")
                .password("a-password-123")
                .build();

        final UserEntity userEntity2 = UserEntity.builder()
                .lastName("family2")
                .firstName("user2")
                .email("my_email_2@yahoo.com")
                .password("a-password-234")
                .build();

        final UserEntity savedUser1 = userRepository.save(userEntity1);
        final UserEntity savedUser2 = userRepository.save(userEntity2);

        final AddressEntity address1 = AddressEntity.builder()
                .address1("line 1")
                .country("Romania")
                .city("Bucharest")
                .state("Bucharest")
                .zip("300-200")
                .build();

        final AddressEntity address2 = AddressEntity.builder()
                .address1("line 1")
                .address2("line 2")
                .country("USA")
                .city("New York")
                .state("NY")
                .zip("400-200")
                .build();

        final AddressEntity savedAddress1 = addressRepository.save(address1);
        final AddressEntity savedAddress2 = addressRepository.save(address2);

        savedUser1.setAddresses(Collections.singletonList(savedAddress1));
        savedUser2.setAddresses(Collections.singletonList(savedAddress2));

        userRepository.save(userEntity1);
        userRepository.save(userEntity2);

        final List<UserEntity> foundUsers = userRepository.findUsersByCountry("USA", PageRequest.of(0, 10));
        assertThat(foundUsers, hasSize(1));
        assertThat(foundUsers.get(0).getFirstName(), equalTo("user2"));
        assertThat(foundUsers.get(0).getLastName(), equalTo("family2"));
    }
}
