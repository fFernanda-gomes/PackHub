package com.packhub.product.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.packhub.product.domain.entities.Product;
import com.packhub.product.domain.service.ProductService;
import com.packhub.product.dto.CreateProductDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    @DisplayName("Deve retornar 200 com lista de produtos")
    void shouldReturnProducts() throws Exception {
        Product product = Product.builder()
                .id(1L)
                .name("Produto 1")
                .price(99.9)
                .imageUrl("http://image.com/img.jpg")
                .userCode("123")
                .build();

        when(productService.getProducts()).thenReturn(List.of(product));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Deve retornar 204 quando não houver produtos")
    void shouldReturnNoContentWhenEmpty() throws Exception {
        when(productService.getProducts()).thenReturn(List.of());

        mockMvc.perform(get("/products"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve criar produto com sucesso")
    void shouldCreateProductSuccessfully() throws Exception {
        String jsonData = """
        {
            "name": "Produto Exemplo",
            "price": 19.99
        }
        """;

        MockMultipartFile image = new MockMultipartFile(
                "image", "imagem.jpg", "image/jpeg", "imagem-falsa".getBytes()
        );

        Product mockProduct = Product.builder()
                .id(1L)
                .name("Produto Exemplo")
                .price(19.99)
                .imageUrl("url")
                .userCode("123")
                .build();

        when(productService.createProduct(any(), any())).thenReturn(mockProduct);

        mockMvc.perform(multipart("/products")
                        .file(image)
                        .param("data", jsonData) // Envia como String
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Produto Exemplo"))
                .andExpect(jsonPath("$.price").value(19.99));
    }

    @Test
    @DisplayName("Deve retornar 400 quando JSON estiver malformado")
    void shouldReturnBadRequestWhenJsonIsMalformed() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "imagem.jpg", "image/jpeg", "fake-image".getBytes());
        MockMultipartFile json = new MockMultipartFile("data", "", "application/json", "{nome:}".getBytes()); // JSON inválido

        mockMvc.perform(multipart("/products")
                        .file(image)
                        .param("data", "{malformado")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

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
    @DisplayName("Deve atualizar produto com sucesso")
    void shouldUpdateProductSuccessfully() throws Exception {
        Long id = 1L;
        String jsonData = """
        {
            "name": "Produto Atualizado",
            "price": 29.99
        }
        """;

        Product updatedProduct = Product.builder()
                .id(id)
                .name("Produto Atualizado")
                .price(29.99)
                .imageUrl("nova-url")
                .userCode("123")
                .build();

        when(productService.updateProduct(eq(id), any(), any())).thenReturn(updatedProduct);

        MockMultipartFile image = new MockMultipartFile("image", "nova-imagem.jpg", "image/jpeg", "imagem".getBytes());

        mockMvc.perform(multipart(HttpMethod.PUT, "/products/{id}", id)
                        .file(image)
                        .param("data", jsonData)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Produto Atualizado"))
                .andExpect(jsonPath("$.price").value(29.99));
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar atualizar produto inexistente")
    void shouldReturnNotFoundWhenUpdatingNonExistentProduct() throws Exception {
        Long id = 999L;
        String jsonData = """
        {
            "name": "Novo Nome",
            "price": 99.99
        }
        """;

        MockMultipartFile image = new MockMultipartFile("image", "imagem.jpg", "image/jpeg", "dados".getBytes());

        when(productService.updateProduct(eq(id), any(), any()))
                .thenThrow(new EntityNotFoundException("Produto não encontrado"));

        mockMvc.perform(multipart(HttpMethod.PUT, "/products/{id}", id)
                        .file(image)
                        .param("data", jsonData)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateWithoutImage() throws Exception {
        CreateProductDTO dto = new CreateProductDTO("Novo nome", 99.0, null);
        String json = objectMapper.writeValueAsString(dto);

        when(productService.updateProduct(eq(1L), any(CreateProductDTO.class), isNull()))
                .thenReturn(new Product());

        mockMvc.perform(multipart("/products/1")
                .file(new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[]{1, 2}))
                .param("data", json) // <== aqui está a diferença
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }));
    }

    @Test
    void shouldReturnBadRequestWhenJsonIsInvalid() throws Exception {
        mockMvc.perform(multipart("/products/1")
                        .file(new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[]{1, 2}))
                        .param("data", "invalido") // ✅ depois do .file(...)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isBadRequest());
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

        Mockito.doThrow(new AccessDeniedException("Você não tem permissão para excluir este produto."))
                .when(productService).deleteProduct(id);

        mockMvc.perform(delete("/products/{id}", id))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar deletar produto inexistente")
    void shouldReturnNotFoundWhenDeletingNonExistentProduct() throws Exception {
        Long id = 999L;

        Mockito.doThrow(new EntityNotFoundException("Produto não encontrado"))
                .when(productService).deleteProduct(id);

        mockMvc.perform(delete("/products/{id}", id))
                .andExpect(status().isNotFound());
    }
}
