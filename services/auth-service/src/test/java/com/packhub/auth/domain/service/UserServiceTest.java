package com.packhub.auth.domain.service;

import com.packhub.auth.config.JwtConfig;
import com.packhub.auth.domain.entities.User;
import com.packhub.auth.domain.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.AssertionErrors;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;


@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtConfig jwtConfig;

    @InjectMocks
    private UserService userService;


    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void shouldDeleteUserSuccessfully() {
        User user = User.builder().id(1L).userCode(12345).password("senha").build();

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        Mockito.verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar usuário inexistente")
    void shouldThrowExceptionWhenDeletingNonExistentUser() {
        shouldThrowExceptionWhenDeletingNonExistentUser(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            userService.deleteUser(1L);
        });

        AssertionErrors.assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        AssertionErrors.assertEquals("Usuário não encontrado", exception.getReason());
    }
}
