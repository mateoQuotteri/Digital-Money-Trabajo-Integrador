package com.example.account_service.Dto;

import com.example.account_service.Entity.TipoTransaccion;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransaccionResponse {

    private Long id;
    private BigDecimal monto;
    private TipoTransaccion tipo;
    private String descripcion;
    private LocalDateTime fecha;

    public TransaccionResponse() {}

    public TransaccionResponse(Long id, BigDecimal monto, TipoTransaccion tipo, String descripcion, LocalDateTime fecha) {
        this.id = id;
        this.monto = monto;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.fecha = fecha;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public TipoTransaccion getTipo() { return tipo; }
    public void setTipo(TipoTransaccion tipo) { this.tipo = tipo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
