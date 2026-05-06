import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { setToken, getUserRole } from './auth';

export default function Login() {
  const navigate = useNavigate();
  const [form, setForm] = useState({ correo: '', password: '' });
  const [error, setError] = useState('');

  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');

    try {
      const res = await fetch('/api/auth/login', { 
        method: 'POST', 
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(form) 
      });
      
      if (res.ok) {
        const data = await res.json();
        setToken(data.token); // Guardamos el JWT recibido
        
        // Redirección en base al rol decodificado del JWT
        const role = getUserRole();
        if (role && role.toLowerCase() === 'admin') navigate('/admin');
        else navigate('/dashboard');
      } else {
        setError('Credenciales inválidas.');
      }
    } catch (err) {
      setError('Error de conexión con el servidor.');
    }
  };

  return (
    <div style={{ padding: '20px', maxWidth: '400px', margin: '0 auto', fontFamily: 'sans-serif' }}>
      <h2>Iniciar Sesión</h2>
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <form onSubmit={handleLogin} style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
        <input type="email" placeholder="Correo electrónico" value={form.correo} onChange={e => setForm({...form, correo: e.target.value})} required />
        <input type="password" placeholder="Contraseña" value={form.password} onChange={e => setForm({...form, password: e.target.value})} required />
        <button type="submit" style={{ padding: '10px', cursor: 'pointer' }}>Ingresar</button>
      </form>
      <p style={{ marginTop: '15px' }}>¿No tienes cuenta? <Link to="/signup">Regístrate aquí</Link></p>
    </div>
  );
}