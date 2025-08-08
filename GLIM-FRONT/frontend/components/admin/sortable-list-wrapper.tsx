/**
 * @file sortable-list-wrapper.tsx
 * @description 드래그 앤 드롭으로 정렬 가능한 목록을 렌더링하는 재사용 가능한 컴포넌트.
 *              각 항목의 편집, 삭제 기능도 포함합니다.
 *              주로 `curation-editor.tsx`에서 큐레이션 항목의 순서를 관리하는 데 사용됩니다.
 *
 * @param {CurationItem[]} items - 정렬할 항목들의 배열.
 * @param {React.Dispatch<React.SetStateAction<CurationItem[]>>} setItems - 항목 배열을 업데이트하는 함수.
 * @param {(id: string) => void} onRemoveItem - 항목 삭제 시 호출될 콜백 함수.
 * @param {(updatedItem: CurationItem) => void} onUpdateItem - 항목 업데이트 시 호출될 콜백 함수.
 *
 * @backend_note
 * 이 컴포넌트는 UI와 드래그 앤 드롭 로직만 담당하며 백엔드와 직접 통신하지 않습니다.
 * 데이터는 부모 컴포넌트로부터 `items` prop으로 받아와 관리하며,
 * 변경된 순서나 항목 데이터는 `setItems` 및 `onUpdateItem` 콜백을 통해 부모 컴포넌트로 전달됩니다.
 * 백엔드 연동은 이 컴포넌트를 사용하는 부모 컴포넌트에서 처리해야 합니다.
 */
"use client";

import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Trash2, GripVertical, Edit, Save, XCircle } from "lucide-react";

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
import { useState } from "react";

// Define CurationItem types (copied from curation-editor.tsx to avoid circular dependency)
interface Quote {
  id: string;
  type: "quote";
  content: string;
  author: string;
  source?: string; // Book title or other source
}

interface Book {
  id: string;
  type: "book";
  title: string;
  author: string;
  coverImage?: string;
}

export type CurationItem = Quote | Book; // Union type for items

interface SortableItemProps {
  item: CurationItem;
  onUpdate: (updatedItem: CurationItem) => void;
  onRemove: (id: string) => void;
}

function SortableItem({ item, onUpdate, onRemove }: SortableItemProps) {
  const { attributes, listeners, setNodeRef, transform, transition } = useSortable({ id: item.id });
  const [isEditing, setIsEditing] = useState(false);
  const [editedContent, setEditedContent] = useState("content" in item ? item.content : item.title);
  const [editedAuthor, setEditedAuthor] = useState(item.author);
  const [editedSource, setEditedSource] = useState("source" in item ? item.source || "" : "");

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
  };

  const handleSave = () => {
    if ("content" in item) {
      onUpdate({
        ...item,
        content: editedContent,
        author: editedAuthor,
        source: editedSource,
      });
    } else {
      onUpdate({
        ...item,
        title: editedContent,
        author: editedAuthor,
      });
    }
    setIsEditing(false);
  };

  const handleCancel = () => {
    setIsEditing(false);
    setEditedContent("content" in item ? item.content : item.title);
    setEditedAuthor(item.author);
    setEditedSource("source" in item ? item.source || "" : "");
  };

  return (
    <Card ref={setNodeRef} style={style} className="flex flex-col p-4 shadow-sm space-y-2">
      <div className="flex items-center justify-between">
        <div {...listeners} {...attributes} className="cursor-grab mr-2">
          <GripVertical className="h-5 w-5 text-muted-foreground" />
        </div>
        <div className="flex-1 flex items-center">
          <span className="text-xs font-semibold uppercase text-muted-foreground mr-2">
            {item.type === "quote" ? "인용구" : "도서"}
          </span>
          {isEditing ? (
            <Input
              value={editedContent}
              onChange={(e) => setEditedContent(e.target.value)}
              className="flex-1"
            />
          ) : (
            <p className="font-medium line-clamp-1 flex-1">{"content" in item ? item.content : item.title}</p>
          )}
        </div>
        <div className="flex space-x-1 ml-2">
          {isEditing ? (
            <>
              <Button variant="ghost" size="icon" onClick={handleSave}>
                <Save className="h-4 w-4" />
              </Button>
              <Button variant="ghost" size="icon" onClick={handleCancel}>
                <XCircle className="h-4 w-4" />
              </Button>
            </>
          ) : (
            <>
              <Button variant="ghost" size="icon" onClick={() => setIsEditing(true)}>
                <Edit className="h-4 w-4" />
              </Button>
              <Button variant="ghost" size="icon" onClick={() => onRemove(item.id)}>
                <Trash2 className="h-4 w-4 text-red-500" />
              </Button>
            </>
          )}
        </div>
      </div>
      <div className="flex items-center justify-between text-sm text-muted-foreground">
        {isEditing ? (
          <Input
            value={editedAuthor}
            onChange={(e) => setEditedAuthor(e.target.value)}
            placeholder="저자"
            className="flex-1 mr-2"
          />
        ) : (
          <p className="flex-1">{item.author}</p>
        )}
        {"source" in item && (
          isEditing ? (
            <Input
              value={editedSource}
              onChange={(e) => setEditedSource(e.target.value)}
              placeholder="출처"
              className="flex-1"
            />
          ) : (
            <p className="flex-1">{item.source}</p>
          )
        )}
      </div>
    </Card>
  );
}

interface SortableListWrapperProps {
  items: CurationItem[];
  setItems: React.Dispatch<React.SetStateAction<CurationItem[]>>;
  onUpdateItem: (updatedItem: CurationItem) => void;
  onRemoveItem: (id: string) => void;
}

export function SortableListWrapper({ items, setItems, onUpdateItem, onRemoveItem }: SortableListWrapperProps) {
  const sensors = useSensors(
    useSensor(PointerSensor),
    useSensor(KeyboardSensor, {
      coordinateGetter: sortableKeyboardCoordinates,
    })
  );

  const handleDragEnd = (event: DragEndEvent) => {
    const { active, over } = event;

    if (active.id !== over?.id) {
      setItems((prevItems) => {
        const oldIndex = prevItems.findIndex((item) => item.id === active.id);
        const newIndex = prevItems.findIndex((item) => item.id === over?.id);
        return arrayMove(prevItems, oldIndex, newIndex);
      });
    }
  };

  return (
    <DndContext
      sensors={sensors}
      collisionDetection={closestCenter}
      onDragEnd={handleDragEnd}
    >
      <SortableContext
        items={items.map(item => item.id)}
        strategy={verticalListSortingStrategy}
      >
        <div className="space-y-3">
          {items.map((item) => (
            <SortableItem key={item.id} item={item} onUpdate={onUpdateItem} onRemove={onRemoveItem} />
          ))}
        </div>
      </SortableContext>
    </DndContext>
  );
}