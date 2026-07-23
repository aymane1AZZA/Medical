import {
  auditEntries,
  clinicalPatients,
  notifications,
  patientBiology,
  patientPrescription,
  patientTimeline,
  sessionCockpit,
} from '@/data/clinicalMocks'

const MOCK_DELAY = 280

function wait(value, state) {
  return new Promise((resolve, reject) => {
    window.setTimeout(() => {
      if (state === 'error') {
        reject(new Error('Le service clinique est momentanément indisponible.'))
        return
      }
      resolve(state === 'empty' ? [] : value)
    }, MOCK_DELAY)
  })
}

export function listPatients({ state } = {}) {
  return wait(clinicalPatients, state)
}

export function getPatientRecord(patientId, { state } = {}) {
  const patient = clinicalPatients.find((item) => item.id === patientId)
  if (state === 'error') return wait(null, state)
  if (!patient || state === 'empty') return wait(null)
  return wait({
    patient,
    biology: patientBiology[patientId] || [],
    prescription: patientPrescription[patientId] || null,
    timeline: patientTimeline[patientId] || [],
    audit: auditEntries,
  })
}

export function getSession(sessionId, { state } = {}) {
  if (state === 'error') return wait(null, state)
  if (state === 'empty' || sessionId !== sessionCockpit.id) return wait(null)
  return wait(sessionCockpit)
}

export function getNotifications() {
  return notifications
}
