package com.packhub.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO {
    @Schema(description = "Código único do usuário (string)", example = "1111")
    @NotNull(message = "userCode é obrigatório")
    @Min(value = 3, message = "userCode deve ter no mínimo 3 dígitos")
    @Max(value = 999999, message = "userCode deve ter no máximo 6 dígitos")
    private Integer userCode;

    @Schema(description = "Senha do usuário", example = "senha123")
    @NotBlank(message = "password é obrigatória")
    @Size(min = 4, message = "password deve ter no mínimo 6 caracteres")
    private String password;
}

