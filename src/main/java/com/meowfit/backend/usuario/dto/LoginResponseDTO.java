package com.meowfit.backend.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO de respuesta al login
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private String correo;
    private String rol;
}
