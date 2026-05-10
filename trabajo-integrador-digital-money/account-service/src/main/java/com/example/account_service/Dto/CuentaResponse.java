package com.example.account_service.Dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CuentaResponse {

    private Long id;
    private Long userId;
    private BigDecimal saldo;
    private String cvu;
    private String alias;
    private LocalDateTime fechaCreacion;

    public CuentaResponse() {}

    public CuentaResponse(Long id, Long userId, BigDecimal saldo, String cvu, String alias, LocalDateTime fechaCreacion) {
        this.id = id;
        this.userId = userId;
        this.saldo = saldo;
        this.cvu = cvu;
        this.alias = alias;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }

    public String getCvu() { return cvu; }
    public void setCvu(String cvu) { this.cvu = cvu; }

    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
