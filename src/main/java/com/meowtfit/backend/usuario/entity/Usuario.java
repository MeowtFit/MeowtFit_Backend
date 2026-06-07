package com.meowtfit.backend.usuario.entity;

import java.time.LocalDateTime;
// Importaciones existentes:
import java.time.LocalDate;           // Para fechaNacimiento (tipo DATE)
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import com.meowtfit.backend.common.Estado;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Entidad que representa un usuario interno del sistema
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
// La tabla se coloca en mayúsculas pq así está en la BD
@Table(name = "Usuario")
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

    // Campos de ClienteB2C (null si no es CLIENTE o es B2B)
    @Column(name = "dni", unique = true, length = 20)
    private String dni;

    @Column(name = "fechaNacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "direccionEnvio", columnDefinition = "TEXT")
    private String direccionEnvio;

    // Campos de ClienteB2B (null si no es CLIENTE o es B2C)
    @Column(name = "ruc", unique = true, length = 20)
    private String ruc;

    @Column(name = "razonSocial", length = 150)
    private String razonSocial;

    @Column(name = "telefono2", length = 20)
    private String telefono2;

    // Campo faltante
    @Column(name = "fechaActualizacion")
    @LastModifiedDate
    private LocalDateTime fechaActualizacion;
}
