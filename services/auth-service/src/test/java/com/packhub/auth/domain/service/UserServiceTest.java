package com.packhub.auth.domain.service;

import com.packhub.auth.config.JwtConfig;
import com.packhub.auth.domain.entities.User;
import com.packhub.auth.domain.repositories.UserRepository;
import com.packhub.auth.dto.RegisterDTO;
import com.packhub.auth.dto.UserDTO;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


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
    void shouldRegisterUserSuccessfully() throws Exception {
        User user = User.builder().id(1L).userCode(12345).password("senha").build();
        RegisterDTO dto = new RegisterDTO(12345, "senha");

        when(userRepository.findByUserCode(12345)).thenReturn(Optional.empty());
        when(passwordEncoder.encode("senha")).thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User u = invocation.getArgument(0);
                    u.setId(1L);
                    return u;
                });

        UserDTO result = userService.register(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(12345, result.getUserCode());

        verify(userRepository).findByUserCode(12345);
        verify(passwordEncoder).encode("senha");
        verify(userRepository).save(Mockito.any(User.class));
    }

    @Test
    @DisplayName("Deve lançar erro quando userCode já estiver em uso")
    void shouldThrowConflictWhenUserCodeAlreadyExists() {
        RegisterDTO dto = new RegisterDTO(12345, "senha");

        when(userRepository.findByUserCode(12345))
                .thenReturn(Optional.of(new User()));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.register(dto)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Código de usuário já em uso", exception.getReason());

        verify(userRepository).findByUserCode(12345);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve buscar usuário por ID")
    void shouldFindUserById() {
        User user = User.builder().id(1L).userCode(123).password("senha").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(1L);

        AssertionErrors.assertTrue(result.isPresent());
        AssertionErrors.assertEquals(123, result.get().getUserCode());
    }


    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void shouldDeleteUserSuccessfully() {
        User user = User.builder().id(1L).userCode(12345).password("senha").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        Mockito.verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar usuário inexistente")
    void shouldThrowExceptionWhenDeletingNonExistentUser() {
        shouldThrowExceptionWhenDeletingNonExistentUser(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.deleteUser(1L);
        });

        AssertionErrors.assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        AssertionErrors.assertEquals("Usuário não encontrado", exception.getReason());
    }
}
