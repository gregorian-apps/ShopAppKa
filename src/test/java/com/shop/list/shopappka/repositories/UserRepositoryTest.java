package com.shop.list.shopappka.repositories;

import com.shop.list.shopappka.models.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.*;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldReturnSavedUserAfterSaveOperation(){
        User user = User.builder()
                .email("dummyemail@gmail.com")
                .firstName("Dummy Name")
                .password("Dummy Password")
                .role("ROLE_USER")
                .build();

        User savedUser = userRepository.save(user);

        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
    }

    @Test
    void shouldReturnUserWhenUserFindsByEmail() {
        User user = User.builder()
                .email("dummyemail@gmail.com")
                .firstName("Dummy Name")
                .password("Dummy Password")
                .role("ROLE_USER")
                .build();
        User savedUser = userRepository.save(user);

        User foundUser = userRepository.findUserByEmail(savedUser.getEmail()).get();

        assertNotNull(foundUser);
    }
}