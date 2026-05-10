# Testing Exploratorio — Sprint 4
## Digital Money House

**Fecha:** 07/04/2026
**Tester:** Mateo Quotteri
**Sprint:** 4
**Servicio bajo prueba:** account-service
**Duración total de las sesiones:** ~2.5 horas estimadas

---

## Funcionalidades incorporadas en el sprint

| Funcionalidad | Endpoint | Descripción |
|---|---|---|
| Últimos destinatarios | `GET /accounts/{id}/transferences` | Devuelve el historial de transferencias enviadas por la cuenta, ordenadas por fecha descendente |
| Realizar transferencia | `POST /accounts/{id}/transferences` | Transfiere dinero desde el saldo de la cuenta hacia otra cuenta identificada por CVU o alias. Body: `{ "destination": "string", "amount": number }` |
| Ingreso desde tarjeta (movido) | `POST /accounts/{id}/deposits` | Endpoint anterior de ingreso por tarjeta, reubicado para separar semánticamente ingresos de transferencias |

---

## Organización del testing exploratorio

El testing exploratorio se organiza en **sesiones con charter definido**. Cada sesión tiene:
- Un objetivo concreto
- Una duración estimada
- La técnica de exploración utilizada (tour)
- Los escenarios explorados

---

## Sesión 1 — Transferencia exitosa (happy paths)

**Charter:** Explorar el comportamiento de `POST /accounts/{id}/transferences` cuando los datos son válidos y hay fondos suficientes.

**Duración estimada:** 30 minutos
**Tour aplicado:** Tour del turista — explorar todas las variantes válidas del recurso.

### Escenario 1.1 — Transferencia por CVU exitosa

**Precondición:** Usuario A con saldo $1000.00. Usuario B con cuenta existente y CVU conocido.

**Pasos:**
1. `POST /users/login` como usuario A → token + accountId
2. `GET /accounts/{idA}` → registrar saldo inicial ($1000.00)
3. `POST /accounts/{idA}/transferences` con body `{ "destination": "<CVU de B>", "amount": 300.00 }`
4. `GET /accounts/{idA}` → verificar saldo actualizado ($700.00)
5. `GET /accounts/{idB}` → verificar que B recibió el dinero (+$300.00)

**Resultado esperado:**
- Status 200 en el POST
- Body con TransaccionResponse: `id`, `monto`: 300.00, `tipo`: DEBITO, `descripcion`: "Transferencia enviada a <CVU>", `fecha`
- Saldo de A pasa de $1000 a $700
- Saldo de B aumenta en $300

**Notas de la sesión:**
- Verificar que el tipo es DEBITO (no CREDITO).
- Verificar que el response tiene status 200 (no 201 como el ingreso por tarjeta).

---

### Escenario 1.2 — Transferencia por alias exitosa

**Precondición:** Usuario A con saldo suficiente. Usuario B con alias conocido (ej: `sol.luna.rio`).

**Pasos:**
1. Login como usuario A
2. `POST /accounts/{idA}/transferences` con `{ "destination": "sol.luna.rio", "amount": 150.00 }`
3. Verificar saldos en ambas cuentas

**Resultado esperado:**
- Status 200
- El sistema resuelve el alias correctamente al CVU interno
- Saldo de A disminuye; saldo de B aumenta

**Notas de la sesión:**
- Confirmar que el campo `cbuCvuAlias` en el registro de la transferencia almacena el alias tal como fue ingresado.

---

### Escenario 1.3 — Monto exacto igual al saldo disponible

**Precondición:** Usuario A con saldo exacto $500.00.

**Pasos:**
1. `POST /accounts/{idA}/transferences` con `{ "destination": "<CVU B>", "amount": 500.00 }`
2. `GET /accounts/{idA}` → verificar saldo

**Resultado esperado:**
- Status 200
- Saldo de A queda exactamente en $0.00

**Notas de la sesión:**
- Verificar que el comparador usa `>=` (mayor o igual) para permitir el caso límite.

---

### Escenario 1.4 — Múltiples transferencias consecutivas

**Pasos:**
1. Transferencia de $100 → saldo A: $900, saldo B: +$100
2. Transferencia de $200 → saldo A: $700, saldo B: +$200
3. Transferencia de $50 → saldo A: $650, saldo B: +$50
4. `GET /accounts/{idA}/activity` → verificar 3 transacciones DEBITO

**Resultado esperado:** Los 3 registros aparecen en actividad ordenados DESC por fecha. Saldos son consistentes.

---

## Sesión 2 — Últimos destinatarios (GET /transferences)

**Charter:** Explorar el endpoint `GET /accounts/{id}/transferences` para verificar que devuelve correctamente el historial de transferencias enviadas.

