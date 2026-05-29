 -- Descomentar el drop después de la primera corrida
 DROP DATABASE meowfit;
 CREATE DATABASE meowfit;
 USE meowfit;
-- Tabla: Usuario 
CREATE TABLE Usuario (
    idUsuario INT PRIMARY KEY AUTO_INCREMENT,
    nombres VARCHAR(100) NOT NULL,
    correo VARCHAR(100) UNIQUE NOT NULL,
    contrasena VARCHAR(255) NOT NULL,
    telefono VARCHAR(20),
    estadoCuenta ENUM('ACTIVO','INACTIVO') DEFAULT 'ACTIVO',
	fechaCreacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fechaActualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    rol ENUM('ADMINISTRADOR','COMERCIANTE', 'CLIENTE') NOT NULL DEFAULT 'CLIENTE'
);

-- Tabla: Cliente B2C
CREATE TABLE ClienteB2C (
    idUsuario INT PRIMARY KEY,
    apellidos VARCHAR(100) NOT NULL,
    dni VARCHAR(20) UNIQUE NOT NULL,
    fechaNacimiento DATE,
    direccionEnvio TEXT,
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario) ON DELETE CASCADE
);

-- Tabla: Comerciante
CREATE TABLE Comerciante (
    idUsuario INT PRIMARY KEY,
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario) ON DELETE CASCADE
);

-- Tabla: Cliente B2B
CREATE TABLE ClienteB2B (
    idUsuario INT PRIMARY KEY,
    ruc VARCHAR(20) UNIQUE NOT NULL,
    razonSocial VARCHAR(150) NOT NULL,
    telefono2 VARCHAR(20),
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario) ON DELETE CASCADE
);

-- Tabla: Categoria
CREATE TABLE Categoria (
    idCategoria INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL
);

-- Tabla: Producto
CREATE TABLE Producto (
    idProducto INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(150) NOT NULL,
    precioBase DECIMAL(10, 2) NOT NULL,
    estado ENUM('ACTIVO','INACTIVO') DEFAULT 'ACTIVO',
    descripcion TEXT,
    idCategoria INT NOT NULL,
    FOREIGN KEY (idCategoria) REFERENCES Categoria(idCategoria)
);

-- Tabla: VarianteProducto
CREATE TABLE VarianteProducto (
    idVariante INT PRIMARY KEY AUTO_INCREMENT,
    talla VARCHAR(10),
    color VARCHAR(50),
    stockDisponible INT DEFAULT 0,
    stockReservado INT DEFAULT 0,
    idProducto INT NOT NULL,
    FOREIGN KEY (idProducto) REFERENCES Producto(idProducto) ON DELETE CASCADE
);

-- Tabla: Pedido
CREATE TABLE Pedido (
    idPedido INT PRIMARY KEY AUTO_INCREMENT,
    fechaHoraRegistro DATETIME DEFAULT CURRENT_TIMESTAMP,
    estado ENUM('REGISTRADO', 'CONFIRMADO', 'ENVIADO', 'ENTREGADO') DEFAULT 'REGISTRADO',
    montoTotal DECIMAL(10, 2) NOT NULL,
    metodoPago VARCHAR(50),
    descuento DECIMAL(10, 2) DEFAULT 0,
    fechaEntrega DATE,
    igv DECIMAL(10, 2),
    idUsuario INT NOT NULL,
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario)
);

-- Tabla: LineaPedido
CREATE TABLE LineaPedido (
    idLineaPedido INT PRIMARY KEY AUTO_INCREMENT,
    cantidad INT NOT NULL,
    precioVenta DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    precioUnitario DECIMAL(10, 2) NOT NULL,
    descuentoAplicado DECIMAL(10, 2) DEFAULT 0,
    idPedido INT NOT NULL,
    idVariante INT NOT NULL,
    FOREIGN KEY (idPedido) REFERENCES Pedido(idPedido) ON DELETE CASCADE,
    FOREIGN KEY (idVariante) REFERENCES VarianteProducto(idVariante)
);

