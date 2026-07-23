/**
 * @typedef {'eligible'|'pending'|'ineligible'} EligibilityStatus
 * @typedef {'normal'|'abnormal'|'critical'} LabStatus
 */

export const clinicalPatients = [
  {
    id: 'pat-001',
    ipp: 'IPP-2024-008731',
    cin: 'BE812946',
    firstName: 'Salma',
    lastName: 'El Mansouri',
    birthDate: '1986-04-12',
    age: 39,
    sex: 'F',
    bloodGroup: 'A+',
    phone: '+212 6 12 34 56 78',
    city: 'Casablanca',
    eligibility: 'eligible',
    diagnosis: 'Myasthénie auto-immune',
    allergies: ['Latex'],
    referringPhysician: { name: 'Dr Yasmine Alaoui', npe: 'NPE-10482' },
    protocol: 'Échanges plasmatiques thérapeutiques',
    nextSession: '2026-07-20T10:30:00',
    lastSession: '2026-07-18T09:00:00',
  },
  {
    id: 'pat-002', ipp: 'IPP-2025-002184', cin: 'BK450231', firstName: 'Omar', lastName: 'Bennani',
    birthDate: '1972-09-03', age: 53, sex: 'M', bloodGroup: 'O+', phone: '+212 6 22 41 08 97', city: 'Rabat',
    eligibility: 'pending', diagnosis: 'Hypercholestérolémie familiale', allergies: [],
    referringPhysician: { name: 'Dr Mehdi Naciri', npe: 'NPE-11803' }, protocol: 'LDL-aphérèse',
    nextSession: '2026-07-20T13:30:00', lastSession: '2026-07-13T13:30:00',
  },
  {
    id: 'pat-003', ipp: 'IPP-2023-011902', cin: 'CD891470', firstName: 'Imane', lastName: 'Chraïbi',
    birthDate: '1991-12-21', age: 34, sex: 'F', bloodGroup: 'B-', phone: '+212 6 08 31 77 40', city: 'Mohammedia',
    eligibility: 'eligible', diagnosis: 'Neuromyélite optique', allergies: ['Pénicilline'],
    referringPhysician: { name: 'Pr Amine Idrissi', npe: 'NPE-09551' }, protocol: 'Échanges plasmatiques thérapeutiques',
    nextSession: '2026-07-21T08:00:00', lastSession: '2026-07-19T08:00:00',
  },
  {
    id: 'pat-004', ipp: 'IPP-2026-000774', cin: 'EE127805', firstName: 'Hassan', lastName: 'Tazi',
    birthDate: '1964-02-17', age: 62, sex: 'M', bloodGroup: 'AB+', phone: '+212 6 71 02 43 19', city: 'Casablanca',
    eligibility: 'ineligible', diagnosis: 'Syndrome hyperviscosité', allergies: [],
    referringPhysician: { name: 'Dr Sara Berrada', npe: 'NPE-12174' }, protocol: 'Plasmaphérèse',
    nextSession: null, lastSession: null,
  },
  {
    id: 'pat-005', ipp: 'IPP-2022-006381', cin: 'GA334198', firstName: 'Nadia', lastName: 'Lamrani',
    birthDate: '1980-07-29', age: 45, sex: 'F', bloodGroup: 'O-', phone: '+212 6 40 52 10 88', city: 'El Jadida',
    eligibility: 'pending', diagnosis: 'Polyneuropathie inflammatoire', allergies: ['Chlorhexidine'],
    referringPhysician: { name: 'Dr Yasmine Alaoui', npe: 'NPE-10482' }, protocol: 'Immunoadsorption',
    nextSession: '2026-07-22T09:30:00', lastSession: '2026-07-15T09:30:00',
  },
]

