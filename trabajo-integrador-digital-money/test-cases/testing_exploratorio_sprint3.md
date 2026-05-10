# Testing Exploratorio — Sprint 3
## Digital Money House

**Fecha:** 02/04/2026
**Tester:** Mateo Quotteri
**Sprint:** 3
**Servicio bajo prueba:** account-service
**Duración total de las sesiones:** ~2 horas estimadas

---

## Funcionalidades incorporadas en el sprint

| Funcionalidad | Endpoint | Descripción |
|---|---|---|
| Historial de actividad | `GET /accounts/{id}/activity` | Devuelve todas las transacciones de la cuenta ordenadas por fecha descendente |
| Detalle de actividad | `GET /accounts/{id}/activity/{transferId}` | Devuelve el detalle de una transacción específica perteneciente a la cuenta |
| Listado de tarjetas (global) | `GET /cards` | Devuelve todas las tarjetas del usuario autenticado sin necesidad de especificar accountId |
| Ingreso desde tarjeta | `POST /accounts/{id}/transferences` | Suma un monto al saldo de la cuenta usando una tarjeta registrada y genera una transacción CREDITO. Body: `{ "cardId": number, "amount": number }` |

---

## Organización del testing exploratorio

El testing exploratorio se organiza en **sesiones con charter definido**. Cada sesión tiene:
- Un objetivo concreto
- Una duración estimada
- La técnica de exploración utilizada (tour)
- Los escenarios explorados

---

## Sesión 1 — Actividad de cuenta (happy paths)

**Charter:** Explorar el comportamiento del endpoint `GET /accounts/{id}/activity` cuando el usuario tiene transacciones registradas.

**Duración estimada:** 25 minutos
**Tour aplicado:** Tour del cartógrafo — mapear el flujo normal completo de principio a fin.

### Escenario 1.1 — Actividad con múltiples transacciones

**Precondición:** Usuario autenticado con cuenta id=1 que tiene 3 transacciones previas (ej: ingresos desde tarjeta).

**Pasos:**
1. `POST /users/login` → obtener token y userId
2. `POST /accounts/{id}/transferences` × 3 (con montos distintos)
3. `GET /accounts/{id}/activity`

**Resultado esperado:**
- Status 200
- Array JSON con 3 objetos, cada uno con: `id`, `monto`, `tipo` (CREDITO), `descripcion`, `fecha`
- Ordenados de más reciente a más antiguo

**Notas de la sesión:**
- Verificar que el campo `descripcion` incluye el tipo y los últimos 4 dígitos de la tarjeta.
- Verificar que `tipo` sea siempre `CREDITO` cuando el ingreso proviene de tarjeta.

---

### Escenario 1.2 — Actividad con cuenta sin transacciones

**Precondición:** Usuario con cuenta recién creada, sin ninguna transacción.

**Pasos:**
1. `POST /users/login` → obtener token
2. `POST /accounts` → crear cuenta nueva
3. `GET /accounts/{id}/activity`

**Resultado esperado:**
- Status 200
- Array vacío `[]`

**Notas de la sesión:**
- Confirmar que no se retorna 404 ni error, sino array vacío.

---

## Sesión 2 — Actividad de cuenta (seguridad y casos borde)

**Charter:** Explorar el comportamiento del endpoint `GET /accounts/{id}/activity` ante accesos no autorizados e identificadores inválidos.

**Duración estimada:** 20 minutos
**Tour aplicado:** Tour del ladrón — intentar acceder a datos sin autorización o con identificadores de otros usuarios.

### Escenario 2.1 — Acceso a actividad de cuenta ajena

**Precondición:** Existen dos usuarios (usuario1 con cuenta id=1, usuario2 con cuenta id=2).

**Pasos:**
1. `POST /users/login` como usuario1 → obtener token
2. `GET /accounts/2/activity` usando el token de usuario1

**Resultado esperado:**
- Status 403
- Body: `{ "error": "No tienes permiso para acceder a esta cuenta" }`

---

### Escenario 2.2 — Cuenta inexistente

**Precondición:** No existe ninguna cuenta con id=9999.

**Pasos:**
1. `POST /users/login` → obtener token
2. `GET /accounts/9999/activity`

**Resultado esperado:**
- Status 400 (según manejo actual en controller)
- Body con mensaje de error

