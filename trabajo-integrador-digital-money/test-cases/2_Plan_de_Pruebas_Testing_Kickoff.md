# Plan de Pruebas — Testing Kickoff
## Digital Money House — Billetera Digital

---

## Índice

1. [Introducción y Objetivos](#1-introducción-y-objetivos)
2. [Alcance](#2-alcance)
3. [¿Cómo escribir un caso de prueba?](#3-cómo-escribir-un-caso-de-prueba)
4. [¿Cómo reportar un defecto?](#4-cómo-reportar-un-defecto)
5. [Criterio para suite de Smoke Test](#5-criterio-para-suite-de-smoke-test)
6. [Criterio para suite de Regression Test](#6-criterio-para-suite-de-regression-test)
7. [Estrategia de Pruebas](#7-estrategia-de-pruebas)
8. [Ambiente y Herramientas](#8-ambiente-y-herramientas)
9. [Roles y Responsabilidades](#9-roles-y-responsabilidades)
10. [Criterios de Entrada y Salida](#10-criterios-de-entrada-y-salida)

---

## 1. Introducción y Objetivos

### 1.1 Propósito del documento

Este documento define el plan de pruebas para el proyecto **Digital Money House**, una aplicación de billetera digital que permite a los usuarios registrarse, autenticarse, consultar saldos, realizar transferencias y cargar dinero mediante tarjetas.

El plan establece los lineamientos, estándares y criterios que guiarán todas las actividades de testing a lo largo de los sprints del proyecto.

### 1.2 Objetivos de las pruebas

- Verificar que las funcionalidades implementadas cumplen con los requisitos especificados.
- Detectar defectos antes de que lleguen a producción.
- Validar la experiencia del usuario (flujos, mensajes de error, usabilidad).
- Garantizar que los cambios nuevos no rompan funcionalidades ya existentes.
- Proveer evidencia objetiva de la calidad del software.

### 1.3 Contexto del proyecto

| Campo              | Detalle                                          |
|--------------------|--------------------------------------------------|
| **Proyecto**       | Digital Money House (Billetera Digital)          |
| **Tipo**           | Aplicación web — SPA + API REST                 |
| **Frontend**       | React 18 + TypeScript (Puerto 3000)              |
| **Backend**        | Spring Boot 3 + Java 21 (Puerto 8081)            |
| **Base de datos**  | H2 (dev) / MySQL (producción)                    |
| **Autenticación**  | JWT — 24 horas de expiración                     |

---

## 2. Alcance

### 2.1 En alcance

| Módulo                  | Funcionalidades a probar                                        |
|-------------------------|-----------------------------------------------------------------|
| Registro                | Validaciones, unicidad de email/DNI, generación de CVU y alias |
| Autenticación           | Login, logout, manejo de sesión, token JWT                      |
| Dashboard               | Visualización de saldo y últimas transacciones                  |
| Historial de actividad  | Listado, paginación, detalle de transacción                     |
| Cargar dinero           | Depósito con tarjeta, validación de monto máximo               |
| Transferir dinero       | Envío de fondos, validación de saldo y destinatario            |
| Gestión de tarjetas     | Alta, baja y listado de tarjetas                               |
| Perfil                  | Visualización de CVU, alias y datos del usuario                |

### 2.2 Fuera de alcance

- Testing de rendimiento / carga / estrés
- Pruebas de seguridad avanzada (penetration testing)
- Integración con pasarelas de pago reales
- Microservicios de sprints futuros

---

## 3. ¿Cómo escribir un caso de prueba?

### 3.1 Definición

Un **caso de prueba** es un conjunto de condiciones o variables bajo las cuales el tester determinará si el sistema bajo prueba funciona correctamente. Define QUÉ probar, CÓMO probarlo y cuál es el resultado esperado.

### 3.2 Estructura estándar de un caso de prueba

Cada caso de prueba debe contener los siguientes campos:

| Campo                  | Descripción                                                                 | Ejemplo                                          |
|------------------------|-----------------------------------------------------------------------------|--------------------------------------------------|
| **ID**                 | Identificador único. Formato: `CP-[Módulo]-[Número]`                        | `CP-REG-001`                                     |
| **Módulo**             | Funcionalidad o sección de la app                                           | Registro                                         |
| **Nombre**             | Nombre descriptivo y conciso del caso                                       | Registro exitoso con datos válidos               |
| **Tipo**               | Tipo de prueba: Funcional / Negativo / Borde / UI                           | Funcional                                        |
| **Prioridad**          | Alta / Media / Baja                                                         | Alta                                             |
| **Precondiciones**     | Estado que debe existir ANTES de ejecutar el caso                           | El usuario no existe en el sistema               |
| **Pasos**              | Acciones detalladas a ejecutar, numeradas                                   | 1. Ir a /register 2. Completar campos ...        |
| **Datos de prueba**    | Datos específicos a usar durante la ejecución                               | email: test@test.com, dni: 12345678              |
| **Resultado esperado** | Lo que DEBERÍA suceder al ejecutar los pasos                                | El usuario es redirigido al login                |
| **Resultado obtenido** | Lo que REALMENTE sucedió (completar durante ejecución)                      | *(completar al ejecutar)*                        |
| **Estado**             | PASS / FAIL / BLOQUEADO / NO EJECUTADO                                      | PASS                                             |
| **Suite**              | Smoke / Regression / Ambas                                                  | Smoke                                            |
| **Notas**              | Observaciones adicionales, evidencia, links                                 | Captura: img/cp-reg-001.png                      |

### 3.3 Buenas prácticas para escribir casos de prueba

**DO — Hacer:**
- Escribir casos **atómicos**: un caso verifica una sola cosa.
- Usar verbos de acción en los pasos: "Ingresar", "Hacer click", "Verificar", "Navegar".
- Hacer los pasos **reproducibles** por cualquier tester, sin ambigüedad.
- Incluir **datos de prueba concretos**, no genéricos ("usuario_test@mail.com" en lugar de "un email").
- Escribir el resultado esperado en términos **observables** (lo que se ve en pantalla o en la respuesta).
- Cubrir escenarios positivos (happy path) Y negativos (unhappy path).

**DON'T — No hacer:**
- Escribir casos que dependan del orden de ejecución de otros casos.
- Usar datos de producción reales (DNI, emails o tarjetas reales).
- Escribir pasos ambiguos como "verificar que funciona".
- Combinar múltiples verificaciones en un solo paso.

### 3.4 Ejemplo completo de caso de prueba

```
ID:                   CP-REG-001
Módulo:               Registro
Nombre:               Registro exitoso con datos válidos
Tipo:                 Funcional - Happy Path
Prioridad:            Alta
Suite:                Smoke

Precondiciones:
  - La aplicación está corriendo en localhost:3000
  - El email test_new@mail.com NO existe en el sistema
  - El DNI 12345678 NO existe en el sistema

Pasos:
  1. Navegar a http://localhost:3000/register
  2. Ingresar en el campo "Nombre": Juan
  3. Ingresar en el campo "Apellido": Pérez
  4. Ingresar en el campo "DNI": 12345678
  5. Ingresar en el campo "Email": test_new@mail.com
  6. Ingresar en el campo "Teléfono": +5491123456789
  7. Ingresar en el campo "Contraseña": Test1234!
  8. Hacer click en el botón "Registrarse"

Datos de prueba:
  nombre: Juan | apellido: Pérez | dni: 12345678
  email: test_new@mail.com | telefono: +5491123456789
  password: Test1234!

Resultado esperado:
  - El sistema procesa el registro exitosamente
  - Se muestra un mensaje de confirmación
  - El usuario es redirigido a la página de login (/login)
  - Se puede iniciar sesión con las credenciales registradas

Resultado obtenido: *(completar al ejecutar)*
Estado: NO EJECUTADO
```

---

## 4. ¿Cómo reportar un defecto?

### 4.1 Definición

Un **defecto (bug)** es una discrepancia entre el comportamiento actual del sistema y el comportamiento esperado según los requisitos o el criterio de aceptación.

### 4.2 Cuándo reportar un defecto

Reportar un defecto cuando:
- El resultado obtenido no coincide con el resultado esperado de un caso de prueba.
- Se encuentra un comportamiento inesperado durante testing exploratorio.
- El sistema arroja errores no controlados (500, crash, pantalla en blanco).
- Los mensajes de error no son descriptivos o son incorrectos.
- Hay inconsistencias visuales graves o de usabilidad.

### 4.3 Estructura estándar de un reporte de defecto

| Campo                     | Descripción                                                                   |
|---------------------------|-------------------------------------------------------------------------------|
| **ID**                    | Identificador único. Formato: `DEF-[Número]`                                  |
| **Título**                | Descripción breve y específica del problema                                   |
| **Módulo afectado**       | Sección de la app donde ocurre                                                |
| **Severidad**             | Crítica / Alta / Media / Baja (ver tabla abajo)                               |
| **Prioridad**             | Alta / Media / Baja (urgencia para el equipo)                                 |
| **Ambiente**              | URL, versión, browser/OS donde se reproduce                                   |
| **Pasos para reproducir** | Pasos detallados y ordenados para que el dev pueda reproducirlo               |
| **Resultado actual**      | Lo que ocurre actualmente                                                     |
| **Resultado esperado**    | Lo que debería ocurrir                                                        |
| **Evidencia**             | Capturas de pantalla, videos, logs de consola, respuestas de red              |
| **Caso de prueba**        | ID del caso de prueba que descubrió el bug (si aplica)                        |
| **Estado**                | Nuevo / En análisis / En progreso / Resuelto / Cerrado / Reabierto            |
| **Asignado a**            | Desarrollador o equipo responsable                                            |
| **Versión detectada**     | Sprint o versión donde se encontró                                            |

### 4.4 Escala de severidad

| Severidad    | Descripción                                                                | Ejemplo                                      |
|--------------|----------------------------------------------------------------------------|----------------------------------------------|
| **Crítica**  | El sistema no puede operar. Bloquea el flujo principal.                    | No se puede iniciar sesión. App no carga.    |
| **Alta**     | Funcionalidad principal afectada, sin workaround posible.                  | La transferencia de dinero no se ejecuta.    |
| **Media**    | Funcionalidad afectada pero existe workaround o impacto limitado.          | El historial no filtra correctamente.        |
| **Baja**     | Problemas visuales, de texto o cosméticos. No afecta la funcionalidad.     | Typo en mensaje de error. Padding incorrecto.|

### 4.5 Ejemplo completo de reporte de defecto

```
ID:               DEF-001
Título:           El monto 0 es aceptado al cargar dinero
Módulo:           Cargar dinero
Severidad:        Alta
Prioridad:        Alta
Estado:           Nuevo
Versión:          Sprint 1
Caso de prueba:   CP-LOAD-003

Ambiente:
  - Frontend: http://localhost:3000
  - Browser: Chrome 122.0.0
  - OS: Windows 10

Pasos para reproducir:
  1. Iniciar sesión con credenciales válidas
  2. Desde el dashboard, hacer click en "Cargar dinero"
  3. Seleccionar una tarjeta guardada
  4. Ingresar el monto: 0
  5. Hacer click en "Confirmar"

Resultado actual:
  La operación se procesa sin errores y se muestra un mensaje de éxito.
  El saldo no cambia, pero se registra una actividad con monto $0.

Resultado esperado:
  El sistema debería mostrar un mensaje de error indicando que el monto
  debe ser mayor a 0 y no permitir continuar.

Evidencia:
  - Captura: evidencia/DEF-001-monto-cero.png
  - Console log: No hay errores en consola
  - Network: POST /activities retorna 200 con body {"amount": 0}
```

### 4.6 Buenas prácticas al reportar defectos

- **Un defecto = un reporte.** No mezclar múltiples bugs en un ticket.
- Los pasos deben ser **reproducibles** por otra persona sin conocimiento previo.
- Incluir **siempre evidencia** (screenshot o video). Un bug sin evidencia es difícil de priorizar.
- Separar **severidad** (impacto técnico) de **prioridad** (urgencia de negocio).
- Escribir el título en presente indicativo: "El botón no responde" en lugar de "Botón que no responde".
- Verificar que el defecto **no haya sido reportado antes** antes de crear uno nuevo.

---

## 5. Criterio para incluir un caso de prueba en una Suite de Smoke Test

### 5.1 Definición de Smoke Test

El **Smoke Test** (también llamado Build Verification Test) es un conjunto reducido de casos de prueba que verifica que las funcionalidades **críticas y más básicas** del sistema funcionan correctamente. Su propósito es determinar, de manera rápida, si el build es estable y apto para pruebas más exhaustivas.

> Si el Smoke Test falla → el build se **rechaza** y no se continúa con más pruebas.

### 5.2 Criterios de inclusión en Smoke Test

Un caso de prueba se incluye en la suite de smoke si cumple **al menos uno** de estos criterios:

| Criterio | Descripción |
|----------|-------------|
| **C1 — Flujo crítico** | Es parte del camino principal (happy path) de una funcionalidad core del negocio. |
| **C2 — Funcionalidad bloqueante** | Si falla, bloquea la ejecución de otros casos de prueba. |
| **C3 — Alta frecuencia de uso** | Es una acción que el usuario realiza muy frecuentemente (login, ver saldo). |
| **C4 — Impacto en datos** | Implica operaciones que afectan datos críticos (saldo, transferencia). |
| **C5 — Visibilidad alta** | Es la primera funcionalidad que un usuario o stakeholder vería al usar el sistema. |

### 5.3 Características de los casos de smoke test

- **Rápidos:** La suite completa no debería tardar más de 15-20 minutos.
- **Estables:** Deben ser confiables y no dar falsos positivos/negativos.
- **Independientes:** No deben depender unos de otros.
- **Pocos:** Máximo 20-30% del total de casos de prueba.

### 5.4 Ejemplos de casos incluidos en Smoke para Digital Money House

| ID          | Nombre del caso                                  | Criterio |
|-------------|--------------------------------------------------|----------|
| CP-REG-001  | Registro exitoso con datos válidos               | C1, C5   |
| CP-AUTH-001 | Login exitoso con credenciales válidas           | C1, C2   |
| CP-AUTH-003 | Logout cierra sesión correctamente               | C1, C3   |
| CP-DASH-001 | Dashboard muestra saldo e información del usuario| C3, C5   |
| CP-LOAD-001 | Cargar dinero con monto válido y tarjeta         | C1, C4   |
| CP-SEND-001 | Transferir dinero a usuario existente            | C1, C4   |
| CP-CARD-001 | Agregar tarjeta con datos válidos                | C1       |

---

## 6. Criterio para incluir un caso de prueba en una Suite de Regression Test

### 6.1 Definición de Regression Test

El **Regression Test** es un conjunto más amplio de casos de prueba que asegura que los cambios o nuevas funcionalidades incorporadas en un sprint no han roto funcionalidades que ya estaban operativas. Se ejecuta ante cada nueva versión o deploy.

### 6.2 Criterios de inclusión en Regression Test

Un caso de prueba se incluye en la suite de regresión si cumple **al menos uno** de estos criterios:

| Criterio | Descripción |
|----------|-------------|
| **R1 — Funcionalidad completa** | Verifica una funcionalidad o módulo completo, incluyendo casos alternativos y de error. |
| **R2 — Área de cambio reciente** | Corresponde a código que fue modificado en el sprint actual o sprints anteriores recientes. |
| **R3 — Integración entre módulos** | Verifica que la interacción entre dos o más módulos funciona correctamente. |
| **R4 — Casos borde (edge cases)** | Verifica comportamientos en los límites del sistema (mínimos, máximos, valores nulos). |
| **R5 — Defecto corregido** | Corresponde a un defecto que fue encontrado y corregido anteriormente (test de no regresión). |
| **R6 — Reglas de negocio clave** | Verifica una regla de negocio específica que tiene impacto en la operación (unicidad de CVU, monto máximo). |

### 6.3 Características de los casos de regression test

- **Completos:** Cubren tanto el happy path como los flujos alternativos.
- **Mantenidos:** Se actualizan cuando cambian los requisitos.
- **Priorizados:** Los de mayor riesgo se ejecutan primero.
- **Documentados con evidencia:** Incluyen capturas o datos que faciliten diagnóstico.

### 6.4 Ejemplos de casos incluidos en Regression para Digital Money House

| ID          | Nombre del caso                                               | Criterio |
|-------------|---------------------------------------------------------------|----------|
| CP-REG-002  | Registro falla con email duplicado                            | R1, R6   |
| CP-REG-003  | Registro falla con DNI duplicado                             | R1, R6   |
| CP-REG-004  | Registro falla con contraseña menor a 8 caracteres           | R1, R4   |
| CP-AUTH-002 | Login falla con contraseña incorrecta                        | R1       |
| CP-AUTH-004 | Acceso a ruta protegida sin token redirige a login           | R1, R3   |
| CP-LOAD-002 | Cargar dinero falla con monto mayor a 30.000                 | R4, R6   |
| CP-LOAD-003 | Cargar dinero falla con monto igual a 0                      | R4, R6   |
| CP-SEND-002 | Transferir falla con saldo insuficiente                      | R1, R6   |
| CP-SEND-003 | Transferir falla con CVU inexistente                         | R1       |
| CP-CARD-002 | Agregar tarjeta con número inválido muestra error            | R1, R4   |
| CP-ACT-001  | Historial muestra todas las transacciones con paginación     | R1, R3   |

---

## 7. Estrategia de Pruebas

### 7.1 Niveles de prueba

| Nivel              | Descripción                                          | Herramienta      |
|--------------------|------------------------------------------------------|------------------|
| Pruebas unitarias  | Funciones y servicios individuales (backend)         | JUnit 5          |
| Pruebas de integración | Integración entre capas (Controller, Service, Repo) | Spring Boot Test |
| Pruebas de componente | Componentes React aislados                         | Jest + RTL       |
| Pruebas E2E        | Flujos completos de usuario                         | Testing manual / Postman |
| Pruebas manuales   | Casos exploratorios y de UI/UX                      | Manual           |

### 7.2 Tipos de prueba

| Tipo               | Cuándo aplicar                                              |
|--------------------|-------------------------------------------------------------|
| Funcional          | Para verificar que cada función cumple sus requisitos       |
| Negativo           | Para verificar el comportamiento con datos inválidos        |
| Boundary (límite)  | Para verificar valores en los extremos permitidos           |
| Regresión          | Antes de cada release / deploy                              |
| Exploratorio       | Al inicio de cada sprint para familiarización               |
| Smoke              | Inmediatamente después de cada nuevo build/deploy           |

---

## 8. Ambiente y Herramientas

| Componente     | Valor                                              |
|----------------|----------------------------------------------------|
| Frontend URL   | http://localhost:3000                              |
| Mock API       | http://localhost:3500                              |
| Backend API    | http://localhost:8081                              |
| H2 Console     | http://localhost:8081/h2-console                   |
| Swagger UI     | http://localhost:8081/swagger-ui.html              |
| Browser        | Google Chrome (versión actualizada)                |
| Herramientas   | Postman (pruebas de API), DevTools (red/consola)   |
| Gestión bugs   | GitLab Issues                                      |
| Casos de prueba| Planilla de casos de prueba (Excel/Google Sheets)  |

---

## 9. Roles y Responsabilidades

| Rol            | Responsabilidad                                                        |
|----------------|------------------------------------------------------------------------|
| QA Tester      | Ejecutar casos de prueba, reportar defectos, actualizar estados        |
| Desarrollador  | Corregir defectos, revisar y cerrar reportes                           |
| Tech Lead      | Revisar plan de pruebas, priorizar defectos críticos                   |
| Product Owner  | Validar criterios de aceptación, aprobar releases                      |

---

## 10. Criterios de Entrada y Salida

### 10.1 Criterios de entrada (para comenzar a probar)

- El build está disponible en el ambiente de testing.
- El smoke test pasa (no hay errores bloqueantes).
- Los requisitos o criterios de aceptación del sprint están documentados.
- Los datos de prueba están disponibles o el mock está configurado.

### 10.2 Criterios de salida (para considerar el sprint probado)

- El 100% de los casos de smoke test pasan.
- El 90% o más de los casos de regression test pasan.
- No hay defectos de severidad Crítica o Alta sin resolver.
- Los defectos de severidad Media son conocidos y aceptados por el Product Owner.
- Toda la evidencia de ejecución está subida y documentada.

---

*Documento generado para Digital Money House — Especialización Backend*
*Fecha: 2026-03-12*
