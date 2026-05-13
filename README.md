# Digital Money House

Billetera digital desarrollada como trabajo integrador final, con arquitectura de microservicios. Permite a los usuarios registrarse, autenticarse, consultar su saldo, transferir dinero a otros usuarios por CVU o alias, asociar tarjetas y gestionar su información de manera segura.

---

## Links necesarios:
## Config Server

Repositorio del Config Server utilizado en el proyecto:

🔗 https://github.com/mateoQuotteri/digital-money-house-config

## Video (Youtube)

Video explicativo del proyecto:

🔗 https://youtu.be/qDrduwWnGME
---

## Tabla de contenidos

1. [Estructura del proyecto](#estructura-del-proyecto)
2. [Tecnologías utilizadas](#tecnologías-utilizadas)
3. [Arquitectura](#arquitectura)
4. [Guía de instalación y ejecución](#guía-de-instalación-y-ejecución)
5. [Endpoints principales](#endpoints-principales)
6. [Guía de testing](#guía-de-testing)
7. [Decisiones de diseño y buenas prácticas](#decisiones-de-diseño-y-buenas-prácticas)
8. [Problemas comunes y soluciones](#problemas-comunes-y-soluciones)

---

## Estructura del proyecto

```
DIGITAL MONEY - ENTREGA/
└── CODIGO/
    ├── README.md                          ← Guía principal del proyecto
    ├── digital-money-house-config/        ← Repositorio de configuración centralizada (Config Server)
    └── trabajo-integrador-digital-money/
        ├── docker-compose.yml             ← Orquestación de todos los servicios
        ├── eureka-server/                 ← Service Discovery (Spring Cloud Eureka)
        ├── config-server/                 ← Configuración centralizada (Spring Cloud Config)
        ├── gateway/                       ← API Gateway + validación JWT
        ├── user-service/                  ← Registro, login, perfil de usuario
        ├── account-service/               ← Cuentas, saldo, tarjetas, transferencias
        └── test-cases/                    ← Casos de prueba, colecciones Postman y testing exploratorio
            ├── postman_sprint3_digital_money.json
            ├── postman_sprint4_digital_money.json
            ├── casos_de_prueba_sprint1_sprint2.csv
            ├── casos_de_prueba_sprint3.csv
            ├── casos_de_prueba_sprint4.csv
            ├── testing_exploratorio_sprint3.md
            └── testing_exploratorio_sprint4.md
```

---

## Tecnologías utilizadas

| Categoría | Tecnología |
|---|---|
| Lenguaje | Java 21 (servicios principales), Java 17 (Eureka) |
| Framework base | Spring Boot 3.2.8 |
| Microservicios | Spring Cloud 2023.0.3 (Eureka, Config, Gateway) |
| Autenticación | JWT (jjwt 0.12.3) |
| Persistencia | Spring Data JPA / Hibernate |
| Base de datos (dev) | H2 in-memory |
| Base de datos (prod) | MySQL |
| Validaciones | Spring Boot Validation (Jakarta) |
| Documentación API | SpringDoc OpenAPI / Swagger UI |
| Contenerización | Docker / Docker Compose |
| Deploy en nube | AWS (EC2 + ECR) |
| Frontend | Next.js / React / Tailwind CSS |
| Testing | Postman + scripts automatizados |
| Control de versiones | Git / GitHub |

---

## Arquitectura

El sistema sigue una arquitectura de microservicios con un único punto de entrada (gateway):

```
                        ┌────────────────────────────────┐
                        │         GitHub Config Repo      │
                        │  digital-money-house-config.git │
                        └───────────────┬────────────────┘
                                        │ (lee configs)
                                        ▼
Cliente (frontend / Postman)   ┌─────────────────┐
        │                      │  Config Server   │ :8888
        │                      └────────┬────────┘
        ▼                               │
┌──────────────┐    registra    ┌───────▼────────┐
│   Gateway    │◄──────────────►│ Eureka Server  │ :8761
│    :8080     │    y descubre  └───────┬────────┘
└──────┬───────┘                        │
       │                       ┌────────┴─────────┐
       ├──── /users/**  ──────►│   User Service   │ :8081
       │                       └──────────────────┘
       └──── /accounts/**      ┌──────────────────┐
            /cards/**   ──────►│ Account Service  │ :8082
                               └──────────────────┘
```

### Responsabilidades de cada servicio

| Servicio | Puerto | Responsabilidad |
|---|---|---|
| `eureka-server` | 8761 | Service Discovery — registro y localización de servicios |
| `config-server` | 8888 | Configuración centralizada — lee de un repositorio Git remoto |
| `gateway` | 8080 | Punto de entrada único — valida JWT e inyecta `X-User-Id` en cada request |
| `user-service` | 8081 | Registro, login, logout y gestión del perfil de usuario |
| `account-service` | 8082 | Cuentas, saldo, tarjetas, actividad y transferencias |

---

## Guía de instalación y ejecución

### Prerrequisitos

- Docker Desktop instalado y corriendo
- Git

### 1. Clonar el repositorio

```bash
git clone https://github.com/mateoQuotteri/trabajo-integrador-digital-money.git
cd trabajo-integrador-digital-money
```

### 2. Configurar variables de entorno

```bash
cp .env.example .env
```

El `.env` contiene valores por defecto que funcionan sin modificación para desarrollo local. Si querés cambiar el secreto JWT o las credenciales del config-server, editalo antes de continuar.

```env
CONFIG_USER=admin
CONFIG_PASSWORD=digital2024
JWT_SECRET=digitalMoneyHouseSecretKeyForJWT2024MustBeLongEnoughForHS256Algorithm
```

### 3. Levantar todos los servicios con Docker Compose

```bash
docker compose up --build
```

El compose levanta los servicios en el orden correcto esperando que cada uno esté saludable antes de arrancar el siguiente:

```
eureka-server → config-server → user-service + account-service → gateway
```

El proceso demora entre 2 y 4 minutos la primera vez (descarga de imágenes + compilación Maven).

### 4. Verificar que todo está corriendo

| URL | Qué muestra |
|---|---|
| `http://localhost:8761` | Dashboard de Eureka (debe mostrar USER-SERVICE y ACCOUNT-SERVICE registrados) |
| `http://localhost:8888/actuator/health` | Estado del config-server |
| `http://localhost:8080/users/test` | Prueba rápida del gateway → user-service |

### 5. Ejecución local sin Docker (desarrollo)

Si preferís correr los servicios directamente con Maven, el orden es:

```bash
# Terminal 1
cd eureka-server && ./mvnw spring-boot:run

# Terminal 2
cd config-server && ./mvnw spring-boot:run

# Terminal 3
cd user-service && ./mvnw spring-boot:run

# Terminal 4
cd account-service && ./mvnw spring-boot:run

# Terminal 5
cd gateway && ./mvnw spring-boot:run
```

En este modo todos los servicios usan sus valores por defecto (`localhost`) sin necesidad de variables de entorno adicionales.

---

## Endpoints principales

Todos los endpoints se consumen a través del **gateway en el puerto 8080**. El gateway valida el JWT y reenvía la request al microservicio correspondiente.

> Los endpoints marcados con 🔒 requieren el header `Authorization: Bearer <token>`.

---

### User Service — `/users`

#### `POST /users/register` — Registrar usuario

```json
// Request
{
  "nombre": "Ana",
  "apellido": "García",
  "dni": "12345678",
  "email": "ana@mail.com",
  "telefono": "+5491112345678",
  "password": "Segura123!"
}

// Response 201 Created
{
  "id": 1,
  "nombre": "Ana",
  "apellido": "García",
  "dni": "12345678",
  "email": "ana@mail.com",
  "telefono": "+5491122345678",
  "cvu": "4591823740192837465102",
  "alias": "sol.luna.rio"
}
```

| Status | Situación |
|---|---|
| 201 | Usuario creado correctamente |
| 400 | Email duplicado o datos inválidos |

---

#### `POST /users/login` — Iniciar sesión

```json
// Request
{
  "email": "ana@mail.com",
  "password": "Segura123!"
}

// Response 200 OK
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "accountId": 1
}
```

| Status | Situación |
|---|---|
| 200 | Login exitoso, retorna JWT |
| 400 | Contraseña incorrecta |
| 404 | Usuario no encontrado |

---

#### `POST /users/logout` 🔒 — Cerrar sesión

```
// Response 200 OK
{ "message": "Sesión cerrada correctamente" }
```

---

#### `GET /users/{id}` 🔒 — Ver perfil

```json
// Response 200 OK
{
  "id": 1,
  "nombre": "Ana",
  "apellido": "García",
  "email": "ana@mail.com",
  "telefono": "+5491112345678",
  "dni": "12345678"
}
```

| Status | Situación |
|---|---|
| 200 | Perfil del usuario |
| 403 | Intentando ver el perfil de otro usuario |
| 404 | Usuario no encontrado |

---

#### `PATCH /users/{id}` 🔒 — Actualizar perfil

```json
// Request (todos los campos son opcionales)
{
  "nombre": "Ana María",
  "telefono": "+5491199887766"
}

// Response 200 OK — perfil actualizado
```

---

### Account Service — `/accounts`

#### `POST /accounts` 🔒 — Crear cuenta

Crea la cuenta del usuario autenticado (una cuenta por usuario).

```json
// Response 201 Created
{
  "id": 1,
  "userId": 1,
  "saldo": 0.00,
  "cvu": "4591823740192837465102",
  "alias": "sol.luna.rio",
  "fechaCreacion": "2026-04-07T10:30:00"
}
```

---

#### `GET /accounts/{id}` 🔒 — Ver cuenta y saldo

```json
// Response 200 OK
{
  "id": 1,
  "userId": 1,
  "saldo": 1500.00,
  "cvu": "4591823740192837465102",
  "alias": "sol.luna.rio",
  "fechaCreacion": "2026-04-07T10:30:00"
}
```

---

#### `PATCH /accounts/{id}` 🔒 — Actualizar alias

```json
// Request
{ "alias": "nuevo.alias.aqui" }

// Response 200 OK — cuenta con alias actualizado
```

---

#### `POST /accounts/{id}/deposits` 🔒 — Ingresar dinero desde tarjeta

```json
// Request
{
  "cardId": 1,
  "amount": 500.00
}

// Response 201 Created
{
  "id": 5,
  "monto": 500.00,
  "tipo": "CREDITO",
  "descripcion": "Ingreso desde tarjeta debito terminada en 4321",
  "fecha": "2026-04-07T11:00:00"
}
```

| Status | Situación |
|---|---|
| 201 | Ingreso acreditado |
| 400 | Monto inválido |
| 403 | Cuenta ajena |
| 404 | Tarjeta o cuenta no encontrada |

---

#### `POST /accounts/{id}/transferences` 🔒 — Transferir dinero

Transfiere dinero desde el saldo hacia otra cuenta identificada por CVU o alias.

```json
// Request
{
  "destination": "4591823740192837465102",
  "amount": 300.00
}

// Response 200 OK
{
  "id": 8,
  "monto": 300.00,
  "tipo": "DEBITO",
  "descripcion": "Transferencia enviada a 4591823740192837465102",
  "fecha": "2026-04-07T11:15:00"
}
```

| Status | Situación |
|---|---|
| 200 | Transferencia realizada |
| 400 | Cuenta destino inexistente, monto inválido o auto-transferencia |
| 403 | Cuenta origen ajena |
| 410 | Fondos insuficientes |

---

#### `GET /accounts/{id}/transferences` 🔒 — Últimos destinatarios

```json
// Response 200 OK
[
  {
    "id": 8,
    "cbuCvuAlias": "4591823740192837465102",
    "monto": 300.00,
    "fecha": "2026-04-07T11:15:00"
  }
]
```

| Status | Situación |
|---|---|
| 200 | Lista de destinatarios (puede ser vacía `[]`) |
| 403 | Cuenta ajena |

---

#### `GET /accounts/{id}/activity` 🔒 — Historial de actividad

Devuelve todas las transacciones ordenadas de más reciente a más antigua.

```json
// Response 200 OK
[
  {
    "id": 8,
    "monto": 300.00,
    "tipo": "DEBITO",
    "descripcion": "Transferencia enviada a ...",
    "fecha": "2026-04-07T11:15:00"
  },
  {
    "id": 5,
    "monto": 500.00,
    "tipo": "CREDITO",
    "descripcion": "Ingreso desde tarjeta debito terminada en 4321",
    "fecha": "2026-04-07T11:00:00"
  }
]
```

---

#### `GET /accounts/{id}/activity/{transferId}` 🔒 — Detalle de actividad

```json
// Response 200 OK — mismo formato que un elemento del listado
```

| Status | Situación |
|---|---|
| 200 | Detalle de la transacción |
| 403 | Cuenta ajena |
| 404 | Transacción no encontrada |

---

### Tarjetas — `/accounts/{id}/cards` y `/cards`

#### `GET /cards` 🔒 — Listar todas las tarjetas del usuario autenticado

No requiere `accountId` explícito; el gateway lo resuelve desde el token.

```json
// Response 200 OK
[
  {
    "id": 1,
    "numeroTarjeta": "************4321",
    "titular": "ANA GARCIA",
    "vencimiento": "12/28",
    "tipo": "DEBITO"
  }
]
```

---

#### `POST /accounts/{id}/cards` 🔒 — Agregar tarjeta

```json
// Request
{
  "numeroTarjeta": "1234567890124321",
  "titular": "ANA GARCIA",
  "vencimiento": "12/28",
  "cvv": "123",
  "tipo": "DEBITO"
}

// Response 201 Created — tarjeta creada
```

| Status | Situación |
|---|---|
| 201 | Tarjeta agregada |
| 400 | Datos inválidos |
| 409 | Tarjeta ya registrada |

---

#### `DELETE /accounts/{id}/cards/{cardId}` 🔒 — Eliminar tarjeta

```
// Response 200 OK (sin body)
```

---

## Guía de testing

### Colecciones Postman

Las colecciones se encuentran en la carpeta `test-cases/`:

| Archivo | Cubre |
|---|---|
| `postman_sprint3_digital_money.json` | Actividad, detalle de actividad, ingreso desde tarjeta |
| `postman_sprint4_digital_money.json` | Transferencias y últimos destinatarios |

**Cómo importar:**
1. Abrir Postman → *Import* → seleccionar el archivo `.json`
2. La colección tiene **variables de colección** preconfiguradas: `base_url`, `token`, `accountId`, etc.
3. Ejecutar primero el request **"POST /users/login"** — el script automático guarda el token y el accountId en las variables
4. Completar `cvuDestino` y `aliasDestino` con los datos de un segundo usuario para los tests de transferencias

**Variables que necesitás completar manualmente antes de correr la suite:**

```
base_url     → http://localhost:8080  (ya configurado)
cvuDestino   → CVU del usuario B (obtenelo con GET /accounts/{idB})
aliasDestino → alias del usuario B
cardId       → id de una tarjeta registrada (obtenelo con GET /cards)
accountIdB   → id de cuenta de un segundo usuario (para tests de 403)
```

### Ejecutar los scripts de test automáticos (Collection Runner)

1. En Postman, click derecho sobre la colección → *Run collection*
2. Seleccionar las carpetas a ejecutar (podés correr solo "Smoke" o toda la suite)
3. Los scripts validan status codes, tipos de respuesta y campos requeridos automáticamente

### Tests manuales — Flujo principal

Seguí este flujo para verificar el sistema de punta a punta:

```
1. POST /users/register         → crear usuario A
2. POST /users/login            → obtener token A
3. POST /accounts               → crear cuenta de A
4. POST /accounts/{id}/cards    → agregar tarjeta a la cuenta de A
5. POST /accounts/{id}/deposits → cargar $1000 desde la tarjeta
6. GET  /accounts/{id}          → verificar saldo = $1000

   (repetir pasos 1-3 para crear usuario B)

7. POST /accounts/{idA}/transferences  → transferir $300 de A a B
                                         destination: CVU de B, amount: 300
8. GET  /accounts/{idA}                → saldo de A = $700
9. GET  /accounts/{idB}                → saldo de B = $300
10. GET /accounts/{idA}/transferences  → ver a B como destinatario
11. GET /accounts/{idA}/activity       → ver el DEBITO de $300
```

### Casos de prueba manuales

Los casos de prueba completos están documentados en los archivos CSV de `test-cases/`:

| Archivo | Sprint | Suite |
|---|---|---|
| `casos_de_prueba_sprint1_sprint2.csv` | 1 y 2 | Registro, login, cuenta, tarjetas |
| `casos_de_prueba_sprint3.csv` | 3 | Actividad, detalle, ingreso por tarjeta |
| `casos_de_prueba_sprint4.csv` | 4 | Transferencias, últimos destinatarios |

Cada caso incluye: precondiciones, pasos, datos de entrada, resultado esperado, prioridad y clasificación Smoke/Regression.

### Tests automatizados (Maven)

```bash
# Desde la raíz de cada microservicio
cd user-service && ./mvnw test
cd account-service && ./mvnw test
```

---

## Decisiones de diseño y buenas prácticas

### JWT puro sin Keycloak
Se eligió JWT gestionado directamente en el `user-service` para mantener el proyecto sin dependencias de infraestructura adicional. El gateway valida el token en cada request e inyecta el `X-User-Id` en el header, de modo que los servicios aguas abajo confían en ese valor sin volver a validar el JWT.

### Config Server con repositorio Git remoto
Toda la configuración de `user-service` y `account-service` vive en un repositorio Git separado (`digital-money-house-config`). Esto permite cambiar parámetros (URLs de BD, timeouts, JWT secret) sin recompilar ni redeployar los servicios.

### Operaciones transaccionales en transferencias
El método `realizarTransferencia` usa `@Transactional`. Si cualquier paso falla (guardar el débito, el crédito, o el registro de la transferencia), toda la operación hace rollback. Esto garantiza que nunca se descuente saldo sin acreditarlo en el destino.

### Separación de endpoints: `/deposits` vs `/transferences`
El ingreso de dinero desde tarjeta y la transferencia a otra cuenta son operaciones semánticamente distintas. Se separaron en `/deposits` (ingreso) y `/transferences` (envío), lo que hace la API más expresiva y facilita la evolución independiente de cada operación.

### Multi-stage Dockerfiles
Cada Dockerfile usa dos etapas: una con JDK para compilar y otra con JRE para ejecutar. La imagen final solo contiene el JRE y el JAR, sin el código fuente ni Maven. Esto reduce el tamaño de la imagen y la superficie de ataque.

### Usuario no-root en contenedores
Todos los contenedores corren con un usuario `spring` sin privilegios de root, siguiendo el principio de mínimo privilegio.

---

## Problemas comunes y soluciones

### Los servicios no se registran en Eureka al correr con Docker

**Causa:** Las configs del repositorio Git apuntan a `localhost:8761`, pero en Docker los contenedores se llaman por nombre de servicio.

**Solución:** El `docker-compose.yml` sobreescribe la URL de Eureka con una variable de entorno:
```yaml
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/
```
Spring Boot respeta esta variable por encima del valor del config-server.

---

### `user-service` o `account-service` no arrancan (fail-fast)

**Causa:** `fail-fast: true` en el bootstrap de ambos servicios hace que fallen inmediatamente si no pueden conectar al `config-server`.

**Solución:** El docker-compose usa `healthcheck` + `condition: service_healthy` para garantizar que el config-server esté listo antes de arrancar los servicios de negocio. Si aún ocurre, esperá unos segundos y reiniciá el servicio afectado:
```bash
docker compose restart user-service
```

---

### Errores de CORS en el frontend

**Causa:** El gateway no tenía configurado CORS correctamente para el origen del frontend.

**Solución:** Se configuró `@CrossOrigin(origins = "*")` en todos los controllers y una configuración explícita de CORS en el `SecurityConfig` con `allowedOriginPatterns`. Para producción, reemplazar el `*` con el dominio real del frontend.

---

### Los datos se pierden al reiniciar los contenedores

**Causa:** Ambos servicios usan H2 in-memory como base de datos. Al detener el contenedor, los datos desaparecen.

**Solución para producción:** Configurar una instancia de MySQL (local o AWS RDS) y actualizar los configs en el repositorio de configuración con las variables de conexión correspondientes:
```yaml
spring:
  datasource:
    url: jdbc:mysql://host:3306/dmh_db
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
```

---

### Alcance de la suite de tests

La suite de Postman cubre:
- Autenticación (registro, login, logout)
- Gestión de cuenta (crear, consultar, actualizar alias)
- Tarjetas (alta, baja, listado)
- Ingreso de dinero desde tarjeta
- Transferencias a CVU y alias (happy path + errores)
- Últimos destinatarios
- Control de acceso (403 en todos los endpoints críticos)
- Validaciones de entrada (400 por campos inválidos o ausentes)
- Fondos insuficientes (410)

El testing exploratorio está documentado en `test-cases/testing_exploratorio_sprint3.md` y `test-cases/testing_exploratorio_sprint4.md`, organizados en sesiones con charter, tours y hallazgos.
# Digital-Money-Trabajo-Integrador
