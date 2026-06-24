package com.meowtfit.backend.cotizacion.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.meowtfit.backend.pedido.entity.Pedido;
import com.meowtfit.backend.usuario.entity.Usuario;
import com.meowtfit.backend.catalogo.entity.ReglaDescuento; 

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
    @Column(name = "fecha", updatable = false)
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoCotizacion estado = EstadoCotizacion.PENDIENTE;

    @Column(name = "precioPresupuesto", precision = 10, scale = 2)
    private BigDecimal precioPresupuesto;

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "montoTotal", precision = 10, scale = 2)
    private BigDecimal montoTotal;

    @Column(name = "descuento", precision = 10, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;

    // Relaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idRegla")
    private ReglaDescuento reglaDescuento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idPedido")
    private Pedido pedido;  // Opcional: se vincula si el pedido generó esta cotización

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