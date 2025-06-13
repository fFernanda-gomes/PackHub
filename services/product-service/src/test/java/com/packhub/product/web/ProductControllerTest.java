package com.packhub.product.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.packhub.product.domain.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;

import static org.hibernate.internal.util.ExceptionHelper.doThrow;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(ProductController.class)
public class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve deletar produto com sucesso e retornar 204")
    void shouldDeleteProductSuccessfully() throws Exception {
        Long id = 1L;

        doNothing().when(productService).deleteProduct(id);

        mockMvc.perform(delete("/products/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar 403 ao tentar deletar produto de outro usuário")
    void shouldReturnForbiddenWhenDeletingProductFromAnotherUser() throws Exception {
        Long id = 1L;

        doThrow(new AccessDeniedException("Você não tem permissão para excluir este produto."))
                Mockito.when(productService).deleteProduct(id);

        mockMvc.perform(delete("/products/{id}", id))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar deletar produto inexistente")
    void shouldReturnNotFoundWhenDeletingNonExistentProduct() throws Exception {
        Long id = 999L;

        doThrow(new EntityNotFoundException("Produto não encontrado"))
                Mockito.when(productService).deleteProduct(id);

        mockMvc.perform(delete("/products/{id}", id))
                .andExpect(status().isNotFound());
    }
}
