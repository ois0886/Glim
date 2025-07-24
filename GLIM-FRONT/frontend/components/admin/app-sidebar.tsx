"use client"

import { Users, FileText, BarChart3, PieChart, Star, BookOpen } from "lucide-react"
import {
  Sidebar,
  SidebarContent,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from "@/components/admin/ui/sidebar"

const menuItems = [
  {
    id: "users",
    title: "사용자 관리",
    icon: Users,
  },
  {
    id: "posts",
    title: "게시물 관리",
    icon: FileText,
  },
  {
    id: "analytics",
    title: "방문자 분석",
    icon: BarChart3,
  },
  {
    id: "demographics",
    title: "사용자 통계",
    icon: PieChart,
  },
  {
    id: "curation",
    title: "큐레이션 편집",
    icon: Star,
  },
]

interface AppSidebarProps {
  activeSection: string
  setActiveSection: (section: string) => void
}

export function AppSidebar({ activeSection, setActiveSection }: AppSidebarProps) {
  return (
    <Sidebar>
      <SidebarHeader className="border-b p-4">
        <div className="flex items-center gap-2">
          <BookOpen className="h-6 w-6" />
          <span className="text-lg font-semibold">글:림 Admin</span>
        </div>
      </SidebarHeader>
      <SidebarContent>
        <SidebarGroup>
          <SidebarGroupLabel>관리 메뉴</SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              {menuItems.map((item) => (
                <SidebarMenuItem key={item.id}>
                  <SidebarMenuButton isActive={activeSection === item.id} onClick={() => setActiveSection(item.id)}>
                    <item.icon />
                    <span>{item.title}</span>
                  </SidebarMenuButton>
                </SidebarMenuItem>
              ))}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>
    </Sidebar>
  )
}
