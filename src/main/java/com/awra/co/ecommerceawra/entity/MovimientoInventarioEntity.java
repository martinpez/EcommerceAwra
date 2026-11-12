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
import jakarta.persistence.Table;

@Entity
@Table(name = "MOVIMIENTO_INVENTARIO")
public class MovimientoInventarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "VARIANTE_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_MOVIMIENTO_VARIANTE"))
    private VarianteEntity variante;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO", nullable = false, length = 20)
    private TipoMovimiento tipo;

    @Column(name = "CANTIDAD", nullable = false)
    private int cantidad;

    @Column(name = "STOCK_RESULTANTE", nullable = false)
    private int stockResultante;

    @Column(name = "MOTIVO", length = 255)
    private String motivo;

    @Column(name = "FECHA_MOVIMIENTO", nullable = false)
    private LocalDateTime fechaMovimiento;

    protected MovimientoInventarioEntity() {
    }

    public MovimientoInventarioEntity(VarianteEntity variante, TipoMovimiento tipo, int cantidad,
            int stockResultante, String motivo) {
        this.variante = variante;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.stockResultante = stockResultante;
        this.motivo = motivo;
    }

    @PrePersist
    void alCrear() {
        fechaMovimiento = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public VarianteEntity getVariante() {
        return variante;
    }

    public TipoMovimiento getTipo() {
        return tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public int getStockResultante() {
        return stockResultante;
    }

    public String getMotivo() {
        return motivo;
    }

    public LocalDateTime getFechaMovimiento() {
        return fechaMovimiento;
    }
}
