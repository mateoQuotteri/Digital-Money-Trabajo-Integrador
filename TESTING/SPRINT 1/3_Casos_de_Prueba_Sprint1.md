# Planilla de Casos de Prueba — Sprint 1
## Digital Money House — Billetera Digital

---

## Índice de suites

| Suite      | Descripción                                            | Total casos |
|------------|--------------------------------------------------------|-------------|
| **Smoke**  | Funcionalidades críticas — verificación rápida de build | 14          |
| **Regression** | Suite completa — todos los flujos y casos borde    | 40          |

> Los casos de Smoke también están incluidos en Regression (son un subconjunto).

---

## Leyenda de campos

| Campo              | Valores posibles                                     |
|--------------------|------------------------------------------------------|
| **Tipo**           | Funcional / Negativo / Borde / UI / Integración / Seguridad |
| **Prioridad**      | Alta / Media / Baja                                  |
| **Suite**          | Smoke / Regression / Ambas                           |
| **Estado**         | NO EJECUTADO / PASS / FAIL / BLOQUEADO               |

---

## Módulo 1: Registro

### CP-REG-001 — Registro exitoso con todos los datos válidos
| Campo | Detalle |
|-------|---------|
| **Tipo** | Funcional - Happy Path |
| **Prioridad** | Alta |
| **Suite** | **Smoke** |
| **Precondiciones** | La app está corriendo en localhost:3000. El email `test_new@mail.com` y DNI `12345678` NO existen en el sistema. |
| **Pasos** | 1. Navegar a `/register` <br>2. Ingresar Nombre: `Juan` <br>3. Ingresar Apellido: `Pérez` <br>4. Ingresar DNI: `12345678` <br>5. Ingresar Email: `test_new@mail.com` <br>6. Ingresar Teléfono: `+5491123456789` <br>7. Ingresar Contraseña: `Test1234!` <br>8. Click en "Registrarse" |
| **Datos de prueba** | nombre: Juan \| apellido: Pérez \| dni: 12345678 \| email: test_new@mail.com \| telefono: +5491123456789 \| password: Test1234! |
| **Resultado esperado** | El registro es exitoso. Se muestra confirmación. Se redirige a `/login`. El usuario puede loguearse con las credenciales registradas. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-REG-002 — Registro falla con email ya registrado
| Campo | Detalle |
|-------|---------|
| **Tipo** | Funcional - Negativo |
| **Prioridad** | Alta |
| **Suite** | Regression |
| **Precondiciones** | La app está corriendo. El email `existente@mail.com` YA existe en el sistema. |
| **Pasos** | 1. Navegar a `/register` <br>2. Completar todos los campos con datos válidos <br>3. Ingresar Email: `existente@mail.com` <br>4. Click en "Registrarse" |
| **Datos de prueba** | email: existente@mail.com (ya registrado) |
| **Resultado esperado** | Se muestra un mensaje de error indicando que el email ya está en uso. El usuario NO es registrado. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-REG-003 — Registro falla con DNI ya registrado
| Campo | Detalle |
|-------|---------|
| **Tipo** | Funcional - Negativo |
| **Prioridad** | Alta |
| **Suite** | Regression |
| **Precondiciones** | La app está corriendo. El DNI `99887766` YA existe en el sistema. |
| **Pasos** | 1. Navegar a `/register` <br>2. Completar todos los campos <br>3. Ingresar DNI: `99887766` <br>4. Click en "Registrarse" |
| **Datos de prueba** | dni: 99887766 (ya registrado) |
| **Resultado esperado** | Se muestra un mensaje de error indicando que el DNI ya está en uso. El usuario NO es registrado. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-REG-004 — Registro falla con contraseña menor a 8 caracteres
| Campo | Detalle |
|-------|---------|
| **Tipo** | Borde - Negativo |
| **Prioridad** | Alta |
| **Suite** | Regression |
| **Precondiciones** | La app está corriendo en localhost:3000. |
| **Pasos** | 1. Navegar a `/register` <br>2. Completar nombre, apellido, DNI y email correctamente <br>3. Ingresar Contraseña: `abc123` <br>4. Click en "Registrarse" o salir del campo |
| **Datos de prueba** | password: abc123 (6 caracteres) |
| **Resultado esperado** | Se muestra un mensaje de validación indicando que la contraseña debe tener al menos 8 caracteres. El botón de registro está deshabilitado. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-REG-005 — Registro falla con DNI que contiene letras
| Campo | Detalle |
|-------|---------|
| **Tipo** | Borde - Negativo |
| **Prioridad** | Media |
| **Suite** | Regression |
| **Precondiciones** | La app está corriendo en localhost:3000. |
| **Pasos** | 1. Navegar a `/register` <br>2. Ingresar DNI: `1234AB78` <br>3. Salir del campo (onBlur) |
| **Datos de prueba** | dni: 1234AB78 |
| **Resultado esperado** | Se muestra mensaje de validación. El campo rechaza el valor o muestra error de formato. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-REG-006 — Registro falla con email sin formato válido
| Campo | Detalle |
|-------|---------|
| **Tipo** | Funcional - Negativo |
| **Prioridad** | Media |
| **Suite** | Regression |
| **Precondiciones** | La app está corriendo en localhost:3000. |
| **Pasos** | 1. Navegar a `/register` <br>2. Ingresar Email: `usuario.sin-arroba` <br>3. Salir del campo |
| **Datos de prueba** | email: usuario.sin-arroba |
| **Resultado esperado** | Se muestra mensaje de error de formato de email. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-REG-007 — Registro falla dejando campo obligatorio vacío
| Campo | Detalle |
|-------|---------|
| **Tipo** | Funcional - Negativo |
| **Prioridad** | Alta |
| **Suite** | Regression |
| **Precondiciones** | La app está corriendo en localhost:3000. |
| **Pasos** | 1. Navegar a `/register` <br>2. Completar todos los campos excepto Nombre <br>3. Click en "Registrarse" |
| **Datos de prueba** | Campo Nombre: vacío |
| **Resultado esperado** | Se muestra validación en el campo vacío. El registro no se procesa. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-REG-008 — CVU y alias se generan automáticamente al registrarse
| Campo | Detalle |
|-------|---------|
| **Tipo** | Funcional |
| **Prioridad** | Alta |
| **Suite** | Regression |
| **Precondiciones** | Registro exitoso completado (CP-REG-001). |
| **Pasos** | 1. Completar registro exitoso <br>2. Iniciar sesión con las credenciales <br>3. Navegar a `/profile` |
| **Datos de prueba** | (usa datos de CP-REG-001) |
| **Resultado esperado** | El perfil muestra un CVU de 22 dígitos y un alias con formato `palabra.palabra.palabra` generados automáticamente. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-REG-009 — Registro falla con nombre que contiene números
| Campo | Detalle |
|-------|---------|
| **Tipo** | Borde - Negativo |
| **Prioridad** | Baja |
| **Suite** | Regression |
| **Precondiciones** | La app está corriendo en localhost:3000. |
| **Pasos** | 1. Navegar a `/register` <br>2. Ingresar Nombre: `Juan123` <br>3. Salir del campo |
| **Datos de prueba** | nombre: Juan123 |
| **Resultado esperado** | Se muestra mensaje de validación indicando que el nombre solo debe contener letras. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-REG-010 — Registro falla con DNI de menos de 7 dígitos
| Campo | Detalle |
|-------|---------|
| **Tipo** | Borde - Negativo |
| **Prioridad** | Media |
| **Suite** | Regression |
| **Precondiciones** | La app está corriendo en localhost:3000. |
| **Pasos** | 1. Navegar a `/register` <br>2. Ingresar DNI: `12345` (5 dígitos) <br>3. Salir del campo |
| **Datos de prueba** | dni: 12345 |
| **Resultado esperado** | Se muestra mensaje de validación indicando que el DNI debe tener entre 7 y 8 dígitos. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

