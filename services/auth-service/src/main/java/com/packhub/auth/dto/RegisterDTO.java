package com.packhub.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO {

    @Schema(description = "Código único do usuário (string)", example = "1111")
    @NotBlank(message = "userCode é obrigatório")
    @Size(min = 3, max = 20, message = "userCode deve ter entre 3 e 20 caracteres")
    private Integer userCode;

    @Schema(description = "Senha do usuário", example = "senha123")
    @NotBlank(message = "password é obrigatória")
    @Size(min = 6, message = "password deve ter no mínimo 6 caracteres")
    private String password;
}

