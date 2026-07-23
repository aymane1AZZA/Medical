import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { Separator } from '@/components/ui/separator'
import { SidebarTrigger } from '@/components/ui/sidebar'
import { Tooltip, TooltipContent, TooltipTrigger } from '@/components/ui/tooltip'
import { useAuth } from '@/contexts/AuthContext'
import { roleProfession } from '@/config/navigation'
import { GlobalSearch } from './GlobalSearch'
import { NotificationPanel } from './NotificationPanel'

export function TopHeader() {
  const { user } = useAuth()
  const initials = user?.fullName?.split(' ').map((part) => part[0]).slice(0, 2).join('') || 'CH'
  return (
    <header className="sticky top-0 flex h-14 items-center gap-2 border-b bg-card px-3 sm:px-4">
      <Tooltip><TooltipTrigger asChild><SidebarTrigger /></TooltipTrigger><TooltipContent>Afficher ou réduire la navigation</TooltipContent></Tooltip>
      <Separator orientation="vertical" className="h-5" />
      <GlobalSearch />
      <div className="ml-auto flex items-center gap-2">
        <NotificationPanel />
        <Separator orientation="vertical" className="hidden h-6 sm:block" />
        <div className="hidden text-right sm:block"><p className="text-sm font-semibold leading-4">{user?.fullName}</p><p className="mt-1 text-xs text-muted-foreground">{roleProfession[user?.role] || user?.role}</p></div>
        <Avatar className="hidden size-8 sm:flex"><AvatarFallback>{initials}</AvatarFallback></Avatar>
      </div>
    </header>
  )
}