**Duración estimada:** 20 minutos
**Tour aplicado:** Tour del detective — verificar que los datos persisten y se recuperan correctamente.

### Escenario 2.1 — Lista con transferencias realizadas

**Precondición:** Usuario A con al menos 2 transferencias enviadas a destinatarios distintos.

**Pasos:**
1. Login como A
2. `POST /accounts/{idA}/transferences` × 2 (a destinos distintos)
3. `GET /accounts/{idA}/transferences`

**Resultado esperado:**
- Status 200
- Array con 2 (o más) elementos
- Cada elemento tiene: `id`, `cbuCvuAlias` (el destino ingresado), `monto`, `fecha`
- Ordenados de más reciente a más antiguo

**Notas de la sesión:**
- Verificar que el campo `cbuCvuAlias` refleja exactamente lo que se ingresó (CVU o alias).
- Verificar el ordenamiento por fecha DESC.

---

### Escenario 2.2 — Lista vacía (sin transferencias previas)

**Precondición:** Cuenta sin transferencias enviadas.

**Pasos:**
1. Login
2. `GET /accounts/{id}/transferences` en cuenta nueva

**Resultado esperado:**
- Status 200
- Array vacío `[]`

**Notas de la sesión:**
- Confirmar que no retorna 404 ni error, sino array vacío. Consistente con el comportamiento de `/activity`.

---

### Escenario 2.3 — La transferencia aparece inmediatamente después de realizarla

**Pasos:**
1. `GET /accounts/{id}/transferences` → registrar cantidad inicial
2. `POST /accounts/{id}/transferences` (nueva transferencia)
3. `GET /accounts/{id}/transferences` → verificar que aparece como primer elemento

**Resultado esperado:**
- La nueva transferencia es el primer elemento del array (más reciente)
- El `cbuCvuAlias` y `monto` coinciden con lo enviado

---

## Sesión 3 — Seguridad y control de acceso

**Charter:** Explorar que los endpoints respetan los controles de autorización.

**Duración estimada:** 25 minutos
**Tour aplicado:** Tour del ladrón — intentar acceder y operar con credenciales ajenas.

### Escenario 3.1 — Transferir desde cuenta ajena

**Pasos:**
1. Login como usuario A (cuenta id=X)
2. `POST /accounts/{idCuentaB}/transferences` con token de A

**Resultado esperado:**
- Status 403 — "No tienes permiso para operar en esta cuenta"
- El saldo de ninguna cuenta cambia

---

### Escenario 3.2 — Ver destinatarios de cuenta ajena

**Pasos:**
1. Login como usuario A
2. `GET /accounts/{idCuentaB}/transferences` con token de A

**Resultado esperado:**
- Status 403

---

### Escenario 3.3 — Sin token (gateway)

**Pasos:**
1. `POST /accounts/{id}/transferences` sin header Authorization
2. `GET /accounts/{id}/transferences` sin header Authorization

**Resultado esperado:**
- Status 401 en ambos casos, rechazado por el gateway antes de llegar al servicio

---

### Escenario 3.4 — Auto-transferencia (cuenta propia como destino)

**Pasos:**
1. Login como usuario A
2. Obtener propio CVU con `GET /accounts/{id}`
3. `POST /accounts/{id}/transferences` con el propio CVU como destino

**Resultado esperado:**
- Status 400 — "No podés transferirte dinero a vos mismo"
- El saldo no cambia

---

## Sesión 4 — Validaciones y casos de error

**Charter:** Explorar el comportamiento del endpoint `POST /accounts/{id}/transferences` ante datos inválidos o condiciones de error.

**Duración estimada:** 30 minutos
**Tour aplicado:** Tour del vándalo — ingresar datos inválidos, límites y extremos.

### Escenario 4.1 — Cuenta destino inexistente (400)

**Pasos:**
1. `POST /accounts/{id}/transferences` con CVU que no existe en el sistema

**Resultado esperado:**
- Status 400 Bad Request
- Mensaje: "Cuenta destino no encontrada: <CVU>"

**Notas:**
- Verificar que el saldo de la cuenta origen NO se modifica.
- Confirmar que el error es 400 (no 404), según el contrato del sprint.

---

### Escenario 4.2 — Fondos insuficientes (410)

**Pasos:**
1. Verificar saldo actual (ej: $100.00)
2. `POST /accounts/{id}/transferences` con amount de $9999.99

**Resultado esperado:**
- Status 410 Gone
- Mensaje: "Saldo insuficiente para realizar la transferencia"
- Verificar con `GET /accounts/{id}` que el saldo NO cambió

