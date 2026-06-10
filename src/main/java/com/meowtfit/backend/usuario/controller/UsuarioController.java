package com.meowtfit.backend.usuario.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meowtfit.backend.usuario.dto.CambiarContrasenaRequestDTO;
import com.meowtfit.backend.usuario.dto.UsuarioDTO;
import com.meowtfit.backend.usuario.dto.UsuarioRequestDTO;
import com.meowtfit.backend.usuario.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // Lista todos los usuarios
    @GetMapping
    public List<UsuarioDTO> ListarTodos() {
        return usuarioService.ListarTodos();
    }

    // Lista solo los usuarios con rol COMERCIANTE
    @GetMapping("/comerciantes")
    public List<UsuarioDTO> ListarComerciantes() {
        return usuarioService.ListarComerciantes();
    }

    // Obtiene un usuario por su ID
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> BuscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.BuscarPorId(id));
    }

    // Crea un nuevo usuario
    @PostMapping
    public ResponseEntity<UsuarioDTO> Crear(@Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.Crear(dto));
    }

    // Edita un usuario existente
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> Editar(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.ok(usuarioService.Editar(id, dto));
    }

    // Desactiva un usuario cambiando su estado a INACTIVO
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<UsuarioDTO> Desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.Desactivar(id));
    }

    // Activa un usuario cambiando su estado a ACTIVO
    @PatchMapping("/{id}/activar")
    public ResponseEntity<UsuarioDTO> Activar(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.Activar(id));
    }

    // Cambia la contraseña de un usuario
    @PatchMapping("/{id}/cambiar-contrasena")
    public ResponseEntity<UsuarioDTO> CambiarContrasena(@PathVariable Long id,
            @Valid @RequestBody CambiarContrasenaRequestDTO dto) {
        return ResponseEntity.ok(usuarioService.CambiarContrasena(id, dto));
    }

}
