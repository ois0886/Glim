/**
 * '글:림' 관리자 대시보드의 사이드바 메뉴 컴포넌트
 * 각 메뉴 항목의 `id`는 해당 관리 기능의 논리적 식별자입니다.
 * 이 컴포넌트 자체는 백엔드와 직접 통신하지 않으며, 프론트엔드 라우팅 및 UI 상태 관리에 사용됩니다.
 */
"use client"

import { Users, FileText, BarChart3, PieChart, Star } from "lucide-react"
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
} from "@/components/ui/sidebar"

/**
 * @file AppSidebar.tsx
 * @description '글:림' 관리자 대시보드의 사이드바 메뉴 컴포넌트.
 *              `app/page.tsx`에서 사용되며, 메뉴 클릭 시 해당 관리 섹션을 동적으로 로드합니다.
 *
 * @param {string} activeSection - 현재 활성화된 메뉴 ID.
 * @param {(section: string) => void} setActiveSection - 메뉴 활성화 함수.
 *
 * @backend_note
 * 이 컴포넌트는 UI만 담당하며 백엔드와 직접 통신하지 않습니다.
 * `menuItems`의 `id` (예: "users", "posts")는 각 관리 기능의 논리적 식별자입니다.
 * 백엔드 개발자는 이 ID를 기반으로 관련 API 엔드포인트나 데이터 모델을 유추할 수 있습니다.
 */

const menuItems = [
  {
    id: "users",
    title: "사용자 관리",
    icon: Users,
  },
  {
    id: "posts",
    title: "글귀 관리",
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