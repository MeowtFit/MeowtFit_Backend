package com.meowtfit.backend.usuario.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meowtfit.backend.usuario.dto.EmailRequestDTO;
import com.meowtfit.backend.usuario.dto.ResetPasswordDTO;
import com.meowtfit.backend.usuario.service.PasswordResetService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth/recuperar-password")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/solicitar")
    public ResponseEntity<Map<String, String>> solicitarRecuperacion(@Valid @RequestBody EmailRequestDTO request) {
        passwordResetService.solicitarRecuperacion(request.getCorreo());
        // Se devuelve siempre 200 OK con el mismo mensaje para evitar enumeración de correos
        return ResponseEntity.ok(Map.of("mensaje", "Si el correo está registrado, recibirá un enlace de recuperación."));
    }

    @PostMapping("/verificar-token")
    public ResponseEntity<Map<String, String>> verificarToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "El token es obligatorio."));
        }
        
        passwordResetService.verificarToken(token);
        return ResponseEntity.ok(Map.of("mensaje", "Token válido."));
    }

    @PostMapping("/confirmar")
    public ResponseEntity<Map<String, String>> confirmarRecuperacion(@Valid @RequestBody ResetPasswordDTO request) {
        passwordResetService.confirmarRecuperacion(request.getToken(), request.getNuevaContrasena(), request.getConfirmarContrasena());
        return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada exitosamente."));
    }
}
