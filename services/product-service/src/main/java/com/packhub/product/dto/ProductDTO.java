package com.packhub.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductDTO {

    @Schema(description = "ID do produto", example = "1")
    private Long id;

    @Schema(description = "Nome do produto", example = "Camiseta branca")
    private String name;

    @Schema(description = "Preço do produto", example = "59.90")
    private Double price;

    @Schema(description = "URL da imagem do produto", example = "https://res.cloudinary.com/app/image/upload/abc123.jpg")
    private String imageUrl;

    @Schema(description = "Código do usuário", example = "user123")
    private String userCode;
}

