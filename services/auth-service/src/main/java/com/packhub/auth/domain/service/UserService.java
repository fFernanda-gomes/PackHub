package com.packhub.auth.domain.service;

import com.packhub.auth.config.JwtConfig;
import com.packhub.auth.domain.entities.User;
import com.packhub.auth.domain.repositories.UserRepository;
import com.packhub.auth.dto.AuthDTO;
import com.packhub.auth.dto.RegisterDTO;
import com.packhub.auth.dto.UserDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtConfig jwtConfig;

    @Transactional
    public UserDTO register(RegisterDTO dto) {
        if (userRepository.findByUserCode(dto.getUserCode()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Código de usuário já em uso");
        }

        User user = User.builder()
                .userCode(dto.getUserCode())
                .password(passwordEncoder.encode(dto.getPassword()))
                .build();

        userRepository.save(user);


        log.info("Saving user: {}", user.getUserCode());

        return new UserDTO(user.getId(), user.getUserCode());
    }

    public AuthDTO auth(AuthDTO authDTO) {
        User user = this.userRepository.findByUserCode(authDTO.getUserCode())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!this.passwordEncoder.matches(authDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("Senha inválida");
        }

        String token = jwtConfig.generateToken(String.valueOf(user.getUserCode()));

        return AuthDTO.builder()
                .id(user.getId())
                .userCode(user.getUserCode())
                .token(token)
                .build();
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        userRepository.delete(user);
    }
}
