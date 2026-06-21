package com.meowtfit.backend.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordDTO {
    @NotBlank(message = "El token es obligatorio")
    private String token;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    private String nuevaContrasena;

    @NotBlank(message = "La confirmación de la contraseña es obligatoria")
    private String confirmarContrasena;
}
