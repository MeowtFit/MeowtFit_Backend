package com.meowfit.backend.usuario.dto;

import com.meowfit.backend.usuario.entity.Usuario.Rol;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO para creación y edición de usuario
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 200, message = "El nombre no puede superar los 200 caracteres")
    private String nombres;

    @NotBlank(message = "El telefono es obligatorio")
    @Size(max = 20, message = "El telefono no puede superar los 20 caracteres")
    private String telefono;

    @NotBlank(message = "El correo  es obligatorio")
    @Email(message = "El correo  debe tener un formato válido")
    @Size(max = 100, message = "El correo no puede superar los 100 caracteres")
    private String correo;

    @NotNull(message = "El rol es obligatorio")
    private Rol rol;

    // Contraseña en texto plano — obligatoria solo en creación, se hashea con
    // BCrypt en el service
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String contrasena;

}
