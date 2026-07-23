const directionData = {
  a: {
    label: 'Clinical Precision',
    summary: 'Structure rigoureuse, donnees compactes et tracabilite au premier plan.',
  },
  b: {
    label: 'Human Clinical Operations',
    summary: 'Parcours lisible, contexte humain et reduction du stress pendant la surveillance.',
  },
  c: {
    label: 'Advanced Hospital Command',
    summary: 'Coordination en temps reel, alertes persistantes et actions critiques immediates.',
  },
}

const body = document.body
const summary = document.querySelector('#direction-summary')
const tabs = [...document.querySelectorAll('.direction-tab')]
const drawer = document.querySelector('#secondary-panel')
const drawerTrigger = document.querySelector('#open-drawer')
const drawerBackdrop = document.querySelector('#drawer-backdrop')
const drawerClose = document.querySelector('.close-panel')
const modalBackdrop = document.querySelector('#sensitive-modal')
const openModal = document.querySelector('#open-modal')
const cancelModal = document.querySelector('#cancel-modal')
const confirmCheck = document.querySelector('#confirm-check')
const confirmSensitive = document.querySelector('#confirm-sensitive')
const toast = document.querySelector('#toast')
const observationForm = document.querySelector('#observation-form')
const observation = document.querySelector('#observation')
const observationError = document.querySelector('#observation-error')
let modalReturnFocus = null
let toastTimer = null

function setDirection(direction) {
  body.classList.remove('theme-a', 'theme-b', 'theme-c')
  body.classList.add(`theme-${direction}`)
  tabs.forEach((tab) => {
    const selected = tab.dataset.direction === direction
    tab.classList.toggle('is-selected', selected)
    tab.setAttribute('aria-selected', String(selected))
  })
  summary.textContent = directionData[direction].summary
  document.title = `Apheris - ${directionData[direction].label}`
  history.replaceState(null, '', `#${direction}`)
}

tabs.forEach((tab, index) => {
  tab.addEventListener('click', () => setDirection(tab.dataset.direction))
  tab.addEventListener('keydown', (event) => {
    if (!['ArrowLeft', 'ArrowRight', 'Home', 'End'].includes(event.key)) return
    event.preventDefault()
    const nextIndex = event.key === 'Home'
      ? 0
      : event.key === 'End'
        ? tabs.length - 1
        : (index + (event.key === 'ArrowRight' ? 1 : -1) + tabs.length) % tabs.length
    tabs[nextIndex].focus()
    setDirection(tabs[nextIndex].dataset.direction)
  })
})

window.addEventListener('hashchange', () => {
  const direction = location.hash.slice(1)
  if (directionData[direction]) setDirection(direction)
})

function showToast(message, tone = 'success') {
  clearTimeout(toastTimer)
  toast.textContent = message
  toast.dataset.tone = tone
  toast.hidden = false
  toastTimer = setTimeout(() => { toast.hidden = true }, 3600)
}

function setDrawer(open) {
  drawer.classList.toggle('is-open', open)
  drawerBackdrop.hidden = !open
  drawerTrigger.setAttribute('aria-expanded', String(open))
  if (open) drawerClose.focus()
  else drawerTrigger.focus()
}

drawerTrigger.addEventListener('click', () => setDrawer(!drawer.classList.contains('is-open')))
drawerClose.addEventListener('click', () => setDrawer(false))
drawerBackdrop.addEventListener('click', () => setDrawer(false))

function setModal(open) {
  modalBackdrop.hidden = !open
  if (open) {
    modalReturnFocus = document.activeElement
    confirmCheck.checked = false
    confirmSensitive.disabled = true
    requestAnimationFrame(() => confirmCheck.focus())
  } else {
    modalReturnFocus?.focus()
  }
}

openModal.addEventListener('click', () => setModal(true))
cancelModal.addEventListener('click', () => setModal(false))
modalBackdrop.addEventListener('click', (event) => {
  if (event.target === modalBackdrop) setModal(false)
})
confirmCheck.addEventListener('change', () => { confirmSensitive.disabled = !confirmCheck.checked })
confirmSensitive.addEventListener('click', () => {
  if (confirmSensitive.disabled) return
  confirmSensitive.disabled = true
  confirmSensitive.textContent = 'Cloture en cours...'
  setTimeout(() => {
    setModal(false)
    confirmSensitive.textContent = 'Confirmer la cloture'
    showToast('Seance transmise pour validation medicale.')
  }, 800)
})

document.addEventListener('keydown', (event) => {
  if (event.key === 'Tab' && !modalBackdrop.hidden) {
    const focusable = [...modalBackdrop.querySelectorAll('button:not([disabled]), input:not([disabled])')]
    const first = focusable[0]
    const last = focusable.at(-1)
    if (event.shiftKey && document.activeElement === first) {
      event.preventDefault()
      last.focus()
    } else if (!event.shiftKey && document.activeElement === last) {
      event.preventDefault()
      first.focus()
    }
    return
  }
  if (event.key !== 'Escape') return
  if (!modalBackdrop.hidden) setModal(false)
  else if (drawer.classList.contains('is-open')) setDrawer(false)
})

document.querySelectorAll('.workflow-step').forEach((step) => {
  step.addEventListener('click', () => {
    document.querySelectorAll('.workflow-step').forEach((item) => {
      const current = item === step
      item.classList.toggle('is-current', current)
      if (current) item.setAttribute('aria-current', 'step')
      else item.removeAttribute('aria-current')
    })
    showToast(`Etape ${step.dataset.step} ouverte.`, 'info')
  })
})

document.querySelectorAll('.tolerance').forEach((button) => {
  button.addEventListener('click', () => {
    document.querySelectorAll('.tolerance').forEach((item) => {
      const selected = item === button
      item.classList.toggle('is-selected', selected)
      item.setAttribute('aria-pressed', String(selected))
    })
  })
})

document.querySelectorAll('.row-select').forEach((checkbox) => {
  checkbox.addEventListener('change', () => {
    checkbox.closest('tr').classList.toggle('is-selected', checkbox.checked)
    const count = document.querySelectorAll('.row-select:checked').length
    document.querySelector('#selected-count').textContent = `${count} selection${count > 1 ? 's' : ''}`
  })
})

observationForm.addEventListener('submit', (event) => {
  event.preventDefault()
  const value = observation.value.trim()
  const invalid = value.length < 8
  observationError.hidden = !invalid
  observation.setAttribute('aria-invalid', String(invalid))
  if (invalid) {
    observation.focus()
    return
  }
  const button = observationForm.querySelector('[type="submit"]')
  button.disabled = true
  button.textContent = 'Enregistrement...'
  setTimeout(() => {
    button.disabled = false
    button.textContent = "Enregistrer l'observation"
    observation.value = ''
    observation.removeAttribute('aria-invalid')
    showToast('Observation horodatee et ajoutee au journal.')
  }, 700)
})

document.querySelector('#simulate-save').addEventListener('click', (event) => {
  const button = event.currentTarget
  button.disabled = true
  button.textContent = 'Enregistrement...'
  setTimeout(() => {
    button.disabled = false
    button.textContent = 'Enregistrer les parametres'
    showToast('Parametres enregistres a 10:02.')
  }, 650)
})

setDirection(['a', 'b', 'c'].includes(location.hash.slice(1)) ? location.hash.slice(1) : 'a')
