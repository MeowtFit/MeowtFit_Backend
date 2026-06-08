package com.meowtfit.backend.catalogo.repository;

import com.meowtfit.backend.catalogo.entity.ConfiguracionNegocio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ConfiguracionNegocioRepository extends JpaRepository<ConfiguracionNegocio, Long> {
    Optional<ConfiguracionNegocio> findTopByOrderByIdConfiguracionAsc();
}