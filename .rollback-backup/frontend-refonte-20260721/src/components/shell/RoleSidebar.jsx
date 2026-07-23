import { LogOut } from 'lucide-react'
import { NavLink } from 'react-router-dom'
import ChuLogo from '@/components/ChuLogo'
import { Button } from '@/components/ui/button'
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarRail,
} from '@/components/ui/sidebar'
import { useAuth } from '@/contexts/AuthContext'
import { navigationByRole } from '@/config/navigation'

export function RoleSidebar() {
  const { user, signOut } = useAuth()
  const groups = navigationByRole[user?.role] || []

  return (
    <Sidebar collapsible="icon">
      <SidebarHeader className="border-b border-sidebar-border p-3"><ChuLogo inverse /></SidebarHeader>
      <SidebarContent className="py-2">
        {groups.map((group) => (
          <SidebarGroup key={group.title}>
            <SidebarGroupLabel>{group.title}</SidebarGroupLabel>
            <SidebarGroupContent><SidebarMenu>
              {group.items.map((item) => {
                const Icon = item.icon
                return <SidebarMenuItem key={item.to}><NavLink to={item.to}>{({ isActive }) => <SidebarMenuButton asChild isActive={isActive} tooltip={item.label}><span><Icon /><span>{item.label}</span></span></SidebarMenuButton>}</NavLink></SidebarMenuItem>
              })}
            </SidebarMenu></SidebarGroupContent>
          </SidebarGroup>
        ))}
      </SidebarContent>
      <SidebarFooter className="border-t border-sidebar-border p-3">
        <Button variant="ghost" className="w-full justify-start text-sidebar-foreground hover:bg-sidebar-accent hover:text-sidebar-accent-foreground" onClick={signOut}><LogOut data-icon="inline-start" />Déconnexion</Button>
      </SidebarFooter>
      <SidebarRail />
    </Sidebar>
  )
}
