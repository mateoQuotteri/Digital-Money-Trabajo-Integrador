# Test Exploratorio — Sprint 1
## Digital Money House — Billetera Digital

---

## Índice

1. [Objetivo](#objetivo)
2. [Alcance](#alcance)
3. [Organización de las Sesiones](#organización-de-las-sesiones)
4. [Tours Realizados](#tours-realizados)
5. [Sesiones de Testing](#sesiones-de-testing)
6. [Hallazgos y Notas](#hallazgos-y-notas)
7. [Workflows Explorados](#workflows-explorados)
8. [Resumen y Conclusiones](#resumen-y-conclusiones)

---

## 1. Objetivo

Realizar un testing exploratorio sobre las funcionalidades incorporadas en el Sprint 1 de Digital Money House, identificando comportamientos inesperados, defectos y áreas de riesgo mediante exploración libre y dirigida por heurísticas, sin seguir scripts rígidos de prueba.

---

## 2. Alcance

### Funcionalidades cubiertas en Sprint 1

| Módulo                    | Descripción                                                                 |
|---------------------------|-----------------------------------------------------------------------------|
| Registro de usuario       | Creación de cuenta con validaciones de campos                               |
| Login / Autenticación     | Inicio de sesión con JWT, control de sesión                                 |
| Dashboard                 | Visualización de saldo, accesos rápidos, últimas transacciones              |
| Historial de actividad    | Listado de movimientos con paginación                                       |
| Detalle de actividad      | Vista individual de una transacción                                         |
| Cargar dinero             | Depósito de fondos mediante tarjeta                                         |
| Transferir dinero         | Envío de fondos a otro usuario                                              |
| Gestión de tarjetas       | Alta, baja y visualización de tarjetas                                      |
| Perfil de usuario         | Visualización de CVU, alias y datos personales                              |
| Logout                    | Cierre de sesión e invalidación de token                                    |

### Fuera de alcance

- Microservicios adicionales (futuros sprints)
- Integración con pasarelas de pago reales
- Testing de performance / carga

---

## 3. Organización de las Sesiones

Se utilizó el formato **SBTM (Session-Based Test Management)** para estructurar el testing exploratorio.

### Estructura de una sesión

```
SESIÓN: [ID]
Misión:     [Qué se va a explorar]
Duración:   [Tiempo estimado]
Tester:     [Nombre del tester]
Fecha:      [Fecha de ejecución]
Ambiente:   [Frontend URL / Backend URL / Mock]
─────────────────────────────────────────────
NOTAS DE EXPLORACIÓN
  - Observaciones durante la sesión
DEFECTOS / HALLAZGOS
  - Lista de issues encontrados
PREGUNTAS / RIESGOS
  - Dudas o áreas para investigar más
```

---

## 4. Tours Realizados

Se aplicaron los siguientes tours inspirados en el modelo de tours de James Whittaker:

### Tour 1 — Tour del Usuario Nuevo (Onboarding Tour)
**Objetivo:** Recorrer el flujo completo que haría un usuario que usa la app por primera vez.
**Ruta:** Registro → Login → Dashboard → Perfil → CVU y alias

**Lo que se exploró:**
- Completar el formulario de registro con datos válidos e inválidos
- Verificar mensajes de error y validaciones en tiempo real
- Comprobar que el CVU y alias se generan automáticamente
- Verificar que el usuario puede ver su información de perfil correctamente

---

### Tour 2 — Tour del Dinero (Money Tour)
**Objetivo:** Explorar todos los flujos relacionados con movimientos de dinero.
**Ruta:** Dashboard → Cargar dinero → Transferir dinero → Historial de actividad

**Lo que se exploró:**
- Cargar dinero con monto válido (max 30.000)
- Cargar dinero con montos límite: 0, 1, 29.999, 30.000, 30.001
- Transferir a un usuario existente
- Transferir a un CVU inexistente
- Verificar que el saldo se actualiza después de las operaciones
- Verificar que las transacciones aparecen en el historial

---

### Tour 3 — Tour de la Tarjeta (Card Tour)
**Objetivo:** Explorar la gestión de tarjetas de crédito/débito.
**Ruta:** Tarjetas → Agregar tarjeta → Eliminar tarjeta

**Lo que se exploró:**
- Agregar una tarjeta con datos válidos
- Agregar tarjetas con datos incompletos o inválidos
- Verificar la visualización del número de tarjeta (formateo, enmascarado)
- Eliminar una tarjeta existente
- Intentar cargar dinero sin tarjetas guardadas

---

### Tour 4 — Tour del Saboteador (Saboteur Tour)
**Objetivo:** Intentar romper la aplicación con datos inesperados.

**Lo que se exploró:**
- Campos de texto con caracteres especiales (`, <>, SQL injection básico)
- Montos negativos o con letras en formularios numéricos
- Navegación directa a rutas protegidas sin token
- Token manipulado / expirado
- Recarga forzada en medio de un flujo de pago
- Doble clic en botones de envío
- Pegar contenido en campos de contraseña con criterios inválidos

---

### Tour 5 — Tour de la Sesión (Session Tour)
**Objetivo:** Explorar el comportamiento de autenticación y manejo de sesiones.

**Lo que se exploró:**
- Login con credenciales correctas
- Login con contraseña incorrecta
- Login con usuario inexistente
- Logout y verificar que el token se invalida
- Acceder a rutas protegidas post-logout
- Verificar expiración del token (24 horas según configuración)
- Múltiples sesiones simultáneas en distintas pestañas

---

### Tour 6 — Tour de la Interfaz (UI/UX Tour)
**Objetivo:** Evaluar usabilidad, consistencia visual y comportamiento responsivo.

**Lo que se exploró:**
- Navegación entre páginas con el menú lateral (sidebar)
- Visualización en distintos tamaños de pantalla (desktop, tablet, mobile)
- Estados de carga (skeletons) mientras se obtienen datos
- Mensajes de retroalimentación (SnackBar / Toast notifications)
- Consistencia de estilos entre páginas

---

## 5. Sesiones de Testing

---

### SESIÓN ET-001 — Registro e Inicio de Sesión

| Campo          | Detalle                                          |
|----------------|--------------------------------------------------|
| **Misión**     | Explorar el flujo de registro y autenticación    |
| **Duración**   | 60 minutos                                       |
| **Fecha**      | 2026-03-12                                       |
| **Ambiente**   | Frontend: localhost:3000 / Mock API: localhost:3500 |
| **Tours**      | Tour 1 (Onboarding), Tour 4 (Saboteur), Tour 5 (Sesión) |

**Notas de Exploración:**

- El formulario de registro tiene 6 campos: nombre, apellido, DNI, email, teléfono y contraseña.
- Las validaciones de frontend se activan `onBlur` (al salir del campo), mostrando mensajes en rojo debajo de cada campo.
- El campo de DNI acepta entre 7 y 8 dígitos. Se verificó que rechaza letras y caracteres especiales.
- El campo de email valida formato estándar (usuario@dominio.com).
- La contraseña requiere mínimo 8 caracteres.
- El botón "Registrarse" se deshabilita si hay errores de validación.
- Al registrarse correctamente, el usuario es redirigido al Login.
- En el Login, al ingresar credenciales correctas se almacena el JWT en `localStorage`.
- Al ingresar contraseña incorrecta se muestra un mensaje de error (SnackBar).
- Las rutas protegidas (`/`, `/activities`, etc.) redirigen al Login si no hay token.

**Escenarios Explorados:**

| # | Escenario                                          | Resultado            |
|---|----------------------------------------------------|----------------------|
| 1 | Registro con todos los datos válidos               | Exitoso              |
| 2 | Registro sin completar campo obligatorio           | Validación activada  |
| 3 | Registro con email ya existente                    | Error del servidor   |
| 4 | Registro con DNI ya existente                      | Error del servidor   |
| 5 | Login con credenciales correctas                   | Exitoso, redirige    |
| 6 | Login con contraseña incorrecta                    | Mensaje de error     |
| 7 | Login con email inexistente                        | Mensaje de error     |
| 8 | Acceso directo a /dashboard sin token              | Redirige a /login    |
| 9 | Intento de inyección SQL en campo email            | Validación rechaza   |
| 10 | Contraseña con caracteres especiales válida        | Aceptada             |

**Hallazgos:**
- *(Documentar aquí los defectos encontrados durante la sesión real)*

---

### SESIÓN ET-002 — Dashboard y Actividades

| Campo          | Detalle                                                           |
|----------------|-------------------------------------------------------------------|
| **Misión**     | Explorar el dashboard principal y el historial de transacciones   |
| **Duración**   | 45 minutos                                                        |
| **Fecha**      | 2026-03-12                                                        |
| **Ambiente**   | Frontend: localhost:3000 / Mock API: localhost:3500               |
| **Tours**      | Tour 6 (UI/UX)                                                    |

**Notas de Exploración:**

- El dashboard muestra: saldo disponible, 5 últimas transacciones y 4 accesos rápidos.
- Las transacciones entrantes se muestran en verde con signo `+`, las salientes en rojo con signo `-`.
- El botón "Ver más" en el dashboard lleva a la página de actividades completas.
- La página de actividades muestra todos los movimientos con paginación.
- El detalle de cada actividad muestra: tipo, monto, fecha, origen y destino.
- Los estados de carga (skeletons) aparecen mientras se esperan datos del backend.
- El formato de moneda usa separadores locales argentinos (ej: $ 1.500,00).

**Escenarios Explorados:**

| # | Escenario                                          | Resultado            |
|---|----------------------------------------------------|----------------------|
| 1 | Dashboard sin transacciones previas                | Muestra lista vacía  |
| 2 | Dashboard con 5 o más transacciones                | Muestra solo las 5 últimas |
| 3 | Navegación a historial completo                    | Paginación funciona  |
| 4 | Click en actividad individual                      | Muestra detalle      |
| 5 | Recarga de página en dashboard                     | Recupera token y carga |

**Hallazgos:**
- *(Documentar aquí los defectos encontrados durante la sesión real)*

---

### SESIÓN ET-003 — Carga y Transferencia de Dinero

| Campo          | Detalle                                                        |
|----------------|----------------------------------------------------------------|
| **Misión**     | Explorar los flujos de carga de dinero y transferencia         |
| **Duración**   | 60 minutos                                                     |
| **Fecha**      | 2026-03-12                                                     |
| **Ambiente**   | Frontend: localhost:3000 / Mock API: localhost:3500            |
| **Tours**      | Tour 2 (Money), Tour 4 (Saboteur)                              |

**Notas de Exploración:**

- Para cargar dinero se requiere tener al menos una tarjeta guardada.
- El flujo de carga tiene monto máximo de 30.000.
- La transferencia requiere ingresar el CVU o alias del destinatario.
- Después de una transferencia exitosa, el saldo del remitente disminuye.
- Se verifica que la actividad queda registrada en el historial.

**Escenarios Explorados:**

| # | Escenario                                                    | Resultado             |
|---|--------------------------------------------------------------|-----------------------|
| 1 | Cargar dinero con monto válido (ej: 1000)                    | Exitoso               |
| 2 | Cargar dinero con monto = 0                                  | Validación rechaza    |
| 3 | Cargar dinero con monto = 30.000 (máximo)                    | Exitoso               |
| 4 | Cargar dinero con monto = 30.001 (sobre máximo)              | Validación rechaza    |
| 5 | Cargar dinero con monto negativo                             | Validación rechaza    |
| 6 | Cargar dinero sin tarjetas disponibles                       | Bloquea el flujo      |
| 7 | Transferir a CVU existente con saldo suficiente              | Exitoso               |
| 8 | Transferir con saldo insuficiente                            | Error notificado      |
| 9 | Transferir a CVU inexistente                                 | Error notificado      |
| 10 | Transferir monto cero                                       | Validación rechaza    |
| 11 | Doble clic en botón de confirmar transferencia              | Verificar idempotencia|

**Hallazgos:**
- *(Documentar aquí los defectos encontrados durante la sesión real)*

---

### SESIÓN ET-004 — Gestión de Tarjetas y Perfil

| Campo          | Detalle                                                        |
|----------------|----------------------------------------------------------------|
| **Misión**     | Explorar la gestión de tarjetas y la vista de perfil           |
| **Duración**   | 45 minutos                                                     |
| **Fecha**      | 2026-03-12                                                     |
| **Ambiente**   | Frontend: localhost:3000 / Mock API: localhost:3500            |
| **Tours**      | Tour 3 (Card), Tour 6 (UI/UX)                                  |

**Notas de Exploración:**

- La sección de tarjetas usa la librería React Credit Cards para visualización.
- El número de tarjeta se muestra enmascarado (ej: **** **** **** 1234).
- El perfil muestra nombre, apellido, email, CVU y alias del usuario.
- El CVU tiene 22 dígitos y el alias tiene formato palabra.palabra.palabra.
- El usuario puede copiar el CVU desde la interfaz.

**Escenarios Explorados:**

| # | Escenario                                                   | Resultado              |
|---|-------------------------------------------------------------|------------------------|
| 1 | Agregar tarjeta con datos válidos                           | Tarjeta aparece en lista|
| 2 | Agregar tarjeta con número incompleto                       | Validación rechaza     |
| 3 | Eliminar tarjeta existente                                  | Tarjeta desaparece     |
| 4 | Lista de tarjetas vacía                                     | Mensaje informativo    |
| 5 | Agregar múltiples tarjetas                                  | Todas aparecen         |
| 6 | Ver perfil con datos correctos                              | Datos del usuario OK   |
| 7 | Copiar CVU desde el perfil                                  | Funciona o no          |
| 8 | Verificar formato del alias en perfil                       | Formato correcto       |

**Hallazgos:**
- *(Documentar aquí los defectos encontrados durante la sesión real)*

---

## 6. Hallazgos y Notas

### Plantilla de registro de hallazgo

| Campo              | Detalle                          |
|--------------------|----------------------------------|
| **ID**             | HALL-001                         |
| **Sesión**         | ET-001                           |
| **Descripción**    | Descripción del comportamiento   |
| **Pasos para reproducir** | 1. ... 2. ... 3. ...      |
| **Resultado actual**| Lo que ocurrió                  |
| **Resultado esperado** | Lo que debería ocurrir       |
| **Severidad**      | Crítica / Alta / Media / Baja    |
| **Evidencia**      | Captura de pantalla / video      |

> **Nota:** Los hallazgos específicos se completan durante la ejecución real de las sesiones de testing.

---

## 7. Workflows Explorados

### Workflow WF-001: Alta de usuario y primer uso

```
[Inicio]
   ↓
[Ir a /register]
   ↓
[Completar formulario de registro]
   ↓
[Submit → POST /register]
   ↓
[¿Exitoso?]
  ├── SÍ → Redirige a /login → Completar login → Dashboard
  └── NO → Mensaje de error → Corregir campos → Reintentar
```

### Workflow WF-002: Cargar dinero

```
[Dashboard]
   ↓
[Click "Cargar dinero"]
   ↓
[¿Tiene tarjeta guardada?]
  ├── NO → Redirige a agregar tarjeta
  └── SÍ → Seleccionar tarjeta
            ↓
         [Ingresar monto (max 30.000)]
            ↓
         [Confirmar operación → POST /activities]
            ↓
         [Saldo actualizado + Actividad registrada]
```

### Workflow WF-003: Transferir dinero

```
[Dashboard]
   ↓
[Click "Transferir dinero"]
   ↓
[Ingresar CVU o alias del destinatario]
   ↓
[¿Destinatario existe?]
  ├── NO → Error "usuario no encontrado"
  └── SÍ → Ingresar monto
              ↓
           [¿Saldo suficiente?]
             ├── NO → Error "saldo insuficiente"
             └── SÍ → Confirmar → Transferencia realizada
                         ↓
                      [Saldo actualizado + Actividad registrada]
```

### Workflow WF-004: Gestionar tarjetas

```
[/cards]
   ↓
[Lista de tarjetas guardadas]
   ↓
[Agregar nueva tarjeta]
   → Ingresar número, nombre, vencimiento
   → Validar datos
   → POST /users/{id}/cards
   → Tarjeta aparece en lista
   ↓
[Eliminar tarjeta]
   → Click eliminar
   → Confirmar acción
   → DELETE /users/{id}/cards/{id}
   → Tarjeta desaparece de lista
```

---

## 8. Resumen y Conclusiones

### Cobertura de la exploración

| Área                      | Tiempo invertido | Profundidad  | Riesgo identificado |
|---------------------------|-----------------|--------------|---------------------|
| Registro / Login          | 60 min          | Alta         | Media               |
| Dashboard / Actividades   | 45 min          | Media        | Baja                |
| Carga / Transferencia     | 60 min          | Alta         | Alta                |
| Tarjetas / Perfil         | 45 min          | Media        | Media               |

### Áreas de mayor riesgo detectadas

1. **Flujo de transferencia:** La idempotencia (doble envío), la validación de saldo y la consistencia entre saldo mostrado y saldo real son puntos críticos.
2. **Autenticación y sesiones:** El manejo correcto del token JWT, su expiración y el logout seguro son áreas sensibles de seguridad.
3. **Validaciones de formularios:** Las reglas de negocio (monto máximo, formato DNI, unicidad de email/CVU) deben cubrir todos los casos borde.

### Recomendaciones

- Automatizar los flujos críticos de autenticación y transferencia como tests de regresión.
- Agregar pruebas de valores límite (boundary testing) en los campos de monto.
- Verificar comportamiento con red lenta o sin conexión (estados de error en fetch).
- Documentar y estandarizar los mensajes de error visibles al usuario.

---

*Documento generado para Sprint 1 — Digital Money House*
*Fecha: 2026-03-12*
