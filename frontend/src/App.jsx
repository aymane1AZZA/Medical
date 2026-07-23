import { Navigate, Route, Routes } from 'react-router-dom';
import ProtectedRoute from './routes/ProtectedRoute.jsx';
import LoginPage from './pages/LoginPage.jsx';
import ForgotPasswordPage from './pages/ForgotPasswordPage.jsx';
import AdminDashboard from './pages/dashboards/AdminDashboard.jsx';
import MedecinDashboard from './pages/dashboards/MedecinDashboard.jsx';
import InfirmierDashboard from './pages/dashboards/InfirmierDashboard.jsx';
import BiomedicalDashboard from './pages/dashboards/BiomedicalDashboard.jsx';
import PatientDashboard from './pages/dashboards/PatientDashboard.jsx';
import LaboDashboard from './pages/dashboards/LaboDashboard.jsx';

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/mot-de-passe-oublie" element={<ForgotPasswordPage />} />

      <Route element={<ProtectedRoute allowedRoles={['ROLE_ADMIN']} />}>
        <Route path="/admin" element={<AdminDashboard />} />
      </Route>
      <Route element={<ProtectedRoute allowedRoles={['ROLE_MEDECIN']} />}>
        <Route path="/medecin" element={<MedecinDashboard />} />
      </Route>
      <Route element={<ProtectedRoute allowedRoles={['ROLE_INFERMIER']} />}>
        <Route path="/infirmier" element={<InfirmierDashboard />} />
      </Route>
      <Route element={<ProtectedRoute allowedRoles={['ROLE_BIOMEDICAL']} />}>
        <Route path="/biomedical" element={<BiomedicalDashboard />} />
      </Route>
      <Route element={<ProtectedRoute allowedRoles={['ROLE_PATIENT']} />}>
        <Route path="/patient" element={<PatientDashboard />} />
      </Route>
      <Route element={<ProtectedRoute allowedRoles={['ROLE_LABO']} />}>
        <Route path="/labo" element={<LaboDashboard />} />
      </Route>

      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}
