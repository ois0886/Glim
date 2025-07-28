"use client"

import { useState } from "react";
import { AppSidebar } from "@/components/admin/app-sidebar";
import { VisitorAnalytics } from "@/components/admin/visitor-analytics";
import { UserManagement } from "@/components/admin/user-management";
import { PostManagement } from "@/components/admin/post-management";
import { Card, CardContent, CardHeader, CardTitle, CardDescription, CardFooter } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { UserDemographics } from "@/components/admin/user-demographics";
import dynamic from 'next/dynamic';

// Dynamically import CurationEditor to ensure it's client-side rendered
const CurationEditor = dynamic(
  () => import('@/components/admin/curation-editor').then(mod => mod.CurationEditor),
  { ssr: false }
);

// Dynamically import CurationList to ensure it's client-side rendered
const CurationList = dynamic(
  () => import('@/components/admin/curation-list').then(mod => mod.CurationList),
  { ssr: false }
);

export default function Dashboard() {
  const [activeSection, setActiveSection] = useState("users"); // Default to user management
  const [editingCurationId, setEditingCurationId] = useState<string | undefined>(undefined);

  const handleNewCuration = () => {
    setEditingCurationId(undefined);
    setActiveSection("curation-editor");
  };

  const handleEditCuration = (id: string) => {
    setEditingCurationId(id);
    setActiveSection("curation-editor");
  };

  const handleSaveSuccess = () => {
    setActiveSection("curation-list"); // Go back to list after saving
  };

  const renderContent = () => {
    switch (activeSection) {
      case "users":
        return <UserManagement />;
      case "posts":
        return <PostManagement />;
      case "analytics":
        return <VisitorAnalytics />;
      case "demographics":
        return <UserDemographics />;
      case "curation":
        return <CurationList onNewCuration={handleNewCuration} onEditCuration={handleEditCuration} />;
      case "curation-editor":
        return <CurationEditor curationId={editingCurationId} onSaveSuccess={handleSaveSuccess} />;
      case "curation-list":
        return <CurationList onNewCuration={handleNewCuration} onEditCuration={handleEditCuration} />;
      default:
        return <UserManagement />;
    }
  };

  return (
    <div className="flex min-h-screen w-full flex-col bg-muted/40">
      <AppSidebar activeSection={activeSection} setActiveSection={setActiveSection} />
      <div className="flex flex-col sm:gap-4 sm:py-4 sm:pl-60">
        <main className="grid flex-1 items-start gap-4 p-4 sm:px-6 sm:py-0 md:gap-8">
          {renderContent()}
        </main>
      </div>
    </div>
  );
}