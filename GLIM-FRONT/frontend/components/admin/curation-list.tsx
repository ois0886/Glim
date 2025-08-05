/**
 * @file curation-list.tsx
 * @description '글:림' 관리자 대시보드에서 저장된 큐레이션 목록을 표시하고 관리하는 컴포넌트.
 *              큐레이션 목록을 드래그 앤 드롭으로 정렬하고, 편집하거나 삭제할 수 있습니다.
 *              `app/page.tsx`에서 '큐레이션 목록' 섹션에 동적으로 로드되어 사용됩니다.
 */
"use client";

import { useState, useEffect } from "react";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Separator } from "@/components/ui/separator";
import { useToast } from "@/components/ui/use-toast";
import { Toaster } from "@/components/ui/toaster";
import { BookText, Quote, GripVertical, Edit, Trash2, PlusCircle } from "lucide-react";

import {
  DndContext,
  closestCenter,
  KeyboardSensor,
  PointerSensor,
  useSensor,
  useSensors,
  DragEndEvent,
} from '@dnd-kit/core';
import {
  SortableContext,
  sortableKeyboardCoordinates,
  verticalListSortingStrategy,
  arrayMove,
  useSortable,
} from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';

// ✅ [수정됨] API 파일에서 올바른 함수들을 import 합니다.
import { getCurations, deleteCuration as apiDeleteCuration, ApiCuration } from "@/lib/api/curations";

// 프론트엔드에서 사용할 큐레이션 데이터 타입
interface CurationMetadata {
  id: string; // API의 curationItemId를 문자열로 변환하여 사용
  title: string;
  description: string;
  quoteCount: number;
  bookCount: number;
  contents: ApiCuration['contents']; 
}

interface SortableCurationItemProps {
  curation: CurationMetadata;
  onEdit: (id: string) => void;
  onDelete: (id: string) => void;
}

function SortableCurationItem({ curation, onEdit, onDelete }: SortableCurationItemProps) {
  const { attributes, listeners, setNodeRef, transform, transition } = useSortable({ id: curation.id });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
  };

  return (
    <Card ref={setNodeRef} style={style} className="flex flex-col bg-white dark:bg-gray-800 shadow-sm">
      <div className="flex items-start p-4">
        <div {...listeners} {...attributes} className="cursor-grab p-2 -ml-2 mt-1">
          <GripVertical className="h-5 w-5 text-muted-foreground" />
        </div>
        <div className="flex-1 ml-2">
          <p className="font-bold text-lg leading-tight">{curation.title}</p>
          <p className="text-sm text-muted-foreground mt-1">{curation.description}</p>
        </div>
        <div className="flex space-x-1">
          <Button variant="ghost" size="icon" onClick={() => onEdit(curation.id)}>
            <Edit className="h-4 w-4" />
          </Button>
          <Button variant="ghost" size="icon" onClick={() => onDelete(curation.id)}>
            <Trash2 className="h-4 w-4 text-red-500" />
          </Button>
        </div>
      </div>

      { (curation.bookCount > 0 || curation.quoteCount > 0) && Array.isArray(curation.contents) && (
        <div className="px-5 pt-3 pb-4 border-t">
          <div className="space-y-3">
            {curation.contents.map((item) => {
              if (item.bookId && item.quoteId === null) {
                return (
                  <div key={`book-${item.bookId}`} className="flex items-center space-x-3">
                    <img
                      src={item.bookCoverUrl || '/placeholder.png'}
                      alt={item.bookTitle}
                      className="h-16 w-12 rounded-sm object-cover shadow-md"
                    />
                    <div>
                      <p className="text-sm font-semibold leading-tight">{item.bookTitle}</p>
                      <p className="text-xs text-gray-500">{item.author}</p>
                    </div>
                  </div>
                );
              }
              if (item.quoteId) {
                 return (
                  <div key={`quote-${item.quoteId}`} className="flex items-center space-x-3">
                     <div className="w-12 h-16 flex items-center justify-center bg-gray-100 rounded-sm">
                        <Quote className="h-6 w-6 text-gray-400" />
                     </div>
                     <div>
                       <p className="text-sm font-semibold leading-tight italic">"{item.bookTitle}"에서 발췌</p>
                       <p className="text-xs text-gray-500">{item.author}</p>
                     </div>
                   </div>
                 );
              }
              return null;
            })}
          </div>
        </div>
      )}
      
      <div className="border-t px-4 py-3 flex items-center justify-start gap-6 text-sm text-muted-foreground">
        <div className="flex items-center gap-1.5">
          <Quote className="h-4 w-4" />
          <span>인용구 {curation.quoteCount}개</span>
        </div>
        <div className="flex items-center gap-1.5">
          <BookText className="h-4 w-4" />
          <span>도서 {curation.bookCount}개</span>
        </div>
      </div>
    </Card>
  );
}

