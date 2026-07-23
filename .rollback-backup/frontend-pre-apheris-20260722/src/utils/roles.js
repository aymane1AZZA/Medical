export const roles = [
  { value: 'ROLE_ADMIN', label: 'Administrateur', description: 'Pilotage global', path: '/admin', accent: 'from-slate-800 to-teal-700' },
  { value: 'ROLE_MEDECIN', label: 'Médecin', description: 'Dossiers et prescriptions', path: '/medecin', accent: 'from-sky-700 to-teal-600' },
  { value: 'ROLE_INFERMIER', label: 'Infirmier', description: 'Suivi des soins', path: '/infirmier', accent: 'from-emerald-700 to-cyan-700' },
  { value: 'ROLE_BIOMEDICAL', label: 'Ingénieur biomédical', description: 'Équipements et maintenance', path: '/biomedical', accent: 'from-indigo-700 to-teal-700' },
  { value: 'ROLE_PATIENT', label: 'Patient', description: 'Espace personnel', path: '/patient', accent: 'from-teal-700 to-lime-700' },
  { value: 'ROLE_LABO', label: 'Personnel de laboratoire', description: 'Analyses et résultats', path: '/labo', accent: 'from-violet-700 to-sky-700' },
];

export function rolePath(role) {
  return roles.find((item) => item.value === role)?.path || '/login';
}

export function roleLabel(role) {
  return roles.find((item) => item.value === role)?.label || role;
}
