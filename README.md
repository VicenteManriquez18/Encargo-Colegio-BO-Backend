# Encargo-Colegio-BO-Backend

Este repositorio contiene la arquitectura de microservicios para la plataforma de gestión escolar del **Colegio BO**. El sistema está diseñado en una arquitectura orientada a servicios (SOA) y desacoplada utilizando Spring Cloud Gateway, RabbitMQ, WebClient para comunicación síncrona y bases de datos PostgreSQL independientes por cada microservicio.

---

## 📋 Información General y Puertos

| Microservicio / Componente | Puerto Expresado | Base URL Directa |
| :--- | :---: | :--- |
| **API Gateway** (Punto de Entrada) | `9090` | `http://localhost:9090` |
| **Servicio Usuarios** (Cuentas y Auth) | `8081` | `http://localhost:8081` |
| **Servicio Asistencia** (Control Diario) | `8082` | `http://localhost:8082` |
| **Servicio Matrícula** (Estudiantes/Fichas) | `8083` | `http://localhost:8083` |
| **Servicio Académico** (Cursos/Notas) | `8084` | `http://localhost:8084` |
| **Servicio Reportes** (Consolidación/Conducta) | `8085` | `http://localhost:8085` |
| **Servicio Mensajería** (Chat RabbitMQ) | `8086` | `http://localhost:8086` |
| **SonarQube** (Calidad de Código) | `9000` / `9001` | `http://localhost:9000` |

> [!NOTE]
> Todas las peticiones externas del Frontend o clientes HTTP deben dirigirse a través del **API Gateway (puerto 9090)** utilizando el prefijo `/api/`.

---

## 🔐 Autenticación y Cuentas

### Registrar Nuevo Usuario
Crea las credenciales de inicio de sesión de un usuario.
```http
POST /api/auth/register
```
**Body (JSON):**
```json
{
  "correo": "usuario@ejemplo.com",
  "password": "Password123#",
  "rol": "Alumno",
  "telefono": "+56912345678"
}
```
*Nota: La contraseña debe tener mínimo 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial.*

---

### Login (Obtener JWT)
Autentica al usuario y devuelve el token firmado JWT para autorizar peticiones protegidas.
```http
POST /api/auth/login
```
**Body (JSON):**
```json
{
  "correo": "usuario@ejemplo.com",
  "password": "Password123#"
}
```

---

## 👥 Gestión de Usuarios (Admin)

### Listar Todos los Usuarios
```http
GET /api/usuarios
```

### Obtener Usuario por ID
```http
GET /api/usuarios/{id}
```

### Crear Nuevo Usuario (Directo)
```http
POST /api/usuarios
```
**Body (JSON):**
```json
{
  "correo": "usuario@ejemplo.com",
  "password": "Password123#",
  "rol": "Profesor"
}
```

### Actualizar Usuario
Permite al administrador modificar el rol, el número telefónico y el curso asignado.
```http
PUT /api/usuarios/{id}
```
**Body (JSON):**
```json
{
  "correo": "usuario@ejemplo.com",
  "rol": "Profesor",
  "telefono": "+56998765432",
  "cursoId": 2
}
```

### Eliminar Usuario
Elimina al usuario del sistema y propaga la eliminación a las fichas de estudiantes o apoderados en los demás microservicios.
```http
DELETE /api/usuarios/{id}
```

---

## 📅 Gestión de Asistencia

### Listar Todas las Asistencias
```http
GET /api/asistencia
```

### Obtener Asistencia por ID
```http
GET /api/asistencia/{id}
```

### Crear Asistencia Manual
```http
POST /api/asistencia
```
**Body (JSON):**
```json
{
  "usuarioId": 1,
  "nombreUsuario": "alumno@gmail.com",
  "fecha": "2026-06-15T09:00:00"
}
```

### Registrar Asistencia (Automático)
Valida la existencia del estudiante y registra el marcaje con la fecha y hora actual en el servidor.
```http
POST /api/asistencia/registrar
```
**Body (JSON):**
```json
{
  "usuarioId": 1
}
```

### Eliminar Registro de Asistencia
```http
DELETE /api/asistencia/{id}
```

---

## 📝 Gestión de Matrícula y Estudiantes

### Listar Todos los Estudiantes (Fichas)
```http
GET /api/matricula/estudiantes
```

### Buscar Estudiante por RUT
```http
GET /api/matricula/estudiantes/rut/{rut}
```

