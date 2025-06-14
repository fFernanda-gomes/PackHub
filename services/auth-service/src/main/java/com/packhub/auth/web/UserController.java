package com.packhub.auth.web;

import com.packhub.auth.domain.entities.User;
import com.packhub.auth.domain.service.UserService;
import com.packhub.auth.dto.AuthDTO;
import com.packhub.auth.dto.RegisterDTO;
import com.packhub.auth.dto.UserDTO;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

@Tag(name = "Usuários", description = "Endpoints de autenticação e gestão de usuários")
@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Registrar novo usuário", description = "Cria um novo usuário com userCode e password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    @PostMapping
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody RegisterDTO dto) {
        UserDTO userDTO = userService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }

    @Operation(summary = "Autenticar usuário e retornar token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticação bem-sucedida"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping("/auth")
    public ResponseEntity<AuthDTO> authenticate(@Valid @RequestBody AuthDTO dto) {
        return ResponseEntity.ok(userService.auth(dto));
    }

    @Operation(summary = "Listar todos os usuários")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso"),
            @ApiResponse(responseCode = "204", description = "Nenhum usuário encontrado")
    })
    @GetMapping
    public ResponseEntity<List<User>> getProducts() {
        List<User> products = this.userService.getAllUsers();
        return !products.isEmpty() ? ResponseEntity.ok(products) : ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar usuário por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("{id}")
    public ResponseEntity<User> getUser(
            @Parameter(description = "ID do usuário", example = "1")
            @PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Atualizar um usuário pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @Parameter(description = "ID do usuário", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody RegisterDTO dto) {
    ) {
        User updated = userService.updateUser(id, dto);
        return ResponseEntity.ok(new UserDTO(updated.getId(), updated.getUserCode()));
    }

    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    @Operation(summary = "Deletar usuário por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID do usuário", example = "3")
            @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}