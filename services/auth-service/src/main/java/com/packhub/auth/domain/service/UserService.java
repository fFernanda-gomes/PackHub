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

import java.util.List;
import java.util.Optional;

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

        return new UserDTO(user.getId(), user.getUserCode());
    }

    public AuthDTO auth(AuthDTO authDTO) {
        User user = this.userRepository.findByUserCode(authDTO.getUserCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas"));

        if (!passwordEncoder.matches(authDTO.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
        }

        String token = jwtConfig.generateToken(String.valueOf(user.getUserCode()));

        return AuthDTO.builder()
                .id(user.getId())
                .userCode(user.getUserCode())
                .token(token)
                .build();
    }
    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return this.userRepository.findById(id);
    }

    public User updateUser(Long id, RegisterDTO user) {
        User UserExist = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        if (user.getUserCode() != null) UserExist.setUserCode(user.getUserCode());
        if (user.getPassword() != null) UserExist.setPassword(user.getPassword());

        return userRepository.save(UserExist);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        userRepository.delete(user);
    }
}
