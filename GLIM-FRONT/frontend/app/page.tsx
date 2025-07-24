"use client"

import { useState } from "react";
import { AppSidebar } from "@/components/admin/app-sidebar";
import { VisitorAnalytics } from "@/components/admin/visitor-analytics";
import { UserManagement } from "@/components/admin/user-management";
import { PostManagement } from "@/components/admin/post-management";
import { Card, CardContent, CardHeader, CardTitle, CardDescription, CardFooter } from "@/components/admin/ui/card";
import { Button } from "@/components/admin/ui/button";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/admin/ui/tabs";
import { UserDemographics } from "@/components/admin/user-demographics";
import { CurationEditor } from "@/components/admin/curation-editor";

export default function Dashboard() {
  const [activeSection, setActiveSection] = useState("users");

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
        return <CurationEditor />;
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