package com.meowtfit.backend.negocio.repository;

import com.meowtfit.backend.negocio.entity.ConfiguracionNegocio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ConfiguracionNegocioRepository extends JpaRepository<ConfiguracionNegocio, Long> {
    Optional<ConfiguracionNegocio> findTopByOrderByIdConfiguracionAsc();
}