## Módulo 2: Autenticación (Login / Logout)

### CP-AUTH-001 — Login exitoso con credenciales válidas
| Campo | Detalle |
|-------|---------|
| **Tipo** | Funcional - Happy Path |
| **Prioridad** | Alta |
| **Suite** | **Smoke** |
| **Precondiciones** | La app está corriendo. El usuario `test@mail.com` con contraseña `Test1234!` existe en el sistema. |
| **Pasos** | 1. Navegar a `/login` <br>2. Ingresar Email: `test@mail.com` <br>3. Ingresar Contraseña: `Test1234!` <br>4. Click en "Ingresar" |
| **Datos de prueba** | email: test@mail.com \| password: Test1234! |
| **Resultado esperado** | El login es exitoso. Se almacena el JWT en `localStorage`. El usuario es redirigido al Dashboard (`/`). |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-AUTH-002 — Login falla con contraseña incorrecta
| Campo | Detalle |
|-------|---------|
| **Tipo** | Funcional - Negativo |
| **Prioridad** | Alta |
| **Suite** | Regression |
| **Precondiciones** | El usuario `test@mail.com` existe en el sistema. |
| **Pasos** | 1. Navegar a `/login` <br>2. Ingresar Email: `test@mail.com` <br>3. Ingresar Contraseña: `ContraseñaMal123` <br>4. Click en "Ingresar" |
| **Datos de prueba** | email: test@mail.com \| password: ContraseñaMal123 |
| **Resultado esperado** | Se muestra un mensaje de error (SnackBar) indicando credenciales inválidas. El usuario NO es redirigido. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-AUTH-003 — Login falla con email inexistente
| Campo | Detalle |
|-------|---------|
| **Tipo** | Funcional - Negativo |
| **Prioridad** | Alta |
| **Suite** | Regression |
| **Precondiciones** | El email `no_existe@mail.com` NO existe en el sistema. |
| **Pasos** | 1. Navegar a `/login` <br>2. Ingresar Email: `no_existe@mail.com` <br>3. Ingresar Contraseña: `cualquiera123` <br>4. Click en "Ingresar" |
| **Datos de prueba** | email: no_existe@mail.com \| password: cualquiera123 |
| **Resultado esperado** | Se muestra mensaje de error. El usuario NO es redirigido al dashboard. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-AUTH-004 — Acceso a ruta protegida sin token redirige a login
| Campo | Detalle |
|-------|---------|
| **Tipo** | Seguridad |
| **Prioridad** | Alta |
| **Suite** | Regression |
| **Precondiciones** | El usuario NO está logueado (sin token en localStorage). |
| **Pasos** | 1. Navegar directamente a `http://localhost:3000/` <br>2. Observar comportamiento |
| **Datos de prueba** | Sin token |
| **Resultado esperado** | El sistema redirige automáticamente a `/login` sin mostrar contenido protegido. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-AUTH-005 — Logout cierra sesión e invalida token
| Campo | Detalle |
|-------|---------|
| **Tipo** | Funcional - Happy Path |
| **Prioridad** | Alta |
| **Suite** | **Smoke** |
| **Precondiciones** | El usuario está logueado en el sistema. |
| **Pasos** | 1. Desde el Dashboard hacer click en el botón de logout/salir <br>2. Verificar la redirección <br>3. Intentar navegar a `/` sin hacer login de nuevo |
| **Datos de prueba** | Usuario logueado |
| **Resultado esperado** | El usuario es redirigido a `/login`. El token es eliminado de `localStorage`. Las rutas protegidas no son accesibles. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-AUTH-006 — Campos de login vacíos muestran validación
| Campo | Detalle |
|-------|---------|
| **Tipo** | Funcional - Negativo |
| **Prioridad** | Media |
| **Suite** | Regression |
| **Precondiciones** | La app está corriendo en localhost:3000. |
| **Pasos** | 1. Navegar a `/login` <br>2. Dejar email y contraseña vacíos <br>3. Click en "Ingresar" |
| **Datos de prueba** | Sin datos |
| **Resultado esperado** | Se muestran mensajes de validación en los campos vacíos. El login no se procesa. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-AUTH-007 — Múltiples intentos de login fallidos
| Campo | Detalle |
|-------|---------|
| **Tipo** | Borde |
| **Prioridad** | Media |
| **Suite** | Regression |
| **Precondiciones** | El usuario `test@mail.com` existe en el sistema. |
| **Pasos** | 1. Navegar a `/login` <br>2. Intentar login 5 veces consecutivas con contraseña incorrecta |
| **Datos de prueba** | email: test@mail.com \| password: ContraseñaMal (repetir 5 veces) |
| **Resultado esperado** | Cada intento muestra mensaje de error. Verificar si hay limitación de intentos o bloqueo de cuenta. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

