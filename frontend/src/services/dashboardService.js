import api from './api';

const endpointByRole = {
  ROLE_ADMIN: '/admin/dashboard',
  ROLE_MEDECIN: '/medecin/dashboard',
  ROLE_INFERMIER: '/infirmier/dashboard',
  ROLE_BIOMEDICAL: '/biomedical/dashboard',
  ROLE_PATIENT: '/patient/dashboard',
  ROLE_LABO: '/labo/dashboard',
};

export async function getDashboard(role) {
  const endpoint = endpointByRole[role];
  if (!endpoint) throw new Error('Rôle non pris en charge.');
  const { data } = await api.get(endpoint);
  return data;
}
