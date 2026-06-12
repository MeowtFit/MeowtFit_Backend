-- login admin
-- correo: admin@gmail.com.pe
-- contraseña: test1234
INSERT INTO Usuario (idUsuario, nombres, correo, telefono, contrasena, estadoCuenta, rol, fechaCreacion, fechaActualizacion) VALUES
(1, 'ABC', 'admin@gmail.com.pe', '123456789', '$2a$10$KK4hFlseROkNV1fgqdrk1.yyxf2gjGElcvxLEY0fF1UjwFJHmzB0G', 'ACTIVO', 'ADMINISTRADOR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'XYZ', 'XYZ@gmail.com.pe', '123456789', '$2a$10$KK4hFlseROkNV1fgqdrk1.yyxf2gjGElcvxLEY0fF1UjwFJHmzB0G', 'ACTIVO', 'CLIENTE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'DEF', 'DEF@gmail.com.pe', '123456789', '$2a$10$KK4hFlseROkNV1fgqdrk1.yyxf2gjGElcvxLEY0fF1UjwFJHmzB0G', 'ACTIVO', 'COMERCIANTE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

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
('Polo Clásico Blanco', 35.00, 'ACTIVO', 'Polo de algodón de alta calidad, cómodo y versátil para cualquier ocasión.', 'https://res.cloudinary.com/dkflowz3b/image/upload/v1781133876/polo_blanco_iqu7gi.png', 5);

-- VARIANTES DE PRODUCTOS (Tallas, colores y stock)
-- =====================================================
-- TABLA VARIANTEPRODUCTO (ahora con idColor)
-- =====================================================
-- Variantes para: Blusa de Seda Estampada (idProducto = 1)
INSERT INTO VarianteProducto (talla, idColor, stockDisponible, stockReservado, idProducto) VALUES 
('S', (SELECT idColor FROM Color WHERE nombre = 'Blanco'), 50, 0, 1),
('M', (SELECT idColor FROM Color WHERE nombre = 'Blanco'), 40, 5, 1),
('L', (SELECT idColor FROM Color WHERE nombre = 'Blanco'), 20, 0, 1),
('S', (SELECT idColor FROM Color WHERE nombre = 'Rosa Pastel'), 30, 2, 1);

-- Variantes para: Pantalón Jean High Waist (idProducto = 2)
INSERT INTO VarianteProducto (talla, idColor, stockDisponible, stockReservado, idProducto) VALUES 
('28', (SELECT idColor FROM Color WHERE nombre = 'Azul Clásico'), 100, 10, 2),
('30', (SELECT idColor FROM Color WHERE nombre = 'Azul Clásico'), 80, 5, 2),
('32', (SELECT idColor FROM Color WHERE nombre = 'Azul Clásico'), 60, 0, 2),
('28', (SELECT idColor FROM Color WHERE nombre = 'Negro'), 50, 0, 2);

-- Variantes para: Vestido de Noche Elegante (idProducto = 3)
INSERT INTO VarianteProducto (talla, idColor, stockDisponible, stockReservado, idProducto) VALUES 
('S', (SELECT idColor FROM Color WHERE nombre = 'Rojo Vino'), 15, 2, 3),
('M', (SELECT idColor FROM Color WHERE nombre = 'Rojo Vino'), 10, 0, 3),
('M', (SELECT idColor FROM Color WHERE nombre = 'Negro Noche'), 25, 5, 3);

-- Variantes para: Casaca Cortavientos Ligera (idProducto = 4)
INSERT INTO VarianteProducto (talla, idColor, stockDisponible, stockReservado, idProducto) VALUES 
('Única', (SELECT idColor FROM Color WHERE nombre = 'Beige'), 45, 0, 4),
('Única', (SELECT idColor FROM Color WHERE nombre = 'Negro'), 60, 2, 4);

-- Variantes para: Falda Plisada Midi (idProducto = 5)
INSERT INTO VarianteProducto (talla, idColor, stockDisponible, stockReservado, idProducto) VALUES 
('S', (SELECT idColor FROM Color WHERE nombre = 'Verde Olivo'), 35, 0, 5),
('M', (SELECT idColor FROM Color WHERE nombre = 'Verde Olivo'), 40, 0, 5);

-- Variantes para: Top Básico Rib (idProducto = 6)
INSERT INTO VarianteProducto (talla, idColor, stockDisponible, stockReservado, idProducto) VALUES 
('S', (SELECT idColor FROM Color WHERE nombre = 'Blanco'), 100, 0, 6),
('M', (SELECT idColor FROM Color WHERE nombre = 'Blanco'), 150, 10, 6),
('S', (SELECT idColor FROM Color WHERE nombre = 'Negro'), 100, 5, 6);

-- Variantes para: Polo Clásico Blanco (idProducto = 7)
INSERT INTO VarianteProducto (talla, idColor, stockDisponible, stockReservado, idProducto) VALUES 
('S', (SELECT idColor FROM Color WHERE nombre = 'Blanco'), 80, 0, 7),
('M', (SELECT idColor FROM Color WHERE nombre = 'Blanco'), 120, 8, 7),
('L', (SELECT idColor FROM Color WHERE nombre = 'Blanco'), 90, 0, 7),
('M', (SELECT idColor FROM Color WHERE nombre = 'Azul Clásico'), 60, 2, 7),
('L', (SELECT idColor FROM Color WHERE nombre = 'Azul Clásico'), 50, 0, 7);

INSERT INTO ConfiguracionNegocio (stockMinimoCotizacion, porcentajePrecioPiso) 
VALUES (1000, 20.00);

-- Descuentos para: Blusa de Seda Estampada (idProducto = 1)
-- Llevando de 12 a 49 unidades -> 5% de descuento
-- Llevando de 50 a 999 unidades -> 10% de descuento
INSERT INTO ReglaDescuento (rangoMinimo, rangoMaximo, porcentaje, idProducto) VALUES 
(12, 49, 5.00, 1),
(50, 999, 10.00, 1);

-- Descuentos para: Pantalón Jean High Waist (idProducto = 2)
-- Llevando de 6 a 24 unidades -> 8% de descuento
-- Llevando de 25 a 999 unidades -> 15% de descuento
INSERT INTO ReglaDescuento (rangoMinimo, rangoMaximo, porcentaje, idProducto) VALUES 
(6, 24, 8.00, 2),
(25, 999, 15.00, 2);

-- Descuentos para: Vestido de Noche Elegante (idProducto = 3)
-- Al ser más caro, el descuento empieza a partir de pocas unidades
INSERT INTO ReglaDescuento (rangoMinimo, rangoMaximo, porcentaje, idProducto) VALUES 
(3, 10, 5.00, 3),
(11, 999, 12.00, 3);

-- Descuentos para: Top Básico Rib (idProducto = 6)
-- Al ser un producto básico de alta rotación, requiere comprar más volumen para el descuento
INSERT INTO ReglaDescuento (rangoMinimo, rangoMaximo, porcentaje, idProducto) VALUES 
(24, 99, 10.00, 6),
(100, 499, 15.00, 6),
(500, 999, 25.00, 6);