import { AlertCircle, FileSearch, LockKeyhole, RefreshCw } from 'lucide-react'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { Button } from '@/components/ui/button'
import { Skeleton } from '@/components/ui/skeleton'

export function LoadingState({ rows = 5, label = 'Chargement des données cliniques' }) {
  return (
    <section aria-busy="true" aria-label={label} className="clinical-panel p-4">
      <span className="sr-only">{label}</span>
      <div className="flex flex-col gap-3">
        {Array.from({ length: rows }).map((_, index) => <Skeleton key={index} className="h-12 w-full rounded-md" />)}
      </div>
    </section>
  )
}

export function ErrorState({ message, onRetry }) {
  return (
    <Alert variant="destructive">
      <AlertCircle />
      <AlertTitle>Impossible de charger les données</AlertTitle>
      <AlertDescription className="flex flex-col items-start gap-3">
        <span>{message}</span>
        {onRetry && <Button variant="outline" size="sm" onClick={onRetry}><RefreshCw data-icon="inline-start" />Réessayer</Button>}
      </AlertDescription>
    </Alert>
  )
}

export function EmptyState({ title = 'Aucune donnée', description = 'Aucun élément ne correspond aux critères actuels.', action }) {
  return (
    <section className="clinical-panel grid min-h-56 place-items-center p-6 text-center">
      <div className="flex max-w-sm flex-col items-center gap-3">
        <span className="grid size-10 place-items-center rounded-lg bg-muted"><FileSearch className="size-5 text-muted-foreground" /></span>
        <div><h2 className="font-semibold">{title}</h2><p className="mt-1 text-sm text-muted-foreground">{description}</p></div>
        {action}
      </div>
    </section>
  )
}

export function PermissionDeniedState({ onBack }) {
  return (
    <main className="grid min-h-screen place-items-center bg-background p-4">
      <section className="clinical-panel w-full max-w-md p-6 text-center">
        <LockKeyhole className="mx-auto size-8 text-destructive" />
        <h1 className="mt-4 text-xl font-semibold">Accès non autorisé</h1>
        <p className="mt-2 text-sm text-muted-foreground">Votre rôle ne permet pas d’accéder à cet espace clinique. Cette tentative peut être tracée dans le journal d’audit.</p>
        <Button className="mt-5" onClick={onBack}>Retour à mon espace</Button>
      </section>
    </main>
  )
}
