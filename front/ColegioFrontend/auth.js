import { jwtDecode } from 'jwt-decode';

export const setToken = (token) => localStorage.setItem('jwt', token);
export const getToken = () => localStorage.getItem('jwt');
export const removeToken = () => localStorage.removeItem('jwt');

export const getUserRole = () => {
  const token = getToken();
  if (!token) return null;
  try {
    const decodedToken = jwtDecode(token);
    return decoded.rol; // Asume que el backend envía "rol" en el payload del JWT
  } catch (error) {
    return null;
  }
};