**Observación:** El controller captura `CuentaNotFoundException` y devuelve 400 en lugar de 404 para este endpoint. Podría ser una inconsistencia a evaluar en el siguiente sprint, ya que los otros endpoints (ej. `GET /accounts/{id}`) devuelven 404.

---

### Escenario 2.3 — Sin token (sin autenticación de gateway)

**Precondición:** Gateway activo.

**Pasos:**
1. `GET /accounts/1/activity` sin header `Authorization`

**Resultado esperado:**
- Status 401 o 403 rechazado por el gateway

---

## Sesión 3 — Ingreso desde tarjeta (happy paths)

**Charter:** Explorar el comportamiento completo del endpoint `POST /accounts/{id}/transferences` cuando los datos son válidos.

**Duración estimada:** 30 minutos
**Tour aplicado:** Tour del turista — explorar todas las variantes válidas del recurso.

### Escenario 3.1 — Ingreso exitoso con tarjeta de débito

**Precondición:** Cuenta id=1 con saldo $0.00. Tarjeta de débito id=1 registrada en esa cuenta.

**Pasos:**
1. Login → token
2. `POST /accounts/1/transferences` con `{ "cardId": 1, "monto": 500.00 }`
3. `GET /accounts/1` para verificar saldo actualizado

**Resultado esperado:**
- Status 201
- Body con TransaccionResponse: `id`, `monto`: 500.00, `tipo`: CREDITO, `descripcion`: "Ingreso desde tarjeta debito terminada en XXXX", `fecha`
- El saldo de la cuenta pasa de 0 a 500.00

**Notas de la sesión:**
- Verificar que la descripción tenga los últimos 4 dígitos del número de tarjeta.
- Verificar que el saldo se acumula en llamadas sucesivas.

---

### Escenario 3.2 — Ingreso exitoso con tarjeta de crédito

**Mismo flujo que 3.1** pero con una tarjeta de tipo CREDITO.

**Resultado esperado:**
- Descripción: "Ingreso desde tarjeta credito terminada en XXXX"

---

### Escenario 3.3 — Múltiples ingresos acumulativos

**Pasos:**
1. Ingreso de $100.00 → saldo: $100.00
2. Ingreso de $250.50 → saldo: $350.50
3. Ingreso de $49.50 → saldo: $400.00
4. `GET /accounts/{id}/activity` → verificar las 3 transacciones ordenadas

**Resultado esperado:** Los 3 registros aparecen en actividad, el saldo final es $400.00.

---

## Sesión 4 — Ingreso desde tarjeta (validaciones y errores)

**Charter:** Explorar los límites y casos de error del endpoint `POST /accounts/{id}/transferences`.

**Duración estimada:** 25 minutos
**Tour aplicado:** Tour del vándalo — ingresar datos inválidos, vacíos, extremos.

### Escenario 4.1 — Monto igual a cero

**Pasos:**
1. `POST /accounts/{id}/transferences` con `{ "cardId": 1, "monto": 0.00 }`

**Resultado esperado:**
- Status 400
- Mensaje indicando que el monto debe ser mayor a cero

---

### Escenario 4.2 — Monto negativo

**Pasos:**
1. `POST /accounts/{id}/transferences` con `{ "cardId": 1, "monto": -100.00 }`

**Resultado esperado:**
- Status 400
- Mensaje de validación

---

### Escenario 4.3 — Monto muy pequeño (0.01)

**Pasos:**
1. `POST /accounts/{id}/transferences` con `{ "cardId": 1, "monto": 0.01 }`

**Resultado esperado:**
- Status 201 — el monto mínimo permitido debe ser aceptado

---

### Escenario 4.4 — cardId inexistente en la cuenta

**Pasos:**
1. `POST /accounts/1/transferences` con `{ "cardId": 9999, "monto": 100.00 }`

**Resultado esperado:**
- Status 404
- Body: `{ "error": "Tarjeta no encontrada con id: 9999" }`

---

### Escenario 4.5 — cardId de otra cuenta

**Contexto:** La tarjeta id=5 pertenece a cuenta id=2 (no a cuenta id=1).

**Pasos:**
1. Login como usuario1 (cuenta id=1)
2. `POST /accounts/1/transferences` con `{ "cardId": 5, "monto": 100.00 }`

