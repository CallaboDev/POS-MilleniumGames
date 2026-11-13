# Especificación Completa de Migración: POS Millenium Games MVC a REST API

Este documento detalla la transformación de todos los controladores basados en vistas (Thymeleaf/Spring MVC) del proyecto **POS-MilleniumGames** hacia una arquitectura **API REST pura** desacoplada, documentada con **OpenAPI 3 / Scalar** y lista para ser consumida por frontend moderno (React, Vue, Flutter, Next.js, etc.) o terminales POS.

---

## 1. Cadena de Conexión Completa a Base de Datos

### Conexión Estándar PostgreSQL (Local)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/pos_milleniumgames
spring.datasource.username=postgres
spring.datasource.password=1234
spring.datasource.driver-class-name=org.postgresql.Driver
```

### Formato URI Completo (JDBC & Variables de Entorno)
* **JDBC URL Completa:** `jdbc:postgresql://postgres:1234@localhost:5432/pos_milleniumgames`
* **Soporte Dinámico de Producción (ej. Render, Railway, Docker):**
  ```properties
  spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/pos_milleniumgames}
  spring.datasource.username=${DB_USER:postgres}
  spring.datasource.password=${DB_PASSWORD:1234}
  spring.datasource.driver-class-name=org.postgresql.Driver
  ```

---

## 2. Relevamiento y Mapeo de Controladores Existentes a REST

A continuación se detalla el análisis de cada controlador del proyecto original y su correspondiente diseño REST bajo el prefijo estándar `/api/v1`:

---

### Módulo 1: Inventario - Categorías
* **Controlador Original:** `CategoriaController.java` (`/categorias`)
  * `POST /categorias/guardar`: Formulario con redirección Thymeleaf.
  * `POST /categorias/api/guardar`: Endpoint híbrido `@ResponseBody`.

#### Diseño REST API (`/api/v1/categorias`):
| Método HTTP | Ruta | Descripción | Request Body | Códigos HTTP |
|---|---|---|---|---|
| `GET` | `/api/v1/categorias` | Listar todas las categorías | Ninguno | `200 OK` |
| `GET` | `/api/v1/categorias/{id}` | Obtener detalle de categoría | Ninguno | `200 OK`, `404 Not Found` |
| `POST` | `/api/v1/categorias` | Crear nueva categoría | `CategoriaRequestDTO` (`nombre`) | `201 Created`, `400 Bad Request` |
| `PUT` | `/api/v1/categorias/{id}` | Actualizar categoría | `CategoriaRequestDTO` (`nombre`) | `200 OK`, `404 Not Found` |
| `DELETE` | `/api/v1/categorias/{id}` | Eliminar categoría | Ninguno | `204 No Content`, `404 Not Found` |

---

### Módulo 2: Inventario - Plataformas
* **Controlador Original:** `PlataformaController.java` (`/plataformas`)
  * `POST /plataformas/guardar`: Formulario con redirección Thymeleaf.
  * `POST /plataformas/api/guardar`: Endpoint híbrido `@ResponseBody`.

#### Diseño REST API (`/api/v1/plataformas`):
| Método HTTP | Ruta | Descripción | Request Body | Códigos HTTP |
|---|---|---|---|---|
| `GET` | `/api/v1/plataformas` | Listar todas las plataformas / consolas | Ninguno | `200 OK` |
| `GET` | `/api/v1/plataformas/{id}` | Obtener plataforma por ID | Ninguno | `200 OK`, `404 Not Found` |
| `POST` | `/api/v1/plataformas` | Crear nueva plataforma | `PlataformaRequestDTO` (`nombre`) | `201 Created`, `400 Bad Request` |
| `PUT` | `/api/v1/plataformas/{id}` | Actualizar plataforma | `PlataformaRequestDTO` (`nombre`) | `200 OK`, `404 Not Found` |
| `DELETE` | `/api/v1/plataformas/{id}` | Eliminar plataforma | Ninguno | `204 No Content`, `404 Not Found` |

---

### Módulo 3: Inventario - Productos
* **Controlador Original:** `ProductoController.java` (`/productos`)
  * `GET /productos`: Renderiza vista `inventario/lista`.
  * `GET /productos/nuevo` & `/productos/editar/{id}`: Formularios HTML.
  * `POST /productos/guardar`: Multipart form con archivo de imagen y redirección.
  * `GET /productos/eliminar/{id}`: Eliminación vía GET con flash attributes.

