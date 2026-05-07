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


## Gestión de Usuarios

### Listar Todos los Usuarios

```http
GET /api/usuarios
```


### Obtener Usuario por ID

```http
GET /api/usuarios/{id}
```

---

### Actualizar Usuario

```http
PUT /api/usuarios/{id}
```



### Eliminar Usuario

```http
DELETE /api/usuarios/{id}
```

----------------------------------------------------------------------------



## Gestión de Asistencia

### Ver Vista de Asistencia

```http
GET /api/asistencia/vista
```


---

### Registrar Asistencia

```http
POST /api/asistencia/registrar
```


---

### Listar Asistencias (Admin)

```http
GET /api/asistencia-admin
```

---

---

### Obtener Asistencia por ID (Admin)

```http
GET /api/asistencia-admin/{id}
```

---

### Eliminar Asistencia (Admin)

```http
DELETE /api/asistencia-admin/{id}
```

----------------------------------------------------------------------------


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

---

### Registrar Matrícula Completa

```http
POST /api/matricula/registrar-completo
```


---





**Última actualización:** 7 de Mayo de 2026
