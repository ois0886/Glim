"use client"

import { useState, Suspense } from "react";
import { useSearchParams, useRouter } from "next/navigation";
import { AppSidebar } from "@/components/admin/app-sidebar";
import { VisitorAnalytics } from "@/components/admin/visitor-analytics";
import { UserManagement } from "@/components/admin/user-management";
import { PostManagement } from "@/components/admin/post-management";
import { UserDemographics } from "@/components/admin/user-demographics";
import dynamic from 'next/dynamic';

const CurationEditor = dynamic(
  () => import('@/components/admin/curation-editor').then(mod => mod.CurationEditor),
  { ssr: false }
);

const CurationList = dynamic(
  () => import('@/components/admin/curation-list').then(mod => mod.CurationList),
  { ssr: false }
);

function DashboardContent() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const activeSection = searchParams.get("section") || "users";
  const editingCurationId = searchParams.get("curationId") || undefined;

  const navigateTo = (section: string, curationId?: string) => {
    const params = new URLSearchParams();
    params.set("section", section);
    if (curationId) {
      params.set("curationId", curationId);
    }
    router.push(`?${params.toString()}`);
  };

  const handleNewCuration = () => {
    navigateTo("curation-editor");
  };

  const handleEditCuration = (id: string) => {
    navigateTo("curation-editor", id);
  };

  const handleSaveSuccess = () => {
    navigateTo("curation-list");
  };

  const handleGoBack = () => {
    navigateTo("curation-list");
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
        return <CurationEditor curationId={editingCurationId} onSaveSuccess={handleSaveSuccess} onGoBack={handleGoBack} />;
      case "curation-list":
        return <CurationList onNewCuration={handleNewCuration} onEditCuration={handleEditCuration} />;
      default:
        return <UserManagement />;
    }
  };

  return (
    <div className="flex min-h-screen w-full flex-col bg-muted/40">
      <AppSidebar activeSection={activeSection} navigateTo={navigateTo} />
      <div className="flex flex-col sm:gap-4 sm:py-4 sm:pl-60">
        <main className="grid flex-1 items-start gap-4 p-4 sm:px-6 sm:py-0 md:gap-8">
          {renderContent()}
        </main>
      </div>
    </div>
  );
}

export default function Dashboard() {
  return (
    <Suspense fallback={<div>Loading...</div>}>
      <DashboardContent />
    </Suspense>
  );
}