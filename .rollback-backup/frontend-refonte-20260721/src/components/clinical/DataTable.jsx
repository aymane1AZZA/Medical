import { ChevronRight } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { EmptyState } from './ClinicalStates'

export function DataTable({ columns, rows, rowKey = 'id', onRowOpen, emptyTitle }) {
  if (!rows.length) return <EmptyState title={emptyTitle} />

  return (
    <div className="clinical-panel overflow-hidden">
      <div className="overflow-x-auto">
        <Table>
          <TableHeader>
            <TableRow>
              {columns.map((column) => <TableHead key={column.key}>{column.label}</TableHead>)}
              {onRowOpen && <TableHead className="w-12"><span className="sr-only">Actions</span></TableHead>}
            </TableRow>
          </TableHeader>
          <TableBody>
            {rows.map((row) => (
              <TableRow key={row[rowKey]}>
                {columns.map((column) => <TableCell key={column.key}>{column.render ? column.render(row) : row[column.key]}</TableCell>)}
                {onRowOpen && <TableCell><Button variant="ghost" size="icon-sm" onClick={() => onRowOpen(row)} aria-label={`Ouvrir le dossier de ${row.firstName} ${row.lastName}`}><ChevronRight /></Button></TableCell>}
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>
    </div>
  )
}
