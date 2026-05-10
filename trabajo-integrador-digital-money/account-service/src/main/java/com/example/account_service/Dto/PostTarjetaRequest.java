package com.example.account_service.Dto;

import com.example.account_service.Entity.TipoTarjeta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PostTarjetaRequest {

    @NotBlank(message = "El número de tarjeta es obligatorio")
    @Pattern(regexp = "^\\d{16}$", message = "El número de tarjeta debe tener 16 dígitos")
    private String numeroTarjeta;

    @NotBlank(message = "El titular es obligatorio")
    @Size(min = 2, max = 100, message = "El titular debe tener entre 2 y 100 caracteres")
    private String titular;

    @NotBlank(message = "La fecha de expiración es obligatoria")
    @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{2}$", message = "La fecha de expiración debe tener formato MM/YY")
    private String fechaExpiracion;

    @NotNull(message = "El tipo de tarjeta es obligatorio")
    private TipoTarjeta tipo;

    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 20, message = "La marca no puede exceder 20 caracteres")
    private String marca;

    public String getNumeroTarjeta() { return numeroTarjeta; }
    public void setNumeroTarjeta(String numeroTarjeta) { this.numeroTarjeta = numeroTarjeta; }

    public String getTitular() { return titular; }
    public void setTitular(String titular) { this.titular = titular; }

    public String getFechaExpiracion() { return fechaExpiracion; }
    public void setFechaExpiracion(String fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }

    public TipoTarjeta getTipo() { return tipo; }
    public void setTipo(TipoTarjeta tipo) { this.tipo = tipo; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
}
