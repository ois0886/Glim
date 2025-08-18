'use client'

import * as React from 'react'
import { cva } from 'class-variance-authority'
import { cn } from '@/lib/utils'

const sidebarStyles = cva(
  'fixed top-0 left-0 h-full bg-card text-card-foreground border-r z-30 transition-all duration-300 ease-in-out',
  {
    variants: {
      state: {
        open: 'w-60',
        closed: 'w-14',
      },
    },
    defaultVariants: {
      state: 'open',
    },
  },
)

const Sidebar = React.forwardRef<HTMLDivElement, React.HTMLAttributes<HTMLDivElement>>(({ className, ...props }, ref) => {
  return <div ref={ref} className={cn(sidebarStyles(), className)} {...props} />
})
Sidebar.displayName = 'Sidebar'

const SidebarHeader = React.forwardRef<HTMLDivElement, React.HTMLAttributes<HTMLDivElement>>(
  ({ className, ...props }, ref) => {
    return <div ref={ref} className={cn('p-4', className)} {...props} />
  },
)
SidebarHeader.displayName = 'SidebarHeader'

const SidebarContent = React.forwardRef<HTMLDivElement, React.HTMLAttributes<HTMLDivElement>>(
  ({ className, ...props }, ref) => {
    return <div ref={ref} className={cn('flex-1 overflow-y-auto', className)} {...props} />
  },
)
SidebarContent.displayName = 'SidebarContent'

const SidebarGroup = React.forwardRef<HTMLDivElement, React.HTMLAttributes<HTMLDivElement>>(
  ({ className, ...props }, ref) => {
    return <div ref={ref} className={cn('p-2', className)} {...props} />
  },
)
SidebarGroup.displayName = 'SidebarGroup'

const SidebarGroupLabel = React.forwardRef<HTMLDivElement, React.HTMLAttributes<HTMLDivElement>>(
  ({ className, ...props }, ref) => {
    return <div ref={ref} className={cn('px-2 py-1 text-xs font-semibold text-muted-foreground', className)} {...props} />
  },
)
SidebarGroupLabel.displayName = 'SidebarGroupLabel'

const SidebarGroupContent = React.forwardRef<HTMLDivElement, React.HTMLAttributes<HTMLDivElement>>(
  ({ className, ...props }, ref) => {
    return <div ref={ref} className={cn('mt-1', className)} {...props} />
  },
)
SidebarGroupContent.displayName = 'SidebarGroupContent'

const SidebarMenu = React.forwardRef<HTMLUListElement, React.HTMLAttributes<HTMLUListElement>>(
  ({ className, ...props }, ref) => {
    return <ul ref={ref} className={cn('space-y-1', className)} {...props} />
  },
)
SidebarMenu.displayName = 'SidebarMenu'

const SidebarMenuItem = React.forwardRef<HTMLLIElement, React.HTMLAttributes<HTMLLIElement>>(
  ({ className, ...props }, ref) => {
    return <li ref={ref} className={cn('', className)} {...props} />
  },
)
SidebarMenuItem.displayName = 'SidebarMenuItem'

const sidebarMenuButtonStyles = cva(
  'flex items-center gap-3 rounded-md px-3 py-2 text-sm font-medium transition-colors',
  {
    variants: {
      isActive: {
        true: 'bg-primary text-primary-foreground',
        false: 'hover:bg-muted',
      },
    },
    defaultVariants: {
      isActive: false,
    },
  },
)

const SidebarMenuButton = React.forwardRef<
  HTMLButtonElement,
  React.ButtonHTMLAttributes<HTMLButtonElement> & { isActive?: boolean }
>(({ className, isActive, ...props }, ref) => {
  return <button ref={ref} className={cn(sidebarMenuButtonStyles({ isActive }), className)} {...props} />
})
SidebarMenuButton.displayName = 'SidebarMenuButton'

export {
  Sidebar,
  SidebarHeader,
  SidebarContent,
  SidebarGroup,
  SidebarGroupLabel,
  SidebarGroupContent,
  SidebarMenu,
  SidebarMenuItem,
  SidebarMenuButton,
}