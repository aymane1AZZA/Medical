import { useState } from 'react'
import { AnimatePresence, motion } from 'framer-motion'
import {
  Activity,
  AlertTriangle,
  ArrowRight,
  Bell,
  CalendarDays,
  Check,
  ChevronRight,
  CircleUserRound,
  Clock3,
  Command,
  FileText,
  FlaskConical,
  Gauge,
  HeartPulse,
  History,
  KeyRound,
  LayoutDashboard,
  LockKeyhole,
  LogOut,
  Menu,
  MonitorDot,
  MoreHorizontal,
  Plus,
  Search,
  Settings,
  ShieldCheck,
  Stethoscope,
  UserRound,
  UsersRound,
  Wrench,
  X,
} from 'lucide-react'

import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { Input } from '@/components/ui/input'
import { Progress } from '@/components/ui/progress'
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetFooter,
  SheetHeader,
  SheetTitle,
} from '@/components/ui/sheet'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { Textarea } from '@/components/ui/textarea'
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from '@/components/ui/tooltip'

const navigation = [
  { id: 'cockpit', label: 'Séance active', icon: Activity },
  { id: 'patient', label: 'Dossier patient', icon: CircleUserRound },
  { id: 'planning', label: 'Planning', icon: CalendarDays, disabled: true },
  { id: 'biology', label: 'Biologie', icon: FlaskConical, disabled: true },
  { id: 'equipment', label: 'Équipements', icon: Wrench, disabled: true },
]

const stages = [
  { id: 1, label: 'Admission', detail: 'Identité confirmée', complete: true },
  { id: 2, label: 'Préparation', detail: 'Prescription validée', complete: true },
  { id: 3, label: 'Traitement', detail: 'En cours', current: true },
  { id: 4, label: 'Clôture', detail: 'À venir' },
]

const labs = [
  { test: 'Calcium ionisé', result: '1,08 mmol/L', range: '1,12–1,32', status: 'Bas' },
  { test: 'Hémoglobine', result: '11,8 g/dL', range: '12,0–16,0', status: 'Limite' },
  { test: 'Plaquettes', result: '186 G/L', range: '150–400', status: 'Normal' },
  { test: 'Fibrinogène', result: '2,4 g/L', range: '2,0–4,0', status: 'Normal' },
]

function PageMotion({ children, pageKey }) {
  return (
    <motion.div
      key={pageKey}
      initial={{ opacity: 0, y: 5 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, y: -4 }}
      transition={{ duration: 0.18, ease: [0.22, 1, 0.36, 1] }}
      className="page-motion"
    >
      {children}
    </motion.div>
  )
}

function Brand({ compact = false }) {
  return (
    <div className={`brand ${compact ? 'brand-compact' : ''}`}>
      <img src="/chu-logo.png" alt="CHU Mohammed VI Marrakech" />
      <div>
        <strong>Apheris</strong>
        {!compact && <span>Clinical OS</span>}
      </div>
    </div>
  )
}

