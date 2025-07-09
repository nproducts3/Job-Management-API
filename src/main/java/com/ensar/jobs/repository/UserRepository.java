package com.ensar.jobs.repository;

import com.ensar.jobs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.role WHERE u.email = :email")
    Optional<User> findByEmailWithRole(@Param("email") String email);

    @Query("SELECT u FROM User u JOIN u.role r WHERE r.roleName = :roleName")
    java.util.List<User> findAllByRoleName(@Param("roleName") String roleName);

    @Query("SELECT u FROM User u JOIN u.role r WHERE r.roleName = :roleName")
    org.springframework.data.domain.Page<User> findAllByRoleName(@Param("roleName") String roleName, org.springframework.data.domain.Pageable pageable);
} 