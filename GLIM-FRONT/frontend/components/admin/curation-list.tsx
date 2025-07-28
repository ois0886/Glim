/**
 * @file curation-list.tsx
 * @description '글:림' 관리자 대시보드에서 저장된 큐레이션 목록을 표시하고 관리하는 컴포넌트.
 *              큐레이션 목록을 드래그 앤 드롭으로 정렬하고, 편집하거나 삭제할 수 있습니다.
 *              `app/page.tsx`에서 '큐레이션 목록' 섹션에 동적으로 로드되어 사용됩니다.
 *
 * @param {() => void} onNewCuration - 새 큐레이션 생성 버튼 클릭 시 호출될 콜백 함수.
 * @param {(id: string) => void} onEditCuration - 큐레이션 편집 버튼 클릭 시 호출될 콜백 함수.
 *
 * @backend_note
 * - 현재 큐레이션 목록 데이터는 로컬 스토리지에서 로드 및 관리됩니다.
 * - **향후 백엔드 연동 시, 데이터 로드 (`loadCurations`) 및 삭제 (`handleDeleteCuration`) 로직을
 *   백엔드 API 호출로 변경해야 합니다.**
 */
"use client";

import { useState, useEffect } from "react";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Separator } from "@/components/ui/separator";
import { useToast } from "@/components/ui/use-toast";
import { Toaster } from "@/components/ui/toaster";
import { BookText, Quote, Image as ImageIcon, GripVertical, Edit, Trash2, PlusCircle } from "lucide-react";

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

// New interfaces for previews (copied from curation-editor.tsx)
interface QuotePreview {
  id: string;
  contentSnippet: string;
  author: string;
  source?: string;
}

interface BookPreview {
  id: string;
  title: string;
  author: string;
  coverImage?: string;
}

interface CurationMetadata {
  id: string;
  title: string;
  description: string;
  quotePreviews: QuotePreview[];
  bookPreviews: BookPreview[];
  quoteCount: number;
  bookCount: number;
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
    <Card ref={setNodeRef} style={style} className="flex flex-col p-3 shadow-sm">
      <div className="flex items-center justify-between">
        <div {...listeners} {...attributes} className="cursor-grab mr-2">
          <GripVertical className="h-5 w-5 text-muted-foreground" />
        </div>
        <div className="flex-1">
          <p className="font-medium line-clamp-1">{curation.title}</p>
          <p className="text-sm text-muted-foreground line-clamp-2">{curation.description}</p>
        </div>
        <div className="flex space-x-1 ml-2">
          <Button variant="ghost" size="icon" onClick={() => onEdit(curation.id)}>
            <Edit className="h-4 w-4" />
          </Button>
          <Button variant="ghost" size="icon" onClick={() => onDelete(curation.id)}>
            <Trash2 className="h-4 w-4" />
          </Button>
        </div>
      </div>

      <Separator className="my-2" />

      {/* Quote Previews */}
      <div className="space-y-1">
        <p className="text-sm font-semibold">인용구 ({curation.quoteCount}개)</p>
        {curation.quoteCount === 0 ? (
          <p className="text-xs text-muted-foreground">추가된 인용구 없음</p>
        ) : (
          <ul className="list-disc list-inside text-xs text-muted-foreground">
            {curation.quotePreviews.map((quote) => (
              <li key={quote.id} className="line-clamp-1">
                <Quote className="inline-block h-3 w-3 mr-1 text-muted-foreground" />
                &quot;{quote.contentSnippet}&quot; — {quote.author}
                {quote.source && `, ${quote.source}`}
              </li>
            ))}
            {curation.quoteCount > 3 && (
              <li className="text-right">+{curation.quoteCount - 3}개 더보기</li>
            )}
          </ul>
        )}
      </div>

      {/* Book Previews */}
      <div className="space-y-1 mt-2">
        <p className="text-sm font-semibold">도서 ({curation.bookCount}개)</p>
        {curation.bookCount === 0 ? (
          <p className="text-xs text-muted-foreground">추가된 도서 없음</p>
        ) : (
          <ul className="list-disc list-inside text-xs text-muted-foreground">
            {curation.bookPreviews.map((book) => (
              <li key={book.id} className="flex items-center line-clamp-1">
                {book.coverImage ? (
                  <img src={book.coverImage} alt="표지" className="h-5 w-5 object-cover mr-1 rounded" />
                ) : (
                  <ImageIcon className="h-5 w-5 mr-1 text-muted-foreground" />
                )}
                <BookText className="inline-block h-3 w-3 mr-1 text-muted-foreground" />
                {book.title} — {book.author}
              </li>
            ))}
            {curation.bookCount > 3 && (
              <li className="text-right">+{curation.bookCount - 3}개 더보기</li>
            )}
          </ul>
        )}
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

  const loadCurations = () => {
    const savedMetadata = localStorage.getItem("curations_metadata");
    if (savedMetadata) {
      const parsedMetadata: CurationMetadata[] = JSON.parse(savedMetadata);
      // Ensure quotePreviews and bookPreviews are always arrays
      const sanitizedMetadata = parsedMetadata.map(curation => ({
        ...curation,
        quotePreviews: curation.quotePreviews || [],
        bookPreviews: curation.bookPreviews || [],
        quoteCount: curation.quoteCount || 0,
        bookCount: curation.bookCount || 0,
      }));
      setCurations(sanitizedMetadata);
    }
  };

  const handleDeleteCuration = (id: string) => {
    // Remove from individual curation data
    localStorage.removeItem(`curation_${id}`);

    // Remove from metadata list
    const updatedCurations = curations.filter(curation => curation.id !== id);
    setCurations(updatedCurations);
    localStorage.setItem("curations_metadata", JSON.stringify(updatedCurations));

    toast({
      title: "큐레이션 삭제 완료",
      description: "선택한 큐레이션이 삭제되었습니다.",
    });
  };

  const sensors = useSensors(
    useSensor(PointerSensor),
    useSensor(KeyboardSensor, {
      coordinateGetter: sortableKeyboardCoordinates,
    })
  );

  const handleDragEnd = (event: DragEndEvent) => {
    const { active, over } = event;

    if (active.id !== over?.id) {
      setCurations((prevCurations) => {
        const oldIndex = prevCurations.findIndex((curation) => curation.id === active.id);
        const newIndex = prevCurations.findIndex((curation) => curation.id === over?.id);
        return arrayMove(prevCurations, oldIndex, newIndex);
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