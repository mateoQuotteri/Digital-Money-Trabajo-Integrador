package com.example.account_service.Entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transferencias", indexes = {
        @Index(name = "idx_transferencia_cuenta_origen", columnList = "cuenta_origen_id"),
        @Index(name = "idx_transferencia_fecha", columnList = "fecha")
})
public class Transferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_origen_id", nullable = false)
    private Cuenta cuentaOrigen;

    @Column(nullable = false, length = 100)
    private String destinoCbuCvuAlias;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_destino_id")
    private Cuenta cuentaDestino;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fecha;

    public Transferencia() {}

    public Transferencia(Cuenta cuentaOrigen, String destinoCbuCvuAlias, Cuenta cuentaDestino, BigDecimal monto) {
        this.cuentaOrigen = cuentaOrigen;
        this.destinoCbuCvuAlias = destinoCbuCvuAlias;
        this.cuentaDestino = cuentaDestino;
        this.monto = monto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cuenta getCuentaOrigen() { return cuentaOrigen; }
    public void setCuentaOrigen(Cuenta cuentaOrigen) { this.cuentaOrigen = cuentaOrigen; }

    public String getDestinoCbuCvuAlias() { return destinoCbuCvuAlias; }
    public void setDestinoCbuCvuAlias(String destinoCbuCvuAlias) { this.destinoCbuCvuAlias = destinoCbuCvuAlias; }

    public Cuenta getCuentaDestino() { return cuentaDestino; }
    public void setCuentaDestino(Cuenta cuentaDestino) { this.cuentaDestino = cuentaDestino; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