## Módulo 3: Dashboard

### CP-DASH-001 — Dashboard muestra saldo y datos del usuario correctamente
| Campo | Detalle |
|-------|---------|
| **Tipo** | Funcional - Happy Path |
| **Prioridad** | Alta |
| **Suite** | **Smoke** |
| **Precondiciones** | El usuario está logueado y tiene saldo > 0 en su cuenta. |
| **Pasos** | 1. Iniciar sesión exitosamente <br>2. Verificar que el dashboard muestra el saldo disponible <br>3. Verificar que muestra el nombre del usuario |
| **Datos de prueba** | Usuario con saldo: $5000 |
| **Resultado esperado** | El dashboard muestra correctamente el saldo disponible y el nombre del usuario. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-DASH-002 — Dashboard muestra las últimas 5 transacciones
| Campo | Detalle |
|-------|---------|
| **Tipo** | Funcional |
| **Prioridad** | Alta |
| **Suite** | Regression |
| **Precondiciones** | El usuario está logueado y tiene al menos 5 transacciones registradas. |
| **Pasos** | 1. Iniciar sesión <br>2. Observar la sección de últimas transacciones en el dashboard |
| **Datos de prueba** | Usuario con 7 transacciones registradas |
| **Resultado esperado** | Se muestran exactamente las 5 transacciones más recientes. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-DASH-003 — Dashboard con cero transacciones muestra estado vacío
| Campo | Detalle |
|-------|---------|
| **Tipo** | Borde |
| **Prioridad** | Media |
| **Suite** | Regression |
| **Precondiciones** | El usuario está logueado y NO tiene transacciones registradas. |
| **Pasos** | 1. Iniciar sesión con usuario nuevo (sin transacciones) <br>2. Observar sección de actividades del dashboard |
| **Datos de prueba** | Usuario sin transacciones |
| **Resultado esperado** | Se muestra un mensaje indicando que no hay actividad reciente (lista vacía, sin error). |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-DASH-004 — Accesos rápidos del dashboard navegan correctamente
| Campo | Detalle |
|-------|---------|
| **Tipo** | Funcional - UI |
| **Prioridad** | Media |
| **Suite** | **Smoke** |
| **Precondiciones** | El usuario está logueado. |
| **Pasos** | 1. Click en "Cargar dinero" <br>2. Volver y click en "Transferir" <br>3. Volver y click en "Mis tarjetas" <br>4. Volver y click en "Ver CVU" |
| **Datos de prueba** | Usuario logueado |
| **Resultado esperado** | Cada botón navega a la sección correcta de la aplicación. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-DASH-005 — Recarga de página mantiene la sesión activa
| Campo | Detalle |
|-------|---------|
| **Tipo** | Funcional |
| **Prioridad** | Alta |
| **Suite** | Regression |
| **Precondiciones** | El usuario está logueado. |
| **Pasos** | 1. Estar en el Dashboard <br>2. Presionar F5 (recargar página) <br>3. Observar el resultado |
| **Datos de prueba** | Token válido en localStorage |
| **Resultado esperado** | El usuario permanece en el Dashboard. No se redirige al login. Los datos se recargan correctamente. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

