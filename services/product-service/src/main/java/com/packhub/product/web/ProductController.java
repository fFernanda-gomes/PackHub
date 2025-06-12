package com.packhub.product.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.packhub.product.domain.entities.Product;
import com.packhub.product.domain.service.ProductService;
import com.packhub.product.dto.CreateProductDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    //Teste da rota pra ver se traz o userCode -> usar Bearer na req
    @GetMapping("/produtos")
    public ResponseEntity<?> getProdutos(Authentication authentication) {
        String userCode = authentication.getName();
        return ResponseEntity.ok("Usuário autenticado: " + userCode);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> create(
            @RequestParam("image") MultipartFile image,
            @RequestParam("data") String jsonData
    ) throws JsonProcessingException {

        CreateProductDTO dto = objectMapper.readValue(jsonData, CreateProductDTO.class);
        Product product = productService.createProduct(dto, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getProducts() {
        List<Product> products = this.productService.getProducts();
        return !products.isEmpty() ? ResponseEntity.ok(products) : ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userCode}")
    public ResponseEntity<List<Product>> getProductsByUserCode(@PathVariable String userCode) {
        List<Product> products = productService.getProductsByUserCode(userCode);
        return products.isEmpty()
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(products);
    }

}
