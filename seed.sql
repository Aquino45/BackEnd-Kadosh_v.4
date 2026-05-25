-- ============================================================
-- KADOSH - SEED DATA
-- Contrasena de todos los usuarios: password
-- ============================================================
-- Si un INSERT falla por nombre de columna, descomenta la
-- seccion DISCOVERY y verifica los nombres reales:
--
-- SELECT column_name, data_type
-- FROM user_tab_columns
-- WHERE table_name = UPPER('persona')
-- ORDER BY column_id;
-- ============================================================

SET DEFINE OFF;

-- ============================================================
-- 1. ROLES
-- ============================================================
INSERT INTO rol (rol_id, nombre)
VALUES (HEXTORAW('A0000000000000000000000000000001'), 'ADMIN');

INSERT INTO rol (rol_id, nombre)
VALUES (HEXTORAW('A0000000000000000000000000000002'), 'CAJERO');

INSERT INTO rol (rol_id, nombre)
VALUES (HEXTORAW('A0000000000000000000000000000003'), 'CLIENTE');

INSERT INTO rol (rol_id, nombre)
VALUES (HEXTORAW('A0000000000000000000000000000004'), 'OPTICO');

-- ============================================================
-- 2. PERSONAS
-- ============================================================
INSERT INTO persona (persona_id, nombre, apellido, edad, email, telefono, dni, activo, fecha_nacimiento, created_at)
VALUES (HEXTORAW('B0000000000000000000000000000001'), 'Admin', 'Kadosh', 30, 'admin@kadosh.pe', '999000001', '10000001', true, DATE '1994-01-01', SYSTIMESTAMP);

INSERT INTO persona (persona_id, nombre, apellido, edad, email, telefono, dni, activo, fecha_nacimiento, created_at)
VALUES (HEXTORAW('B0000000000000000000000000000002'), 'Maria', 'Quispe', 25, 'maria.q@gmail.com', '987654321', '72345678', true, DATE '1999-06-15', SYSTIMESTAMP);

INSERT INTO persona (persona_id, nombre, apellido, edad, email, telefono, dni, activo, fecha_nacimiento, created_at)
VALUES (HEXTORAW('B0000000000000000000000000000003'), 'Pedro', 'Cajero', 32, 'cajero@kadosh.pe', '955000003', '43210987', true, DATE '1992-03-22', SYSTIMESTAMP);

-- ============================================================
-- 3. USUARIOS  (password = "password")
-- ============================================================
INSERT INTO usuario (usuario_id, password, estado, created_at, persona_persona_id)
VALUES (HEXTORAW('C0000000000000000000000000000001'),
        '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.',
        'ACTIVO', SYSTIMESTAMP,
        HEXTORAW('B0000000000000000000000000000001'));

INSERT INTO usuario (usuario_id, password, estado, created_at, persona_persona_id)
VALUES (HEXTORAW('C0000000000000000000000000000002'),
        '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.',
        'ACTIVO', SYSTIMESTAMP,
        HEXTORAW('B0000000000000000000000000000002'));

INSERT INTO usuario (usuario_id, password, estado, created_at, persona_persona_id)
VALUES (HEXTORAW('C0000000000000000000000000000003'),
        '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.',
        'ACTIVO', SYSTIMESTAMP,
        HEXTORAW('B0000000000000000000000000000003'));

-- ============================================================
-- 4. USUARIO_ROL
-- ============================================================
INSERT INTO usuario_rol (usuario_rol_id, usuario_usuario_id, rol_rol_id)
VALUES (HEXTORAW('D0000000000000000000000000000001'),
        HEXTORAW('C0000000000000000000000000000001'),
        HEXTORAW('A0000000000000000000000000000001'));

INSERT INTO usuario_rol (usuario_rol_id, usuario_usuario_id, rol_rol_id)
VALUES (HEXTORAW('D0000000000000000000000000000002'),
        HEXTORAW('C0000000000000000000000000000002'),
        HEXTORAW('A0000000000000000000000000000003'));

