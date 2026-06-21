package com.meowtfit.backend.usuario.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.meowtfit.backend.usuario.entity.TokenReinicioContrasena;
import com.meowtfit.backend.usuario.entity.Usuario;

@Repository
public interface TokenReinicioContrasenaRepository extends JpaRepository<TokenReinicioContrasena, Long> {

    Optional<TokenReinicioContrasena> findByToken(String token);

    @Modifying
    @Query("DELETE FROM TokenReinicioContrasena t WHERE t.usuario = :usuario")
    void deleteByUsuario(Usuario usuario);
}
