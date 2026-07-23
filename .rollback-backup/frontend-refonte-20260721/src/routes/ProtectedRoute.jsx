import { Navigate, Outlet, useLocation, useNavigate } from 'react-router-dom';
import Loader from '../components/Loader.jsx';
import { PermissionDeniedState } from '../components/clinical/ClinicalStates.jsx';
import { useAuth } from '../contexts/AuthContext.jsx';
import { rolePath } from '../utils/roles';

export default function ProtectedRoute({ allowedRoles }) {
  const { bootstrapped, isAuthenticated, user } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();

  if (!bootstrapped) {
    return <Loader fullScreen label="Chargement de la session" />;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }

  if (!allowedRoles.includes(user.role)) {
    return <PermissionDeniedState onBack={() => navigate(rolePath(user.role), { replace: true })} />;
  }

  return <Outlet />;
}
