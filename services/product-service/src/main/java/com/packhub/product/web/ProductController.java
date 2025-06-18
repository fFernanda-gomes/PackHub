package com.packhub.product.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.packhub.product.domain.entities.Product;
import com.packhub.product.domain.service.ProductService;
import com.packhub.product.dto.CreateProductDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Produto criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou imagem ausente")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> create(
            @RequestParam("image") MultipartFile image,
            @RequestParam("data") String jsonData
    ) throws JsonProcessingException {
        CreateProductDTO dto = objectMapper.readValue(jsonData, CreateProductDTO.class);
        Product product = productService.createProduct(dto, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @Operation(summary = "Listar todos os produtos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de produtos retornada"),
            @ApiResponse(responseCode = "204", description = "Nenhum produto encontrado")
    })
    @GetMapping
    public ResponseEntity<List<Product>> getProducts() {
        List<Product> products = this.productService.getProducts();
        return products.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(products);
    }

    @Operation(summary = "Listar produtos por código do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produtos do usuário retornados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Nenhum produto encontrado para o usuário")
    })
    @GetMapping("/user/{userCode}")
    public ResponseEntity<List<Product>> getProductsByUserCode(
            @Parameter(description = "Código único do usuário", example = "abc123")
            @PathVariable String userCode
    ) {
        List<Product> products = productService.getProductsByUserCode(userCode);
        return products.isEmpty()
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(products);
    }

    @Operation(summary = "Atualizar produto com nova imagem")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> update(
            @Parameter(description = "ID do produto", example = "1")
            @PathVariable Long id,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("data") String jsonData
    ) throws JsonProcessingException {
        CreateProductDTO dto = objectMapper.readValue(jsonData, CreateProductDTO.class);
        Product updated = productService.updateProduct(id, dto, image);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Deletar produto por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Produto deletado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "ID do produto", example = "1")
            @PathVariable Long id
    ) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