**Notas:**
- El 410 Gone es el código HTTP para "el recurso ya no está disponible" — en este contexto semántico representa fondos agotados/insuficientes.
- Confirmar que la transacción es atómica: si falla por fondos, no se registra ningún cambio.

---

### Escenario 4.3 — Monto cero

**Pasos:**
1. `POST /accounts/{id}/transferences` con `{ "destination": "<CVU válido>", "amount": 0 }`

**Resultado esperado:**
- Status 400 — falla la validación @DecimalMin(0.01)

---

### Escenario 4.4 — Monto negativo

**Pasos:**
1. `POST /accounts/{id}/transferences` con `{ "destination": "<CVU válido>", "amount": -50.00 }`

**Resultado esperado:**
- Status 400 — falla la validación de monto

---

### Escenario 4.5 — Monto mínimo válido (0.01)

**Pasos:**
1. `POST /accounts/{id}/transferences` con `{ "destination": "<CVU válido>", "amount": 0.01 }`
2. Usuario con saldo >= 0.01

**Resultado esperado:**
- Status 200 — el monto mínimo debe ser aceptado

---

### Escenario 4.6 — Destination vacío

**Pasos:**
1. `POST /accounts/{id}/transferences` con `{ "destination": "", "amount": 100 }`

**Resultado esperado:**
- Status 400 — falla la validación @NotBlank

---

### Escenario 4.7 — Body vacío

**Pasos:**
1. `POST /accounts/{id}/transferences` con body `{}`

**Resultado esperado:**
- Status 400 con mensajes de error para `destination` (obligatorio) y `amount` (obligatorio)

---

## Sesión 5 — Atomicidad y consistencia de datos

**Charter:** Verificar que las transferencias son atómicas: si algo falla, ningún saldo cambia.

**Duración estimada:** 20 minutos
**Tour aplicado:** Tour del saboteador — inducir condiciones de fallo para verificar consistencia.

### Escenario 5.1 — Fondos insuficientes no afectan el saldo

**Pasos:**
1. Registrar saldo inicial de A
2. Intentar transferencia con monto > saldo → 410
3. `GET /accounts/{idA}` → verificar que el saldo no cambió

**Resultado esperado:** El saldo permanece igual al registrado en el paso 1.

---

### Escenario 5.2 — Cuenta destino inexistente no afecta el saldo

**Pasos:**
1. Registrar saldo inicial de A
2. `POST /accounts/{idA}/transferences` con CVU inexistente → 400
3. `GET /accounts/{idA}` → verificar que el saldo no cambió

**Resultado esperado:** El saldo permanece intacto.

---

### Escenario 5.3 — Verificación de consistencia bidireccional

**Pasos:**
1. Saldo A = $1000, Saldo B = $200
2. Transferencia de A hacia B por $300
3. `GET /accounts/{idA}` → $700
4. `GET /accounts/{idB}` → $500
5. Suma total = $1200 (igual a la suma antes de la transferencia)

**Resultado esperado:** La suma total de saldos se conserva ($1200 antes y después).

---

## Sesión 6 — Mantenimiento: ingreso por tarjeta en /deposits

**Charter:** Verificar que el endpoint de ingreso por tarjeta sigue funcionando en su nueva ubicación `/deposits` después del cambio de contrato del sprint 4.

**Duración estimada:** 15 minutos
**Tour aplicado:** Tour del cartógrafo — mapear el comportamiento del endpoint en su nueva ruta.

### Escenario 6.1 — Ingreso exitoso por tarjeta en /deposits

**Pasos:**
1. Login → token
2. `GET /cards` → obtener cardId
3. `POST /accounts/{id}/deposits` con `{ "cardId": <id>, "amount": 500.00 }`
4. `GET /accounts/{id}` → verificar saldo actualizado

**Resultado esperado:**
- Status 201 Created (se mantiene el 201 del endpoint original)
- Body con TransaccionResponse tipo CREDITO

**Notas:**
- El cambio de `/transferences` a `/deposits` para el ingreso por tarjeta es el principal cambio de contrato de este sprint.
- Verificar que los clientes que usaban `/transferences` para ingresar saldo necesitan actualizar su URL.

---

### Escenario 6.2 — Acceso a /deposits sin token

**Pasos:**
1. `POST /accounts/{id}/deposits` sin header Authorization

**Resultado esperado:**
- Status 401 rechazado por gateway

---

## Workflows explorados

### Workflow A — Ciclo completo de transferencia

```
Login → GET /accounts/{id} (saldo inicial) →
POST /accounts/{id}/deposits (cargar saldo) →
POST /accounts/{id}/transferences (transferir) →
GET /accounts/{id} (verificar saldo debitado) →
GET /accounts/{idDestino} (verificar saldo acreditado) →
GET /accounts/{id}/transferences (ver destinatario) →
GET /accounts/{id}/activity (ver DEBITO en historial)
```

