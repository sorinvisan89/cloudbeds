package com.cloudbeds.demo.repository;

import com.cloudbeds.demo.entity.AddressEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class AddressRepositoryTest {

    @Autowired
    private AddressRepository addressRepository;

    @Test
    public void findUserByZip_whenExistingZip_shouldReturnExpected() {
        final AddressEntity expected = AddressEntity.builder()
                .addressId(678)
                .address1("Sunset boulevard 1000")
                .zip("already_exists")
                .state("California")
                .city("Los Angeles")
                .country("USA")
                .build();

        final AddressEntity saved = addressRepository.save(expected);

        final Optional<AddressEntity> retrieved = addressRepository.findAddressByZip(saved.getZip());
        assertTrue(retrieved.isPresent());
        final AddressEntity actual = retrieved.get();
        assertThat(actual.getZip(), equalTo(expected.getZip()));
    }
}
