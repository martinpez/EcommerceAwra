package com.awra.co.ecommerceawra.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.jdbc.Expectation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "IMAGEN_PRODUCTO")
@SQLDelete(sql = "update imagen_producto set fecha_baja = current_timestamp where id = ?", verify = Expectation.RowCount.class)
@SQLRestriction("fecha_baja is null")
public class ImagenProductoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PRODUCTO_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_IMAGEN_PRODUCTO"))
    private ProductoEntity producto;

    @Column(name = "URL", nullable = false, length = 500)
    private String url;

    @Column(name = "PUBLIC_ID", nullable = false, length = 200)
    private String publicId;

    @Column(name = "ORDEN", nullable = false)
    private int orden;

    @Column(name = "ES_PRINCIPAL", nullable = false)
    private boolean esPrincipal;

    @Column(name = "FECHA_BAJA")
    private LocalDateTime fechaBaja;

    protected ImagenProductoEntity() {
    }

    public ImagenProductoEntity(ProductoEntity producto, String url, String publicId, int orden, boolean esPrincipal) {
        this.producto = producto;
        this.url = url;
        this.publicId = publicId;
        this.orden = orden;
        this.esPrincipal = esPrincipal;
    }

    public Long getId() {
        return id;
    }

    public ProductoEntity getProducto() {
        return producto;
    }

    public String getUrl() {
        return url;
    }

    public String getPublicId() {
        return publicId;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public boolean isEsPrincipal() {
        return esPrincipal;
    }

    public void setEsPrincipal(boolean esPrincipal) {
        this.esPrincipal = esPrincipal;
    }
}