function Login({ onAuthenticated }) {
  const [loading, setLoading] = useState(false)
  const [email, setEmail] = useState('salma.bennani@chu-ibnsina.ma')
  const [password, setPassword] = useState('demo-clinique')

  function submit(event) {
    event.preventDefault()
    if (!email || !password || loading) return
    setLoading(true)
    window.setTimeout(onAuthenticated, 720)
  }

  return (
    <main className="login-page">
      <section className="login-brand-panel" aria-label="Apheris Clinical OS">
        <Brand />
        <div className="login-statement">
          <span className="secure-label"><ShieldCheck /> Espace clinique sécurisé</span>
          <h1>La précision clinique,<br />sans le bruit.</h1>
          <p>Un environnement de travail conçu pour les décisions qui comptent.</p>
        </div>
        <div className="login-trust-row">
          <span><LockKeyhole /> Session chiffrée</span>
          <span><History /> Traçabilité complète</span>
        </div>
      </section>

      <section className="login-form-panel">
        <div className="login-form-wrap">
          <div className="mobile-brand"><Brand /></div>
          <header>
            <span className="overline">Accès professionnel</span>
            <h2>Bienvenue</h2>
            <p>Connectez-vous à votre espace de soins.</p>
          </header>

          <form onSubmit={submit} className="login-form">
            <div className="field-group">
              <label htmlFor="email">Adresse e-mail CHU</label>
              <div className="input-with-icon">
                <UserRound aria-hidden="true" />
                <Input id="email" type="email" autoComplete="username" value={email} onChange={(event) => setEmail(event.target.value)} />
              </div>
            </div>
            <div className="field-group">
              <div className="label-row"><label htmlFor="password">Code d’accès</label><button type="button">Code oublié ?</button></div>
              <div className="input-with-icon">
                <KeyRound aria-hidden="true" />
                <Input id="password" type="password" autoComplete="current-password" value={password} onChange={(event) => setPassword(event.target.value)} />
              </div>
            </div>
            <label className="trust-device"><input type="checkbox" /> <span>Faire confiance à cet appareil pendant 8 heures</span></label>
            <Button type="submit" size="lg" className="login-submit" disabled={loading}>
              {loading ? <span className="spinner" aria-hidden="true" /> : <ArrowRight data-icon="inline-end" />}
              {loading ? 'Ouverture de la session…' : 'Ouvrir la session'}
            </Button>
          </form>

          <button className="demo-profile" type="button" onClick={() => { setEmail('salma.bennani@chu-ibnsina.ma'); setPassword('demo-clinique') }}>
            <span className="demo-avatar">SB</span>
            <span><strong>Profil de démonstration</strong><small>Salma Bennani · Infirmière</small></span>
            <ChevronRight aria-hidden="true" />
          </button>
        </div>
        <footer>CHU Mohammed VI · Marrakech <span>Environnement de démonstration</span></footer>
      </section>
    </main>
  )
}

function AppRail({ view, setView, onLogout }) {
  return (
    <aside className="app-rail">
      <Brand compact />
      <nav aria-label="Navigation principale">
        {navigation.map((item) => {
          const Icon = item.icon
          return (
            <Tooltip key={item.id}>
              <TooltipTrigger asChild>
                <button
                  className={`rail-button ${view === item.id ? 'is-active' : ''}`}
                  aria-label={item.label}
                  aria-current={view === item.id ? 'page' : undefined}
                  disabled={item.disabled}
                  onClick={() => !item.disabled && setView(item.id)}
                >
                  <Icon />
                </button>
              </TooltipTrigger>
              <TooltipContent side="right">{item.label}{item.disabled ? ' · bientôt' : ''}</TooltipContent>
            </Tooltip>
          )
        })}
      </nav>
      <div className="rail-footer">
        <Tooltip><TooltipTrigger asChild><button className="rail-button" aria-label="Réglages"><Settings /></button></TooltipTrigger><TooltipContent side="right">Réglages</TooltipContent></Tooltip>
        <Tooltip><TooltipTrigger asChild><button className="rail-avatar" aria-label="Déconnexion" onClick={onLogout}>SB</button></TooltipTrigger><TooltipContent side="right">Fermer la session</TooltipContent></Tooltip>
      </div>
    </aside>
  )
}

function Topbar({ onSearch, onMenu }) {
  return (
    <header className="app-topbar">
      <div className="topbar-start">
        <Button variant="ghost" size="icon" className="mobile-menu" aria-label="Ouvrir le menu" onClick={onMenu}><Menu /></Button>
        <div className="location"><span>Unité d’aphérèse</span><strong>Marrakech · Box 02</strong></div>
      </div>
      <button className="command-search" onClick={onSearch}><Search /><span>Rechercher un patient, une séance…</span><kbd><Command /> K</kbd></button>
      <div className="topbar-actions">
        <Button variant="ghost" size="icon" aria-label="Notifications" className="notification-button"><Bell /><span /></Button>
        <div className="clinician"><span className="clinician-avatar">SB</span><span><strong>Salma Bennani</strong><small>Infirmière</small></span></div>
      </div>
    </header>
  )
}

