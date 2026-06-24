package com.meowtfit.backend.usuario.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meowtfit.backend.common.service.EmailService;
import com.meowtfit.backend.exception.BadRequestException;
import com.meowtfit.backend.usuario.entity.TokenReinicioContrasena;
import com.meowtfit.backend.usuario.entity.Usuario;
import com.meowtfit.backend.usuario.repository.TokenReinicioContrasenaRepository;
import com.meowtfit.backend.usuario.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UsuarioRepository usuarioRepository;
    private final TokenReinicioContrasenaRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    @Transactional
    public void solicitarRecuperacion(String correo) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);
        
        // Si no existe el usuario, no hacemos nada para evitar enumeración de cuentas
        if (usuarioOpt.isEmpty()) {
            return;
        }

        Usuario usuario = usuarioOpt.get();

        // 1. Invalidar/Eliminar tokens anteriores
        tokenRepository.deleteByUsuario(usuario);

        // 2. Crear nuevo token
        String tokenStr = UUID.randomUUID().toString();
        TokenReinicioContrasena token = TokenReinicioContrasena.builder()
                .token(tokenStr)
                .fechaExpiracion(LocalDateTime.now().plusMinutes(15)) // Expira en 15 minutos
                .usuario(usuario)
                .build();
        
        tokenRepository.save(token);

        // 3. Enviar correo (Se ejecuta asíncronamente gracias a @Async en EmailService)
        String enlaceRecuperacion = frontendUrl + "/recuperar-password?token=" + tokenStr;
        String asunto = "Recuperación de Contraseña - MeowtFit";
        String cuerpoHTML = "<h3>Hola " + usuario.getNombres() + ",</h3>"
                + "<p>Has solicitado restablecer tu contraseña.</p>"
                + "<p>Haz clic en el siguiente enlace para continuar. El enlace expirará en 15 minutos:</p>"
                + "<a href=\"" + enlaceRecuperacion + "\">Restablecer Contraseña</a>"
                + "<br><br><p>Si no fuiste tú, ignora este correo.</p>";

        emailService.sendEmail(usuario.getCorreo(), asunto, cuerpoHTML);
    }

    @Transactional(readOnly = true)
    public void verificarToken(String tokenStr) {
        TokenReinicioContrasena token = tokenRepository.findByToken(tokenStr)
                .orElseThrow(() -> new BadRequestException("El token es inválido."));

        if (token.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("El token ha expirado.");
        }
    }

    @Transactional
    public void confirmarRecuperacion(String tokenStr, String nuevaContrasena, String confirmarContrasena) {
        if (!nuevaContrasena.equals(confirmarContrasena)) {
            throw new BadRequestException("Las contraseñas no coinciden.");
        }

        TokenReinicioContrasena token = tokenRepository.findByToken(tokenStr)
                .orElseThrow(() -> new BadRequestException("El token es inválido."));

        if (token.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("El token ha expirado.");
        }

        Usuario usuario = token.getUsuario();

        // Actualizar contraseña
        String contrasenaEncriptada = passwordEncoder.encode(nuevaContrasena);
        usuarioRepository.updatePassword(usuario.getIdUsuario(), contrasenaEncriptada);

        // Eliminar el token usado
        tokenRepository.deleteByUsuario(usuario);
    }
}
