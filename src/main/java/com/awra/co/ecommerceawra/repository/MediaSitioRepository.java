package com.awra.co.ecommerceawra.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.awra.co.ecommerceawra.entity.MediaSitioEntity;
import com.awra.co.ecommerceawra.entity.TipoMedia;

public interface MediaSitioRepository extends JpaRepository<MediaSitioEntity, Long> {

    List<MediaSitioEntity> findBySeccion_IdAndActivaTrueOrderByOrdenAsc(Long seccionId);

    List<MediaSitioEntity> findBySeccion_IdOrderByOrdenAsc(Long seccionId);

    /** El logo general no pertenece a ninguna seccion (SECCION_ID nulo). */
    List<MediaSitioEntity> findBySeccionIsNullAndTipoAndActivaTrueOrderByOrdenAsc(TipoMedia tipo);
}
