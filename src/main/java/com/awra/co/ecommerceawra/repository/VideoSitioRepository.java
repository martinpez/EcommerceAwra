package com.awra.co.ecommerceawra.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.awra.co.ecommerceawra.entity.VideoSitioEntity;

public interface VideoSitioRepository extends JpaRepository<VideoSitioEntity, Long> {

    List<VideoSitioEntity> findByActivaTrueOrderByOrdenAsc();

    List<VideoSitioEntity> findBySeccion_IdAndActivaTrueOrderByOrdenAsc(Long seccionId);

    List<VideoSitioEntity> findBySeccion_IdOrderByOrdenAsc(Long seccionId);
}
