package com.meowfit.backend.usuario.dto;

import java.time.LocalDateTime;
import java.time.LocalDate;

import com.meowfit.backend.common.Estado;
import com.meowfit.backend.usuario.entity.Usuario.Rol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO para transferencia de datos del usuario
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Long idUsuario;
    private String nombres;
    private String telefono;
    private String correo;
    private LocalDateTime fechaCreacion;
    private Estado estadoCuenta;
    private Rol rol;
    private String dni;
    private LocalDate fechaNacimiento;
    private String direccionEnvio;
    private String ruc;
    private String razonSocial;
    private String telefono2;
    private LocalDateTime fechaActualizacion;
}
