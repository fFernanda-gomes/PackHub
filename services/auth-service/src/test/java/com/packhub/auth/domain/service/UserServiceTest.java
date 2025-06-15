package com.packhub.auth.domain.service;

import com.packhub.auth.config.JwtConfig;
import com.packhub.auth.domain.entities.User;
import com.packhub.auth.domain.repositories.UserRepository;
import com.packhub.auth.dto.AuthDTO;
import com.packhub.auth.dto.RegisterDTO;
import com.packhub.auth.dto.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    @DisplayName("Deve retornar todos os usuários")
    void shouldReturnAllUsers() {
        List<User> users = List.of(
                User.builder().id(1L).userCode(123).password("senha").build(),
                User.builder().id(2L).userCode(456).password("senha2").build()
        );

        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals(123, result.get(0).getUserCode());
        assertEquals(456, result.get(1).getUserCode());
    }

    @Test
    @DisplayName("Deve atualizar usuário existente")
    void shouldUpdateExistingUser() {
        RegisterDTO dto = new RegisterDTO(88888, "novaSenha");
        User existingUser = User.builder().id(1L).userCode(12345).password("antigaSenha").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        User updated = userService.updateUser(1L, dto);

        assertEquals(88888, updated.getUserCode());
        assertEquals("novaSenha", updated.getPassword());
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterDTO dto = new RegisterDTO(12345, "senha");

        when(userRepository.findByUserCode(12345)).thenReturn(Optional.empty());
        when(passwordEncoder.encode("senha")).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(1L);
            return u;
        });

        UserDTO result = userService.register(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(12345, result.getUserCode());
    }

    @Test
    @DisplayName("Deve lançar erro quando userCode já estiver em uso")
    void shouldThrowConflictWhenUserCodeAlreadyExists() {
        RegisterDTO dto = new RegisterDTO(12345, "senha");

        when(userRepository.findByUserCode(12345))
                .thenReturn(Optional.of(User.builder()
                        .id(1L)
                        .userCode(12345)
                        .password("senha")
                        .build()));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            userService.register(dto);
        });

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertEquals("Código de usuário já em uso", ex.getReason());
    }

    @Test
    @DisplayName("Deve buscar usuário por ID")
    void shouldFindUserById() {
        User user = User.builder().id(1L).userCode(123).password("senha").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals(123, result.get().getUserCode());
    }

    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void shouldDeleteUserSuccessfully() {
        User user = User.builder().id(1L).userCode(12345).password("senha").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar usuário inexistente")
    void shouldThrowExceptionWhenDeletingNonExistentUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.deleteUser(1L);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Usuário não encontrado", exception.getReason());
    }

    @Test
    @DisplayName("Deve autenticar usuário com sucesso")
    void shouldAuthenticateSuccessfully() {
        User user = User.builder().id(1L).userCode(12345).password("encoded").build();
        AuthDTO authDTO = AuthDTO.builder()
                .userCode(12345)
                .password("senha")
                .build();
        when(userRepository.findByUserCode(12345)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("senha", "encoded")).thenReturn(true);
        when(jwtConfig.generateToken("12345")).thenReturn("token123");

        AuthDTO result = userService.auth(authDTO);

        assertEquals("token123", result.getToken());
    }

    @Test
    @DisplayName("Deve lançar exceção se usuário não for encontrado")
    void shouldThrowWhenUserNotFound() {
        AuthDTO authDTO = AuthDTO.builder()
                .userCode(99999)
                .password("senhaErrada")
                .build();

        when(userRepository.findByUserCode(99999)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            userService.auth(authDTO);
        });

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Usuário não encontrado", ex.getReason());
    }


    @Test
    @DisplayName("Deve lançar exceção se senha estiver incorreta")
    void shouldThrowWhenPasswordIncorrect() {
        User user = User.builder().id(1L).userCode(12345).password("encoded").build();
        AuthDTO authDTO = AuthDTO.builder()
                .userCode(12345)
                .password("senhaErrada")
                .build();

        when(userRepository.findByUserCode(12345)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("senhaErrada", "encoded")).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            userService.auth(authDTO);
        });

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
        assertEquals("Credenciais inválidas", ex.getReason());
    }

}
