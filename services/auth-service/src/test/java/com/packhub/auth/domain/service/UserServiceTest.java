package com.packhub.auth.domain.service;

import com.packhub.auth.config.JwtConfig;
import com.packhub.auth.domain.entities.User;
import com.packhub.auth.domain.repositories.UserRepository;
import com.packhub.auth.dto.AuthDTO;
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
import org.springframework.test.web.servlet.htmlunit.webdriver.MockMvcHtmlUnitDriverBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
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

        verify(userRepository).findAll();
    }



    @Test
    @DisplayName("Deve atualizar usuário existente")
    void shouldUpdateExistingUser() {
        RegisterDTO dto = new RegisterDTO(88888, "novaSenha");

        User existingUser = User.builder().id(1L).userCode(12345).password("antigaSenha").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User updated = userService.updateUser(1L, dto);

        assertEquals(88888, updated.getUserCode());
        assertEquals("novaSenha", updated.getPassword());
    }

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
    @DisplayName("Deve autenticar usuário com sucesso")
    void shouldAuthenticateUser() throws Exception {
        AuthDTO input = new AuthDTO(12345, "senha", null, null);
        AuthDTO output = new AuthDTO(12345, "senha", 1L, "token-falso");

        Mockito.when(userService.auth(any(AuthDTO.class))).thenReturn(output);

        MockMvcHtmlUnitDriverBuilder.mockMvcSetup().perform(userRepository("/users/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value("token-falso"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userCode").value(12345));
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
