import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'

export function MetricCard({ label, value, detail, icon: Icon }) {
  return (
    <Card>
      <CardHeader className="grid grid-cols-[1fr_auto] items-center gap-3 pb-2">
        <CardTitle className="text-sm text-muted-foreground">{label}</CardTitle>
        {Icon && <Icon className="size-4 text-primary" aria-hidden="true" />}
      </CardHeader>
      <CardContent>
        <p className="tabular-clinical text-2xl font-semibold">{value}</p>
        {detail && <p className="mt-1 text-xs text-muted-foreground">{detail}</p>}
      </CardContent>
    </Card>
  )
}
