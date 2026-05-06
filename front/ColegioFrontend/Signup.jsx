import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';

export default function Signup() {
  const navigate = useNavigate();
  const [form, setForm] = useState({ correo: '', password: '' });
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      // El rol no se envía, lo asigna el backend por defecto (ej: 'Alumno')
      const res = await fetch('/api/auth/register', { 
        method: 'POST', 
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(form) 
      });
      
      if (res.ok) {
        navigate('/login');
      } else {
        const errData = await res.json();
        setError(errData.error || 'Error al registrar la cuenta.');
      }
    } catch (err) {
      setError('Error de conexión con el servidor.');
    }
  };

  return (
    <div style={{ padding: '20px', maxWidth: '400px', margin: '0 auto', fontFamily: 'sans-serif' }}>
      <h2>Crear Cuenta</h2>
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
        <input type="email" placeholder="Correo electrónico" value={form.correo} onChange={e => setForm({...form, correo: e.target.value})} required />
        <input type="password" placeholder="Contraseña" value={form.password} onChange={e => setForm({...form, password: e.target.value})} required />
        <button type="submit" style={{ padding: '10px', cursor: 'pointer' }}>Registrarse</button>
      </form>
      <p style={{ marginTop: '15px' }}>¿Ya tienes cuenta? <Link to="/login">Inicia sesión aquí</Link></p>
    </div>
  );
}