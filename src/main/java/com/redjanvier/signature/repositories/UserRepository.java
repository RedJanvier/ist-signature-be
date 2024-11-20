package com.redjanvier.signature.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.redjanvier.signature.models.User;

public interface UserRepository extends JpaRepository<User, Integer> {

  Optional<User> findByEmail(String email);
  Boolean existsByEmail(String email);

}
