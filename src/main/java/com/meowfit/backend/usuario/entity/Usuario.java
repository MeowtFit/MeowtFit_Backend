package com.meowfit.backend.usuario.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.meowfit.backend.common.Estado;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Entidad que representa un usuario interno del sistema
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idUsuario")
    private Long idUsuario;

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "telefono", nullable = false, unique = true, length = 20)
    private String telefono;

    @Column(name = "correo", nullable = false, unique = true, length = 100)
    private String correo;

    // Estado del usuario en el sistema
    @Enumerated(EnumType.STRING)
    @Column(name = "estadoCuenta", nullable = false)
    private Estado estado = Estado.ACTIVO;

    // Rol del usuario en el sistema
    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    private Rol rol;

    // Contraseña almacenada en formato hash
    @Column(name = "contrasena", nullable = false)
    private String contrasena;

    @CreationTimestamp
    @Column(name = "fechaCreacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    // Valores posibles para el rol del usuario
    public enum Rol {
        ADMINISTRADOR, COMERCIANTE, CLIENTE
    }
}
