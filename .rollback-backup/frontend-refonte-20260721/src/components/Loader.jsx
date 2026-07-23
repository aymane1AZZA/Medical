import { LoaderCircle } from 'lucide-react'

export default function Loader({ label = 'Chargement', fullScreen = false, inverse = false }) {
  const content = <div className={`flex items-center justify-center gap-2 text-sm font-medium ${inverse ? 'text-primary-foreground' : 'text-primary'}`} role="status"><LoaderCircle className="size-4 animate-spin" aria-hidden="true" /><span>{label}</span></div>
  return fullScreen ? <main className="grid min-h-screen place-items-center bg-background">{content}</main> : content
}
