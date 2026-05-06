import { useNavigate } from 'react-router-dom';
import { removeToken, getUserRole } from './auth';

export default function Dashboard() {
  const navigate = useNavigate();
  const role = getUserRole();

  const handleLogout = () => {
    removeToken();
    navigate('/login');
  };

  return (
    <div style={{ padding: '20px', fontFamily: 'sans-serif' }}>
      <h1>Panel Principal</h1>
      <p>Bienvenido. Tu rol asignado es: <strong>{role}</strong></p>
      <button onClick={handleLogout} style={{ padding: '10px', cursor: 'pointer' }}>Cerrar Sesión</button>
    </div>
  );
}