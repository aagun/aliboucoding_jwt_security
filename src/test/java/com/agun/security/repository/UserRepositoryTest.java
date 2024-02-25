package com.agun.security.repository;

import com.agun.security.enums.Role;
import com.agun.security.model.User;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.jpa.properties.jakarta.persistence.validation.mode=none")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class UserRepositoryTest {

    private final UserRepository underTest;

    User user = User.builder()
            .firstName("John")
            .lastName("Doe")
            .email("test@example.com")
            .role(Role.USER)
            .build();

    @BeforeEach
    void beforeEach() {
        underTest.save(user);
    }

    @Test
    void itShouldFindUserByEmail() {
        // Given
        // When
        // Then
        Optional<User> optionalUser = underTest.findByEmail(user.getEmail());
        assertThat(optionalUser)
                .isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c)
                            .usingRecursiveComparison()
                            .ignoringFields("id")
                            .isEqualTo(user);
                });
    }

    @Test
    void itShouldCheckEmailIfExist() {
        // Given
        // When
        boolean existingUser = underTest.existsByEmail("test@example.com");
        boolean notExistingUser = underTest.existsByEmail("agun@mail.com");

        // Then
        assertThat(existingUser).isTrue();
        assertThat(notExistingUser).isFalse();
    }
}