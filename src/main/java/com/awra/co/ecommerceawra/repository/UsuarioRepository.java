package com.awra.co.ecommerceawra.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.awra.co.ecommerceawra.entity.UsuarioEntity;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

    Optional<UsuarioEntity> findByUsername(String username);

    boolean existsByUsername(String username);
}
