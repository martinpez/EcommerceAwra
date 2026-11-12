package com.awra.co.ecommerceawra.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.awra.co.ecommerceawra.entity.RolEntity;

public interface RolRepository extends JpaRepository<RolEntity, Long> {

    Optional<RolEntity> findByNombre(String nombre);
}
