# MeowtFit — Backend

API REST del sistema MeowtFit (catálogo, carrito, cotizaciones B2B, pedidos, pagos y usuarios), construida con **Spring Boot 4 / Java 26**.

El proyecto está pensado para desplegarse en la nube (RDS, S3, API Gateway), pero **corre completo en local sin ninguna cuenta de AWS ni de Gmail**: por defecto los archivos se guardan en disco y los correos se registran en el log en vez de enviarse.

## Requisitos

- **Java 26** (JDK)
- **MySQL 8** — local, o vía Docker (ver abajo)
- No necesitas instalar Maven: el proyecto trae el wrapper (`mvnw` / `mvnw.cmd`)
- Docker Desktop (opcional, solo si quieres levantar MySQL con un comando)

## 1. Base de datos

### Opción A — Docker (recomendada, más rápida)

Con Docker Desktop corriendo:

```bash
docker compose up -d
```

Esto levanta un contenedor MySQL en `localhost:3306`, base de datos `meowtfit`, usuario `root` / contraseña `root`, y carga automáticamente el esquema (`scripts/scriptMeowtfit.sql`) y los datos de prueba (`scripts/seed.sql`) la primera vez que se crea el volumen.

Para reiniciar desde cero: `docker compose down -v && docker compose up -d`.

### Opción B — MySQL instalado localmente

1. Crea la base de datos y el esquema ejecutando el script:
   ```bash
   mysql -u root -p < scripts/scriptMeowtfit.sql
   ```
2. (Opcional) Carga datos de prueba — productos, categorías, colores y usuarios demo:
   ```bash
   mysql -u root -p meowtfit < scripts/seed.sql
   ```

## 2. Variables de entorno (opcional para correr en local)

Si usaste la Opción A (Docker) con los valores por defecto, **puedes saltarte este paso**: `application-dev.properties` ya trae como valor por defecto la misma conexión (`localhost:3306/meowtfit`, `root`/`root`), S3 desactivado y correo en modo log — el backend arranca sin ningún archivo adicional.

Si necesitas otros valores (otra base de datos, probar S3 o el envío real de correos), copia la plantilla y ajústala:

```bash
cp .env.properties.example .env.properties
```

`.env.properties` está en `.gitignore`: Spring lo carga automáticamente al arrancar (no necesitas exportar variables de entorno a mano) y sus valores pisan a los por defecto. Resumen de lo que trae:

| Variable | Por defecto | Para qué sirve |
|---|---|---|
| `DB_URL`, `DB_USER`, `DB_PASS` | `jdbc:mysql://localhost:3306/meowtfit`, `root`, `root` | Conexión a MySQL |
| `CORS_ORIGINS` | `http://localhost:5173` | Origen permitido para el frontend |
| `FRONTEND_URL` | `http://localhost:5173` | Usada para armar los enlaces de los correos (ej. recuperar contraseña) |
| `AWS_S3_ENABLED` | `false` | Si es `false`, los comprobantes de pago y las imágenes de producto se guardan en `./uploads/` en vez de S3. Ponlo en `true` y completa `AWS_*` solo si quieres probar contra un bucket S3 real |
| `MAIL_ENABLED` | `false` | Si es `false`, los correos (recuperación de contraseña) no se envían: quedan impresos en la consola del backend para que copies el enlace manualmente. Ponlo en `true` y completa `MAIL_USERNAME`/`MAIL_PASSWORD` (contraseña de aplicación de Gmail) para enviarlos de verdad |

## 3. Levantar el backend

```bash
./mvnw spring-boot:run          # Linux/Mac
mvnw.cmd spring-boot:run        # Windows
```

El perfil activo por defecto es `dev` (`application.properties` → `spring.profiles.active=dev`), pensado para correr 100% en local. El perfil `prod` (`application-prod.properties`) es el que se usa en el despliegue en la nube y exige credenciales reales de AWS/Gmail — no lo necesitas para desarrollar.

La API queda en **http://localhost:8080**.

### Verificar que funciona

```bash
curl http://localhost:8080/api/categorias
```

Con el seed cargado, puedes iniciar sesión con estos usuarios de prueba (contraseña `test1234` para todos):

| Rol | Correo |
|---|---|
| Administrador | `admin@gmail.com` |
| Comerciante | `comerciante@gmail.com` |
| Cliente B2C | `usuariob2c@gmail.com` |
| Cliente B2B | `usuariob2b@gmail.com` |

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"correo":"admin@gmail.com","contrasena":"test1234"}'
```

## Estructura del proyecto

Organizado por módulo de negocio (no por capa técnica), cada uno con su `controller/`, `dto/`, `entity/`, `mapper/`, `repository/` y `service/`:

- `usuario` — autenticación, registro, recuperación de contraseña, roles (Administrador / Comerciante / Cliente B2C-B2B)
- `catalogo` — productos, variantes, categorías, reglas de descuento
- `color` — catálogo de colores
- `carrito` / `lineaCarrito` — carrito de compras
- `cotizacion` — cotizaciones y contrapropuestas (flujo B2B)
- `pedido` — pedidos
- `facturacion` — comprobantes de pago (subida/descarga de archivos)
- `negocio` — configuración general del negocio
- `config` — seguridad (Spring Security + sesión por cookie), CORS, S3, tareas async
- `common` / `exception` — utilidades y manejo de errores compartidos

Principales endpoints (todos bajo `/api`): `/auth`, `/usuarios`, `/categorias`, `/productos`, `/variantes`, `/reglas`, `/colores`, `/carritos`, `/lineas-carrito`, `/cotizaciones`, `/pedidos`, `/comprobantes-pago`, `/configuracion`.

## Notas de arquitectura local vs. nube

Todo lo que en el despliegue original dependía de servicios administrados de AWS ya tiene una ruta local equivalente, sin tocar código:

- **Base de datos**: RDS (prod) → MySQL local o vía `docker-compose.yml` (dev)
- **Archivos (comprobantes/imágenes)**: S3 (prod, `aws.s3.enabled=true`) → disco local en `./uploads/` (dev, `AWS_S3_ENABLED=false` por defecto) — mismo código en `ComprobantePagoServiceImpl` y `ProductoServiceImpl`, decide en tiempo de ejecución según si hay un `S3Client` disponible
- **Correo**: SMTP Gmail real (prod, `app.mail.enabled=true`) → log en consola (dev, `app.mail.enabled=false` por defecto) — ver `EmailServiceImpl` (real) y `EmailServiceLogImpl` (local)
- **Cookies de sesión cross-origin** (`SameSite=None; Secure`, necesarias cuando el front vive en otro dominio detrás de API Gateway): solo se activan en `application-prod.properties`; en `dev` la cookie es la de siempre, válida para `http://localhost`

Si en algún momento se quiere volver a desplegar en la nube, basta con arrancar con `-Dspring.profiles.active=prod` y proveer las variables de entorno reales (`DB_HOST`, `DB_NAME`, `AWS_*`, `MAIL_*`, `CORS_ORIGINS`, `FRONTEND_URL`).