## Módulo 4: Historial de Actividad

### CP-ACT-001 — Historial muestra todas las transacciones con paginación
| Campo | Detalle |
|-------|---------|
| **Tipo** | Funcional - Happy Path |
| **Prioridad** | Alta |
| **Suite** | **Smoke** |
| **Precondiciones** | El usuario está logueado y tiene más de 10 transacciones registradas. |
| **Pasos** | 1. Navegar a `/activities` <br>2. Verificar listado de transacciones <br>3. Navegar a páginas siguientes con la paginación |
| **Datos de prueba** | Usuario con múltiples transacciones |
| **Resultado esperado** | Se muestra el historial completo con paginación funcional. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-ACT-002 — Detalle de transacción muestra información correcta
| Campo | Detalle |
|-------|---------|
| **Tipo** | Funcional - Happy Path |
| **Prioridad** | Media |
| **Suite** | Regression |
| **Precondiciones** | El usuario está logueado y tiene al menos una transacción. |
| **Pasos** | 1. Navegar a `/activities` <br>2. Click en una transacción de la lista <br>3. Verificar la información mostrada |
| **Datos de prueba** | Transacción de tipo depósito de $1000 |
| **Resultado esperado** | El detalle muestra: tipo, monto, fecha, origen y destino correctamente. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-ACT-003 — Las transacciones entrantes se muestran en verde con signo +
| Campo | Detalle |
|-------|---------|
| **Tipo** | UI |
| **Prioridad** | Baja |
| **Suite** | Regression |
| **Precondiciones** | El usuario está logueado y tiene una transferencia recibida. |
| **Pasos** | 1. Navegar a `/activities` <br>2. Identificar una transacción entrante (TRANSFER_IN o DEPOSIT) |
| **Datos de prueba** | Transacción entrante de $500 |
| **Resultado esperado** | El monto se muestra en color verde con signo `+` (ej: `+$500`). |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-ACT-004 — Las transacciones salientes se muestran en rojo con signo -
| Campo | Detalle |
|-------|---------|
| **Tipo** | UI |
| **Prioridad** | Baja |
| **Suite** | Regression |
| **Precondiciones** | El usuario está logueado y tiene una transferencia enviada. |
| **Pasos** | 1. Navegar a `/activities` <br>2. Identificar una transacción saliente (TRANSFER_OUT) |
| **Datos de prueba** | Transacción saliente de $500 |
| **Resultado esperado** | El monto se muestra en color rojo con signo `-` (ej: `-$500`). |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-ACT-005 — El botón 'Ver más' del dashboard lleva al historial completo
| Campo | Detalle |
|-------|---------|
| **Tipo** | Funcional - UI |
| **Prioridad** | Media |
| **Suite** | **Smoke** |
| **Precondiciones** | El usuario está logueado y tiene transacciones. |
| **Pasos** | 1. Estar en el Dashboard <br>2. Click en el botón "Ver más" de la sección de actividades |
| **Datos de prueba** | Usuario logueado |
| **Resultado esperado** | Se navega a `/activities` mostrando el historial completo. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

