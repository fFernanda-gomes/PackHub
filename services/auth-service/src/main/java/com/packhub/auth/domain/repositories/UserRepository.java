package com.packhub.auth.domain.repositories;

import com.packhub.auth.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>  {

    Optional<User> findByUserCode(int userCode);
}
