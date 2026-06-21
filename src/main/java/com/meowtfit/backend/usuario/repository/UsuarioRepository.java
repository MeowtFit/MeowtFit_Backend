package com.meowtfit.backend.usuario.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.meowtfit.backend.usuario.entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Busca un usuario por su correo
    Optional<Usuario> findByCorreo(String correo);

    // Busca un usuario por su telefono
    Optional<Usuario> findByTelefono(String telefono);

    // Busca usuarios por rol
    List<Usuario> findByRol(Usuario.Rol rol);

    // Actualiza la contraseña de un usuario
    @Modifying
    @Query("UPDATE Usuario u SET u.contrasena = :nuevaContrasena WHERE u.idUsuario = :idUsuario")
    void updatePassword(Long idUsuario, String nuevaContrasena);
}
