package com.packhub.auth.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.packhub.auth.domain.entities.User;
import com.packhub.auth.domain.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;

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
    @DisplayName("Deve retornar usuário pelo ID com sucesso")
    void shouldReturnUserById() throws Exception {
        User user = User.builder().id(1L).userCode(123).password("senha").build();

        Mockito.when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userCode").value(123));
    }

    @Test
    @DisplayName("Deve retornar 404 se usuário não for encontrado")
    void shouldReturn404WhenUserNotFound() throws Exception {
        Mockito.when(userService.getUserById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/users/99"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void shouldDeleteUserSuccessfully() throws Exception {
        mockMvc.perform(shouldDeleteUserSuccessfully("/users/1");)
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(userService).deleteUser(1L);
    }

    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void shouldDeleteUserSuccessfully() throws Exception {
        mockMvc.perform(shouldDeleteUserSuccessfully("/users/1");)
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(userService).deleteUser(1L);
    }
}