INSERT INTO usuario_rol (usuario_rol_id, usuario_usuario_id, rol_rol_id)
VALUES (HEXTORAW('D0000000000000000000000000000003'),
        HEXTORAW('C0000000000000000000000000000003'),
        HEXTORAW('A0000000000000000000000000000002'));

-- ============================================================
-- 5. CATEGORIAS
-- ============================================================
INSERT INTO categoria (categoria_id, nombre, activo)
VALUES (HEXTORAW('E0000000000000000000000000000001'), 'Monturas', true);

INSERT INTO categoria (categoria_id, nombre, activo)
VALUES (HEXTORAW('E0000000000000000000000000000002'), 'Lentes de Contacto', true);

INSERT INTO categoria (categoria_id, nombre, activo)
VALUES (HEXTORAW('E0000000000000000000000000000003'), 'Accesorios Opticos', true);

-- ============================================================
-- 6. SUB_CATEGORIAS
-- ============================================================
INSERT INTO sub_categoria (sub_categoria_id, nombre, activo, categoria_categoria_id)
VALUES (HEXTORAW('F0000000000000000000000000000001'), 'Metalicas', true, HEXTORAW('E0000000000000000000000000000001'));

INSERT INTO sub_categoria (sub_categoria_id, nombre, activo, categoria_categoria_id)
VALUES (HEXTORAW('F0000000000000000000000000000002'), 'Acetato', true, HEXTORAW('E0000000000000000000000000000001'));

INSERT INTO sub_categoria (sub_categoria_id, nombre, activo, categoria_categoria_id)
VALUES (HEXTORAW('F0000000000000000000000000000003'), 'Blandas Diarias', true, HEXTORAW('E0000000000000000000000000000002'));

INSERT INTO sub_categoria (sub_categoria_id, nombre, activo, categoria_categoria_id)
VALUES (HEXTORAW('F0000000000000000000000000000004'), 'Liquidos', true, HEXTORAW('E0000000000000000000000000000003'));

-- ============================================================
-- 7. PRODUCTOS
-- ============================================================
INSERT INTO producto (producto_id, nombre, precio_actual, stock, activo, categoria_categoria_id, sub_categoria_sub_categoria_id)
VALUES (HEXTORAW('11000000000000000000000000000001'), 'Montura Aviador Pro', 150.00, 10, true,
        HEXTORAW('E0000000000000000000000000000001'), HEXTORAW('F0000000000000000000000000000001'));

INSERT INTO producto (producto_id, nombre, precio_actual, stock, activo, categoria_categoria_id, sub_categoria_sub_categoria_id)
VALUES (HEXTORAW('11000000000000000000000000000002'), 'Montura Cuadrada Titan', 200.00, 5, true,
        HEXTORAW('E0000000000000000000000000000001'), HEXTORAW('F0000000000000000000000000000002'));

INSERT INTO producto (producto_id, nombre, precio_actual, stock, activo, categoria_categoria_id, sub_categoria_sub_categoria_id)
VALUES (HEXTORAW('11000000000000000000000000000003'), 'Lentes Daily Acuvue', 80.00, 20, true,
        HEXTORAW('E0000000000000000000000000000002'), HEXTORAW('F0000000000000000000000000000003'));

INSERT INTO producto (producto_id, nombre, precio_actual, stock, activo, categoria_categoria_id, sub_categoria_sub_categoria_id)
VALUES (HEXTORAW('11000000000000000000000000000004'), 'Liquido Renu 355ml', 45.00, 30, true,
        HEXTORAW('E0000000000000000000000000000003'), HEXTORAW('F0000000000000000000000000000004'));

-- ============================================================
-- 8. VISION (para historial optico)
-- ============================================================
INSERT INTO vision (vision_id, esfera_o_d, esfera_o_i, cilindro_o_d, cilindro_o_i, eje_o_d, eje_o_i, adicion_o_d, adicion_o_i)
VALUES (HEXTORAW('BB000000000000000000000000000001'), -1.50, -1.75, -0.50, -0.25, 180, 175, 0.0, 0.0);

