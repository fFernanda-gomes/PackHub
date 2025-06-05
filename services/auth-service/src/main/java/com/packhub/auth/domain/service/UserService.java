package com.packhub.auth.domain.service;

import com.packhub.auth.domain.entities.User;
import com.packhub.auth.domain.repositories.UsersRepository;
import com.packhub.auth.dto.AuthDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
        private UsersRepository userRepository;

        private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        public User registerUser(AuthDTO dto) {
            User user = new User();

            return userRepository.save(user);
        }
    }
