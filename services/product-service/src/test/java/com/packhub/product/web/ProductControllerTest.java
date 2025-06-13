package com.packhub.product.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.packhub.product.domain.entities.Product;
import com.packhub.product.domain.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hibernate.internal.util.ExceptionHelper.doThrow;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
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
    @DisplayName("Deve retornar produtos do usuário por userCode")
    void shouldReturnProductsByUserCode() throws Exception {
        Product product = Product.builder()
                .id(1L)
                .name("Produto do Usuário")
                .price(55.0)
                .imageUrl("imagem.jpg")
                .userCode("abc123")
                .build();

        when(productService.getProductsByUserCode("abc123")).thenReturn(List.of(product));

        mockMvc.perform(get("/products/user/abc123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].userCode").value("abc123"));
    }

    @Test
    @DisplayName("Deve retornar 404 quando nenhum produto for encontrado por userCode")
    void shouldReturnNotFoundWhenUserHasNoProducts() throws Exception {
        when(productService.getProductsByUserCode("nao-tem")).thenReturn(List.of());

        mockMvc.perform(get("/products/user/nao-tem"))
                .andExpect(status().isNotFound());
    }


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
