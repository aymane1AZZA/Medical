import api from './api';

export async function login(credentials) {
  if (import.meta.env.VITE_USE_MOCKS === 'true') {
    const fullNameByRole = {
      ROLE_ADMIN: 'Amine El Idrissi',
      ROLE_MEDECIN: 'Dr Yasmine Alaoui',
      ROLE_INFERMIER: 'Kawtar El Fassi',
      ROLE_BIOMEDICAL: 'Rachid Bennis',
      ROLE_PATIENT: 'Salma El Mansouri',
      ROLE_LABO: 'Dr Lina Amrani',
    }
    const header = btoa(JSON.stringify({ alg: 'none', typ: 'JWT' }))
    const payload = btoa(JSON.stringify({ sub: credentials.identifier, exp: Math.floor(Date.now() / 1000) + 8 * 60 * 60 }))
    return {
      token: `${header}.${payload}.demo`,
      user: { username: credentials.identifier, email: `${credentials.identifier}@demo.local`, fullName: fullNameByRole[credentials.role], role: credentials.role },
    }
  }
  const { data } = await api.post('/auth/login', credentials);
  return data;
}
