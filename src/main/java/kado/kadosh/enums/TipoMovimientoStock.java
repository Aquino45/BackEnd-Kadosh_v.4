package kado.kadosh.enums;

public enum TipoMovimientoStock {
    ENTRADA,    // Ingreso manual de mercadería (compra a proveedor)
    SALIDA,     // Salida manual (merma, daño, robo)
    AJUSTE,     // Corrección tras conteo físico de inventario
    VENTA,      // Descuento automático al crear una cotización
    ANULACION   // Restitución automática al anular una factura
}