**Resultado esperado:**
- Status 404 — la tarjeta no es encontrada para esa cuenta (la query filtra por cuentaId)

**Notas de la sesión:**
- Confirmar que `TarjetaRepository.findByIdAndCuentaId()` impide usar tarjetas de otras cuentas. Esto es importante para la seguridad.

---

### Escenario 4.6 — Campo `cardId` ausente en el body

**Pasos:**
1. `POST /accounts/{id}/transferences` con `{ "monto": 100.00 }` (sin cardId)

**Resultado esperado:**
- Status 400
- Mensaje: "El id de la tarjeta es obligatorio"

---

### Escenario 4.7 — Campo `monto` ausente en el body

**Pasos:**
1. `POST /accounts/{id}/transferences` con `{ "cardId": 1 }` (sin monto)

**Resultado esperado:**
- Status 400
- Mensaje: "El monto es obligatorio"

---

### Escenario 4.8 — Body vacío

**Pasos:**
1. `POST /accounts/{id}/transferences` con `{}`

**Resultado esperado:**
- Status 400 con errores de validación por ambos campos obligatorios

---

## Sesión 5 — Detalle de actividad

**Charter:** Explorar el endpoint `GET /accounts/{id}/activity/{transferId}` para validar que devuelve correctamente el detalle de una actividad y aplica los controles de acceso.

**Duración estimada:** 20 minutos
**Tour aplicado:** Tour del detective — partir de un listado y profundizar en cada ítem.

### Escenario 5A.1 — Detalle de actividad existente

**Precondición:** Cuenta con al menos 1 transacción. Se conoce el transferId.

**Pasos:**
1. Login → token
2. `GET /accounts/{id}/activity` → tomar el id del primer elemento
3. `GET /accounts/{id}/activity/{transferId}`

**Resultado esperado:**
- Status 200
- Body con los campos: `id`, `monto`, `tipo`, `descripcion`, `fecha`
- Los valores deben coincidir exactamente con el elemento del listado

---

### Escenario 5A.2 — transferId inexistente

**Pasos:**
1. `GET /accounts/{id}/activity/999999`

**Resultado esperado:**
- Status 404
- Body: `{ "error": "Actividad no encontrada con id: 999999" }`

---

### Escenario 5A.3 — transferId de otra cuenta (acceso cruzado)

**Contexto:** transferId=5 pertenece a la cuenta id=2.

**Pasos:**
1. Login como usuario1 (cuenta id=1)
2. `GET /accounts/1/activity/5`

**Resultado esperado:**
- Status 404 — la query usa `findByIdAndCuentaId` y no encontrará la transacción

**Notas de la sesión:**
- Confirmar que un usuario no puede ver transacciones de otra cuenta usando el detalle.
- El 404 es más seguro que el 403 ya que no revela que la transacción existe.

---

### Escenario 5A.4 — accountId de otra cuenta — Forbidden

**Pasos:**
1. Login como usuario1
2. `GET /accounts/2/activity/{transferId}` (cuenta ajena)

**Resultado esperado:**
- Status 403

---

## Sesión 5B — Listado de tarjetas (GET /cards)

**Charter:** Explorar el endpoint `GET /cards` (global, sin accountId) para verificar que devuelve las tarjetas correctas del usuario autenticado.

**Duración estimada:** 15 minutos
**Tour aplicado:** Tour del turista — explorar variantes normales.

### Escenario 5B.1 — Listar tarjetas del usuario autenticado

**Precondición:** Usuario con cuenta y al menos 1 tarjeta asociada.

**Pasos:**
1. Login → token
2. `GET /cards`

**Resultado esperado:**
- Status 200
- Array de TarjetaResponse con las tarjetas del usuario

**Notas:**
- Verificar que solo aparecen las tarjetas del usuario autenticado, no las de otros.
- El endpoint determina el accountId internamente a partir del X-User-Id inyectado por el gateway.

---

### Escenario 5B.2 — Listar tarjetas sin token

**Pasos:**
1. `GET /cards` sin header Authorization

**Resultado esperado:**
- Status 401 (rechazado por el gateway)

---

### Escenario 5B.3 — Listar tarjetas cuando no hay ninguna

**Precondición:** Usuario con cuenta pero sin tarjetas.

**Pasos:**
1. Login → token
2. `GET /cards`

