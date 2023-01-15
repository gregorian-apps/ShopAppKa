package com.shop.list.shopappka.repositories;

import com.shop.list.shopappka.models.domain.User;
import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@Profile("test")
@ContextConfiguration(initializers = {UserRepositoryTest.Initializer.class})
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @ClassRule
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11-alpine")
            .withDatabaseName("integration-tests-db")
            .withUsername("sa")
            .withPassword("sa");

    static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            postgreSQLContainer.start();
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword(),
                    "spring.jpa.hibernate.ddl-auto=create-drop"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

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
        User foundUser = userRepository.findUserByEmail(user.getEmail()).get();

        assertNotNull(foundUser);
    }
}