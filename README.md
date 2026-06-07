# 🛒 MiniMarket Plus — Backend con Spring Security + JWT

Proyecto backend de la cadena de minimarkets **MiniMarket Plus**, implementado con Spring Boot.  
Incluye autenticación JWT, autorización basada en roles y protección contra amenazas comunes (SQL Injection, XSS, CSRF).

---

## 📋 Descripción

Sistema de gestión para minimarket que cubre inventario, productos, ventas y usuarios.  
La seguridad fue implementada con **Spring Security + JWT (JJWT 0.12.3)**, arquitectura **stateless** y control de acceso por roles mediante `@PreAuthorize`.

---

## ⚙️ Tecnologías

| Tecnología | Versión |
|---|---|
| Java | 17 |
| Spring Boot | 3.4.1 |
| Spring Security | (incluido en Boot) |
| JJWT | 0.12.3 |
| H2 Database | (en memoria) |
| Lombok | (incluido en Boot) |
| Maven | 3.x |

---

## 🚀 Cómo levantar el proyecto

### Prerequisitos
- Java 17 instalado (`java -version`)
- Maven instalado (`mvn -version`) — o usar el wrapper `./mvnw` incluido

### 1. Clonar el repositorio

```bash
git clone https://github.com/<tu-usuario>/minimarket.git
cd minimarket
```

### 2. Variables de entorno (opcional)

El proyecto funciona sin configuración adicional usando valores por defecto para desarrollo local.  
Para producción, define estas variables de entorno:

```bash
export JWT_SECRET=TuClaveSecretaMuyLargaYSegura2024  # mínimo 32 caracteres
export JWT_EXPIRATION=86400000                        # milisegundos (24 horas)
export CORS_ORIGINS=http://localhost:3000             # orígenes permitidos
```

### 3. Compilar y ejecutar

```bash
./mvnw spring-boot:run
```

O con Maven instalado:

```bash
mvn spring-boot:run
```

La aplicación levanta en `http://localhost:8080`.

### 4. Consola H2 (base de datos en memoria)

Disponible en `http://localhost:8080/h2-console`

| Campo | Valor |
|---|---|
| JDBC URL | `jdbc:h2:mem:testdb` |
| Usuario | `sa` |
| Contraseña | *(vacío)* |

---

## 👥 Usuarios de prueba

El `DataInitializer` crea automáticamente estos usuarios al iniciar:

| Usuario | Contraseña | Rol | Acceso |
|---|---|---|---|
| `gerente` | `gerente123` | `ROLE_GERENTE` | Total (inventario, usuarios, todas las operaciones) |
| `empleado` | `empleado123` | `ROLE_EMPLEADO` | Productos, categorías, ventas, carrito |
| `cliente` | `cliente123` | `ROLE_CLIENTE` | Solo carrito propio |

---

## 🔐 Endpoints de autenticación

### Login

```
POST /api/auth/login
Content-Type: application/json

{
  "username": "gerente",
  "password": "gerente123"
}
```

**Respuesta exitosa (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tipo": "Bearer",
  "username": "gerente",
  "roles": "[ROLE_GERENTE]"
}
```

### Registro

```
POST /api/auth/registro
Content-Type: application/json

{
  "username": "nuevo_cliente",
  "password": "mi_password"
}
```

Asigna `ROLE_CLIENTE` por defecto.

---

## 🗂️ Endpoints por rol

Incluir en todas las requests protegidas:  
`Authorization: Bearer <token>`

| Endpoint | Método | Roles permitidos |
|---|---|---|
| `/api/auth/**` | POST | Público |
| `/api/productos/**` | GET, POST | EMPLEADO, GERENTE |
| `/api/categorias/**` | GET, POST, PUT, DELETE | EMPLEADO, GERENTE |
| `/api/inventario/**` | GET | EMPLEADO, GERENTE |
| `/api/inventario/**` | POST, PUT, DELETE | GERENTE |
| `/api/ventas/**` | GET, POST, PUT, DELETE | EMPLEADO, GERENTE |
| `/api/detalle-ventas/**` | GET, POST, PUT, DELETE | EMPLEADO, GERENTE |
| `/api/carrito/**` | GET, POST, PUT, DELETE | CLIENTE, EMPLEADO, GERENTE |
| `/api/usuarios/**` | GET, POST, PUT, DELETE | GERENTE |

---

## 🏗️ Estructura del proyecto

```
src/main/java/com/minimarket/
├── config/
│   └── DataInitializer.java          # Usuarios de prueba iniciales
├── controller/                        # Controladores REST por entidad
│   ├── CarritoController.java
│   ├── CategoriaController.java
│   ├── DetalleVentaController.java
│   ├── InventarioController.java
│   ├── ProductoController.java
│   ├── UsuarioController.java
│   └── VentaController.java
├── dto/                               # Data Transfer Objects
├── entity/                            # Entidades JPA
├── exception/
│   └── GlobalExceptionHandler.java   # Manejo centralizado de errores
├── repository/                        # Repositorios Spring Data JPA
├── security/
│   ├── config/
│   │   └── SecurityConfig.java       # Configuración Spring Security + CORS
│   ├── controller/
│   │   └── AuthController.java       # Login y registro
│   ├── filter/
│   │   └── JwtAuthFilter.java        # Filtro de validación JWT
│   ├── model/
│   │   ├── CustomUserDetails.java
│   │   └── LoginRequest.java
│   ├── service/
│   │   └── CustomUserDetailsService.java
│   └── util/
│       └── JwtUtil.java              # Generación y validación de tokens
└── service/                           # Lógica de negocio
```

---

## 🛡️ Seguridad implementada

| Amenaza | Mecanismo de protección |
|---|---|
| Acceso no autorizado | JWT + Spring Security + `@PreAuthorize` |
| SQL Injection | Spring Data JPA con Prepared Statements (Hibernate) |
| XSS | Arquitectura stateless sin cookies de sesión; CORS configurado |
| CSRF | `csrf.disable()` justificado por arquitectura stateless + JWT en header |
| Exposición de errores | `GlobalExceptionHandler` con mensajes genéricos al cliente |
| Sesiones persistentes | `SessionCreationPolicy.STATELESS` + expiración configurable del token |

---

## 📚 Asignatura

**PBY2202 – Desarrollo Backend II**  
Duoc UC — Analista Programador Computacional  
Semana 3 — Integrando seguridad en aplicaciones Backend