interface CurationListProps {
  onNewCuration: () => void;
  onEditCuration: (id: string) => void;
}

export function CurationList({ onNewCuration, onEditCuration }: CurationListProps) {
  const [curations, setCurations] = useState<CurationMetadata[]>([]);
  const { toast } = useToast();

  useEffect(() => {
    loadCurations();
  }, []);

  const loadCurations = async () => {
    try {
      const apiData = await getCurations();
      const formattedData: CurationMetadata[] = apiData.map((curation) => {
        let bookCount = 0;
        let quoteCount = 0;

        if (Array.isArray(curation.contents)) {
          curation.contents.forEach(item => {
            if (item.quoteId !== null) {
              quoteCount++;
            } else if (item.bookId !== null) {
              bookCount++;
            }
          });
        }

        return {
          id: String(curation.curationItemId),
          title: curation.title,
          description: curation.description,
          bookCount,
          quoteCount,
          contents: curation.contents || [],
        };
      });
      setCurations(formattedData);
    } catch (error) {
      console.error("Failed to fetch curations:", error);
      toast({
        title: "오류",
        description: "큐레이션 목록을 불러오는 데 실패했습니다.",
        variant: "destructive",
      });
    }
  };

  // ✅ [수정됨] 삭제 핸들러가 올바른 API 함수를 호출하도록 변경
  const handleDeleteCuration = async (id: string) => {
    try {
      const numericId = parseInt(id, 10);
      if (isNaN(numericId)) {
        toast({ title: "오류", description: "유효하지 않은 ID입니다.", variant: "destructive" });
        return;
      }

      await apiDeleteCuration(numericId); // 올바른 API 함수 호출

      setCurations((prev) => prev.filter(curation => curation.id !== id));
      
      toast({
        title: "✅ 큐레이션 삭제 완료",
        description: "큐레이션이 성공적으로 삭제되었습니다.",
      });

    } catch (error) {
      console.error("Failed to delete curation:", error);
      const errorMsg = error.response?.data?.message || "삭제 중 서버 오류가 발생했습니다.";
      toast({
        title: "❌ 삭제 실패",
        description: errorMsg,
        variant: "destructive",
      });
    }
  };

  const sensors = useSensors(
    useSensor(PointerSensor),
    useSensor(KeyboardSensor, {
      coordinateGetter: sortableKeyboardCoordinates,
    })
  );

  const handleDragEnd = (event: DragEndEvent) => {
    const { active, over } = event;

    if (over && active.id !== over.id) {
      setCurations((prevCurations) => {
        const oldIndex = prevCurations.findIndex((curation) => curation.id === active.id);
        const newIndex = prevCurations.findIndex((curation) => curation.id === over.id);
        const newOrder = arrayMove(prevCurations, oldIndex, newIndex);
        
        console.log("New order:", newOrder.map(c => c.id));
        // TODO: 백엔드 순서 변경 API 연동 필요

        return newOrder;
      });
    }
  };

  return (
    <div className="space-y-6 p-4">
      <h2 className="text-2xl font-bold tracking-tight">큐레이션 목록</h2>
      <p className="text-muted-foreground">저장된 큐레이션을 확인하고 관리합니다.</p>
      <Separator />

      <Card>
        <CardHeader>
          <CardTitle>내 큐레이션</CardTitle>
          <CardDescription>저장된 큐레이션 목록입니다. 드래그하여 순서를 변경할 수 있습니다.</CardDescription>
        </CardHeader>
        <CardContent>
          <ScrollArea className="h-[600px] border rounded-md p-4 bg-gray-50 dark:bg-gray-900">
            {curations.length === 0 ? (
              <p className="text-muted-foreground text-center py-10">
                저장된 큐레이션이 없습니다. 새로운 큐레이션을 생성해보세요.
              </p>
            ) : (
              <DndContext
                sensors={sensors}
                collisionDetection={closestCenter}
                onDragEnd={handleDragEnd}
              >
                <SortableContext
                  items={curations.map(curation => curation.id)}
                  strategy={verticalListSortingStrategy}
                >
                  <div className="space-y-3">
                    {curations.map((curation) => (
                      <SortableCurationItem
                        key={curation.id}
                        curation={curation}
                        onEdit={onEditCuration}
                        onDelete={handleDeleteCuration}
                      />
                    ))}
                  </div>
                </SortableContext>
              </DndContext>
            )}
          </ScrollArea>
          <div className="mt-4 flex justify-center">
            <Button onClick={onNewCuration} variant="outline">
              <PlusCircle className="h-4 w-4 mr-2" /> 새 큐레이션 생성
            </Button>
          </div>
        </CardContent>
      </Card>
      <Toaster />
    </div>
  );
}