## Módulo 5: Cargar Dinero

### CP-LOAD-001 — Cargar dinero exitoso con monto válido y tarjeta guardada
| Campo | Detalle |
|-------|---------|
| **Tipo** | Funcional - Happy Path |
| **Prioridad** | Alta |
| **Suite** | **Smoke** |
| **Precondiciones** | El usuario está logueado y tiene al menos una tarjeta guardada. Saldo inicial conocido. |
| **Pasos** | 1. Navegar a `/load-money` <br>2. Seleccionar una tarjeta guardada <br>3. Ingresar monto: `1000` <br>4. Confirmar la operación |
| **Datos de prueba** | monto: 1000 |
| **Resultado esperado** | La operación es exitosa. El saldo aumenta en $1000. La transacción aparece en el historial como DEPOSIT. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-LOAD-002 — Cargar dinero falla con monto mayor al máximo permitido (30.000)
| Campo | Detalle |
|-------|---------|
| **Tipo** | Borde - Negativo |
| **Prioridad** | Alta |
| **Suite** | Regression |
| **Precondiciones** | El usuario está logueado y tiene una tarjeta guardada. |
| **Pasos** | 1. Navegar a `/load-money` <br>2. Seleccionar tarjeta <br>3. Ingresar monto: `30001` <br>4. Intentar confirmar |
| **Datos de prueba** | monto: 30001 |
| **Resultado esperado** | Se muestra mensaje de error indicando que el monto máximo es $30.000. La operación NO se procesa. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-LOAD-003 — Cargar dinero falla con monto igual a cero
| Campo | Detalle |
|-------|---------|
| **Tipo** | Borde - Negativo |
| **Prioridad** | Alta |
| **Suite** | Regression |
| **Precondiciones** | El usuario está logueado y tiene una tarjeta guardada. |
| **Pasos** | 1. Navegar a `/load-money` <br>2. Seleccionar tarjeta <br>3. Ingresar monto: `0` <br>4. Intentar confirmar |
| **Datos de prueba** | monto: 0 |
| **Resultado esperado** | Se muestra mensaje de error indicando que el monto debe ser mayor a 0. La operación NO se procesa. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-LOAD-004 — Cargar dinero con monto igual al máximo permitido (30.000)
| Campo | Detalle |
|-------|---------|
| **Tipo** | Borde |
| **Prioridad** | Alta |
| **Suite** | Regression |
| **Precondiciones** | El usuario está logueado y tiene una tarjeta guardada. |
| **Pasos** | 1. Navegar a `/load-money` <br>2. Seleccionar tarjeta <br>3. Ingresar monto: `30000` <br>4. Confirmar |
| **Datos de prueba** | monto: 30000 |
| **Resultado esperado** | La operación es exitosa con el monto máximo. El saldo se actualiza correctamente. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-LOAD-005 — Cargar dinero falla con monto negativo
| Campo | Detalle |
|-------|---------|
| **Tipo** | Borde - Negativo |
| **Prioridad** | Media |
| **Suite** | Regression |
| **Precondiciones** | El usuario está logueado y tiene una tarjeta guardada. |
| **Pasos** | 1. Navegar a `/load-money` <br>2. Ingresar monto: `-500` <br>3. Intentar confirmar |
| **Datos de prueba** | monto: -500 |
| **Resultado esperado** | Se muestra mensaje de validación. La operación NO se procesa. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-LOAD-006 — Cargar dinero sin tarjetas guardadas bloquea el flujo
| Campo | Detalle |
|-------|---------|
| **Tipo** | Funcional - Negativo |
| **Prioridad** | Media |
| **Suite** | Regression |
| **Precondiciones** | El usuario está logueado y NO tiene tarjetas guardadas. |
| **Pasos** | 1. Navegar a `/load-money` <br>2. Observar el comportamiento |
| **Datos de prueba** | Sin tarjetas guardadas |
| **Resultado esperado** | Se muestra mensaje indicando que no hay tarjetas disponibles o se redirige a agregar tarjeta. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-LOAD-007 — El saldo se actualiza en el dashboard tras cargar dinero
| Campo | Detalle |
|-------|---------|
| **Tipo** | Integración |
| **Prioridad** | Alta |
| **Suite** | Regression |
| **Precondiciones** | El usuario está logueado con saldo inicial conocido (ej: $5000). |
| **Pasos** | 1. Anotar saldo inicial en el Dashboard <br>2. Cargar $1000 exitosamente <br>3. Navegar de regreso al Dashboard |
| **Datos de prueba** | monto a cargar: 1000 \| saldo inicial: 5000 |
| **Resultado esperado** | El Dashboard muestra el nuevo saldo: $6000. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