function PatientBar({ onOpenRecord }) {
  return (
    <section className="patient-bar" aria-labelledby="patient-name">
      <div className="patient-primary"><span className="patient-monogram">YA</span><div><span>Patiente confirmée</span><h1 id="patient-name">Yasmine Amrani</h1></div></div>
      <dl>
        <div><dt>IPP</dt><dd>P-240184</dd></div>
        <div><dt>Âge</dt><dd>34 ans</dd></div>
        <div><dt>Groupe</dt><dd>O positif</dd></div>
        <div className="patient-indication"><dt>Indication</dt><dd>Myasthénie généralisée</dd></div>
      </dl>
      <Button variant="outline" size="lg" onClick={onOpenRecord}>Dossier <ChevronRight data-icon="inline-end" /></Button>
    </section>
  )
}

function SessionHero({ onFinish }) {
  return (
    <section className="session-hero">
      <div className="session-title-block">
        <Badge className="live-badge"><span className="live-dot" /> Séance en cours</Badge>
        <h2>Échanges plasmatiques thérapeutiques</h2>
        <p>APH-2026-0714 · Séance 4 sur 5 · Spectra Optia SO-042</p>
      </div>
      <div className="session-time"><span>Temps écoulé</span><strong>01:18:42</strong><small>Début à 08:42</small></div>
      <div className="session-progress">
        <div><span>Volume traité</span><strong>1 780 <small>/ 3 200 mL</small></strong></div>
        <Progress value={56} />
        <span>56 %</span>
      </div>
      <Button variant="secondary" size="lg" onClick={onFinish}>Terminer la séance</Button>
    </section>
  )
}

function StageRail({ selected, onSelect }) {
  return (
    <nav className="stage-rail" aria-label="Étapes de la séance">
      {stages.map((stage) => (
        <button key={stage.id} className={`${stage.current ? 'is-current' : ''} ${selected === stage.id ? 'is-selected' : ''}`} onClick={() => onSelect(stage.id)} aria-current={stage.current ? 'step' : undefined}>
          <span className="stage-icon">{stage.complete ? <Check /> : stage.id}</span>
          <span><strong>{stage.label}</strong><small>{stage.detail}</small></span>
        </button>
      ))}
    </nav>
  )
}

function MiniTrend() {
  return (
    <div className="mini-trend" role="img" aria-label="Fréquence cardiaque stable entre 8 h 42 et 10 h">
      <svg viewBox="0 0 640 150" preserveAspectRatio="none" aria-hidden="true">
        <path className="trend-zone" d="M0 45 H640 V112 H0 Z" />
        <path className="trend-grid" d="M0 45 H640 M0 78 H640 M0 112 H640" />
        <path className="trend-line" d="M0 89 C42 86 61 94 103 87 S171 72 220 78 S302 91 354 79 S433 60 485 67 S572 74 640 58" />
      </svg>
      <div className="trend-axis"><span>08:42</span><span>09:20</span><span>10:00</span></div>
    </div>
  )
}

function MonitoringPanel() {
  const vitals = [
    { label: 'Pression artérielle', value: '119/73', unit: 'mmHg', delta: 'Dans la cible' },
    { label: 'Fréquence cardiaque', value: '77', unit: 'bpm', delta: '+2 sur 20 min' },
    { label: 'SpO₂', value: '98', unit: '%', delta: 'Stable' },
    { label: 'Température', value: '36,7', unit: '°C', delta: 'Stable' },
  ]
  return (
    <section className="monitoring-panel" aria-labelledby="monitoring-title">
      <header className="section-header"><div><span className="overline">Dernier relevé · 10:00</span><h3 id="monitoring-title">Surveillance continue</h3></div><Badge variant="outline" className="stable-badge"><Check /> Paramètres stables</Badge></header>
      <div className="vitals-row">
        {vitals.map((vital) => <article key={vital.label}><span>{vital.label}</span><div><strong>{vital.value}</strong><small>{vital.unit}</small></div><em>{vital.delta}</em></article>)}
      </div>
      <div className="trend-heading"><div><HeartPulse /><span><strong>Fréquence cardiaque</strong><small>Plage cible 60–90 bpm</small></span></div><button><MoreHorizontal /><span className="sr-only">Options du graphique</span></button></div>
      <MiniTrend />
    </section>
  )
}

