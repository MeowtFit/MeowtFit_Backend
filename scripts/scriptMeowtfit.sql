DROP DATABASE IF EXISTS meowtfit;
CREATE DATABASE meowtfit;
USE meowtfit;

-- ============================================================
-- USUARIOS Y ROLES  (Single Table Inheritance)
-- ============================================================
-- Discriminador: columna `rol`
--   ADMINISTRADOR → solo campos base
--   COMERCIANTE   → solo campos base
--   CLIENTE       → campos base + B2C (fechaNacimiento, direccionEnvio)
--                                  o B2B (ruc, razonSocial, telefono2)
--                   ruc NOT NULL distingue B2B de B2C en la capa de negocio
CREATE TABLE Usuario (
    idUsuario          INT PRIMARY KEY AUTO_INCREMENT,
    -- campos comunes a todos los roles
    nombres            VARCHAR(100) NOT NULL, 
    correo             VARCHAR(100) UNIQUE NOT NULL,
    contrasena         VARCHAR(255) NOT NULL,
    telefono           VARCHAR(20),
    estadoCuenta       ENUM('ACTIVO','INACTIVO') DEFAULT 'ACTIVO',
    fechaCreacion      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fechaActualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    rol                ENUM('ADMINISTRADOR','COMERCIANTE','CLIENTE') NOT NULL DEFAULT 'CLIENTE',
    -- campos exclusivos de ClienteB2C (null para otros roles)
    dni                VARCHAR(20) UNIQUE, 
    fechaNacimiento    DATE,
    direccionEnvio     TEXT,
    -- campos exclusivos de ClienteB2B (null para otros roles; ruc NOT NULL → B2B)
    ruc                VARCHAR(20) UNIQUE,
    razonSocial        VARCHAR(150),
    telefono2          VARCHAR(20)
);

-- ============================================================
-- CATÁLOGO DE PRODUCTOS
-- ============================================================

-- Tabla: Categoria
CREATE TABLE Categoria (
    idCategoria INT PRIMARY KEY AUTO_INCREMENT,
    nombre      VARCHAR(100) NOT NULL
);

-- Tabla: Producto
CREATE TABLE Producto (
    idProducto  INT PRIMARY KEY AUTO_INCREMENT,
    nombre      VARCHAR(150) NOT NULL,
    precioBase  DECIMAL(10, 2) NOT NULL,
    estado      ENUM('ACTIVO','INACTIVO','AGOTADO') DEFAULT 'ACTIVO',
    descripcion TEXT,
    imagenUrl   VARCHAR(500),
    idCategoria INT NOT NULL,
    FOREIGN KEY (idCategoria) REFERENCES Categoria(idCategoria)
);

CREATE TABLE Color (
    idColor     INT PRIMARY KEY AUTO_INCREMENT,
    nombre      VARCHAR(50) NOT NULL,
    hexadecimal VARCHAR(7) NOT NULL  -- Formato: #RRGGBB
);

-- Tabla: VarianteProducto (combinaciones talla/color de un producto)
CREATE TABLE VarianteProducto (
    idVariante      INT PRIMARY KEY AUTO_INCREMENT,
    talla           VARCHAR(10),
    idColor         INT,  
    stockDisponible INT DEFAULT 0,
    stockReservado  INT DEFAULT 0,
    idProducto      INT NOT NULL,
    FOREIGN KEY (idProducto) REFERENCES Producto(idProducto) ON DELETE CASCADE,
    FOREIGN KEY (idColor) REFERENCES Color(idColor) ON DELETE SET NULL
);

-- ============================================================
-- CONFIGURACIÓN DEL NEGOCIO Y REGLAS DE DESCUENTO
-- ============================================================

-- Tabla: ConfiguracionNegocio (singleton — parámetros globales del negocio)
-- stockMinimoCotizacion: unidades a partir de las cuales se habilita cotización
-- porcentajePrecioPiso : porcentaje mínimo de venta aceptable en cotizaciones
CREATE TABLE ConfiguracionNegocio (
  idConfiguracion BIGINT AUTO_INCREMENT PRIMARY KEY,
  stockMinimoCotizacion INT NOT NULL DEFAULT 1000,
  porcentajePrecioPiso DECIMAL(5,2) DEFAULT 0.00
);

CREATE TABLE ReglaDescuento (
    idRegla INT AUTO_INCREMENT PRIMARY KEY,
    rangoMinimo INT NOT NULL,
    rangoMaximo INT NOT NULL,
    porcentaje DECIMAL(5,2) NOT NULL,
    idProducto INT NOT NULL,
    CONSTRAINT fk_regla_producto FOREIGN KEY (idProducto) REFERENCES Producto(idProducto)
);