INSERT INTO vision (vision_id, esfera_o_d, esfera_o_i, cilindro_o_d, cilindro_o_i, eje_o_d, eje_o_i, adicion_o_d, adicion_o_i)
VALUES (HEXTORAW('BB000000000000000000000000000002'), 1.00, 1.25, -0.25, -0.50, 90, 85, 1.25, 1.25);

-- ============================================================
-- 9. HISTORIAL_OPTICO
-- ============================================================
INSERT INTO historial_optico (
    historial_optico_id, edad, fecha, recomendaciones, evaluador,
    created_at, usuario_usuario_id,
    vision_lejos_vision_id, vision_cerca_vision_id
) VALUES (
    HEXTORAW('CC000000000000000000000000000001'), 25, SYSTIMESTAMP,
    'Uso de lentes de contacto recomendado. Control en 6 meses.',
    'c0000000-0000-0000-0000-000000000001',
    SYSTIMESTAMP,
    HEXTORAW('C0000000000000000000000000000002'),
    HEXTORAW('BB000000000000000000000000000001'),
    HEXTORAW('BB000000000000000000000000000002')
);

-- ============================================================
-- 10. COTIZACION (pagada = lista para emitir factura)
-- ============================================================
INSERT INTO cotizacion (
    cotizacion_id, edad, total, estado_pago, activo,
    created_at, fecha_creacion, fecha_pago,
    usuario_usuario_id, historial_optico_historial_optico_id
) VALUES (
    HEXTORAW('DD000000000000000000000000000001'),
    25, 330.00, true, true,
    SYSTIMESTAMP,
    SYSTIMESTAMP,
    SYSTIMESTAMP,
    HEXTORAW('C0000000000000000000000000000002'),
    HEXTORAW('CC000000000000000000000000000001')
);

-- ============================================================
-- 11. DETALLE_COTIZACION
-- ============================================================
INSERT INTO detalle_cotizacion (
    detalle_id, cantidad, precio_congelado, activo,
    cotizacion_cotizacion_id, producto_producto_id
) VALUES (
    HEXTORAW('EE000000000000000000000000000001'),
    1, 150.00, true,
    HEXTORAW('DD000000000000000000000000000001'),
    HEXTORAW('11000000000000000000000000000001')
);

INSERT INTO detalle_cotizacion (
    detalle_id, cantidad, precio_congelado, activo,
    cotizacion_cotizacion_id, producto_producto_id
) VALUES (
    HEXTORAW('EE000000000000000000000000000002'),
    2, 80.00, true,
    HEXTORAW('DD000000000000000000000000000001'),
    HEXTORAW('11000000000000000000000000000003')
);

-- ============================================================
-- COMMIT
-- ============================================================
COMMIT;

-- ============================================================
-- REFERENCIA DE UUIDs PARA POSTMAN
-- ============================================================
-- Usuario admin (ADMIN):   c0000000-0000-0000-0000-000000000001  password: password
-- Usuario maria (CLIENTE): c0000000-0000-0000-0000-000000000002  password: password
-- Usuario cajero (CAJERO): c0000000-0000-0000-0000-000000000003  password: password
--
-- Producto Montura Aviador:   11000000-0000-0000-0000-000000000001  S/150
-- Producto Montura Cuadrada:  11000000-0000-0000-0000-000000000002  S/200
-- Producto Lentes Daily:      11000000-0000-0000-0000-000000000003  S/80
-- Producto Liquido Renu:      11000000-0000-0000-0000-000000000004  S/45
--
-- Categoria Monturas:         e0000000-0000-0000-0000-000000000001
-- Categoria Lentes Contacto:  e0000000-0000-0000-0000-000000000002
--
-- Cotizacion (pagada):        dd000000-0000-0000-0000-000000000001
--   → total: S/310 (1x150 + 2x80)
--   → LISTA PARA EMITIR FACTURA con POST /api/facturas
-- ============================================================

EXIT;