function AlertsPanel({ onObserve }) {
  return (
    <section className="alerts-panel" aria-labelledby="alerts-heading">
      <header className="section-header"><div><span className="overline">À traiter</span><h3 id="alerts-heading">Décisions cliniques</h3></div><span className="alert-count">2</span></header>
      <Alert className="clinical-alert warning-alert"><AlertTriangle /><div><AlertTitle>Calcium IV à vérifier</AlertTitle><AlertDescription>Échéance dans 12 minutes · consigne prophylactique active.</AlertDescription></div><Button variant="ghost" size="sm" onClick={onObserve}>Documenter</Button></Alert>
      <Alert className="clinical-alert abnormal-alert"><FlaskConical /><div><AlertTitle>Calcium ionisé bas</AlertTitle><AlertDescription><strong>1,08 mmol/L</strong> · référence minimale 1,12.</AlertDescription></div><Button variant="ghost" size="sm">Voir le bilan</Button></Alert>
    </section>
  )
}

function ProtocolPanel() {
  return (
    <aside className="protocol-panel" aria-labelledby="protocol-title">
      <header><span className="overline">Protocole actif</span><h3 id="protocol-title">Paramètres séance</h3></header>
      <dl>
        <div><dt>Débit sanguin</dt><dd>58 <small>mL/min</small></dd></div>
        <div><dt>Anticoagulant</dt><dd>ACD-A <small>1:12</small></dd></div>
        <div><dt>Substitution</dt><dd>Albumine <small>5 %</small></dd></div>
        <div><dt>Objectif</dt><dd>1,1 <small>vol. plasmatique</small></dd></div>
      </dl>
      <div className="device-status"><div><MonitorDot /><span><strong>Spectra Optia</strong><small>SO-042 · Auto-test conforme</small></span></div><Badge variant="outline">Connecté</Badge></div>
      <Button variant="outline" size="lg" className="full-button"><Gauge data-icon="inline-start" /> Ajuster les paramètres</Button>
    </aside>
  )
}

function Cockpit({ onOpenPatient }) {
  const [selectedStage, setSelectedStage] = useState(3)
  const [observationOpen, setObservationOpen] = useState(false)
  const [finishOpen, setFinishOpen] = useState(false)
  const [confirmed, setConfirmed] = useState(false)
  const [saved, setSaved] = useState(false)

  return (
    <PageMotion pageKey="cockpit">
      <PatientBar onOpenRecord={onOpenPatient} />
      <div className="cockpit-content">
        <SessionHero onFinish={() => setFinishOpen(true)} />
        <StageRail selected={selectedStage} onSelect={setSelectedStage} />
        <div className="clinical-workspace">
          <div className="clinical-main-column">
            <MonitoringPanel />
            <AlertsPanel onObserve={() => setObservationOpen(true)} />
          </div>
          <ProtocolPanel />
        </div>
      </div>

      <Sheet open={observationOpen} onOpenChange={setObservationOpen}>
        <SheetContent className="clinical-sheet">
          <SheetHeader><SheetTitle>Nouvelle observation</SheetTitle><SheetDescription>La note sera horodatée et associée à la séance APH-2026-0714.</SheetDescription></SheetHeader>
          <div className="sheet-body">
            <div className="field-group"><label htmlFor="observation-note">Observation clinique</label><Textarea id="observation-note" rows={6} placeholder="Décrire un fait observé…" /></div>
            <div className="field-group"><label htmlFor="tolerance">Tolérance</label><select id="tolerance"><option>Bonne</option><option>À surveiller</option><option>Incident</option></select></div>
          </div>
          <SheetFooter><Button onClick={() => { setSaved(true); setObservationOpen(false); window.setTimeout(() => setSaved(false), 3000) }}><Check data-icon="inline-start" /> Enregistrer l’observation</Button></SheetFooter>
        </SheetContent>
      </Sheet>

      <Dialog open={finishOpen} onOpenChange={setFinishOpen}>
        <DialogContent className="finish-dialog">
          <DialogHeader><span className="dialog-icon"><ShieldCheck /></span><DialogTitle>Terminer la séance ?</DialogTitle><DialogDescription>La surveillance infirmière sera figée et le dossier transmis pour validation médicale.</DialogDescription></DialogHeader>
          <label className="confirm-row"><input type="checkbox" checked={confirmed} onChange={(event) => setConfirmed(event.target.checked)} /><span>J’ai vérifié les constantes, volumes et incidents documentés.</span></label>
          <DialogFooter><Button variant="outline" onClick={() => setFinishOpen(false)}>Annuler</Button><Button variant="destructive" disabled={!confirmed}>Confirmer la clôture</Button></DialogFooter>
        </DialogContent>
      </Dialog>

      <AnimatePresence>{saved && <motion.div className="clinical-toast" initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, y: 8 }} role="status"><span><Check /></span><div><strong>Observation enregistrée</strong><small>Ajoutée au journal à 10:02</small></div><button onClick={() => setSaved(false)} aria-label="Fermer"><X /></button></motion.div>}</AnimatePresence>
    </PageMotion>
  )
}

