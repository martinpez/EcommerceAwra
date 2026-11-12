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
 * Una edicion de la Home: la curaduría que el administrador decide mostrar.
 *
 * No duplica el producto. El precio, las tallas, el stock y las imagenes salen
 * de PRODUCTO; aqui vive solo el texto editorial, la posicion y el estado.
 *
 * La edicion se muestra en la web solo si esta activa y su producto esta
 * en estado PUBLICADO.
 */
@Entity
@Table(name = "EDICION")
public class EdicionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PRODUCTO_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_EDICION_PRODUCTO"))
    private ProductoEntity producto;

    @Column(name = "EYEBROW", length = 60)
    private String eyebrow;

    @Column(name = "TITULO", length = 120)
    private String titulo;

    @Column(name = "DESCRIPCION", length = 600)
    private String descripcion;

    @Column(name = "ORDEN", nullable = false)
    private int orden;

    @Column(name = "ACTIVA", nullable = false)
    private boolean activa = true;

    @Column(name = "FECHA_CREACION", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHA_ACTUALIZACION", nullable = false)
    private LocalDateTime fechaActualizacion;

    protected EdicionEntity() {
    }

    public EdicionEntity(ProductoEntity producto, String eyebrow, String titulo, String descripcion, int orden) {
        this.producto = producto;
        this.eyebrow = eyebrow;
        this.titulo = titulo;
        this.descripcion = descripcion;
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

    public ProductoEntity getProducto() {
        return producto;
    }

    public void setProducto(ProductoEntity producto) {
        this.producto = producto;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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
