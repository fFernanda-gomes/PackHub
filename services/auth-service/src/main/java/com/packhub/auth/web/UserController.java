package com.packhub.auth.web;

import com.packhub.auth.domain.entities.User;
import com.packhub.auth.domain.service.UserService;
import com.packhub.auth.dto.AuthDTO;
import com.packhub.auth.dto.RegisterDTO;
import com.packhub.auth.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Tag(name = "Usuários", description = "Endpoints de autenticação e gestão de usuários")
@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Registrar novo usuário")
    @PostMapping
    public ResponseEntity<UserDTO> registerUser(@RequestBody RegisterDTO dto) {
        UserDTO userDTO = userService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }

    @Operation(summary = "Autenticar usuário e retornar token")
    @PostMapping("/auth")
    public ResponseEntity<AuthDTO> authenticate(@RequestBody AuthDTO dto) {
        return ResponseEntity.ok(userService.auth(dto));
    }

    @Operation(summary = "Buscar usuário por ID")
    @GetMapping("{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Deletar usuário")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}