function PatientRecord({ onBack }) {
  return (
    <PageMotion pageKey="patient">
      <header className="record-header">
        <div className="record-title"><Button variant="ghost" size="icon" onClick={onBack} aria-label="Retour à la séance"><ChevronRight className="back-icon" /></Button><span className="record-avatar">YA</span><div><span className="overline">Dossier longitudinal · IPP P-240184</span><h1>Yasmine Amrani</h1><p>34 ans · O positif · Myasthénie généralisée</p></div></div>
        <div className="record-actions"><Button variant="outline"><FileText data-icon="inline-start" /> Exporter</Button><Button><Plus data-icon="inline-start" /> Nouvelle note</Button></div>
      </header>

      <div className="record-content">
        <section className="record-summary">
          <article><span>Protocole</span><strong>EPT · 5 séances</strong><small>4 réalisées</small></article>
          <article><span>Prochaine séance</span><strong>16 juillet · 08:30</strong><small>Box 02</small></article>
          <article className="summary-attention"><span>Point d’attention</span><strong>Calcium ionisé bas</strong><small>1,08 mmol/L</small></article>
        </section>

        <Tabs defaultValue="timeline" className="record-tabs">
          <TabsList variant="line"><TabsTrigger value="timeline"><History /> Chronologie</TabsTrigger><TabsTrigger value="biology"><FlaskConical /> Biologie</TabsTrigger><TabsTrigger value="prescriptions"><Stethoscope /> Prescriptions</TabsTrigger></TabsList>
          <TabsContent value="timeline">
            <div className="record-grid">
              <section className="timeline-panel">
                <header className="section-header"><div><span className="overline">Juillet 2026</span><h3>Parcours récent</h3></div><Button variant="ghost" size="sm">Tout afficher</Button></header>
                <div className="timeline-list">
                  <article className="timeline-current"><span className="timeline-marker"><Activity /></span><div><span>Aujourd’hui · 10:02</span><h4>Séance d’aphérèse 4/5 en cours</h4><p>Constantes stables. Calcium IV à vérifier à 10:14.</p><small>Salma Bennani · Infirmière</small></div></article>
                  <article><span className="timeline-marker"><FlaskConical /></span><div><span>12 juillet · 16:40</span><h4>Bilan pré-séance validé</h4><p>Calcium ionisé sous la valeur de référence.</p><small>Dr H. Belkadi · Biologiste</small></div></article>
                  <article><span className="timeline-marker"><Stethoscope /></span><div><span>8 juillet · 11:20</span><h4>Prescription actualisée</h4><p>Objectif confirmé à 1,1 volume plasmatique.</p><small>Dr El Mansouri · Neurologue</small></div></article>
                </div>
              </section>
              <aside className="care-team-panel"><header><span className="overline">Équipe référente</span><h3>Coordination</h3></header><div className="care-person"><span>ME</span><div><strong>Dr M. El Mansouri</strong><small>Neurologue · Prescripteur</small></div></div><div className="care-person"><span>HB</span><div><strong>Dr H. Belkadi</strong><small>Biologiste</small></div></div><div className="care-person"><span>SB</span><div><strong>Salma Bennani</strong><small>Infirmière référente</small></div></div><Button variant="outline" className="full-button"><UsersRound /> Contacter l’équipe</Button></aside>
            </div>
          </TabsContent>
          <TabsContent value="biology">
            <section className="labs-panel"><header className="section-header"><div><span className="overline">Prélèvement du 12 juillet</span><h3>Derniers résultats</h3></div><Badge variant="outline">Validé · Dr H. Belkadi</Badge></header><Table><TableHeader><TableRow><TableHead>Analyse</TableHead><TableHead>Résultat</TableHead><TableHead>Référence</TableHead><TableHead>Interprétation</TableHead></TableRow></TableHeader><TableBody>{labs.map((lab) => <TableRow key={lab.test}><TableCell className="lab-name">{lab.test}</TableCell><TableCell>{lab.result}</TableCell><TableCell>{lab.range}</TableCell><TableCell><Badge variant="outline" className={lab.status === 'Bas' ? 'lab-low' : ''}>{lab.status}</Badge></TableCell></TableRow>)}</TableBody></Table></section>
          </TabsContent>
          <TabsContent value="prescriptions"><section className="empty-state"><span><FileText /></span><h3>Prescription active</h3><p>Échanges plasmatiques thérapeutiques · 5 séances · objectif 1,1 volume.</p><Button variant="outline">Ouvrir la prescription</Button></section></TabsContent>
        </Tabs>
      </div>
    </PageMotion>
  )
}

