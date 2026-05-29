-- login admin
-- correo: admin@gmail.com.pe
-- contraseña: test1234
INSERT INTO meowfit.usuario (idUsuario, nombres, correo,telefono,contrasena, estadoCuenta,rol) VALUES
(1, 'ABC', 'admin@gmail.com.pe', '123456789', '$2a$10$KK4hFlseROkNV1fgqdrk1.yyxf2gjGElcvxLEY0fF1UjwFJHmzB0G','ACTIVO', 'ADMINISTRADOR' ),
(2, 'XYZ', 'XYZ@gmail.com.pe', '123456789', '$2a$10$KK4hFlseROkNV1fgqdrk1.yyxf2gjGElcvxLEY0fF1UjwFJHmzB0G','ACTIVO', 'CLIENTE'),
(3, 'DEF', 'DEF@gmail.com.pe', '123456789', '$2a$10$KK4hFlseROkNV1fgqdrk1.yyxf2gjGElcvxLEY0fF1UjwFJHmzB0G','ACTIVO', 'COMERCIANTE');