#### Diseño REST API (`/api/v1/productos`):
| Método HTTP | Ruta | Parámetros / Body | Descripción | Códigos HTTP |
|---|---|---|---|---|
| `GET` | `/api/v1/productos` | Query: `?buscar=...` | Listar productos con búsqueda por nombre | `200 OK` |
| `GET` | `/api/v1/productos/{id}` | Path: `id` | Obtener producto por ID numérico | `200 OK`, `404 Not Found` |
| `GET` | `/api/v1/productos/sku/{sku}` | Path: `sku` | Búsqueda por código de barras (Caja / Escáner) | `200 OK`, `404 Not Found` |
| `POST` | `/api/v1/productos` | Body: `ProductoRequestDTO` | Crear producto en inventario | `201 Created`, `400 Bad Request` |
| `PUT` | `/api/v1/productos/{id}` | Body: `ProductoRequestDTO` | Actualizar precio, stock, nombre o datos | `200 OK`, `404 Not Found` |
| `DELETE` | `/api/v1/productos/{id}` | Path: `id` | Eliminar producto | `204 No Content`, `404 Not Found` |
| `POST` | `/api/v1/productos/{id}/imagen` | Multipart: `archivoImagen` | Subida de portada / foto de producto | `200 OK`, `400 Bad Request` |

---

### Módulo 4: Ventas - Clientes
* **Controlador Original:** `ClienteController.java` (`/clientes`)
  * `GET /clientes`: Renderiza lista de clientes.
  * `GET /clientes/nuevo` & `/clientes/editar/{id}`: Formularios Thymeleaf.
  * `POST /clientes/guardar`: Guardar/Actualizar con redirección.
  * `POST /clientes/eliminar/{id}`: Eliminar cliente con redirección.

#### Diseño REST API (`/api/v1/clientes`):
| Método HTTP | Ruta | Parámetros / Body | Descripción | Códigos HTTP |
|---|---|---|---|---|
| `GET` | `/api/v1/clientes` | Query: `?buscar=...` | Listar clientes (nombre, DUI, teléfono) | `200 OK` |
| `GET` | `/api/v1/clientes/{id}` | Path: `id` | Obtener cliente por ID | `200 OK`, `404 Not Found` |
| `POST` | `/api/v1/clientes` | Body: `ClienteDTO` | Registrar cliente | `201 Created`, `400 Bad Request` |
| `PUT` | `/api/v1/clientes/{id}` | Body: `ClienteDTO` | Actualizar datos del cliente | `200 OK`, `404 Not Found` |
| `DELETE` | `/api/v1/clientes/{id}` | Path: `id` | Eliminar cliente | `204 No Content`, `404 Not Found` |
| `GET` | `/api/v1/clientes/consumidor-final` | Ninguno | Obtener o crear entidad Consumidor Final | `200 OK` |

---

### Módulo 5: Ventas y Punto de Venta (Caja)
* **Controlador Original:** `VentaController.java`
  * `GET /caja`, `/ventas/caja`: Renderiza interfaz web POS.
  * `GET /caja/api/productos`: Búsqueda AJAX de productos para agregar al carrito.
  * `POST /caja/procesar`: Endpoint `@ResponseBody` para registrar la venta vía JSON.
  * `POST /caja/cobrar`: Formulario tradicional de cobro.
  * `GET /caja/ticket/{id}`: Vista HTML de ticket para imprimir.

#### Diseño REST API (`/api/v1/ventas`):
| Método HTTP | Ruta | Parámetros / Body | Descripción | Códigos HTTP |
|---|---|---|---|---|
| `GET` | `/api/v1/ventas` | Ninguno | Historial completo de ventas | `200 OK` |
| `GET` | `/api/v1/ventas/{id}` | Path: `id` | Detalle de venta y sus líneas de detalle | `200 OK`, `404 Not Found` |
| `GET` | `/api/v1/ventas/factura/{numeroFactura}` | Path: `numeroFactura` | Buscar venta por número correlativo de factura | `200 OK`, `404 Not Found` |
| `GET` | `/api/v1/ventas/proximo-numero-factura` | Ninguno | Obtener el siguiente número correlativo sugerido | `200 OK` |
| `POST` | `/api/v1/ventas` | Body: `RegistroVentaDTO` | Procesar y registrar venta (descuenta stock) | `201 Created`, `400 Bad Request` |
| `GET` | `/api/v1/ventas/{id}/ticket` | Path: `id` | Datos estructurados para renderizar/imprimir ticket | `200 OK`, `404 Not Found` |