-- Tabla: Carrito
CREATE TABLE Carrito (
    idCarrito INT PRIMARY KEY AUTO_INCREMENT,
    fechaCreacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    estado ENUM('ACTIVO','INACTIVO') DEFAULT 'ACTIVO',
    idUsuario INT NOT NULL,
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario) ON DELETE CASCADE
);

-- Tabla: LineaCarrito
CREATE TABLE LineaCarrito (
    idLineaCarrito INT PRIMARY KEY AUTO_INCREMENT,
    cantidad INT NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    idCarrito INT NOT NULL,
    idVariante INT NOT NULL,
    FOREIGN KEY (idCarrito) REFERENCES Carrito(idCarrito) ON DELETE CASCADE,
    FOREIGN KEY (idVariante) REFERENCES VarianteProducto(idVariante)
);

-- Tabla: ComprobantePago
CREATE TABLE ComprobantePago (
    idComprobante INT PRIMARY KEY AUTO_INCREMENT,
    archivo VARCHAR(255) NOT NULL,
    fechaSubida DATETIME DEFAULT CURRENT_TIMESTAMP,
    tipoArchivo VARCHAR(50),
    idPedido INT UNIQUE,
    FOREIGN KEY (idPedido) REFERENCES Pedido(idPedido) ON DELETE SET NULL
);

-- Tabla: ReglaDescuento
CREATE TABLE ReglaDescuento (
    idRegla INT PRIMARY KEY AUTO_INCREMENT,
    rangoMinimo INT,
    rangoMaximo INT,
    porcentaje DECIMAL(5, 2) NOT NULL
);

-- Tabla: Cotizacion
CREATE TABLE Cotizacion (
    idCotizacion INT PRIMARY KEY AUTO_INCREMENT,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    estado ENUM('PENDIENTE', 'ACEPTADA', 'RECHAZADA') DEFAULT 'PENDIENTE',
    precioPresupuesto DECIMAL(10, 2),
    comentario TEXT,
    idUsuario INT NOT NULL,
    idRegla INT,
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario),
    FOREIGN KEY (idRegla) REFERENCES ReglaDescuento(idRegla) ON DELETE SET NULL
);

-- Tabla: LineaCotizacion
CREATE TABLE LineaCotizacion (
    idLineaCotizacion INT PRIMARY KEY AUTO_INCREMENT,
    cantidad INT NOT NULL,
    precioReferencial DECIMAL(10, 2),
    idCotizacion INT NOT NULL,
    idVariante INT NOT NULL,
    FOREIGN KEY (idCotizacion) REFERENCES Cotizacion(idCotizacion) ON DELETE CASCADE,
    FOREIGN KEY (idVariante) REFERENCES VarianteProducto(idVariante)
);

-- Tabla: Contrapropuesta
CREATE TABLE Contrapropuesta (
    idContrapropuesta INT PRIMARY KEY AUTO_INCREMENT,
    comentario TEXT,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    precioNuevo DECIMAL(10, 2) NOT NULL,
    idCotizacion INT NOT NULL,
    FOREIGN KEY (idCotizacion) REFERENCES Cotizacion(idCotizacion) ON DELETE CASCADE
);

-- Tabla: LogAuditoria
CREATE TABLE LogAuditoria (
    idLog INT PRIMARY KEY AUTO_INCREMENT,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    accion VARCHAR(100) NOT NULL,
    valorAnterior TEXT,
    valorNuevo TEXT,
    idUsuario INT NOT NULL,
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario) ON DELETE CASCADE
);

-- Índices para mejorar el rendimiento
CREATE INDEX idx_pedido_estado ON Pedido(estado);
CREATE INDEX idx_producto_nombre ON Producto(nombre);
CREATE INDEX idx_usuario_correo ON Usuario(correo);
CREATE INDEX idx_cotizacion_estado ON Cotizacion(estado);