**Resultado esperado:**
- Status 200
- Array vacío `[]`

---

## Sesión 6 — Seguridad del ingreso desde tarjeta

**Charter:** Verificar que el endpoint respeta los controles de autorización.

**Duración estimada:** 20 minutos
**Tour aplicado:** Tour del ladrón — acceder con credenciales de otro usuario.

### Escenario 5.1 — Ingreso a cuenta ajena

**Pasos:**
1. Login como usuario2
2. `POST /accounts/1/transferences` con cardId y monto válidos

**Resultado esperado:**
- Status 403
- Body: `{ "error": "No tienes permiso para operar en esta cuenta" }`

---

### Escenario 5.2 — Sin token (sin autenticación de gateway)

**Pasos:**
1. `POST /accounts/1/transferences` sin header `Authorization`

**Resultado esperado:**
- Status 401 o 403 rechazado por el gateway

---

## Workflows explorados

### Workflow A — Ciclo completo de carga de saldo y consulta de detalle

```
Registro → Login → Crear cuenta → Agregar tarjeta →
GET /cards → POST /transferences (amount) → GET /activity →
GET /activity/{transferId} → verificar saldo en GET /accounts/{id}
```

**Objetivo:** Validar que el flujo completo desde cero funciona de extremo a extremo, incluyendo el detalle de la actividad generada.

---

### Workflow B — Historial acumulativo

```
Login → POST /transferences × N → GET /activity → verificar orden cronológico descendente
```

**Objetivo:** Confirmar que cada transacción queda registrada y el historial refleja el orden correcto.

---

### Workflow C — Seguridad cruzada

```
Crear usuario1 + cuenta1 + tarjeta1
Crear usuario2 + cuenta2
Login como usuario2
→ Intentar GET /accounts/1/activity → esperar 403
→ Intentar POST /accounts/1/transferences → esperar 403
```

**Objetivo:** Confirmar que los controles de acceso por cuentaId + userId funcionan correctamente en todos los nuevos endpoints.

---

## Hallazgos y observaciones

| ID | Tipo | Descripción | Severidad |
|---|---|---|---|
| HAL-01 | Inconsistencia | `GET /activity` devuelve 400 para cuenta inexistente, mientras `GET /{id}` devuelve 404. Inconsistencia en manejo de excepciones del controller. | Baja |
| HAL-02 | Observación | La descripción de la transacción usa `.toLowerCase()` sobre el enum TipoTarjeta. Verificar que el output sea "debito"/"credito" (sin tilde) según se espera. | Informativo |
| HAL-03 | Observación | El endpoint `POST /transferences` usa @Transactional: si falla el guardado de la transacción luego de actualizar el saldo, el rollback protege la consistencia. Confirmar en pruebas de error simulado. | Informativo |
| HAL-04 | Cambio de contrato | El body de `POST /transferences` ahora usa `"amount"` como nombre de campo JSON (se agregó `@JsonProperty("amount")` sobre el campo `monto`). Verificar que las requests usen `amount` y no `monto`. | Media |
| HAL-05 | Seguridad | El endpoint `GET /accounts/{id}/activity/{transferId}` devuelve 404 (no 403) cuando el transferId pertenece a otra cuenta. Esto es correcto desde el punto de vista de seguridad (no revela existencia de recursos ajenos). | Informativo |
| HAL-06 | Diseño | El endpoint `GET /cards` resuelve el accountId internamente desde el userId inyectado por el gateway. Verificar que usuarios sin cuenta reciben 404 y no un error 500. | Media |

---

## Criterios de cobertura exploratoria alcanzados

- [x] Flujo exitoso principal (happy path)
- [x] Validaciones de entrada (campos nulos, valores inválidos)
- [x] Control de acceso (cuenta propia vs cuenta ajena)
- [x] Respuesta ante recursos inexistentes
- [x] Flujo acumulativo (múltiples operaciones consecutivas)
- [x] Integración entre transferences y activity (consistencia de datos)
- [x] Acceso sin autenticación (gateway)
- [x] Detalle de actividad (transferId válido, inexistente y de otra cuenta)
- [x] Listado de tarjetas global sin accountId en URL (`GET /cards`)
- [x] Workflow completo: GET /cards → POST /transferences → GET /activity → GET /activity/{id}
