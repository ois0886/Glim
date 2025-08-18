// @/components/admin/sortable-list-wrapper.tsx 파일의 올바른 내용 (예시)

"use client";

import React from 'react';
import { CurationItem } from '@/lib/types'; // CurationItem 타입을 올바르게 가져옵니다.
import { Button } from '@/components/ui/button';
import { GripVertical, Trash2 } from 'lucide-react';
// 이곳에 dnd-kit 이나 다른 정렬 라이브러리 관련 import가 필요할 수 있습니다.

interface SortableListWrapperProps {
  items: CurationItem[];
  setItems: React.Dispatch<React.SetStateAction<CurationItem[]>>;
  onRemoveItem: (id: string) => void;
  onUpdateItem: (id: string, data: any) => void; // 필요하다면 구현
}

// 중요: 컴포넌트 이름을 'SortableListWrapper'로 하고 'export' 해야 합니다.
export function SortableListWrapper({ items, setItems, onRemoveItem }: SortableListWrapperProps) {
  
  // 여기에 dnd-kit 이나 react-beautiful-dnd 같은
  // 드래그 앤 드롭 라이브러리를 사용한 로직이 와야 합니다.
  // 아래는 정렬 기능이 없는 단순 목록 렌더링 예시입니다.

  if (!items || items.length === 0) {
    return null;
  }
  
  return (
    <div className="space-y-3">
      {items.map((item, index) => (
        <div key={item.id} className="flex items-center p-3 border rounded-md bg-white dark:bg-gray-800 shadow-sm">
          <GripVertical className="h-5 w-5 mr-3 text-muted-foreground cursor-grab" />
          <div className="flex-1 min-w-0">
            {item.type === 'quote' ? (
              <>
                <p className="font-medium italic line-clamp-2">"{item.content}"</p>
                <p className="text-sm text-muted-foreground mt-1">출처: {item.source}</p>
              </>
            ) : (
              <>
                <p className="font-medium line-clamp-1">{item.title}</p>
                <p className="text-sm text-muted-foreground">{item.author}</p>
              </>
            )}
          </div>
          <Button
            variant="ghost"
            size="sm"
            className="ml-2 flex-shrink-0"
            onClick={() => onRemoveItem(item.id)}
          >
            <Trash2 className="h-4 w-4 text-red-500" />
          </Button>
        </div>
      ))}
    </div>
  );
}