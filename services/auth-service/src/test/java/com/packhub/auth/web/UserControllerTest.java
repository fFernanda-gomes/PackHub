package com.packhub.auth.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.packhub.auth.domain.entities.User;
import com.packhub.auth.domain.service.UserService;
import com.packhub.auth.dto.AuthDTO;
import com.packhub.auth.dto.RegisterDTO;
import com.packhub.auth.dto.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve retornar lista de usuários")
    void shouldReturnListOfUsers() throws Exception {
        List<User> users = List.of(
                User.builder().id(1L).userCode(123).password("senha").build(),
                User.builder().id(2L).userCode(456).password("senha2").build()
        );

        Mockito.when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].userCode").value(123))
                .andExpect(jsonPath("$[1].userCode").value(456));
    }

    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void shouldUpdateUserSuccessfully() throws Exception {
        RegisterDTO input = new RegisterDTO(12345, "novaSenha");
        User updatedUser = User.builder().id(1L).userCode(12345).password("novaSenha").build();

        Mockito.when(userService.updateUser(eq(1L), any(RegisterDTO.class)))
                .thenReturn(updatedUser);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userCode").value(12345));
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar atualizar usuário inexistente")
    void shouldReturn404WhenUpdatingNonExistentUser() throws Exception {
        RegisterDTO input = new RegisterDTO(99999, "senha");

        Mockito.when(userService.updateUser(eq(1L), any(RegisterDTO.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve registrar usuário com sucesso")
    void shouldRegisterUser() throws Exception {
        RegisterDTO input = new RegisterDTO(12345, "senha");
        UserDTO output = new UserDTO(1L, 12345);

        Mockito.when(userService.register(any(RegisterDTO.class))).thenReturn(output);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userCode").value(12345));
    }

    @Test
    @DisplayName("Deve retornar 409 quando userCode já estiver em uso")
    void shouldReturnConflictWhenUserCodeExists() throws Exception {
        RegisterDTO dto = new RegisterDTO(12345, "senha");

        Mockito.when(userService.register(any(RegisterDTO.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Código de usuário já em uso"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Deve retornar usuário pelo ID com sucesso")
    void shouldReturnUserById() throws Exception {
        User user = User.builder().id(1L).userCode(123).password("senha").build();

        Mockito.when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userCode").value(123));
    }

    @Test
    @DisplayName("Deve retornar 404 se usuário não for encontrado")
    void shouldReturn404WhenUserNotFound() throws Exception {
        Mockito.when(userService.getUserById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void shouldDeleteUserSuccessfully() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(userService).deleteUser(1L);
    }

    @Test
    @DisplayName("Deve autenticar usuário com sucesso via controller")
    void shouldAuthenticateUserSuccessfully() throws Exception {
        AuthDTO input = AuthDTO.builder()
                .userCode(12345)
                .password("senha")
                .build();

        AuthDTO output = AuthDTO.builder()
                .id(1L)
                .userCode(12345)
                .token("token123")
                .build();

        Mockito.when(userService.auth(any(AuthDTO.class))).thenReturn(output);

        mockMvc.perform(post("/users/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token123"))
                .andExpect(jsonPath("$.userCode").value(12345));
    }

    @Test
    @DisplayName("Deve retornar 401 quando credenciais forem inválidas via controller")
    void shouldReturn401OnInvalidCredentials() throws Exception {
        AuthDTO input = AuthDTO.builder()
                .userCode(12345)
                .password("senhaIncorreta")
                .build();

        Mockito.when(userService.auth(any(AuthDTO.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas"));

        mockMvc.perform(post("/users/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Credenciais inválidas"));
    }
}