### Registrar Matrícula Completa
Crea el perfil del alumno, asocia o guarda los datos de su apoderado correspondiente y sincroniza la matrícula en el servicio académico.
```http
POST /api/matricula/registrar-completo
```
**Body (JSON):**
```json
{
  "nombre": "Diego Rodríguez",
  "rut": "12345678-9",
  "fechaNacimiento": "2012-05-15",
  "cursoId": 1,
  "usuarioId": 3,
  "estado": "Activo",
  "apoderado": {
    "rut": "9876543-2",
    "nombre": "María González",
    "telefono": "987654321",
    "correo": "maria.apoderado@gmail.com",
    "usuarioId": 4
  }
}
```

---

## 🎓 Servicio Académico (Cursos y Notas)

### Listar Cursos Disponibles
```http
GET /api/academico/cursos
```

### Crear Nuevo Curso
```http
POST /api/academico/cursos
```
**Body (JSON):**
```json
{
  "nombre": "1 Medio A",
  "codigo": "CUR-1MA",
  "descripcion": "Primero Medio A"
}
```
*Al crearse el curso, el sistema inicializa automáticamente las asignaturas de inglés, lenguaje, matemática e historia.*

### Asignar Profesor Jefe a un Curso
```http
PUT /api/academico/cursos/{id}/profesor
```
**Body (JSON):**
```json
{
  "profesorId": 2
}
```

### Asignar Profesor a Asignatura del Curso
```http
PUT /api/academico/cursos/{id}/asignaturas/profesor
```
**Body (JSON):**
```json
{
  "asignatura": "ingles 1",
  "profesorId": 2
}
```

### Crear una Evaluación (Pruebas)
Permite al profesor asignado crear una evaluación en una materia de un curso.
```http
POST /api/academico/pruebas/curso/{cursoId}
```
**Body (JSON):**
```json
{
  "titulo": "Prueba 1 Álgebra",
  "asignatura": "matematica 1",
  "ponderacion": 25.0
}
```

### Registrar Notas
Registra la calificación de un alumno para una prueba específica. El valor debe estar entre `1.0` y `7.0`.
```http
POST /api/academico/notas/prueba/{pruebaId}
```
**Body (JSON):**
```json
{
  "alumnoId": 3,
  "valor": 6.5,
  "comentario": "Excelente rendimiento escolar."
}
```

---

## 📊 Servicio de Reportes (Consolidación y Conducta)

### Registrar Reporte de Comportamiento (Profesor)
Guarda anotaciones conductuales de un estudiante.
```http
POST /api/reportes/comportamiento
```
**Body (JSON):**
```json
{
  "alumnoId": 3,
  "tipo": "POSITIVO",
  "descripcion": "Participa activamente en clases y ayuda a sus compañeros.",
  "profesorId": 2
}
```

### Obtener Reporte Consolidado Completo (Apoderados / Profesores)
Genera en tiempo real un informe consolidado del alumno, recuperando síncronamente sus notas del *Servicio Académico*, asistencia del *Servicio Asistencia*, y comportamiento de su base de datos.
```http
GET /api/reportes/completo/alumno/{alumnoId}
```
*Nota: Si es apoderado, se verifica que el alumno esté previamente vinculado a su ficha.*

---

## 💬 Servicio de Mensajería (RabbitMQ)

### Enviar Mensaje (Profesor <-> Apoderado)
Valida los roles y encola un mensaje asíncrono para ser procesado por RabbitMQ.
```http
POST /api/mensajes/enviar
```
**Body (JSON):**
```json
{
  "remitenteId": 2,
  "destinatarioId": 4,
  "contenido": "Estimado, solicito reunión para conversar sobre el desempeño de su pupilo."
}
```

### Obtener Historial de Chat
```http
GET /api/mensajes/historial?user1={id1}&user2={id2}
```

### Listar Contactos Permitidos
Retorna el listado de apoderados vinculados si el consultor es Profesor, o el listado de docentes si es Apoderado.
```http
GET /api/mensajes/contactos/{userId}
```

---

## 🛠️ Stack Tecnológico

- **Backend core:** Java 21 & Spring Boot 3.x
- **Mensajería asíncrona:** RabbitMQ
- **Base de Datos:** PostgreSQL
- **API Gateway:** Spring Cloud Gateway, Resilience4j Circuit Breaker)
- **Calidad de código:** SonarQube
- **Herramienta de construcción:** Maven

---

**Última actualización:** 15 de Junio de 2026
