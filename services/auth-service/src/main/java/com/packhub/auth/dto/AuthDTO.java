package com.packhub.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AuthDTO {

    @Schema(description = "Código do usuário", example = "1111")
    @NotNull(message = "userCode é obrigatório")
    private Integer userCode;

    @Schema(description = "Senha do usuário", example = "senha123")
    @NotBlank(message = "password é obrigatória")
    private String password;
    private Long id;
    private String token;
}