package com.meowtfit.backend.pedido.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.meowtfit.backend.usuario.entity.Usuario;

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
@Table(name = "Pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPedido")
    private Long idPedido;

    @CreationTimestamp
    @Column(name = "fechaHoraRegistro", updatable = false)
    private LocalDateTime fechaHoraRegistro;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoPedido estado = EstadoPedido.REGISTRADO;

    @Column(name = "montoTotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotal;

    @Column(name = "metodoPago", length = 50)
    private String metodoPago;

    @Column(name = "descuento", precision = 10, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;

    @Column(name = "fechaEntrega")
    private LocalDate fechaEntrega;

    @Column(name = "igv", precision = 10, scale = 2)
    private BigDecimal igv;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario usuario;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LineaPedido> lineas = new ArrayList<>();

    // Método helper para agregar líneas y mantener la relación bidireccional
    public void addLinea(LineaPedido linea) {
        lineas.add(linea);
        linea.setPedido(this);
    }
}