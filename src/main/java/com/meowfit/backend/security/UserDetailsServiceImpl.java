package com.meowfit.backend.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.meowfit.backend.common.Estado;
import com.meowfit.backend.usuario.entity.Usuario;
import com.meowfit.backend.usuario.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

// Conecta Spring Security con la tabla acl_usuario para autenticar usuarios
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    // Busca el usuario por correo corporativo para el proceso de login
    // Lanza excepcion si el usuario no existe o esta inactivo
    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con correo: " + correo));

        if (usuario.getEstado() == Estado.INACTIVO) {
            throw new UsernameNotFoundException("Usuario inactivo: " + correo);
        }

        return new User(
                usuario.getCorreo(),
                usuario.getContrasena(),
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name())));
    }

}
