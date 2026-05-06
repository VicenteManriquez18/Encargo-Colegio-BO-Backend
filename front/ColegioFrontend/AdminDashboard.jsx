import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { removeToken, getUserRole, getToken } from './auth';

export default function AdminDashboard() {
  const navigate = useNavigate();
  const role = getUserRole();
  const [usuarios, setUsuarios] = useState([]);
  
  // Estados para el formulario de creación
  const [form, setForm] = useState({ correo: '', password: '', rol: 'Alumno' });
  const [mensaje, setMensaje] = useState({ texto: '', tipo: '' });

  // Función para obtener la lista actualizada de la base de datos
  const cargarUsuarios = () => {
    if (role === 'Admin') {
      fetch('/api/usuarios', {
        headers: { 'Authorization': 'Bearer ' + getToken() }
      })
      .then(res => res.json())
      .then(data => {
        if (Array.isArray(data)) setUsuarios(data);
      })
      .catch(err => console.error("Error al cargar usuarios:", err));
    }
  };

  useEffect(() => {
    cargarUsuarios();
  }, [role]);

  const handleLogout = () => {
    removeToken();
    navigate('/login');
  };

  // Función para registrar al usuario desde el panel
  const handleCreateUser = async (e) => {
    e.preventDefault();
    setMensaje({ texto: '', tipo: '' });
    
    try {
      // Reutilizamos el endpoint de registro que encripta la contraseña
      const res = await fetch('/api/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(form)
      });
      
      if (res.ok) {
        setMensaje({ texto: 'Usuario creado exitosamente.', tipo: 'success' });
        setForm({ correo: '', password: '', rol: 'Alumno' }); // Limpiamos los campos
        cargarUsuarios(); // ¡Recargamos la tabla al instante!
      } else {
        const errData = await res.json();
        setMensaje({ texto: errData.error || 'Error al crear usuario.', tipo: 'error' });
      }
    } catch (err) {
      setMensaje({ texto: 'Error de conexión con el servidor.', tipo: 'error' });
    }
  };

  return (
    <div style={{ padding: '20px', fontFamily: 'sans-serif' }}>
      <h1>Panel de Administración</h1>
      <p>Bienvenido al área exclusiva para directivos/administradores.</p>
      <p>Tu rol es: <strong>{role}</strong></p>
      <button onClick={handleLogout} style={{ padding: '10px', cursor: 'pointer', marginBottom: '20px' }}>Cerrar Sesión</button>

      {/* Formulario de creación de usuarios */}
      <div style={{ marginBottom: '30px', padding: '20px', border: '1px solid #ccc', borderRadius: '5px', maxWidth: '500px', backgroundColor: '#f9f9f9' }}>
        <h3 style={{ marginTop: 0 }}>Registrar Nuevo Usuario</h3>
        
        {mensaje.texto && (
          <p style={{ color: mensaje.tipo === 'error' ? 'red' : 'green', fontWeight: 'bold' }}>{mensaje.texto}</p>
        )}
        
        <form onSubmit={handleCreateUser} style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
          <input type="email" placeholder="Correo electrónico" value={form.correo} onChange={e => setForm({...form, correo: e.target.value})} required style={{ padding: '8px' }} />
          <input type="password" placeholder="Contraseña" value={form.password} onChange={e => setForm({...form, password: e.target.value})} required style={{ padding: '8px' }} />
          
          <select value={form.rol} onChange={e => setForm({...form, rol: e.target.value})} style={{ padding: '8px' }}>
            <option value="Alumno">Alumno</option>
            <option value="Apoderado">Apoderado</option>
            <option value="Profesor">Profesor</option>
            <option value="Admin">Admin</option>
          </select>
          
          <button type="submit" style={{ padding: '10px', cursor: 'pointer', backgroundColor: '#007BFF', color: 'white', border: 'none', borderRadius: '3px' }}>
            Crear Usuario
          </button>
        </form>
      </div>

      {/* Tabla de usuarios */}
      <h2>Lista de Usuarios Registrados</h2>
      <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '10px' }}>
        <thead>
          <tr style={{ backgroundColor: '#eaeaea', textAlign: 'left' }}>
            <th style={{ padding: '10px', border: '1px solid #ddd' }}>ID</th>
            <th style={{ padding: '10px', border: '1px solid #ddd' }}>Correo</th>
            <th style={{ padding: '10px', border: '1px solid #ddd' }}>Rol</th>
          </tr>
        </thead>
        <tbody>
          {usuarios.map(u => (
            <tr key={u.id}>
              <td style={{ padding: '10px', border: '1px solid #ddd' }}>{u.id}</td>
              <td style={{ padding: '10px', border: '1px solid #ddd' }}>{u.correo}</td>
              <td style={{ padding: '10px', border: '1px solid #ddd' }}>
                <strong>{u.rol}</strong>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}