## Módulo 6: Transferir Dinero

### CP-SEND-001 — Transferencia exitosa a usuario existente con saldo suficiente
| Campo | Detalle |
|-------|---------|
| **Tipo** | Funcional - Happy Path |
| **Prioridad** | Alta |
| **Suite** | **Smoke** |
| **Precondiciones** | El usuario A está logueado con saldo >= $500. El usuario B tiene un CVU registrado. |
| **Pasos** | 1. Navegar a `/send-money` <br>2. Ingresar el CVU del usuario B <br>3. Ingresar monto: `500` <br>4. Confirmar la transferencia |
| **Datos de prueba** | CVU destinatario válido \| monto: 500 |
| **Resultado esperado** | La transferencia es exitosa. El saldo del usuario A disminuye en $500. La transacción aparece en el historial. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-SEND-002 — Transferencia falla con saldo insuficiente
| Campo | Detalle |
|-------|---------|
| **Tipo** | Funcional - Negativo |
| **Prioridad** | Alta |
| **Suite** | Regression |
| **Precondiciones** | El usuario está logueado con saldo de $100. |
| **Pasos** | 1. Navegar a `/send-money` <br>2. Ingresar CVU válido <br>3. Ingresar monto: `500` <br>4. Confirmar |
| **Datos de prueba** | monto: 500 \| saldo disponible: 100 |
| **Resultado esperado** | Se muestra mensaje de error indicando saldo insuficiente. La transferencia NO se realiza. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-SEND-003 — Transferencia falla con CVU inexistente
| Campo | Detalle |
|-------|---------|
| **Tipo** | Funcional - Negativo |
| **Prioridad** | Alta |
| **Suite** | Regression |
| **Precondiciones** | El usuario está logueado. |
| **Pasos** | 1. Navegar a `/send-money` <br>2. Ingresar CVU: `9999999999999999999999` (inexistente) <br>3. Ingresar monto: `100` <br>4. Confirmar |
| **Datos de prueba** | CVU: 9999999999999999999999 (no registrado) |
| **Resultado esperado** | Se muestra mensaje de error indicando que el destinatario no existe. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-SEND-004 — Transferencia falla con monto igual a cero
| Campo | Detalle |
|-------|---------|
| **Tipo** | Borde - Negativo |
| **Prioridad** | Alta |
| **Suite** | Regression |
| **Precondiciones** | El usuario está logueado con saldo suficiente. |
| **Pasos** | 1. Navegar a `/send-money` <br>2. Ingresar CVU válido <br>3. Ingresar monto: `0` <br>4. Intentar confirmar |
| **Datos de prueba** | monto: 0 |
| **Resultado esperado** | Se muestra mensaje de validación. La transferencia NO se procesa. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-SEND-005 — Transferencia falla al intentar enviarse a sí mismo
| Campo | Detalle |
|-------|---------|
| **Tipo** | Borde - Negativo |
| **Prioridad** | Media |
| **Suite** | Regression |
| **Precondiciones** | El usuario está logueado. Se conoce el propio CVU del usuario. |
| **Pasos** | 1. Navegar a `/send-money` <br>2. Ingresar el propio CVU del usuario logueado <br>3. Ingresar monto: `100` <br>4. Confirmar |
| **Datos de prueba** | CVU propio del usuario |
| **Resultado esperado** | Se muestra mensaje de error indicando que no se puede transferir al propio usuario. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-SEND-006 — El saldo se actualiza en el dashboard tras transferir
| Campo | Detalle |
|-------|---------|
| **Tipo** | Integración |
| **Prioridad** | Alta |
| **Suite** | Regression |
| **Precondiciones** | El usuario está logueado con saldo de $2000. El usuario B tiene CVU válido. |
| **Pasos** | 1. Anotar saldo inicial en Dashboard <br>2. Transferir $300 exitosamente <br>3. Navegar al Dashboard |
| **Datos de prueba** | monto: 300 \| saldo inicial: 2000 |
| **Resultado esperado** | El Dashboard muestra el nuevo saldo: $1700. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-SEND-007 — La actividad de transferencia aparece en el historial
| Campo | Detalle |
|-------|---------|
| **Tipo** | Integración |
| **Prioridad** | Alta |
| **Suite** | Regression |
| **Precondiciones** | Transferencia exitosa realizada (CP-SEND-001). |
| **Pasos** | 1. Después de una transferencia exitosa <br>2. Navegar a `/activities` <br>3. Verificar que aparece la transacción |
| **Datos de prueba** | Transferencia de $500 |
| **Resultado esperado** | La transacción aparece en el historial con tipo TRANSFER_OUT y el monto correcto. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

