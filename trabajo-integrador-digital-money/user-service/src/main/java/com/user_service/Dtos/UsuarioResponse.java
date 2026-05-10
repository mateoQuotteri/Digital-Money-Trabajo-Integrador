package com.user_service.Dtos;

public class UsuarioResponse {

    private Long id;
    private String nombre;
    private String apellido;
    private String dni;
    private String email;
    private String telefono;
    private String cvu;
    private String alias;

    public UsuarioResponse() {}

    public UsuarioResponse(Long id, String nombre, String apellido, String dni,
                           String email, String telefono, String cvu, String alias) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.email = email;
        this.telefono = telefono;
        this.cvu = cvu;
        this.alias = alias;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCvu() { return cvu; }
    public void setCvu(String cvu) { this.cvu = cvu; }

    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }
}
