package com.example.account_service.Dto;

import com.example.account_service.Entity.TipoTarjeta;

import java.time.LocalDate;

public class TarjetaResponse {

    private Long id;
    private Long cuentaId;
    private String numeroTarjeta;
    private String titular;
    private LocalDate fechaExpiracion;
    private TipoTarjeta tipo;
    private String marca;

    public TarjetaResponse(Long id, Long cuentaId, String numeroTarjeta, String titular,
                           LocalDate fechaExpiracion, TipoTarjeta tipo, String marca) {
        this.id = id;
        this.cuentaId = cuentaId;
        this.numeroTarjeta = numeroTarjeta;
        this.titular = titular;
        this.fechaExpiracion = fechaExpiracion;
        this.tipo = tipo;
        this.marca = marca;
    }

    public Long getId() { return id; }
    public Long getCuentaId() { return cuentaId; }
    public String getNumeroTarjeta() { return numeroTarjeta; }
    public String getTitular() { return titular; }
    public LocalDate getFechaExpiracion() { return fechaExpiracion; }
    public TipoTarjeta getTipo() { return tipo; }
    public String getMarca() { return marca; }
}
