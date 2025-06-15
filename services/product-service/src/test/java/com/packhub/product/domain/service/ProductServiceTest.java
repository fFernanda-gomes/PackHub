package com.packhub.product.domain.service;

import com.packhub.product.config.AuthenticatedUserProvider;
import com.packhub.product.domain.entities.Product;
import com.packhub.product.domain.repositories.ProductRepository;
import com.packhub.product.dto.CreateProductDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository repository;

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;


    @Test
    void shouldReturnAllProducts() {
        List<Product> mockList = List.of(new Product(), new Product());
        when(repository.findAll()).thenReturn(mockList);

        List<Product> result = productService.getProducts();

        assertEquals(2, result.size());
    }

    @Test
    void shouldCreateProductSuccessfully() {
        MultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[]{1, 2});
        CreateProductDTO dto = new CreateProductDTO("Produto Teste", 99.9, null);
        String mockUrl = "http://image.url/teste.jpg";
        String userCode = "12345";

        when(cloudinaryService.upload(image)).thenReturn(mockUrl);
        when(authenticatedUserProvider.getUserCodeFromToken()).thenReturn(userCode);
        when(repository.save(Mockito.<Product>any())).thenAnswer(i -> i.getArgument(0));

        Product result = productService.createProduct(dto, image);

        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getPrice(), result.getPrice());
        assertEquals(mockUrl, result.getImageUrl());
        assertEquals(userCode, result.getUserCode());
    }

    @Test
    @DisplayName("Deve retornar lista de produtos pelo userCode")
    void shouldReturnProductsByUserCode() {
        String userCode = "abc123";
        List<Product> mockList = List.of(
                Product.builder().id(1L).name("Produto 1").userCode(userCode).build()
        );

        when(repository.findAllByUserCode(userCode)).thenReturn(mockList);

        List<Product> result = productService.getProductsByUserCode(userCode);

        assertEquals(1, result.size());
        assertEquals(userCode, result.get(0).getUserCode());
    }


    @Test
    @DisplayName("Deve retornar lista vazia quando userCode não possui produtos")
    void shouldReturnEmptyListWhenUserHasNoProducts() {
        String userCode = "inexistente";

        when(repository.findAllByUserCode(userCode)).thenReturn(List.of());

        List<Product> result = productService.getProductsByUserCode(userCode);

        assertTrue(result.isEmpty());
    }


    @Test
    @DisplayName("Deve atualizar apenas o nome")
    void shouldUpdateOnlyName() {
        Long id = 1L;
        CreateProductDTO dto = new CreateProductDTO("Novo Nome", null, null);
        Product existing = Product.builder()
                .id(id).name("Antigo").price(10.0).imageUrl("img.jpg").userCode("123")
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(authenticatedUserProvider.getUserCodeFromToken()).thenReturn("123");
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        Product result = productService.updateProduct(id, dto, null);

        assertEquals("Novo Nome", result.getName());
        assertEquals(10.0, result.getPrice());
        assertEquals("img.jpg", result.getImageUrl());
    }

    @Test
    @DisplayName("Deve atualizar apenas o preço")
    void shouldUpdateOnlyPrice() {
        Long id = 1L;
        CreateProductDTO dto = new CreateProductDTO(null, 123.45, null);
        Product existing = Product.builder()
                .id(id).name("Nome").price(10.0).imageUrl("img.jpg").userCode("123")
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(authenticatedUserProvider.getUserCodeFromToken()).thenReturn("123");
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        Product result = productService.updateProduct(id, dto, null);

        assertEquals("Nome", result.getName());
        assertEquals(123.45, result.getPrice());
        assertEquals("img.jpg", result.getImageUrl());
    }

    @Test
    @DisplayName("Deve atualizar apenas a imagem")
    void shouldUpdateOnlyImage() {
        Long id = 1L;
        CreateProductDTO dto = new CreateProductDTO(null, null, null);
        MultipartFile image = mock(MultipartFile.class);
        when(image.isEmpty()).thenReturn(false);
        when(cloudinaryService.upload(image)).thenReturn("nova.jpg");

        Product existing = Product.builder()
                .id(id).name("Nome").price(10.0).imageUrl("old.jpg").userCode("123")
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(image.isEmpty()).thenReturn(false);
        when(cloudinaryService.upload(image)).thenReturn("nova.jpg");
        when(authenticatedUserProvider.getUserCodeFromToken()).thenReturn("123");
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        Product result = productService.updateProduct(id, dto, image);

        assertEquals("nova.jpg", result.getImageUrl());
    }

    @Test
    void shouldUpdateProductWithoutOptionalFields() {
        Long id = 1L;
        CreateProductDTO dto = new CreateProductDTO(null, null, null);
        Product existing = Product.builder()
                .id(id).name("Nome").price(99.9).imageUrl("antiga.jpg").userCode("123")
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(authenticatedUserProvider.getUserCodeFromToken()).thenReturn("123");
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        Product result = productService.updateProduct(id, dto, null);

        assertEquals("Nome", result.getName());
        assertEquals(99.9, result.getPrice());
        assertEquals("antiga.jpg", result.getImageUrl());
    }

    @Test
    void shouldUpdateProductWithoutChangingImageWhenImageIsEmpty() {
        Long id = 1L;
        CreateProductDTO dto = new CreateProductDTO("Nome", 10.0, null);
        MultipartFile emptyImage = new MockMultipartFile("image", "img.jpg", "image/jpeg", new byte[0]);
        Product existing = Product.builder()
                .id(id).name("Antigo").price(20.0).imageUrl("old.jpg").userCode("123")
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(authenticatedUserProvider.getUserCodeFromToken()).thenReturn("123");
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        Product result = productService.updateProduct(id, dto, emptyImage);

        assertEquals("Nome", result.getName());
        assertEquals(10.0, result.getPrice());
        assertEquals("old.jpg", result.getImageUrl());
    }

    @Test
    void shouldThrowWhenUpdatingNonExistentProduct() {
        Long id = 999L;
        CreateProductDTO dto = new CreateProductDTO("Nome", 10.0, null);
        MultipartFile image = new MockMultipartFile("image", new byte[0]);

        when(repository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            productService.updateProduct(id, dto, image);
        });

        assertEquals("Produto não encontrado", ex.getMessage());
    }

    @Test
    void shouldDeleteProductSuccessfully() {
        Long id = 1L;
        Product product = Product.builder().id(id).userCode("123").build();

        when(repository.findById(id)).thenReturn(Optional.of(product));
        when(authenticatedUserProvider.getUserCodeFromToken()).thenReturn("123");

        productService.deleteProduct(id);

        verify(repository).delete(product);
    }

    @Test
    void shouldThrowWhenDeletingNonExistentProduct() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            productService.deleteProduct(999L);
        });

        assertEquals("Produto não encontrado", ex.getMessage());
    }

    @Test
    void shouldThrowWhenDeletingProductFromAnotherUser() {
        Long id = 1L;
        Product product = Product.builder().id(id).userCode("999").build();

        when(repository.findById(id)).thenReturn(Optional.of(product));
        when(authenticatedUserProvider.getUserCodeFromToken()).thenReturn("123");

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> {
            productService.deleteProduct(id);
        });

        assertEquals("Você não tem permissão para excluir este produto.", ex.getMessage());
    }
}
