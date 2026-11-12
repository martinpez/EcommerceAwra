package com.awra.co.ecommerceawra.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Check;
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
import jakarta.persistence.Version;

@Entity
@Table(name = "VARIANTE")
@Check(constraints = "STOCK >= 0")
@SQLDelete(sql = "update variante set fecha_baja = current_timestamp where id = ? and version = ?", verify = Expectation.RowCount.class)
@SQLRestriction("fecha_baja is null")
public class VarianteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PRODUCTO_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_VARIANTE_PRODUCTO"))
    private ProductoEntity producto;

    @Column(name = "TALLA", nullable = false, length = 20)
    private String talla;

    @Column(name = "COLOR", nullable = false, length = 40)
    private String color;

    @Column(name = "SKU", nullable = false, length = 50)
    private String sku;

    @Column(name = "STOCK", nullable = false)
    private int stock = 0;

    @Column(name = "STOCK_MINIMO", nullable = false)
    private int stockMinimo = 0;

    @Column(name = "FECHA_BAJA")
    private LocalDateTime fechaBaja;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Long version;

    protected VarianteEntity() {
    }

    public VarianteEntity(ProductoEntity producto, String talla, String color, String sku, int stockMinimo) {
        this.producto = producto;
        this.talla = talla;
        this.color = color;
        this.sku = sku;
        this.stockMinimo = stockMinimo;
    }

    public void modificarStock(int delta) {
        this.stock += delta;
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

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public int getStock() {
        return stock;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public LocalDateTime getFechaBaja() {
        return fechaBaja;
    }

    public Long getVersion() {
        return version;
    }
}