**Objetivo:** Validar el flujo completo de carga y envío de dinero, incluyendo la persistencia en todos los registros relacionados.

---

### Workflow B — Transferencia a usuario nuevo (sin historial)

```
Crear usuario B (nuevo, sin transferencias previas) →
Login como A →
POST /accounts/{idA}/transferences hacia B →
Login como B →
GET /accounts/{idB}/activity → verificar CREDITO →
GET /accounts/{idB}/transferences → debe ser vacío (B no envió nada)
```

**Objetivo:** Confirmar que el historial de destinatarios es exclusivo de transferencias *enviadas*, no recibidas.

---

### Workflow C — Seguridad cruzada

```
Crear usuario A con cuenta + saldo
Crear usuario B con cuenta
Login como B
→ Intentar GET /accounts/{idA}/transferences → esperar 403
→ Intentar POST /accounts/{idA}/transferences (destination: CVU de C) → esperar 403
→ Verificar que saldo de A no cambió
```

**Objetivo:** Confirmar que los controles de acceso funcionan correctamente en ambos endpoints nuevos.

---

### Workflow D — Agotamiento de saldo progresivo

```
Login como A (saldo: $500)
Transferencia $200 → saldo: $300 (OK)
Transferencia $200 → saldo: $100 (OK)
Transferencia $200 → 410 (fondos insuficientes)
GET /accounts/{idA} → verificar saldo: $100 (no cambió)
GET /accounts/{idA}/transferences → solo 2 transferencias registradas
```

**Objetivo:** Verificar el manejo correcto del límite de fondos y la atomicidad de la operación fallida.

---

## Hallazgos y observaciones

| ID | Tipo | Descripción | Severidad |
|---|---|---|---|
| HAL-S4-01 | Cambio de contrato | El endpoint de ingreso por tarjeta se movió de `POST /accounts/{id}/transferences` a `POST /accounts/{id}/deposits`. Los consumidores anteriores deben actualizar sus llamadas. | Alta |
| HAL-S4-02 | Diseño | `POST /accounts/{id}/transferences` retorna status 200 (no 201) para una transferencia exitosa. Consistente con la especificación del sprint pero difiere del endpoint de depósito que retorna 201. | Informativo |
| HAL-S4-03 | Código HTTP | Se usa 410 Gone para fondos insuficientes. Si bien no es el uso estándar del código, es el especificado para este proyecto. | Informativo |
| HAL-S4-04 | Diseño | La auto-transferencia (destino = origen) se valida con 400 Bad Request. Sin esta validación, la operación sería semánticamente incoherente (restaría y sumaría el mismo saldo). | Informativo |
| HAL-S4-05 | Consistencia | El GET /transferences muestra *todas* las transferencias enviadas (no deduplicadas). La deduplicación por destinatario único corresponde al frontend. Confirmar que el frontend filtra correctamente. | Media |
| HAL-S4-06 | Observación | La operación de transferencia usa `@Transactional`: si algún guardado falla, el rollback restaura ambos saldos. Verificar en pruebas de integración que el comportamiento transaccional es correcto bajo fallo simulado. | Informativo |
| HAL-S4-07 | Observación | El campo `destination` acepta CVU (22 dígitos) o alias (formato palabra.palabra.palabra). No hay validación de formato: cualquier string no vacío es aceptado; el error de cuenta inexistente se lanza si no coincide. | Baja |

---

## Criterios de cobertura exploratoria alcanzados

- [x] Flujo exitoso de transferencia por CVU
- [x] Flujo exitoso de transferencia por alias
- [x] Verificación de saldo en cuenta origen (debitado)
- [x] Verificación de saldo en cuenta destino (acreditado)
- [x] Historial de últimos destinatarios (lista con datos, lista vacía)
- [x] Persistencia inmediata: transferencia aparece en GET /transferences
- [x] Control de acceso (cuenta propia vs cuenta ajena) en ambos endpoints
- [x] Acceso sin autenticación (gateway)
- [x] Cuenta destino inexistente (400)
- [x] Fondos insuficientes (410)
- [x] Validaciones de entrada (monto cero, negativo, campos ausentes)
- [x] Auto-transferencia (400)
- [x] Atomicidad: saldo no cambia cuando la operación falla
- [x] Consistencia bidireccional: suma total de saldos se conserva
- [x] Mantenimiento del endpoint /deposits (ingreso por tarjeta)
- [x] Workflow completo: depósito → transferencia → verificación de saldos e historial
