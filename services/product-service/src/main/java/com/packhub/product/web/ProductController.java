package com.packhub.product.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.packhub.product.domain.entities.Product;
import com.packhub.product.domain.service.ProductService;
import com.packhub.product.dto.CreateProductDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private ObjectMapper objectMapper;

    @Operation(summary = "Criar novo produto com imagem")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> create(
            @RequestParam("image") MultipartFile image,
            @RequestParam("data") String jsonData
    ) {
        try {
            CreateProductDTO dto = objectMapper.readValue(jsonData, CreateProductDTO.class);
            Product product = productService.createProduct(dto, image);
            return ResponseEntity.status(HttpStatus.CREATED).body(product);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @Operation(summary = "Listar todos os produtos")
    @GetMapping
    public ResponseEntity<List<Product>> getProducts() {
        List<Product> products = this.productService.getProducts();
        return !products.isEmpty() ? ResponseEntity.ok(products) : ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar produtos por código do usuário")
    @GetMapping("/user/{userCode}")
    public ResponseEntity<List<Product>> getProductsByUserCode(@PathVariable String userCode) {
        List<Product> products = productService.getProductsByUserCode(userCode);
        return products.isEmpty()
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(products);
    }

    @Operation(summary = "Atualizar produto com nova imagem")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> update(
            @PathVariable Long id,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("data") String jsonData
    ) {
        try {
            CreateProductDTO dto = objectMapper.readValue(jsonData, CreateProductDTO.class);
            Product updated = productService.updateProduct(id, dto, image);
            return ResponseEntity.ok(updated);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "Deletar produto por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