-- ============================================================
-- CARRITO DE COMPRAS
-- ============================================================

-- Tabla: Carrito
CREATE TABLE Carrito (
    idCarrito     INT PRIMARY KEY AUTO_INCREMENT,
    fechaCreacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    estado        ENUM('ACTIVO','INACTIVO') DEFAULT 'ACTIVO',
    idUsuario     INT NOT NULL,
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario) ON DELETE CASCADE
);

-- Tabla: LineaCarrito
CREATE TABLE LineaCarrito (
    idLineaCarrito INT PRIMARY KEY AUTO_INCREMENT,
    cantidad       INT NOT NULL,
    precioUnitario DECIMAL(10, 2) NOT NULL,
    subtotal       DECIMAL(10, 2) NOT NULL,
    idCarrito      INT NOT NULL,
    idVariante     INT NOT NULL,
    FOREIGN KEY (idCarrito) REFERENCES Carrito(idCarrito) ON DELETE CASCADE,
    FOREIGN KEY (idVariante) REFERENCES VarianteProducto(idVariante)
);

-- ============================================================
-- PEDIDOS
-- ============================================================

-- Tabla: Pedido
-- Estados: REGISTRADO → CONFIRMADO → EN_PROCESO (stock insuficiente) → ENVIADO → ENTREGADO
--          CANCELADO y PAGO_RECHAZADO como estados terminales de error
CREATE TABLE Pedido (
    idPedido          INT PRIMARY KEY AUTO_INCREMENT,
    fechaHoraRegistro DATETIME DEFAULT CURRENT_TIMESTAMP,
    estado            ENUM('REGISTRADO','CONFIRMADO','EN_PROCESO','ENVIADO','ENTREGADO','CANCELADO','PAGO_RECHAZADO') DEFAULT 'REGISTRADO',
    montoTotal        DECIMAL(10, 2) NOT NULL,
    metodoPago        VARCHAR(50),
    descuento         DECIMAL(10, 2) DEFAULT 0,
    fechaEntrega      DATE,
    igv               DECIMAL(10, 2),
    idUsuario         INT NOT NULL,
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario)
);

-- Tabla: LineaPedido
CREATE TABLE LineaPedido (
    idLineaPedido     INT PRIMARY KEY AUTO_INCREMENT,
    cantidad          INT NOT NULL,
    precioVenta       DECIMAL(10, 2) NOT NULL,
    subtotal          DECIMAL(10, 2) NOT NULL,
    precioUnitario    DECIMAL(10, 2) NOT NULL,
    descuentoAplicado DECIMAL(10, 2) DEFAULT 0,
    idPedido          INT NOT NULL,
    idVariante        INT NOT NULL,
    FOREIGN KEY (idPedido) REFERENCES Pedido(idPedido) ON DELETE CASCADE,
    FOREIGN KEY (idVariante) REFERENCES VarianteProducto(idVariante)
);

-- Tabla: FacturaElectronica (generada automáticamente al confirmar el pedido)
CREATE TABLE FacturaElectronica (
    idFactura     INT PRIMARY KEY AUTO_INCREMENT,
    fechaEmision  DATETIME DEFAULT CURRENT_TIMESTAMP,
    estado        ENUM('PENDIENTE_PAGO','PAGADA','ANULADA') DEFAULT 'PENDIENTE_PAGO',
    idPedido      INT UNIQUE NOT NULL,
    FOREIGN KEY (idPedido) REFERENCES Pedido(idPedido) ON DELETE CASCADE
);

-- Tabla: ComprobantePago (archivo subido por el cliente para validar el pago)
CREATE TABLE ComprobantePago (
    idComprobante INT PRIMARY KEY AUTO_INCREMENT,
    archivo       VARCHAR(255) NOT NULL,
    fechaSubida   DATETIME DEFAULT CURRENT_TIMESTAMP,
    tipoArchivo   VARCHAR(50),
    idPedido      INT UNIQUE,
    FOREIGN KEY (idPedido) REFERENCES Pedido(idPedido) ON DELETE SET NULL
);

-- ============================================================
-- COTIZACIONES (VENTAS B2B AL POR MAYOR)
-- ============================================================

