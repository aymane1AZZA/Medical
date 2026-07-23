import { Bell, Circle } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { ScrollArea } from '@/components/ui/scroll-area'
import { Sheet, SheetContent, SheetDescription, SheetHeader, SheetTitle, SheetTrigger } from '@/components/ui/sheet'
import { getNotifications } from '@/services/clinicalService'
import { StatusBadge } from '@/components/clinical/StatusBadge'

export function NotificationPanel() {
  const items = getNotifications()
  const unread = items.filter((item) => item.unread).length
  return (
    <Sheet>
      <SheetTrigger asChild><Button variant="ghost" size="icon" className="relative" aria-label={`${unread} notifications non lues`}><Bell />{unread > 0 && <span className="absolute right-1 top-1 grid size-4 place-items-center rounded-full bg-critical text-[9px] font-bold text-critical-foreground">{unread}</span>}</Button></SheetTrigger>
      <SheetContent className="w-full p-0 sm:max-w-md">
        <SheetHeader className="border-b p-5"><SheetTitle>Notifications</SheetTitle><SheetDescription>Alertes cliniques et opérationnelles récentes.</SheetDescription></SheetHeader>
        <ScrollArea className="h-[calc(100vh-100px)]"><div className="flex flex-col">
          {items.map((item) => <article key={item.id} className="grid grid-cols-[16px_1fr] gap-3 border-b p-4"><Circle className={`mt-1 size-3 ${item.unread ? 'fill-primary text-primary' : 'text-muted-foreground'}`} /><div><div className="flex flex-wrap items-center justify-between gap-2"><p className="font-semibold">{item.title}</p><StatusBadge status={item.severity} /></div><p className="mt-1 text-sm text-muted-foreground">{item.detail}</p><time className="mt-2 block text-xs text-muted-foreground">{item.time}</time></div></article>)}
        </div></ScrollArea>
      </SheetContent>
    </Sheet>
  )
}
