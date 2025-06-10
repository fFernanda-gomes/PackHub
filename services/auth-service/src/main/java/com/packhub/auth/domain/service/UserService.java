package com.packhub.auth.domain.service;

import com.packhub.auth.domain.entities.User;
import com.packhub.auth.domain.repositories.UsersRepository;
import com.packhub.auth.dto.RegisterDTO;
import com.packhub.auth.dto.UserDTO;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class UserService {
    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


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
    }