## Módulo 7: Tarjetas

### CP-CARD-001 — Agregar tarjeta con datos válidos
| Campo | Detalle |
|-------|---------|
| **Tipo** | Funcional - Happy Path |
| **Prioridad** | Alta |
| **Suite** | **Smoke** |
| **Precondiciones** | El usuario está logueado. |
| **Pasos** | 1. Navegar a `/cards` <br>2. Click en "Agregar tarjeta" <br>3. Ingresar número: `4111111111111111` <br>4. Ingresar nombre: `JUAN PEREZ` <br>5. Ingresar vencimiento: `12/28` <br>6. Confirmar |
| **Datos de prueba** | número: 4111111111111111 \| nombre: JUAN PEREZ \| vencimiento: 12/28 |
| **Resultado esperado** | La tarjeta se agrega exitosamente y aparece en la lista de tarjetas. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-CARD-002 — Agregar tarjeta con número de tarjeta inválido
| Campo | Detalle |
|-------|---------|
| **Tipo** | Funcional - Negativo |
| **Prioridad** | Alta |
| **Suite** | Regression |
| **Precondiciones** | El usuario está logueado. |
| **Pasos** | 1. Navegar a `/cards` <br>2. Click en "Agregar tarjeta" <br>3. Ingresar número: `1234` <br>4. Intentar confirmar |
| **Datos de prueba** | número: 1234 (inválido) |
| **Resultado esperado** | Se muestra mensaje de validación. La tarjeta NO se agrega. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-CARD-003 — Eliminar tarjeta existente
| Campo | Detalle |
|-------|---------|
| **Tipo** | Funcional - Happy Path |
| **Prioridad** | Alta |
| **Suite** | **Smoke** |
| **Precondiciones** | El usuario está logueado y tiene al menos una tarjeta guardada. |
| **Pasos** | 1. Navegar a `/cards` <br>2. Click en eliminar en una tarjeta <br>3. Confirmar la eliminación |
| **Datos de prueba** | Tarjeta existente en la lista |
| **Resultado esperado** | La tarjeta desaparece de la lista. La operación es exitosa. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-CARD-004 — Lista de tarjetas vacía muestra mensaje informativo
| Campo | Detalle |
|-------|---------|
| **Tipo** | Borde |
| **Prioridad** | Media |
| **Suite** | Regression |
| **Precondiciones** | El usuario está logueado y NO tiene tarjetas guardadas. |
| **Pasos** | 1. Navegar a `/cards` <br>2. Observar el estado de la lista |
| **Datos de prueba** | Sin tarjetas |
| **Resultado esperado** | Se muestra un mensaje informativo indicando que no hay tarjetas guardadas (no error ni lista en blanco). |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-CARD-005 — Múltiples tarjetas se muestran correctamente
| Campo | Detalle |
|-------|---------|
| **Tipo** | Funcional |
| **Prioridad** | Media |
| **Suite** | Regression |
| **Precondiciones** | El usuario está logueado. |
| **Pasos** | 1. Agregar 3 tarjetas distintas <br>2. Navegar a `/cards` |
| **Datos de prueba** | 3 tarjetas distintas con números diferentes |
| **Resultado esperado** | Las 3 tarjetas se muestran en la lista con sus datos correctos (número enmascarado). |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-CARD-006 — El número de tarjeta se muestra enmascarado
| Campo | Detalle |
|-------|---------|
| **Tipo** | UI - Seguridad |
| **Prioridad** | Alta |
| **Suite** | Regression |
| **Precondiciones** | El usuario está logueado y tiene al menos una tarjeta guardada. |
| **Pasos** | 1. Navegar a `/cards` <br>2. Observar cómo se muestra el número de la tarjeta `4111111111111111` |
| **Datos de prueba** | Tarjeta con número 4111111111111111 |
| **Resultado esperado** | El número de tarjeta aparece parcialmente enmascarado (ej: `**** **** **** 1111`). |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

## Módulo 8: Perfil

