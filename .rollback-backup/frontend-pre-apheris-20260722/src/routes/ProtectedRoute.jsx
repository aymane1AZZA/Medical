import { Navigate, Outlet, useLocation } from 'react-router-dom';
import Loader from '../components/Loader.jsx';
import { useAuth } from '../contexts/AuthContext.jsx';
import { rolePath } from '../utils/roles';

export default function ProtectedRoute({ allowedRoles }) {
  const { bootstrapped, isAuthenticated, user } = useAuth();
  const location = useLocation();

  if (!bootstrapped) {
    return <Loader fullScreen label="Chargement de la session" />;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }

  if (!allowedRoles.includes(user.role)) {
    return <Navigate to={rolePath(user.role)} replace />;
  }

  return <Outlet />;
}
