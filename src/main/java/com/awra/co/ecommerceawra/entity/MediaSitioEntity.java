package com.awra.co.ecommerceawra.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

/**
 * El logo o una imagen de una seccion del sitio.
 *
 * Se guardan la URL de entrega y el public_id de Cloudinary: con el public_id
 * se puede borrar el archivo original mas adelante.
 */
@Entity
@Table(name = "MEDIA_SITIO")
public class MediaSitioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    /** Nulo cuando el medio no pertenece a una seccion concreta (el logo). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SECCION_ID", foreignKey = @ForeignKey(name = "FK_MEDIA_SITIO_SECCION"))
    private SeccionSitioEntity seccion;

    /** Sin esto JPA guardaria el enum como ordinal (0, 1) y no como texto. */
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO", nullable = false, length = 20)
    private TipoMedia tipo;

    @Column(name = "URL", nullable = false, length = 500)
    private String url;

    @Column(name = "PUBLIC_ID", nullable = false, length = 200)
    private String publicId;

    /** Texto alternativo para lectores de pantalla. */
    @Column(name = "ALT", length = 200)
    private String alt;

    @Column(name = "ANCHO")
    private Integer ancho;

    @Column(name = "ALTO")
    private Integer alto;

    @Column(name = "ORDEN", nullable = false)
    private int orden;

    @Column(name = "ACTIVA", nullable = false)
    private boolean activa = true;

    @Column(name = "FECHA_CREACION", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHA_ACTUALIZACION", nullable = false)
    private LocalDateTime fechaActualizacion;

    protected MediaSitioEntity() {
    }

    public MediaSitioEntity(SeccionSitioEntity seccion, TipoMedia tipo, String url, String publicId,
            String alt, Integer ancho, Integer alto, int orden) {
        this.seccion = seccion;
        this.tipo = tipo;
        this.url = url;
        this.publicId = publicId;
        this.alt = alt;
        this.ancho = ancho;
        this.alto = alto;
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

    public SeccionSitioEntity getSeccion() {
        return seccion;
    }

    public void setSeccion(SeccionSitioEntity seccion) {
        this.seccion = seccion;
    }

    public TipoMedia getTipo() {
        return tipo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public Integer getAncho() {
        return ancho;
    }

    public void setAncho(Integer ancho) {
        this.ancho = ancho;
    }

    public Integer getAlto() {
        return alto;
    }

    public void setAlto(Integer alto) {
        this.alto = alto;
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