### CP-PROF-001 — El perfil muestra los datos correctos del usuario
| Campo | Detalle |
|-------|---------|
| **Tipo** | Funcional - Happy Path |
| **Prioridad** | Alta |
| **Suite** | **Smoke** |
| **Precondiciones** | El usuario está logueado. |
| **Pasos** | 1. Navegar a `/profile` <br>2. Verificar nombre, apellido, email, CVU y alias |
| **Datos de prueba** | Datos del usuario logueado |
| **Resultado esperado** | El perfil muestra los datos correctos del usuario registrado. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-PROF-002 — El CVU tiene exactamente 22 dígitos
| Campo | Detalle |
|-------|---------|
| **Tipo** | Funcional |
| **Prioridad** | Alta |
| **Suite** | Regression |
| **Precondiciones** | El usuario está logueado. |
| **Pasos** | 1. Navegar a `/profile` <br>2. Observar el CVU del usuario y contar sus dígitos |
| **Datos de prueba** | CVU generado automáticamente |
| **Resultado esperado** | El CVU mostrado tiene exactamente 22 dígitos numéricos. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-PROF-003 — El alias tiene el formato palabra.palabra.palabra
| Campo | Detalle |
|-------|---------|
| **Tipo** | Funcional |
| **Prioridad** | Media |
| **Suite** | Regression |
| **Precondiciones** | El usuario está logueado. |
| **Pasos** | 1. Navegar a `/profile` <br>2. Observar el alias del usuario y verificar su formato |
| **Datos de prueba** | Alias generado automáticamente |
| **Resultado esperado** | El alias tiene el formato de tres palabras separadas por puntos (ej: `casa.perro.sol`). |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

### CP-PROF-004 — Cada usuario tiene un CVU único en el sistema
| Campo | Detalle |
|-------|---------|
| **Tipo** | Funcional |
| **Prioridad** | Alta |
| **Suite** | Regression |
| **Precondiciones** | Se registran dos nuevos usuarios consecutivamente. |
| **Pasos** | 1. Registrar usuario A <br>2. Registrar usuario B <br>3. Iniciar sesión con cada uno y ver sus CVU |
| **Datos de prueba** | Dos usuarios distintos |
| **Resultado esperado** | Cada usuario tiene un CVU diferente. No hay duplicados. |
| **Resultado obtenido** | *(completar al ejecutar)* |
| **Estado** | NO EJECUTADO |

---

## Resumen de Suites

### Suite de Smoke Test (14 casos)

| ID | Módulo | Nombre |
|----|--------|--------|
| CP-REG-001 | Registro | Registro exitoso con datos válidos |
| CP-AUTH-001 | Autenticación | Login exitoso con credenciales válidas |
| CP-AUTH-005 | Autenticación | Logout cierra sesión e invalida token |
| CP-DASH-001 | Dashboard | Dashboard muestra saldo y datos del usuario |
| CP-DASH-004 | Dashboard | Accesos rápidos del dashboard navegan correctamente |
| CP-ACT-001 | Actividad | Historial muestra transacciones con paginación |
| CP-ACT-005 | Actividad | Botón 'Ver más' lleva al historial completo |
| CP-LOAD-001 | Cargar Dinero | Cargar dinero exitoso con monto válido |
| CP-SEND-001 | Transferir Dinero | Transferencia exitosa a usuario existente |
| CP-CARD-001 | Tarjetas | Agregar tarjeta con datos válidos |
| CP-CARD-003 | Tarjetas | Eliminar tarjeta existente |
| CP-PROF-001 | Perfil | El perfil muestra los datos correctos |

### Suite de Regression Test (40 casos — todos los de la planilla)

Incluye todos los casos de Smoke más:

| ID | Módulo | Nombre |
|----|--------|--------|
| CP-REG-002 al CP-REG-010 | Registro | Casos negativos y borde de registro |
| CP-AUTH-002 al CP-AUTH-007 | Autenticación | Casos negativos de login y sesión |
| CP-DASH-002 al CP-DASH-005 | Dashboard | Casos alternativos de dashboard |
| CP-ACT-002 al CP-ACT-004 | Actividad | Detalle y visualización de transacciones |
| CP-LOAD-002 al CP-LOAD-007 | Cargar Dinero | Casos borde y negativos de carga |
| CP-SEND-002 al CP-SEND-007 | Transferir Dinero | Casos negativos y de integración |
| CP-CARD-002 al CP-CARD-006 | Tarjetas | Casos negativos y visualización |
| CP-PROF-002 al CP-PROF-004 | Perfil | Validaciones de CVU y alias |

---

*Planilla de casos de prueba — Sprint 1 | Digital Money House*
*Fecha: 2026-03-12*
