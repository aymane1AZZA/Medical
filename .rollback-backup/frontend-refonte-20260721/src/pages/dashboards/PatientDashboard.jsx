import { CalendarDays, Download, FileText, HeartPulse } from 'lucide-react';
import Loader from '../../components/Loader.jsx';
import PermissionGrid from '../../components/PermissionGrid.jsx';
import StatCard from '../../components/StatCard.jsx';
import DashboardLayout from '../../layouts/DashboardLayout.jsx';
import { useDashboard } from '../../hooks/useDashboard';

export default function PatientDashboard() {
  const { dashboard, loading, error } = useDashboard('ROLE_PATIENT');

  return (
    <DashboardLayout title="Mon espace santé" eyebrow="Dossier patient">
      {loading && <Loader label="Chargement du tableau de bord" />}
      {error && <p className="rounded-lg border border-red-200 bg-red-50 p-4 text-sm font-semibold text-chu-red">{error}</p>}
      {dashboard && (
        <div className="space-y-6">
          <div className="grid gap-4 md:grid-cols-4">
            <StatCard label="Documents" value="14" icon={FileText} />
            <StatCard label="Rendez-vous" value="3" icon={CalendarDays} tone="text-info" />
            <StatCard label="Prescriptions" value="5" icon={HeartPulse} tone="text-success" />
            <StatCard label="Téléchargements" value="8" icon={Download} tone="text-primary" />
          </div>
          <PermissionGrid items={dashboard.permissions} />
        </div>
      )}
    </DashboardLayout>
  );
}
