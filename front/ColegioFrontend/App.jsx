import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { getUserRole } from './auth';
import Login from './Login';
import Signup from './Signup';
import Dashboard from './Dashboard';
import AdminDashboard from './AdminDashboard';

// Protege rutas verificando que exista un token y rol permitido
const ProtectedRoute = ({ children, allowedRoles }) => {
  const role = getUserRole();
  if (!role) return <Navigate to="/login" replace />;
  
  // Convertimos ambos a minúsculas para que deje pasar sin importar si dice "Admin" o "admin"
  if (allowedRoles && !allowedRoles.some(r => r.toLowerCase() === role.toLowerCase())) {
    return <Navigate to="/dashboard" replace />;
  }
  return children;
};

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        
        <Route path="/dashboard" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
        <Route path="/admin" element={
          <ProtectedRoute allowedRoles={['Admin']}><AdminDashboard /></ProtectedRoute>
        } />
        
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </BrowserRouter>
  );
}