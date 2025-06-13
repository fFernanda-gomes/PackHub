package com.packhub.product.domain.service;
import com.packhub.product.config.AuthenticatedUserProvider;
import com.packhub.product.domain.entities.Product;
import com.packhub.product.domain.repositories.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class service {
    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository repository;

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

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
