package com.example.account_service.Dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DestinatarioResponse {

    private Long id;
    private String cbuCvuAlias;
    private BigDecimal monto;
    private LocalDateTime fecha;

    public DestinatarioResponse() {}

    public DestinatarioResponse(Long id, String cbuCvuAlias, BigDecimal monto, LocalDateTime fecha) {
        this.id = id;
        this.cbuCvuAlias = cbuCvuAlias;
        this.monto = monto;
        this.fecha = fecha;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCbuCvuAlias() { return cbuCvuAlias; }
    public void setCbuCvuAlias(String cbuCvuAlias) { this.cbuCvuAlias = cbuCvuAlias; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
