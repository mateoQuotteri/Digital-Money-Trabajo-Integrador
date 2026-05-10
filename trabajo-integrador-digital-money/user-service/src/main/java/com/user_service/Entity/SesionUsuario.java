package com.user_service.Entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;


@Entity
@Table(name = "sesiones_usuario", indexes = {
        @Index(name = "idx_token_hash", columnList = "tokenHash"),
        @Index(name = "idx_usuario_sesion", columnList = "usuario_id"),
        @Index(name = "idx_fecha_expiracion", columnList = "fechaExpiracion"),
        @Index(name = "idx_activa", columnList = "activa")
})
public class SesionUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnore
    private Usuario usuario;


    @Column(nullable = false, unique = true, length = 255)
    private String tokenHash;


    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;


    @Column(nullable = false)
    private LocalDateTime fechaExpiracion;


    @Column(nullable = false)
    private Boolean activa = true;


    @Column(length = 45) // IPv6 máximo: 39 caracteres
    private String ipAddress;


    @Column(length = 500)
    private String userAgent;

    // ===============================================
    // CONSTRUCTORES
    // ===============================================

    /**
     * Constructor vacío requerido por JPA
     */
    public SesionUsuario() {}


    public SesionUsuario(Usuario usuario, String tokenHash, LocalDateTime fechaExpiracion) {
        this.usuario = usuario;
        this.tokenHash = tokenHash;
        this.fechaExpiracion = fechaExpiracion;
        this.activa = true;
    }


    public SesionUsuario(Usuario usuario, String tokenHash, LocalDateTime fechaExpiracion,
                         String ipAddress, String userAgent) {
        this.usuario = usuario;
        this.tokenHash = tokenHash;
        this.fechaExpiracion = fechaExpiracion;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.activa = true;
    }


    public void invalidar() {
        this.activa = false;
    }


    public boolean esValida() {
        return activa && fechaExpiracion.isAfter(LocalDateTime.now());
    }


    public boolean estaExpirada() {
        return fechaExpiracion.isBefore(LocalDateTime.now());
    }


    public long getMinutosRestantes() {
        if (estaExpirada()) {
            return 0;
        }
        return java.time.Duration.between(LocalDateTime.now(), fechaExpiracion).toMinutes();
    }


    public String getDescripcionDispositivo() {
        if (userAgent == null || userAgent.isEmpty()) {
            return "Dispositivo desconocido";
        }

        // Lógica básica para extraer info del User Agent
        if (userAgent.contains("Chrome")) {
            return "Chrome";
        } else if (userAgent.contains("Firefox")) {
            return "Firefox";
        } else if (userAgent.contains("Safari")) {
            return "Safari";
        } else if (userAgent.contains("Mobile")) {
            return "Dispositivo móvil";
        } else {
            return "Navegador desconocido";
        }
    }

    // ===============================================
    // GETTERS Y SETTERS
    // ===============================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(LocalDateTime fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public Boolean getActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}