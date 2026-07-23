import { Outlet } from 'react-router-dom'
import { TooltipProvider } from '@/components/ui/tooltip'
import { SidebarInset, SidebarProvider } from '@/components/ui/sidebar'
import { RoleSidebar } from '@/components/shell/RoleSidebar'
import { TopHeader } from '@/components/shell/TopHeader'

export default function AppShell() {
  return (
    <TooltipProvider delayDuration={250}>
      <SidebarProvider>
        <RoleSidebar />
        <SidebarInset>
          <TopHeader />
          <Outlet />
        </SidebarInset>
      </SidebarProvider>
    </TooltipProvider>
  )
}
