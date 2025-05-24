package com.example.runners;

import java.io.Serializable;

public class User implements Serializable {

    private String username;
    private String email;
    private String password;
    private String sexo;
    private final String fechaNacimiento;
    private float peso;

    public User(String username, String email, String password, String sexo, String fechaNacimiento, float peso) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.sexo = sexo;
        this.fechaNacimiento = fechaNacimiento;
        this.peso = peso;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public String getFechaNacimiento() { return fechaNacimiento; }

    public float getPeso() { return peso; }
    public void setPeso(float peso) { this.peso = peso; }
}
