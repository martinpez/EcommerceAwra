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
@Table(name = "ALERTA_STOCK")
public class AlertaStockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "VARIANTE_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_ALERTA_VARIANTE"))
    private VarianteEntity variante;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO", nullable = false, length = 20)
    private TipoAlerta tipo;

    @Column(name = "STOCK_AL_MOMENTO", nullable = false)
    private int stockAlMomento;

    @Column(name = "LEIDA", nullable = false)
    private boolean leida = false;

    @Column(name = "FECHA_CREACION", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHA_RESOLUCION")
    private LocalDateTime fechaResolucion;

    protected AlertaStockEntity() {
    }

    public AlertaStockEntity(VarianteEntity variante, TipoAlerta tipo, int stockAlMomento) {
        this.variante = variante;
        this.tipo = tipo;
        this.stockAlMomento = stockAlMomento;
    }

    @PrePersist
    void alCrear() {
        fechaCreacion = LocalDateTime.now();
    }

    public void escalarA(TipoAlerta nuevoTipo, int stockActual) {
        if (this.tipo != nuevoTipo) {
            this.tipo = nuevoTipo;
            this.stockAlMomento = stockActual;
            this.leida = false;
        }
    }

    public void resolver() {
        this.fechaResolucion = LocalDateTime.now();
    }

    public void marcarLeida() {
        this.leida = true;
    }

    public Long getId() {
        return id;
    }

    public VarianteEntity getVariante() {
        return variante;
    }

    public TipoAlerta getTipo() {
        return tipo;
    }

    public int getStockAlMomento() {
        return stockAlMomento;
    }

    public boolean isLeida() {
        return leida;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaResolucion() {
        return fechaResolucion;
    }
}
