package com.awra.co.ecommerceawra.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.awra.co.ecommerceawra.entity.SeccionSitioEntity;

public interface SeccionSitioRepository extends JpaRepository<SeccionSitioEntity, Long> {

    List<SeccionSitioEntity> findAllByOrderByOrdenAsc();

    List<SeccionSitioEntity> findByActivaTrueOrderByOrdenAsc();

    Optional<SeccionSitioEntity> findByClave(String clave);

    boolean existsByClave(String clave);

    boolean existsByClaveAndIdNot(String clave, Long id);
}
