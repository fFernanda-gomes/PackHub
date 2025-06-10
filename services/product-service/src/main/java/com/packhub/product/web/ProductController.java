package com.packhub.product.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class ProductController {

    //Teste da rota pra ver se traz o userCode -> usar Bearer na req
    @GetMapping("/produtos")
    public ResponseEntity<?> getProdutos(Authentication authentication) {
        String userCode = authentication.getName();
        return ResponseEntity.ok("Usuário autenticado: " + userCode);
    }
}
