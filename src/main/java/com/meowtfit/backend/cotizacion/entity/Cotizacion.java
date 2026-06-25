package com.meowtfit.backend.cotizacion.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.meowtfit.backend.usuario.entity.Usuario;
import com.meowtfit.backend.catalogo.entity.Producto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Cotizacion")
public class Cotizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idCotizacion")
    private Long idCotizacion;

    @CreationTimestamp
    @Column(name = "fechaCreacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoCotizacion estado = EstadoCotizacion.PENDIENTE;

    @Column(name = "precioSugerido", precision = 10, scale = 2, nullable = false)
    private BigDecimal precioSugerido;

    @Column(name = "sustento", columnDefinition = "TEXT", nullable = false)
    private String sustento;

    @Column(name = "montoSugerido", precision = 10, scale = 2, nullable = false)
    private BigDecimal montoSugerido;

    @Column(name = "montoReal", precision = 10, scale = 2, nullable = false)
    private BigDecimal montoReal;

    // Relaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCliente", nullable = false)
    private Usuario cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idComerciante")
    private Usuario comerciante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idProducto", nullable = false)
    private Producto producto;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "cotizacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LineaCotizacion> lineas = new ArrayList<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "cotizacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contrapropuesta> contrapropuestas = new ArrayList<>();

    // Métodos helper
    public void addLinea(LineaCotizacion linea) {
        lineas.add(linea);
        linea.setCotizacion(this);
    }

    public void addContrapropuesta(Contrapropuesta contrapropuesta) {
        contrapropuestas.add(contrapropuesta);
        contrapropuesta.setCotizacion(this);
    }
}