---

### Módulo 6: Dashboard y Reportes
* **Controlador Original:** `HomeController.java`
  * `GET /`: Vista inicial de dashboard con métricas básicas.
  * `GET /dashboard`: Métricas avanzadas, ingresos del día/mes, stock crítico.
  * `GET /reportes`: Filtro de fechas con ventas e ingresos del periodo.

#### Diseño REST API (`/api/v1/dashboard` & `/api/v1/reportes`):
| Método HTTP | Ruta | Parámetros | Descripción | Códigos HTTP |
|---|---|---|---|---|
| `GET` | `/api/v1/dashboard/resumen` | Ninguno | Tarjetas de KPI (total videojuegos, clientes, ventas de hoy, usuarios) | `200 OK` |
| `GET` | `/api/v1/dashboard/metricas` | Ninguno | Ingresos del día, ingresos del mes, cantidad de ventas y alertas de stock | `200 OK` |
| `GET` | `/api/v1/reportes/periodo` | Query: `?desde=YYYY-MM-DD&hasta=YYYY-MM-DD` | Ventas e ingresos dentro de un rango de fechas | `200 OK`, `400 Bad Request` |
| `GET` | `/api/v1/reportes/stock-critico` | Ninguno | Lista de productos con existencias mínimas | `200 OK` |
| `GET` | `/api/v1/reportes/mas-vendidos` | Ninguno | Ranking de los productos con mayor volumen vendido | `200 OK` |

---

### Módulo 7: Usuarios y Seguridad
* **Controladores Originales:** `LoginController.java` y `UsuarioController.java`
  * `GET /login`: Formulario HTML de inicio de sesión.
  * `GET /logout`: Cierre de sesión de servlet HTTP.
  * `GET /usuarios`, `GET /usuarios/nuevo`, `GET /usuarios/editar/{id}`, `POST /guardar`, `POST /actualizar/{id}`, `POST /eliminar/{id}`: CRUD de usuarios con sesiones Spring Security.

#### Diseño REST API (`/api/v1/auth` & `/api/v1/usuarios`):
| Método HTTP | Ruta | Parámetros / Body | Descripción | Códigos HTTP |
|---|---|---|---|---|
| `POST` | `/api/v1/auth/login` | Body: `LoginRequestDTO` (`username`, `password`) | Autenticación y obtención de sesión/token | `200 OK`, `401 Unauthorized` |
| `GET` | `/api/v1/auth/me` | Autenticado | Datos del usuario actual en sesión | `200 OK`, `401 Unauthorized` |
| `GET` | `/api/v1/usuarios` | Ninguno | Listar usuarios y empleados | `200 OK` |
| `GET` | `/api/v1/usuarios/{id}` | Path: `id` | Obtener usuario por ID | `200 OK`, `404 Not Found` |
| `POST` | `/api/v1/usuarios` | Body: `UsuarioCreacionDTO` | Registrar usuario (hashea contraseña con BCrypt) | `201 Created`, `400 Bad Request` |
| `PUT` | `/api/v1/usuarios/{id}` | Body: `UsuarioActualizacionDTO` | Actualizar nombre, username, rol o password | `200 OK`, `404 Not Found` |
| `DELETE` | `/api/v1/usuarios/{id}` | Path: `id` | Eliminar usuario | `204 No Content`, `400 Bad Request` |

---

## 3. Visualización y Documentación con Scalar

* **Especificación OpenAPI:** Expuesta en `/v3/api-docs`.
* **Interfaz Scalar:** Servida en `/docs` vía CDN oficial de `@scalar/api-reference`.
* Todos los endpoints cuentan con `@Tag`, `@Operation`, `@Parameter`, `@ApiResponse` y `@Schema`.
