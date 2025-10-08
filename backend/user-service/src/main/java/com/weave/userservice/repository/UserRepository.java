package com.weave.userservice.repository;

import com.weave.userservice.entity.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User save(User user);
    boolean existsByNickname(@NotBlank(message = "Nickname cannot be blank") String nickname);
    boolean existsByEmail(@NotBlank(message = "Email cannot be blank") String email);
    Optional<User> findByEmail(@NotBlank(message = "Email cannot be blank") String email);
    Optional<User> findById(Long id);

}