function CommandDialog({ open, onOpenChange, setView }) {
  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="command-dialog" showCloseButton={false}>
        <DialogTitle className="sr-only">Recherche clinique</DialogTitle>
        <div className="command-input"><Search /><input autoFocus placeholder="Rechercher une patiente, une séance, une action…" /><kbd>Échap</kbd></div>
        <div className="command-results"><span>Accès rapide</span><button onClick={() => { setView('cockpit'); onOpenChange(false) }}><Activity /><span><strong>Séance APH-2026-0714</strong><small>Yasmine Amrani · active</small></span><ChevronRight /></button><button onClick={() => { setView('patient'); onOpenChange(false) }}><CircleUserRound /><span><strong>Yasmine Amrani</strong><small>IPP P-240184</small></span><ChevronRight /></button></div>
      </DialogContent>
    </Dialog>
  )
}

function ClinicalShell({ onLogout }) {
  const [view, setView] = useState('cockpit')
  const [searchOpen, setSearchOpen] = useState(false)
  const [menuOpen, setMenuOpen] = useState(false)

  return (
    <TooltipProvider delayDuration={250}>
      <div className="clinical-shell">
        <AppRail view={view} setView={setView} onLogout={onLogout} />
        <Topbar onSearch={() => setSearchOpen(true)} onMenu={() => setMenuOpen(true)} />
        <main className="app-main">
          <AnimatePresence mode="wait">
            {view === 'patient' ? <PatientRecord onBack={() => setView('cockpit')} /> : <Cockpit onOpenPatient={() => setView('patient')} />}
          </AnimatePresence>
        </main>
        <nav className="mobile-bottom-nav" aria-label="Navigation mobile">{navigation.slice(0, 4).map((item) => { const Icon = item.icon; return <button key={item.id} disabled={item.disabled} className={view === item.id ? 'is-active' : ''} onClick={() => !item.disabled && setView(item.id)}><Icon /><span>{item.label.split(' ')[0]}</span></button> })}</nav>
      </div>
      <CommandDialog open={searchOpen} onOpenChange={setSearchOpen} setView={setView} />
      <Sheet open={menuOpen} onOpenChange={setMenuOpen}><SheetContent side="left" className="mobile-nav-sheet"><SheetHeader><SheetTitle><Brand /></SheetTitle><SheetDescription>Navigation clinique</SheetDescription></SheetHeader><nav>{navigation.map((item) => { const Icon = item.icon; return <button key={item.id} disabled={item.disabled} className={view === item.id ? 'is-active' : ''} onClick={() => { if (!item.disabled) { setView(item.id); setMenuOpen(false) } }}><Icon /><span>{item.label}</span>{item.disabled && <small>Bientôt</small>}</button> })}</nav><SheetFooter><Button variant="outline" onClick={onLogout}><LogOut /> Fermer la session</Button></SheetFooter></SheetContent></Sheet>
    </TooltipProvider>
  )
}

export function ClinicalOS() {
  const [authenticated, setAuthenticated] = useState(false)
  return authenticated ? <ClinicalShell onLogout={() => setAuthenticated(false)} /> : <Login onAuthenticated={() => setAuthenticated(true)} />
}
