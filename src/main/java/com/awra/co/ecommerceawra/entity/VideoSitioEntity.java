package com.awra.co.ecommerceawra.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * Un video del sitio (la seccion "En movimiento").
 *
 * POSTER_URL es la imagen del primer frame: se muestra mientras el video
 * descarga, para que la seccion no aparezca en negro.
 */
@Entity
@Table(name = "VIDEO_SITIO")
public class VideoSitioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SECCION_ID", foreignKey = @ForeignKey(name = "FK_VIDEO_SITIO_SECCION"))
    private SeccionSitioEntity seccion;

    @Column(name = "TITULO", nullable = false, length = 120)
    private String titulo;

    /** Texto corto que se muestra bajo el reproductor. */
    @Column(name = "ETIQUETA", length = 120)
    private String etiqueta;

    @Column(name = "URL", nullable = false, length = 500)
    private String url;

    @Column(name = "PUBLIC_ID", nullable = false, length = 200)
    private String publicId;

    @Column(name = "POSTER_URL", length = 500)
    private String posterUrl;

    @Column(name = "ANCHO")
    private Integer ancho;

    @Column(name = "ALTO")
    private Integer alto;

    @Column(name = "DURACION_SEG")
    private Integer duracionSeg;

    @Column(name = "ORDEN", nullable = false)
    private int orden;

    @Column(name = "ACTIVA", nullable = false)
    private boolean activa = true;

    @Column(name = "FECHA_CREACION", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHA_ACTUALIZACION", nullable = false)
    private LocalDateTime fechaActualizacion;

    protected VideoSitioEntity() {
    }

    public VideoSitioEntity(SeccionSitioEntity seccion, String titulo, String etiqueta, String url, String publicId,
            String posterUrl, Integer ancho, Integer alto, Integer duracionSeg, int orden) {
        this.seccion = seccion;
        this.titulo = titulo;
        this.etiqueta = etiqueta;
        this.url = url;
        this.publicId = publicId;
        this.posterUrl = posterUrl;
        this.ancho = ancho;
        this.alto = alto;
        this.duracionSeg = duracionSeg;
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

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
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

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
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

    public Integer getDuracionSeg() {
        return duracionSeg;
    }

    public void setDuracionSeg(Integer duracionSeg) {
        this.duracionSeg = duracionSeg;
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
        this.activa =activa;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
}
