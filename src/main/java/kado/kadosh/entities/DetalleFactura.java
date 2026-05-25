package kado.kadosh.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "detalle_factura")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleFactura {

    @Id
    private UUID detalleFacturaId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_id")
    private Factura factura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @Column(length = 255)
    private String descripcion;

    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;

    private Boolean activo = true;

    public Double calcularSubtotal() {
        return (cantidad != null && precioUnitario != null) ? cantidad * precioUnitario : 0.0;
    }
}
