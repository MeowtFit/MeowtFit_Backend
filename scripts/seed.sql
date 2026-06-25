-- login admin
-- correo: admin@gmail.com.pe
-- contraseña: test1234
INSERT INTO Usuario (nombres, correo, telefono, contrasena, estadoCuenta, rol, fechaCreacion, fechaActualizacion) VALUES
('Super Admin', 'admin@gmail.com', '999888777', '$2a$10$KK4hFlseROkNV1fgqdrk1.yyxf2gjGElcvxLEY0fF1UjwFJHmzB0G', 'ACTIVO', 'ADMINISTRADOR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Comerciante Juan', 'comerciante@gmail.com', '987654321', '$2a$10$KK4hFlseROkNV1fgqdrk1.yyxf2gjGElcvxLEY0fF1UjwFJHmzB0G', 'ACTIVO', 'COMERCIANTE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- B2C Cliente
INSERT INTO Usuario (nombres, correo, telefono, contrasena, estadoCuenta, rol, dni, fechaNacimiento, direccionEnvio, fechaCreacion, fechaActualizacion) VALUES
('Cliente Normal', 'usuariob2c@gmail.com', '911222333', '$2a$10$KK4hFlseROkNV1fgqdrk1.yyxf2gjGElcvxLEY0fF1UjwFJHmzB0G', 'ACTIVO', 'CLIENTE', '71234567', '1995-05-15', 'Av. Las Palmas 123', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- B2B Cliente Mayorista
INSERT INTO Usuario (nombres, correo, telefono, contrasena, estadoCuenta, rol, ruc, razonSocial, telefono2, fechaCreacion, fechaActualizacion) VALUES
('Empresa Mayorista SAC', 'usuariob2b@gmail.com', '955444333', '$2a$10$KK4hFlseROkNV1fgqdrk1.yyxf2gjGElcvxLEY0fF1UjwFJHmzB0G', 'ACTIVO', 'CLIENTE', '20123456789', 'Distribuidora Textil Norte SAC', '01-445-6789', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO Color (nombre, hexadecimal) VALUES 
('Blanco', '#FFFFFF'),
('Negro', '#000000'),
('Rojo Vino', '#722F37'),
('Rosa Pastel', '#FFD1DC'),
('Azul Clásico', '#000080'),
('Beige', '#F5F5DC'),
('Verde Olivo', '#556B2F'),
('Negro Noche', '#1a1a1a'),
('Gris', '#808080'),
('Marrón', '#8B4513'),
('Rojo', '#FF0000'),
('Azul', '#0000FF'),
('Verde', '#008000'),
('Amarillo', '#FFFF00'),
('Naranja', '#FFA500'),
('Morado', '#800080'),
('Celeste', '#87CEEB'),
('Dorado', '#FFD700'),
('Plateado', '#C0C0C0'),
('Lavanda', '#E6E6FA');

-- ya está con autoincrement
INSERT INTO Categoria (nombre) VALUES ('Blusas'); -- id: 1
INSERT INTO Categoria (nombre) VALUES ('Vestidos'); -- id: 2
INSERT INTO Categoria (nombre) VALUES ('Tops'); -- id:3
INSERT INTO Categoria (nombre) VALUES ('Camisas'); -- id: 4
INSERT INTO Categoria (nombre) VALUES ('Polos'); -- id: 5
INSERT INTO Categoria (nombre) VALUES ('Pantalones'); -- id: 6
INSERT INTO Categoria (nombre) VALUES ('Shorts'); -- id: 7
INSERT INTO Categoria (nombre) VALUES ('Faldas'); -- id: 8
INSERT INTO Categoria (nombre) VALUES ('Casacas'); -- id: 9

INSERT INTO Producto (nombre, precioBase, estado, descripcion, imagenUrl, idCategoria) VALUES 
('Blusa de Seda Estampada', 45.00, 'ACTIVO', 'Blusa de seda suave con estampado floral de temporada, ideal para verano.', 'https://res.cloudinary.com/dkflowz3b/image/upload/v1781282249/blusa_estampada_dyoeew.jpg', 1),
('Pantalón Jean High Waist', 65.00, 'ACTIVO', 'Pantalón jean denim de tiro alto, corte recto y ajuste perfecto.', 'https://res.cloudinary.com/dkflowz3b/image/upload/v1781282683/pantalon_jean_e8a621.jpg', 6),
('Vestido de Noche Elegante', 120.00, 'ACTIVO', 'Vestido largo de fiesta con abertura lateral y escote en V.', 'https://res.cloudinary.com/dkflowz3b/image/upload/v1781282683/vestido_noche_kkc7g6.jpg', 2),
('Casaca Cortavientos Ligera', 85.00, 'ACTIVO', 'Casaca impermeable y ligera con capucha ajustable.', 'https://res.cloudinary.com/dkflowz3b/image/upload/v1781282739/casaca_cortavientos_sc2vxz.jpg', 9),
('Falda Plisada Midi', 55.00, 'ACTIVO', 'Falda midi con pliegues, cintura elástica muy cómoda.', 'https://res.cloudinary.com/dkflowz3b/image/upload/v1781282893/falda_plisada_ucqzmf.jpg', 8),
('Top Básico Rib', 25.00, 'ACTIVO', 'Top básico de tela rib tejida, ajustado al cuerpo.', 'https://res.cloudinary.com/dkflowz3b/image/upload/v1781282994/top_basico_rib_uneghn.jpg', 3),
('Polo Clásico', 35.00, 'ACTIVO', 'Polo de algodón de alta calidad, cómodo y versátil para cualquier ocasión.', 'https://res.cloudinary.com/dkflowz3b/image/upload/v1781133876/polo_blanco_iqu7gi.png', 5);

-- VARIANTES DE PRODUCTOS (Tallas, colores y stock)
-- =====================================================
-- TABLA VARIANTEPRODUCTO (ahora con idColor)
-- =====================================================

-- Variantes para: Blusa de Seda Estampada (idProducto = 1)
INSERT INTO VarianteProducto (talla, idColor, stockDisponible, stockReservado, idProducto) VALUES 
('S', (SELECT idColor FROM Color WHERE nombre = 'Blanco'), 50, 0, 1),
('M', (SELECT idColor FROM Color WHERE nombre = 'Blanco'), 40, 5, 1),
('L', (SELECT idColor FROM Color WHERE nombre = 'Blanco'), 20, 0, 1),
('S', (SELECT idColor FROM Color WHERE nombre = 'Rosa Pastel'), 30, 2, 1),
('M', (SELECT idColor FROM Color WHERE nombre = 'Rosa Pastel'), 35, 0, 1),
('L', (SELECT idColor FROM Color WHERE nombre = 'Rosa Pastel'), 25, 0, 1),
('XS', (SELECT idColor FROM Color WHERE nombre = 'Celeste'), 15, 0, 1),
('S', (SELECT idColor FROM Color WHERE nombre = 'Celeste'), 45, 0, 1),
('M', (SELECT idColor FROM Color WHERE nombre = 'Celeste'), 45, 5, 1),
('L', (SELECT idColor FROM Color WHERE nombre = 'Celeste'), 30, 0, 1),
('S', (SELECT idColor FROM Color WHERE nombre = 'Lavanda'), 20, 0, 1),
('M', (SELECT idColor FROM Color WHERE nombre = 'Lavanda'), 20, 0, 1);

-- Variantes para: Pantalón Jean High Waist (idProducto = 2)
INSERT INTO VarianteProducto (talla, idColor, stockDisponible, stockReservado, idProducto) VALUES 
('28', (SELECT idColor FROM Color WHERE nombre = 'Azul Clásico'), 100, 10, 2),
('30', (SELECT idColor FROM Color WHERE nombre = 'Azul Clásico'), 80, 5, 2),
('32', (SELECT idColor FROM Color WHERE nombre = 'Azul Clásico'), 60, 0, 2),
('28', (SELECT idColor FROM Color WHERE nombre = 'Negro'), 50, 0, 2),
('26', (SELECT idColor FROM Color WHERE nombre = 'Azul Clásico'), 40, 0, 2),
('34', (SELECT idColor FROM Color WHERE nombre = 'Azul Clásico'), 45, 0, 2),
('30', (SELECT idColor FROM Color WHERE nombre = 'Negro'), 60, 2, 2),
('32', (SELECT idColor FROM Color WHERE nombre = 'Negro'), 55, 0, 2),
('34', (SELECT idColor FROM Color WHERE nombre = 'Negro'), 30, 0, 2),
('28', (SELECT idColor FROM Color WHERE nombre = 'Gris'), 40, 0, 2),
('30', (SELECT idColor FROM Color WHERE nombre = 'Gris'), 40, 0, 2),
('32', (SELECT idColor FROM Color WHERE nombre = 'Gris'), 35, 0, 2);

-- Variantes para: Vestido de Noche Elegante (idProducto = 3)
INSERT INTO VarianteProducto (talla, idColor, stockDisponible, stockReservado, idProducto) VALUES 
('S', (SELECT idColor FROM Color WHERE nombre = 'Rojo Vino'), 15, 2, 3),
('M', (SELECT idColor FROM Color WHERE nombre = 'Rojo Vino'), 10, 0, 3),
('M', (SELECT idColor FROM Color WHERE nombre = 'Negro Noche'), 25, 5, 3),
('S', (SELECT idColor FROM Color WHERE nombre = 'Negro Noche'), 20, 0, 3),
('L', (SELECT idColor FROM Color WHERE nombre = 'Negro Noche'), 15, 0, 3),
('S', (SELECT idColor FROM Color WHERE nombre = 'Dorado'), 10, 0, 3),
('M', (SELECT idColor FROM Color WHERE nombre = 'Dorado'), 12, 0, 3),
('L', (SELECT idColor FROM Color WHERE nombre = 'Dorado'), 8, 0, 3),
('S', (SELECT idColor FROM Color WHERE nombre = 'Plateado'), 14, 0, 3),
('M', (SELECT idColor FROM Color WHERE nombre = 'Plateado'), 15, 2, 3),
('L', (SELECT idColor FROM Color WHERE nombre = 'Plateado'), 5, 0, 3);

-- Variantes para: Casaca Cortavientos Ligera (idProducto = 4)
INSERT INTO VarianteProducto (talla, idColor, stockDisponible, stockReservado, idProducto) VALUES 
('Única', (SELECT idColor FROM Color WHERE nombre = 'Beige'), 45, 0, 4),
('Única', (SELECT idColor FROM Color WHERE nombre = 'Negro'), 60, 2, 4),
('Única', (SELECT idColor FROM Color WHERE nombre = 'Gris'), 50, 0, 4),
('Única', (SELECT idColor FROM Color WHERE nombre = 'Azul'), 30, 0, 4),
('Única', (SELECT idColor FROM Color WHERE nombre = 'Rojo'), 25, 0, 4),
('Única', (SELECT idColor FROM Color WHERE nombre = 'Verde Olivo'), 40, 5, 4);

-- Variantes para: Falda Plisada Midi (idProducto = 5)
INSERT INTO VarianteProducto (talla, idColor, stockDisponible, stockReservado, idProducto) VALUES 
('S', (SELECT idColor FROM Color WHERE nombre = 'Verde Olivo'), 35, 0, 5),
('M', (SELECT idColor FROM Color WHERE nombre = 'Verde Olivo'), 40, 0, 5),
('L', (SELECT idColor FROM Color WHERE nombre = 'Verde Olivo'), 20, 0, 5),
('S', (SELECT idColor FROM Color WHERE nombre = 'Negro'), 50, 0, 5),
('M', (SELECT idColor FROM Color WHERE nombre = 'Negro'), 60, 5, 5),
('L', (SELECT idColor FROM Color WHERE nombre = 'Negro'), 40, 0, 5),
('S', (SELECT idColor FROM Color WHERE nombre = 'Beige'), 30, 0, 5),
('M', (SELECT idColor FROM Color WHERE nombre = 'Beige'), 35, 0, 5),
('S', (SELECT idColor FROM Color WHERE nombre = 'Rosa Pastel'), 25, 0, 5),
('M', (SELECT idColor FROM Color WHERE nombre = 'Rosa Pastel'), 25, 0, 5);

-- Variantes para: Top Básico Rib (idProducto = 6)
INSERT INTO VarianteProducto (talla, idColor, stockDisponible, stockReservado, idProducto) VALUES 
('S', (SELECT idColor FROM Color WHERE nombre = 'Blanco'), 100, 0, 6),
('M', (SELECT idColor FROM Color WHERE nombre = 'Blanco'), 150, 10, 6),
('L', (SELECT idColor FROM Color WHERE nombre = 'Blanco'), 80, 0, 6),
('S', (SELECT idColor FROM Color WHERE nombre = 'Negro'), 100, 5, 6),
('M', (SELECT idColor FROM Color WHERE nombre = 'Negro'), 120, 0, 6),
('L', (SELECT idColor FROM Color WHERE nombre = 'Negro'), 70, 0, 6),
('S', (SELECT idColor FROM Color WHERE nombre = 'Rojo'), 60, 0, 6),
('M', (SELECT idColor FROM Color WHERE nombre = 'Rojo'), 65, 0, 6),
('S', (SELECT idColor FROM Color WHERE nombre = 'Celeste'), 50, 0, 6),
('M', (SELECT idColor FROM Color WHERE nombre = 'Celeste'), 55, 0, 6),
('S', (SELECT idColor FROM Color WHERE nombre = 'Amarillo'), 40, 0, 6),
('M', (SELECT idColor FROM Color WHERE nombre = 'Amarillo'), 45, 0, 6);

-- Variantes para: Polo Clásico (idProducto = 7)
INSERT INTO VarianteProducto (talla, idColor, stockDisponible, stockReservado, idProducto) VALUES 
('S', (SELECT idColor FROM Color WHERE nombre = 'Blanco'), 80, 0, 7),
('M', (SELECT idColor FROM Color WHERE nombre = 'Blanco'), 120, 8, 7),
('L', (SELECT idColor FROM Color WHERE nombre = 'Blanco'), 90, 0, 7),
('XL', (SELECT idColor FROM Color WHERE nombre = 'Blanco'), 40, 0, 7),
('M', (SELECT idColor FROM Color WHERE nombre = 'Azul Clásico'), 60, 2, 7),
('L', (SELECT idColor FROM Color WHERE nombre = 'Azul Clásico'), 50, 0, 7),
('S', (SELECT idColor FROM Color WHERE nombre = 'Azul Clásico'), 45, 0, 7),
('S', (SELECT idColor FROM Color WHERE nombre = 'Gris'), 70, 0, 7),
('M', (SELECT idColor FROM Color WHERE nombre = 'Gris'), 100, 0, 7),
('L', (SELECT idColor FROM Color WHERE nombre = 'Gris'), 80, 0, 7),
('S', (SELECT idColor FROM Color WHERE nombre = 'Verde'), 50, 0, 7),
('M', (SELECT idColor FROM Color WHERE nombre = 'Verde'), 65, 0, 7),
('L', (SELECT idColor FROM Color WHERE nombre = 'Verde'), 45, 0, 7),
('M', (SELECT idColor FROM Color WHERE nombre = 'Negro'), 150, 10, 7),
('L', (SELECT idColor FROM Color WHERE nombre = 'Negro'), 110, 5, 7);

-- ============================================================
-- CONFIGURACIÓN DEL NEGOCIO Y REGLAS DE DESCUENTO
-- ============================================================

INSERT INTO ConfiguracionNegocio (stockMinimoCotizacion, porcentajePrecioPiso) 
VALUES (1000, 20.00);

-- Descuentos para: Blusa de Seda Estampada (idProducto = 1)
INSERT INTO ReglaDescuento (rangoMinimo, rangoMaximo, porcentaje, idProducto) VALUES 
(12, 49, 5.00, 1),
(50, 199, 10.00, 1),
(200, 999, 15.00, 1);

-- Descuentos para: Pantalón Jean High Waist (idProducto = 2)
INSERT INTO ReglaDescuento (rangoMinimo, rangoMaximo, porcentaje, idProducto) VALUES 
(6, 24, 8.00, 2),
(25, 99, 15.00, 2),
(100, 999, 20.00, 2);

-- Descuentos para: Vestido de Noche Elegante (idProducto = 3)
INSERT INTO ReglaDescuento (rangoMinimo, rangoMaximo, porcentaje, idProducto) VALUES 
(3, 10, 5.00, 3),
(11, 49, 12.00, 3),
(50, 999, 18.00, 3);

-- Descuentos para: Casaca Cortavientos Ligera (idProducto = 4)
INSERT INTO ReglaDescuento (rangoMinimo, rangoMaximo, porcentaje, idProducto) VALUES 
(5, 19, 5.00, 4),
(20, 99, 10.00, 4),
(100, 999, 15.00, 4);

-- Descuentos para: Falda Plisada Midi (idProducto = 5)
INSERT INTO ReglaDescuento (rangoMinimo, rangoMaximo, porcentaje, idProducto) VALUES 
(6, 29, 6.00, 5),
(30, 99, 12.00, 5),
(100, 999, 18.00, 5);

-- Descuentos para: Top Básico Rib (idProducto = 6)
INSERT INTO ReglaDescuento (rangoMinimo, rangoMaximo, porcentaje, idProducto) VALUES 
(24, 99, 10.00, 6),
(100, 499, 15.00, 6),
(500, 999, 25.00, 6),
(1000, 9999, 25.00, 7);
