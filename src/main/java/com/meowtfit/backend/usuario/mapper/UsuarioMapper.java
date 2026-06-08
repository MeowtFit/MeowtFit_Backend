package com.meowtfit.backend.usuario.mapper;

import org.springframework.stereotype.Component;

import com.meowtfit.backend.usuario.dto.UsuarioDTO;
import com.meowtfit.backend.usuario.dto.UsuarioRequestDTO;
import com.meowtfit.backend.usuario.entity.Usuario;

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
        // Nuevos campos
        dto.setDni(usuario.getDni());
        dto.setFechaNacimiento(usuario.getFechaNacimiento());
        dto.setDireccionEnvio(usuario.getDireccionEnvio());
        dto.setRuc(usuario.getRuc());
        dto.setRazonSocial(usuario.getRazonSocial());
        dto.setTelefono2(usuario.getTelefono2());
        dto.setFechaActualizacion(usuario.getFechaActualizacion());
        return dto;
    }

    // Convierte DTO de respuesta a entity
    public Usuario ToEntity(UsuarioDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNombres(dto.getNombres());
        usuario.setTelefono(dto.getTelefono());
        usuario.setCorreo(dto.getCorreo());
        usuario.setRol(dto.getRol());
        // Nuevos campos
        usuario.setDni(dto.getDni());
        usuario.setFechaNacimiento(dto.getFechaNacimiento());
        usuario.setDireccionEnvio(dto.getDireccionEnvio());
        usuario.setRuc(dto.getRuc());
        usuario.setRazonSocial(dto.getRazonSocial());
        usuario.setTelefono2(dto.getTelefono2());
        usuario.setFechaActualizacion(dto.getFechaActualizacion());
        return usuario;
    }

    // Convierte DTO de request a entity para creación y edición
    public Usuario ToEntity(UsuarioRequestDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNombres(dto.getNombres());
        usuario.setTelefono(dto.getTelefono());
        usuario.setCorreo(dto.getCorreo());
        usuario.setRol(dto.getRol());
        // Nuevos campos
        usuario.setDni(dto.getDni());
        usuario.setFechaNacimiento(dto.getFechaNacimiento());
        usuario.setDireccionEnvio(dto.getDireccionEnvio());
        usuario.setRuc(dto.getRuc());
        usuario.setRazonSocial(dto.getRazonSocial());
        usuario.setTelefono2(dto.getTelefono2());
        return usuario;
    }   
}
