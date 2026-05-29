package com.meowfit.backend.usuario.mapper;

import org.springframework.stereotype.Component;

import com.meowfit.backend.usuario.dto.UsuarioDTO;
import com.meowfit.backend.usuario.dto.UsuarioRequestDTO;
import com.meowfit.backend.usuario.entity.Usuario;

// Convierte entre la entidad Usuario y su DTO
@Component
public class UsuarioMapper {

    // Convierte entity a DTO de respuesta
    public UsuarioDTO ToDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setNombres(usuario.getNombres());
        dto.setTelefono(usuario.getTelefono());
        dto.setCorreo(usuario.getCorreo());
        dto.setFechaCreacion(usuario.getFechaCreacion());
        dto.setEstadoCuenta(usuario.getEstado());
        dto.setRol(usuario.getRol());
        return dto;
    }

    // Convierte DTO de respuesta a entity
    public Usuario ToEntity(UsuarioDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNombres(dto.getNombres());
        usuario.setTelefono(dto.getTelefono());
        usuario.setCorreo(dto.getCorreo());
        usuario.setRol(dto.getRol());
        return usuario;
    }

    // Convierte DTO de request a entity para creación y edición
    public Usuario ToEntity(UsuarioRequestDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNombres(dto.getNombres());
        usuario.setTelefono(dto.getTelefono());
        usuario.setCorreo(dto.getCorreo());
        usuario.setRol(dto.getRol());
        return usuario;
    }   
}