export const patientBiology = {
  'pat-001': [
    { id: 'lab-1', category: 'Hépatique', analyte: 'Albumine', value: 38, unit: 'g/L', reference: '35–50', status: 'normal', date: '2026-07-20T07:42:00', laboratory: 'Laboratoire central CHU', validatedBy: 'Dr Lina Amrani · NPE-08742', history: [37, 39, 38] },
    { id: 'lab-2', category: 'Rénal', analyte: 'Créatinine', value: 96, unit: 'µmol/L', reference: '45–84', status: 'abnormal', date: '2026-07-20T07:42:00', laboratory: 'Laboratoire central CHU', validatedBy: 'Dr Lina Amrani · NPE-08742', history: [82, 89, 96] },
    { id: 'lab-3', category: 'Rénal', analyte: 'Urée', value: 6.1, unit: 'mmol/L', reference: '2.5–7.5', status: 'normal', date: '2026-07-20T07:42:00', laboratory: 'Laboratoire central CHU', validatedBy: 'Dr Lina Amrani · NPE-08742', history: [5.6, 5.8, 6.1] },
    { id: 'lab-4', category: 'Rénal', analyte: 'Acide urique', value: 344, unit: 'µmol/L', reference: '155–357', status: 'normal', date: '2026-07-20T07:42:00', laboratory: 'Laboratoire central CHU', validatedBy: 'Dr Lina Amrani · NPE-08742', history: [328, 337, 344] },
    { id: 'lab-5', category: 'Diabète', analyte: 'Glycémie', value: 1.42, unit: 'g/L', reference: '0.70–1.10', status: 'abnormal', date: '2026-07-20T07:42:00', laboratory: 'Laboratoire central CHU', validatedBy: 'Dr Lina Amrani · NPE-08742', history: [0.98, 1.12, 1.42] },
    { id: 'lab-6', category: 'Lipidique', analyte: 'Cholestérol total', value: 1.78, unit: 'g/L', reference: '< 2.00', status: 'normal', date: '2026-07-20T07:42:00', laboratory: 'Laboratoire central CHU', validatedBy: 'Dr Lina Amrani · NPE-08742', history: [2.1, 1.92, 1.78] },
    { id: 'lab-7', category: 'Lipidique', analyte: 'HDL', value: 0.48, unit: 'g/L', reference: '> 0.40', status: 'normal', date: '2026-07-20T07:42:00', laboratory: 'Laboratoire central CHU', validatedBy: 'Dr Lina Amrani · NPE-08742', history: [0.44, 0.46, 0.48] },
    { id: 'lab-8', category: 'Lipidique', analyte: 'LDL', value: 1.08, unit: 'g/L', reference: '< 1.30', status: 'normal', date: '2026-07-20T07:42:00', laboratory: 'Laboratoire central CHU', validatedBy: 'Dr Lina Amrani · NPE-08742', history: [1.42, 1.21, 1.08] },
    { id: 'lab-9', category: 'Lipidique', analyte: 'Triglycérides', value: 1.36, unit: 'g/L', reference: '< 1.50', status: 'normal', date: '2026-07-20T07:42:00', laboratory: 'Laboratoire central CHU', validatedBy: 'Dr Lina Amrani · NPE-08742', history: [1.51, 1.44, 1.36] },
  ],
}

export const patientTimeline = {
  'pat-001': [
    { id: 'tl-1', date: '20 juil. · 07:55', title: 'Éligibilité confirmée', description: 'Bilans pré-séance revus par Dr Yasmine Alaoui (NPE-10482).', tone: 'success' },
    { id: 'tl-2', date: '20 juil. · 07:42', title: 'Bilan biologique validé', description: 'Créatinine et glycémie signalées hors intervalle, sans contre-indication.', tone: 'warning' },
    { id: 'tl-3', date: '19 juil. · 16:20', title: 'Prescription renouvelée', description: 'Échange plasmatique, 2 800 mL, anticoagulation ACD-A.', tone: 'info' },
    { id: 'tl-4', date: '18 juil. · 11:18', title: 'Séance clôturée', description: 'Tolérance satisfaisante, aucun incident majeur.', tone: 'neutral' },
  ],
}

