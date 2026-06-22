package com.meowtfit.backend.cotizacion.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Contrapropuesta")
public class Contrapropuesta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idContrapropuesta")
    private Long idContrapropuesta;

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @CreationTimestamp
    @Column(name = "fecha", updatable = false)
    private LocalDateTime fecha;

    @Column(name = "precioNuevo", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioNuevo;

    // Relación
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCotizacion", nullable = false)
    private Cotizacion cotizacion;
}