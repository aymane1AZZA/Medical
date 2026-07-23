import { AlertTriangle, CheckCircle2, Clock3, Info, XCircle } from 'lucide-react'
import { Badge } from '@/components/ui/badge'

const statusConfig = {
  eligible: { label: 'Éligible', variant: 'success', icon: CheckCircle2 },
  pending: { label: 'À valider', variant: 'warning', icon: Clock3 },
  ineligible: { label: 'Non éligible', variant: 'destructive', icon: XCircle },
  normal: { label: 'Normal', variant: 'success', icon: CheckCircle2 },
  abnormal: { label: 'Anormal', variant: 'warning', icon: AlertTriangle },
  critical: { label: 'Critique', variant: 'critical', icon: AlertTriangle },
  active: { label: 'Active', variant: 'info', icon: Info },
  in_progress: { label: 'En cours', variant: 'info', icon: Clock3 },
  success: { label: 'Validé', variant: 'success', icon: CheckCircle2 },
  warning: { label: 'Attention', variant: 'warning', icon: AlertTriangle },
  info: { label: 'Information', variant: 'info', icon: Info },
  neutral: { label: 'Clôturé', variant: 'outline', icon: CheckCircle2 },
}

export function StatusBadge({ status, label }) {
  const config = statusConfig[status] || { label: label || status, variant: 'outline', icon: Info }
  const Icon = config.icon
  return (
    <Badge variant={config.variant}>
      <Icon data-icon="inline-start" />
      {label || config.label}
    </Badge>
  )
}
