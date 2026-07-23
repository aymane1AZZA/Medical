import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'

export function AuditTrail({ entries }) {
  return (
    <Card>
      <CardHeader><CardTitle>Traçabilité récente</CardTitle><CardDescription>Actions sensibles associées au dossier.</CardDescription></CardHeader>
      <CardContent className="overflow-x-auto">
        <Table><TableHeader><TableRow><TableHead>Date</TableHead><TableHead>Professionnel</TableHead><TableHead>Action</TableHead><TableHead>Cible</TableHead></TableRow></TableHeader>
          <TableBody>{entries.map((entry) => <TableRow key={entry.id}><TableCell className="whitespace-nowrap tabular-clinical">{entry.time}</TableCell><TableCell><p className="font-medium">{entry.actor}</p><p className="text-xs text-muted-foreground">{entry.identifier}</p></TableCell><TableCell>{entry.action}</TableCell><TableCell className="font-mono text-xs">{entry.target}</TableCell></TableRow>)}</TableBody>
        </Table>
      </CardContent>
    </Card>
  )
}
