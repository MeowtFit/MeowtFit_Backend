package com.meowtfit.backend.facturacion.entity;

import com.meowtfit.backend.pedido.entity.Pedido;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ComprobantePago")
public class ComprobantePago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idComprobante")
    private Long idComprobante;

    @Column(name = "archivo", nullable = false, length = 255)
    private String archivo;

    @CreationTimestamp
    @Column(name = "fechaSubida", updatable = false)
    private LocalDateTime fechaSubida;

    @Column(name = "tipoArchivo", length = 50)
    private String tipoArchivo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idPedido", unique = true)
    private Pedido pedido;
}
