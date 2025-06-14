package com.packhub.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductDTO {

    @Schema(description = "Nome do produto", example = "Camiseta branca")
    @NotBlank(message = "O nome do produto é obrigatório")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres")
    private String name;

    @Schema(description = "Preço do produto", example = "59.90")
    @NotNull(message = "O preço é obrigatório")
    private Double price;

    @Schema(description = "Código do usuário dono do produto", example = "user123")
    @NotBlank(message = "O código do usuário é obrigatório")
    private String userCode;
}
