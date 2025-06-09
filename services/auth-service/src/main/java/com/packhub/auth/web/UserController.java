package com.packhub.auth.web;

import com.packhub.auth.domain.service.UserService;
import com.packhub.auth.dto.RegisterDTO;
import com.packhub.auth.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<UserDTO> registerUser(@RequestBody RegisterDTO dto) {
        UserDTO userDTO = userService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }
}