-- Tabla: Cotizacion
CREATE TABLE Cotizacion (
    idCotizacion      INT PRIMARY KEY AUTO_INCREMENT,
    fechaCreacion     DATETIME DEFAULT CURRENT_TIMESTAMP,
    estado            ENUM('PENDIENTE','CONTRAPROPUESTA','RECHAZADA','CERRADA') DEFAULT 'PENDIENTE',
    precioSugerido    DECIMAL(10, 2) NOT NULL,
    sustento          TEXT NOT NULL,
    montoSugerido     DECIMAL(10, 2) NOT NULL,
    montoReal         DECIMAL(10, 2) NOT NULL,
    idCliente         INT NOT NULL,
    idComerciante     INT NULL,
    idProducto        INT NOT NULL,
    FOREIGN KEY (idCliente) REFERENCES Usuario(idUsuario) ON DELETE CASCADE,
    FOREIGN KEY (idComerciante) REFERENCES Usuario(idUsuario),
    FOREIGN KEY (idProducto) REFERENCES Producto(idProducto)
);

-- Tabla: LineaCotizacion
CREATE TABLE LineaCotizacion (
    idLineaCotizacion INT PRIMARY KEY AUTO_INCREMENT,
    cantidad          INT NOT NULL,
    precioUnitario    DECIMAL(10, 2) NOT NULL,
    subtotal          DECIMAL(10, 2) NOT NULL,
    idCotizacion      INT NOT NULL,
    idVariante        INT NOT NULL,
    FOREIGN KEY (idCotizacion) REFERENCES Cotizacion(idCotizacion) ON DELETE CASCADE,
    FOREIGN KEY (idVariante) REFERENCES VarianteProducto(idVariante) ON DELETE RESTRICT
);

-- Tabla: Contrapropuesta
CREATE TABLE Contrapropuesta (
    idContrapropuesta INT PRIMARY KEY AUTO_INCREMENT,
    sustento          TEXT,
    fechaCreacion     DATETIME DEFAULT CURRENT_TIMESTAMP,
    precioNuevo       DECIMAL(10, 2) NOT NULL,
    idCotizacion      INT NOT NULL,
    idUserGenerador   INT NOT NULL,
    FOREIGN KEY (idCotizacion) REFERENCES Cotizacion(idCotizacion) ON DELETE CASCADE,
    FOREIGN KEY (idUserGenerador) REFERENCES Usuario(idUsuario)
);

-- ============================================================
-- ALERTAS Y AUDITORÍA
-- ============================================================

-- Tabla: AlertaAbastecimiento (generada automáticamente cuando el stock es insuficiente)
CREATE TABLE AlertaAbastecimiento (
    idAlerta        INT PRIMARY KEY AUTO_INCREMENT,
    fechaGeneracion DATETIME DEFAULT CURRENT_TIMESTAMP,
    estado          ENUM('PENDIENTE','RESUELTA') DEFAULT 'PENDIENTE',
    mensaje         TEXT,
    idVariante      INT NOT NULL,
    FOREIGN KEY (idVariante) REFERENCES VarianteProducto(idVariante)
);

-- Tabla: LogAuditoria (trazabilidad de acciones críticas del sistema)
CREATE TABLE LogAuditoria (
    idLog         INT PRIMARY KEY AUTO_INCREMENT,
    fecha         DATETIME DEFAULT CURRENT_TIMESTAMP,
    accion        VARCHAR(100) NOT NULL,
    valorAnterior TEXT,
    valorNuevo    TEXT,
    idUsuario     INT NOT NULL,
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario) ON DELETE CASCADE
);

-- ============================================================
-- Tokens de reinicio de contraseña
-- ============================================================

CREATE TABLE TokenReinicioContrasena (
    idToken         INT PRIMARY KEY AUTO_INCREMENT,
    token           VARCHAR(255) NOT NULL UNIQUE,
    fechaExpiracion DATETIME NOT NULL,
    idUsuario       INT NOT NULL,
    CONSTRAINT fk_token_reinicio_contrasena_usuario FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario) ON DELETE CASCADE
);

-- ============================================================
-- ÍNDICES
-- ============================================================

CREATE INDEX idx_pedido_estado        ON Pedido(estado);
CREATE INDEX idx_producto_nombre      ON Producto(nombre);
CREATE INDEX idx_usuario_correo       ON Usuario(correo);
CREATE INDEX idx_cotizacion_estado    ON Cotizacion(estado);
CREATE INDEX idx_variante_producto    ON VarianteProducto(idProducto);
CREATE INDEX idx_alerta_estado        ON AlertaAbastecimiento(estado);
CREATE INDEX idx_regla_producto       ON ReglaDescuento(idProducto);
CREATE INDEX idx_cotizacion_cliente_estado ON Cotizacion(idCliente, estado);
CREATE INDEX idx_cotizacion_comerciante_estado ON Cotizacion(idComerciante, estado);
CREATE INDEX idx_linea_cotizacion_variante ON LineaCotizacion(idVariante);
CREATE INDEX idx_contrapropuesta_cotizacion ON Contrapropuesta(idCotizacion);
