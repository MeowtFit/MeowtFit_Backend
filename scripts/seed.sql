-- login admin
-- correo: admin@gmail.com.pe
-- contraseña: test1234
INSERT INTO Usuario (idUsuario, nombres, correo, telefono, contrasena, estadoCuenta, rol, fechaCreacion, fechaActualizacion) VALUES
(1, 'ABC', 'admin@gmail.com.pe', '123456789', '$2a$10$KK4hFlseROkNV1fgqdrk1.yyxf2gjGElcvxLEY0fF1UjwFJHmzB0G', 'ACTIVO', 'ADMINISTRADOR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'XYZ', 'XYZ@gmail.com.pe', '123456789', '$2a$10$KK4hFlseROkNV1fgqdrk1.yyxf2gjGElcvxLEY0fF1UjwFJHmzB0G', 'ACTIVO', 'CLIENTE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'DEF', 'DEF@gmail.com.pe', '123456789', '$2a$10$KK4hFlseROkNV1fgqdrk1.yyxf2gjGElcvxLEY0fF1UjwFJHmzB0G', 'ACTIVO', 'COMERCIANTE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

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
('Blusa de Seda Estampada', 45.00, 'ACTIVO', 'Blusa de seda suave con estampado floral de temporada, ideal para verano.', 'https://res.cloudinary.com/dkflowz3b/image/upload/v1773689638/samples/chair.png', 1),
('Pantalón Jean High Waist', 65.00, 'ACTIVO', 'Pantalón jean denim de tiro alto, corte recto y ajuste perfecto.', 'https://res.cloudinary.com/dkflowz3b/image/upload/v1773689638/samples/chair.png', 6),
('Vestido de Noche Elegante', 120.00, 'ACTIVO', 'Vestido largo de fiesta con abertura lateral y escote en V.', 'https://res.cloudinary.com/dkflowz3b/image/upload/v1773689638/samples/chair.png', 2),
('Casaca Cortavientos Ligera', 85.00, 'ACTIVO', 'Casaca impermeable y ligera con capucha ajustable.', 'https://res.cloudinary.com/dkflowz3b/image/upload/v1773689638/samples/chair.png', 9),
('Falda Plisada Midi', 55.00, 'ACTIVO', 'Falda midi con pliegues, cintura elástica muy cómoda.', 'https://res.cloudinary.com/dkflowz3b/image/upload/v1773689638/samples/chair.png', 8),
('Top Básico Rib', 25.00, 'ACTIVO', 'Top básico de tela rib tejida, ajustado al cuerpo.', 'https://res.cloudinary.com/dkflowz3b/image/upload/v1773689638/samples/chair.png', 3);

-- VARIANTES DE PRODUCTOS (Tallas, colores y stock)

-- Variantes para: Blusa de Seda Estampada (idProducto = 1)
INSERT INTO VarianteProducto (talla, color, stockDisponible, stockReservado, idProducto) VALUES 
('S', 'Blanco', 50, 0, 1),
('M', 'Blanco', 40, 5, 1),
('L', 'Blanco', 20, 0, 1),
('S', 'Rosa Pastel', 30, 2, 1);

-- Variantes para: Pantalón Jean High Waist (idProducto = 2)
INSERT INTO VarianteProducto (talla, color, stockDisponible, stockReservado, idProducto) VALUES 
('28', 'Azul Clásico', 100, 10, 2),
('30', 'Azul Clásico', 80, 5, 2),
('32', 'Azul Clásico', 60, 0, 2),
('28', 'Negro', 50, 0, 2);

-- Variantes para: Vestido de Noche Elegante (idProducto = 3)
INSERT INTO VarianteProducto (talla, color, stockDisponible, stockReservado, idProducto) VALUES 
('S', 'Rojo Vino', 15, 2, 3),
('M', 'Rojo Vino', 10, 0, 3),
('M', 'Negro Noche', 25, 5, 3);

-- Variantes para: Casaca Cortavientos Ligera (idProducto = 4)
INSERT INTO VarianteProducto (talla, color, stockDisponible, stockReservado, idProducto) VALUES 
('Única', 'Beige', 45, 0, 4),
('Única', 'Negro', 60, 2, 4);

-- Variantes para: Falda Plisada Midi (idProducto = 5)
INSERT INTO VarianteProducto (talla, color, stockDisponible, stockReservado, idProducto) VALUES 
('S', 'Verde Olivo', 35, 0, 5),
('M', 'Verde Olivo', 40, 0, 5);

-- Variantes para: Top Básico Rib (idProducto = 6)
INSERT INTO VarianteProducto (talla, color, stockDisponible, stockReservado, idProducto) VALUES 
('S', 'Blanco', 100, 0, 6),
('M', 'Blanco', 150, 10, 6),
('S', 'Negro', 100, 5, 6);
