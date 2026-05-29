package com.meowfit.backend.usuario.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.meowfit.backend.common.Estado;
import com.meowfit.backend.exception.BadRequestException;
import com.meowfit.backend.exception.ResourceNotFoundException;
import com.meowfit.backend.usuario.dto.CambiarContrasenaRequestDTO;
import com.meowfit.backend.usuario.dto.UsuarioDTO;
import com.meowfit.backend.usuario.dto.UsuarioRequestDTO;
import com.meowfit.backend.usuario.entity.Usuario;
import com.meowfit.backend.usuario.mapper.UsuarioMapper;
import com.meowfit.backend.usuario.repository.UsuarioRepository;

import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

        private final UsuarioRepository usuarioRepository;
        private final UsuarioMapper usuarioMapper;
        private final PasswordEncoder passwordEncoder;

        // Lista todos los usuarios
        public List<UsuarioDTO> ListarTodos() {
                return usuarioRepository.findAll()
                                .stream()
                                .map(usuarioMapper::ToDTO)
                                .toList();
        }

        // Busca un usuario por su ID
        public UsuarioDTO BuscarPorId(Long id) {
                Usuario usuario = usuarioRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Usuario no encontrado con id: " + id));
                return usuarioMapper.ToDTO(usuario);
        }

        // Crea un nuevo usuario
        public UsuarioDTO Crear(UsuarioRequestDTO dto) {
                usuarioRepository.findByCorreo(dto.getCorreo())
                                .ifPresent(existente -> {
                                        throw new BadRequestException(
                                                        "Ya existe un usuario con el correo: "
                                                                        + dto.getCorreo());
                                });

                // Valida que la contraseña esté presente en la creación
                if (dto.getContrasena() == null || dto.getContrasena().isBlank()) {
                        throw new BadRequestException("La contraseña es obligatoria al crear un usuario");
                }
                Usuario usuario = usuarioMapper.ToEntity(dto);
                // Hashea la contraseña antes de persistir
                usuario.setContrasena(passwordEncoder.encode(dto.getContrasena()));
                Usuario guardado = usuarioRepository.save(usuario);
                return usuarioMapper.ToDTO(guardado);
        }

        // Edita un usuario existente
        public UsuarioDTO Editar(Long id, UsuarioRequestDTO dto) {
                Usuario usuario = usuarioRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Usuario no encontrado con id: " + id));

                usuarioRepository.findByCorreo(dto.getCorreo())
                                .ifPresent(existente -> {
                                        if (!existente.getIdUsuario().equals(id)) {
                                                throw new BadRequestException(
                                                                "Ya existe un usuario con el correo: "
                                                                                + dto.getCorreo());
                                        }
                                });

                usuario.setNombres(dto.getNombres());
                usuario.setTelefono(dto.getTelefono());
                usuario.setCorreo(dto.getCorreo());
                usuario.setRol(dto.getRol());

                Usuario guardado = usuarioRepository.save(usuario);
                return usuarioMapper.ToDTO(guardado);
        }

        // Desactiva un usuario cambiando su estado a INACTIVO e invalida su sesion
        // activa
        public UsuarioDTO Desactivar(Long id) {
                Usuario usuario = usuarioRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Usuario no encontrado con id: " + id));

                usuario.setEstado(Estado.INACTIVO);
                Usuario guardado = usuarioRepository.save(usuario);
                return usuarioMapper.ToDTO(guardado);
        }

        // Activa un usuario cambiando su estado a ACTIVO
        public UsuarioDTO Activar(Long id) {
                Usuario usuario = usuarioRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Usuario no encontrado con id: " + id));

                usuario.setEstado(Estado.ACTIVO);
                Usuario guardado = usuarioRepository.save(usuario);
                return usuarioMapper.ToDTO(guardado);
        }

        // Cambia la contraseña de un usuario existente
        public UsuarioDTO CambiarContrasena(Long id, CambiarContrasenaRequestDTO dto) {
                // Obtiene el usuario autenticado desde el contexto de seguridad
                String correoAutenticado = org.springframework.security.core.context.SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getName();

                Usuario usuarioAutenticado = usuarioRepository.findByCorreo(correoAutenticado)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Usuario autenticado no encontrado"));

                Usuario usuarioObjetivo = usuarioRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Usuario no encontrado con id: " + id));

                // Solo el ADMINISTRADOR puede cambiar la contraseña de otro usuario
                boolean esMismousuario = usuarioAutenticado.getIdUsuario().equals(id);
                boolean esGerente = usuarioAutenticado.getRol() == Usuario.Rol.ADMINISTRADOR;

                if (!esMismousuario && !esGerente) {
                        throw new BadRequestException(
                                        "No tienes permiso para cambiar la contraseña de otro usuario");
                }

                // Hashea y actualiza la nueva contraseña
                usuarioObjetivo.setContrasena(passwordEncoder.encode(dto.getContrasena()));
                Usuario guardado = usuarioRepository.save(usuarioObjetivo);
                return usuarioMapper.ToDTO(guardado);
        }

}