export const patientPrescription = {
  'pat-001': {
    id: 'RX-APH-2026-1842', procedure: 'Échanges plasmatiques thérapeutiques', indication: 'Myasthénie auto-immune en poussée',
    targetVolume: 2800, replacementFluid: 'Albumine humaine 5 %', anticoagulant: 'ACD-A', ratio: '1:12',
    frequency: '1 séance / 48 h', sessionsPlanned: 5, sessionsCompleted: 3,
    prescriber: 'Dr Yasmine Alaoui', npe: 'NPE-10482', prescribedAt: '2026-07-19T16:20:00', status: 'active',
  },
}

export const sessionCockpit = {
  id: 'SEA-2026-0720-04', patientId: 'pat-001', status: 'in_progress', room: 'Salle APH-02',
  startedAt: '2026-07-20T10:30:00', elapsedMinutes: 72, plannedMinutes: 120, progress: 60,
  procedure: 'Échanges plasmatiques thérapeutiques', operator: 'Mme Kawtar El Fassi', operatorPpr: 'PPR-04821',
  machine: { name: 'Spectra Optia 4', assetId: 'BIO-APH-004', state: 'Opérationnel', maintenanceDue: '2026-08-12' },
  vitals: { heartRate: 78, systolic: 118, diastolic: 72, spo2: 98, temperature: 36.8 },
  parameters: { bloodFlow: 62, processedVolume: 4310, targetVolume: 7200, plasmaVolume: 1680, targetPlasma: 2800, anticoagulantRate: 5.2 },
  checklist: [
    { id: 'c1', label: 'Identité patient contrôlée sur deux identifiants', checked: true },
    { id: 'c2', label: 'Prescription et consentement vérifiés', checked: true },
    { id: 'c3', label: 'Bilans biologiques et voie veineuse validés', checked: true },
    { id: 'c4', label: 'Kit, lot et péremption tracés', checked: true },
    { id: 'c5', label: 'Compte rendu de clôture complété', checked: false },
  ],
  alerts: [
    { id: 'a1', severity: 'warning', time: '11:36', title: 'Pression de retour élevée', detail: 'Valeur 186 mmHg pendant 18 s.', action: 'Contrôler la voie de retour et le positionnement du bras.' },
    { id: 'a2', severity: 'info', time: '11:14', title: 'Objectif à 50 %', detail: 'Volume plasmatique traité : 1 400 mL.', action: 'Poursuivre la surveillance standard.' },
  ],
  events: [
    { time: '11:42', label: 'Constantes relevées', author: 'K. El Fassi · PPR-04821' },
    { time: '11:36', label: 'Alerte pression acquittée', author: 'K. El Fassi · PPR-04821' },
    { time: '11:14', label: 'Palier 50 % atteint', author: 'Système' },
    { time: '10:30', label: 'Séance démarrée', author: 'K. El Fassi · PPR-04821' },
  ],
}

export const notifications = [
  { id: 'n1', title: 'Résultat biologique critique', detail: 'IPP-2025-002184 · potassium à contrôler', time: 'Il y a 8 min', unread: true, severity: 'critical' },
  { id: 'n2', title: 'Séance prête à démarrer', detail: 'Salma El Mansouri · Salle APH-02', time: 'Il y a 24 min', unread: true, severity: 'info' },
  { id: 'n3', title: 'Maintenance planifiée', detail: 'Spectra Optia 2 · 22 juillet à 17:00', time: 'Hier', unread: false, severity: 'warning' },
]

export const auditEntries = [
  { id: 'au1', time: '20/07/2026 07:55', actor: 'Dr Yasmine Alaoui', identifier: 'NPE-10482', action: 'Validation de l’éligibilité', target: 'IPP-2024-008731' },
  { id: 'au2', time: '20/07/2026 07:42', actor: 'Dr Lina Amrani', identifier: 'NPE-08742', action: 'Validation du bilan biologique', target: 'LAB-2026-0720-448' },
  { id: 'au3', time: '19/07/2026 16:20', actor: 'Dr Yasmine Alaoui', identifier: 'NPE-10482', action: 'Signature de prescription', target: 'RX-APH-2026-1842' },
]
