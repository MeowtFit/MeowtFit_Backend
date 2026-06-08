package com.meowtfit.backend.usuario.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meowtfit.backend.usuario.dto.LoginRequestDTO;
import com.meowtfit.backend.usuario.dto.LoginResponseDTO;
import com.meowtfit.backend.exception.BadRequestException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

// Controlador de autenticación — maneja el login sin uso de tokens
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    // Autentica al usuario

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> Login(@Valid @RequestBody LoginRequestDTO dto, HttpServletRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getCorreo(), dto.getContrasena()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        HttpSession session = request.getSession(true);
        session.setAttribute(
            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
            SecurityContextHolder.getContext()
        );

        String rol = authentication.getAuthorities()
                .stream()
                .findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElseThrow(() -> new BadRequestException("El usuario no tiene rol asignado"));

        return ResponseEntity.ok(new LoginResponseDTO(dto.getCorreo(), rol));
    }

}
