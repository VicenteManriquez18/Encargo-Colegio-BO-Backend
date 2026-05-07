# Encargo-Colegio-BO-Backend

---

## Información General

| Propiedad | Valor |
|-----------|-------|
| **Base URL** | `http://localhost:9090` |
| **API Gateway** | Puerto 9090 |
| **Servicio Usuarios** | Puerto 8081 |
| **Servicio Asistencia** | Puerto 8082 |
| **Servicio Matrícula** | Puerto 8083 |

---

## Autenticación

### Registrar Nuevo Usuario

```http
POST /api/auth/register
```

**Body (JSON):**
```json
{
  "correo": "usuario@example.com",
  "password": "password123",
  "rol": "ESTUDIANTE"
}
```

---

### Login (Obtener JWT)

```http
POST /api/auth/login
```

**Body (JSON):**
```json
{
  "correo": "usuario@example.com",
  "password": "password123"
}
```

---

## Gestión de Usuarios

### Listar Todos los Usuarios

```http
GET /api/usuarios
```

---

### Obtener Usuario por ID

```http
GET /api/usuarios/{id}
```

Ejemplo: `GET /api/usuarios/1`

---

### Crear Nuevo Usuario

```http
POST /api/usuarios
```

**Body (JSON):**
```json
{
  "nombre": "Carlos López",
  "correo": "carlos@example.com",
  "password": "password123",
  "rol": "ESTUDIANTE"
}
```

---

### Actualizar Usuario

```http
PUT /api/usuarios/{id}
```

Ejemplo: `PUT /api/usuarios/1`

**Body (JSON):**
```json
{
  "nombre": "Juan Pérez Actualizado",
  "correo": "juan.nuevo@example.com",
  "rol": "PROFESOR"
}
```

---

### Eliminar Usuario

```http
DELETE /api/usuarios/{id}
```

Ejemplo: `DELETE /api/usuarios/1`

---

## Gestión de Asistencia

### Listar Todas las Asistencias

```http
GET /api/asistencia
```

---

### Obtener Asistencia por ID

```http
GET /api/asistencia/{id}
```

Ejemplo: `GET /api/asistencia/2`

---

### Crear Asistencia

```http
POST /api/asistencia
```

**Body (JSON):**
```json
{
  "usuarioId": 1,
  "nombreUsuario": "usuario@example.com",
  "fecha": "2026-05-07T15:24:00"
}
```

---

### Registrar Asistencia (Automático)

```http
POST /api/asistencia/registrar
```

**Body (JSON):**
```json
{
  "usuarioId": 1
}
```

Nota: Valida que el usuario exista en el servicio de usuarios. Registra automáticamente la fecha y hora actual.

---

### Eliminar Asistencia

```http
DELETE /api/asistencia/{id}
```

Ejemplo: `DELETE /api/asistencia/1`

---

## Gestión de Matrícula/Estudiantes

### Listar Todos los Estudiantes

```http
GET /api/matricula/estudiantes
```

---

### Listar Estudiantes por Curso

```http
GET /api/matricula/estudiantes/curso/{curso}
```

Ejemplo: `GET /api/matricula/estudiantes/curso/1A`

---

### Registrar Matrícula Completa

```http
POST /api/matricula/registrar-completo
```

**Body (JSON):**
```json
{
  "nombre": "Diego Rodríguez",
  "rut": "12345678-9",
  "fechaNacimiento": "2010-05-15",
  "curso": "2A",
  "usuarioId": 3,
  "estado": "Activo"
}
```

---

## Stack Tecnológico

- Backend: Spring Boot 3.x
- Java: 17+
- Autenticación: JWT
- Base de Datos: PostgreSQL
- Build Tool: Maven
- API Gateway: Spring Cloud Gateway

---

**Última actualización:** 7 de Mayo de 2026
