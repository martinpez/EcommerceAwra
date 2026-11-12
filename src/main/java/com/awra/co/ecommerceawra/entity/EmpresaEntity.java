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
 * Datos de la empresa: contacto, redes sociales y direccion.
 *
 * Es un registro unico. La tabla tiene un CHECK que obliga a que el ID sea 1,
 * de modo que la base de datos misma impide que exista una segunda empresa.
 */
@Entity
@Table(name = "EMPRESA")
public class EmpresaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    /**
     * Columna constante que hace unique la fila. El indice unico
     * {@code UK_EMPRESA_UNICA} impide que exista una segunda empresa.
     * No se actualiza nunca.
     */
    @Column(name = "SINGULAR", nullable = false, updatable = false)
    private Integer singular = 1;

    @Column(name = "NOMBRE", nullable = false, length = 80)
    private String nombre;

    @Column(name = "SLOGAN", length = 150)
    private String slogan;

    @Column(name = "DESCRIPCION", length = 600)
    private String descripcion;

    /** Numero de WhatsApp en formato internacional sin signos: 573123461277. */
    @Column(name = "WHATSAPP", nullable = false, length = 20)
    private String whatsapp;

    @Column(name = "TELEFONO", length = 20)
    private String telefono;

    @Column(name = "EMAIL", length = 120)
    private String email;

    @Column(name = "DIRECCION", length = 200)
    private String direccion;

    @Column(name = "CIUDAD", length = 80)
    private String ciudad;

    @Column(name = "PAIS", length = 80)
    private String pais;

    @Column(name = "INSTAGRAM", length = 120)
    private String instagram;

    @Column(name = "TIKTOK", length = 120)
    private String tiktok;

    @Column(name = "FACEBOOK", length = 120)
    private String facebook;

    @Column(name = "YOUTUBE", length = 120)
    private String youtube;

    @Column(name = "HORARIO", length = 150)
    private String horario;

    @Column(name = "FECHA_CREACION", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHA_ACTUALIZACION", nullable = false)
    private LocalDateTime fechaActualizacion;

    protected EmpresaEntity() {
    }

    public EmpresaEntity(String nombre, String whatsapp) {
        this.nombre = nombre;
        this.whatsapp = whatsapp;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getTiktok() {
        return tiktok;
    }

    public void setTiktok(String tiktok) {
        this.tiktok = tiktok;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
}
