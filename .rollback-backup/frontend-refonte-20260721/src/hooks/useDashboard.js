import { useEffect, useState } from 'react';
import { getDashboard } from '../services/dashboardService';

export function useDashboard(role) {
  const [dashboard, setDashboard] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let active = true;
    setLoading(true);
    getDashboard(role)
      .then((data) => {
        if (active) setDashboard(data);
      })
      .catch((exception) => {
        if (active) setError(exception.response?.data?.message || exception.message || 'Impossible de charger le tableau de bord.');
      })
      .finally(() => {
        if (active) setLoading(false);
      });

    return () => {
      active = false;
    };
  }, [role]);

  return { dashboard, loading, error };
}
