package com.awra.co.ecommerceawra.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

/**
 * Una seccion del sitio (hero, quienes somos, ediciones, movimiento, contacto).
 *
 * La CLAVE la usa el codigo para saber que seccion es; el contenido
 * (titulos y textos) lo edita el administrador desde el panel.
 */
@Entity
@Table(name = "SECCION_SITIO")
public class SeccionSitioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CLAVE", nullable = false, unique = true, length = 40)
    private String clave;

    @Column(name = "EYEBROW", length = 80)
    private String eyebrow;

    @Column(name = "TITULO", length = 200)
    private String titulo;

    @Column(name = "CUERPO", length = 2000)
    private String cuerpo;

    @Column(name = "ORDEN", nullable = false)
    private int orden;

    @Column(name = "ACTIVA", nullable = false)
    private boolean activa = true;

    @Column(name = "FECHA_CREACION", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHA_ACTUALIZACION", nullable = false)
    private LocalDateTime fechaActualizacion;

    protected SeccionSitioEntity() {
    }

    public SeccionSitioEntity(String clave, String eyebrow, String titulo, String cuerpo, int orden) {
        this.clave = clave;
        this.eyebrow = eyebrow;
        this.titulo = titulo;
        this.cuerpo = cuerpo;
        this.orden = orden;
    }

    @PrePersist
    void alCrear() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = fechaCreacion;
    }

    @PreUpdate
    void alActualizar() {
        fechaActualizacion = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getClave() {
        return clave;
    }

    public String getEyebrow() {
        return eyebrow;
    }

    public void setEyebrow(String eyebrow) {
        this.eyebrow = eyebrow;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(String cuerpo) {
        this.cuerpo = cuerpo;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
}
