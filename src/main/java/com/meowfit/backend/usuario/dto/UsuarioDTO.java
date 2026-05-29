package com.meowfit.backend.usuario.dto;

import java.time.LocalDateTime;

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
}
