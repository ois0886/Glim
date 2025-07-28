/**
 * 이 파일은 '글:림' 관리자 대시보드에서 큐레이션을 생성하고 편집하는 컴포넌트입니다.
 * 백엔드 개발자 참고: 현재 큐레이션 데이터는 로컬 스토리지에 저장됩니다.
 * 향후 실제 백엔드 API 연동 시, `onSubmit` 함수 내의 데이터 저장 로직을 수정해야 합니다.
 * 검색 기능은 `/public` 폴더의 JSON 파일을 사용합니다.
 */
"use client"; // 이 파일이 클라이언트 측에서 렌더링되어야 함을 Next.js에 알립니다.

// --- 라이브러리 임포트 ---
// 폼 유효성 검사를 위한 라이브러리들
import { zodResolver } from "@hookform/resolvers/zod"; // Zod 스키마를 React Hook Form에 연결
import { useForm } from "react-hook-form"; // 폼 상태 관리 및 유효성 검사 훅
import { z } from "zod"; // 데이터 유효성 검사 라이브러리 (스키마 정의)

// shadcn/ui에서 가져온 UI 컴포넌트들
import { Button } from "@/components/ui/button"; // 버튼 컴포넌트
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form"; // 폼 관련 컴포넌트 (폼 구조, 라벨, 에러 메시지 등)
import { Input } from "@/components/ui/input"; // 텍스트 입력 필드
import { Textarea } from "@/components/ui/textarea"; // 여러 줄 텍스트 입력 필드
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card"; // 카드 형태의 UI 컨테이너
import { ScrollArea } from "@/components/ui/scroll-area"; // 스크롤 가능한 영역
import { Separator } from "@/components/ui/separator"; // 구분선
import { useToast } from "@/components/ui/use-toast"; // 토스트(알림) 메시지 표시 훅
import { Toaster } from "@/components/ui/toaster"; // 토스트 메시지를 렌더링하는 컨테이너
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group"; // 라디오 버튼 그룹
import { Label } from "@/components/ui/label"; // 라벨 컴포넌트

// React 내장 훅 및 기타 유틸리티
import { useState, useEffect } from "react"; // 상태 관리 및 사이드 이펙트 처리 훅
import { PlusCircle, Search } from "lucide-react"; // 아이콘 라이브러리
import dynamic from 'next/dynamic'; // 클라이언트 측에서만 컴포넌트를 동적으로 로드하기 위함
import { v4 as uuidv4 } from 'uuid'; // 고유 ID 생성을 위한 라이브러리

// SortableListWrapper 컴포넌트를 동적으로 임포트합니다.
// SSR(서버 사이드 렌더링) 시 오류를 방지하고 클라이언트 측에서만 렌더링되도록 합니다.
// 이 컴포넌트는 드래그 앤 드롭 정렬 기능을 제공합니다.
const SortableListWrapper = dynamic(
  () => import('@/components/admin/sortable-list-wrapper').then(mod => mod.SortableListWrapper),
  { ssr: false }
);

// --- 데이터 스키마 및 타입 정의 ---
// 큐레이션 폼의 유효성 검사를 위한 Zod 스키마를 정의합니다.
// 제목은 최소 2자, 소개글은 최소 10자여야 합니다.
const curationFormSchema = z.object({
  title: z.string().min(2, {
    message: "제목은 2자 이상이어야 합니다.",
  }),
  description: z.string().min(10, {
    message: "소개글은 10자 이상이어야 합니다.",
  }),
});

// Zod 스키마로부터 폼 값의 타입을 추론합니다.
type CurationFormValues = z.infer<typeof curationFormSchema>;

// 큐레이션에 포함될 수 있는 '인용구' 항목의 타입을 정의합니다.
interface Quote {
  id: string;
  type: "quote";
  content: string;
  author: string;
  source?: string; // 책 제목 또는 기타 출처 (선택 사항)
}

// 큐레이션에 포함될 수 있는 '도서' 항목의 타입을 정의합니다.
interface Book {
  id: string;
  type: "book";
  title: string;
  author: string;
  coverImage?: string;
}

// 큐레이션 항목은 인용구 또는 도서가 될 수 있음을 정의하는 유니온 타입입니다.
export type CurationItem = Quote | Book;

// 전체 큐레이션 데이터의 구조를 정의합니다.
export interface CurationData {
  id: string;
  title: string;
  description: string;
  items: CurationItem[]; // 큐레이션에 포함된 인용구/도서 항목들
}

// 큐레이션 메타데이터 저장을 위한 미리보기 인터페이스 (인용구)
interface QuotePreview {
  id: string;
  contentSnippet: string; // 내용의 짧은 발췌본
  author: string;
  source?: string;
}

// 큐레이션 메타데이터 저장을 위한 미리보기 인터페이스 (도서)
interface BookPreview {
  id: string;
  title: string;
  author: string;
  coverImage?: string;
}

// 로컬 스토리지에 저장될 큐레이션 메타데이터의 구조를 정의합니다.
// 큐레이션 목록 페이지에서 미리보기 정보를 보여주기 위함입니다.
interface CurationMetadataForSave {
  id: string;
  title: string;
  description: string;
  quotePreviews: QuotePreview[]; // 인용구 미리보기 목록
  bookPreviews: BookPreview[];   // 도서 미리보기 목록
  quoteCount: number; // 인용구 개수
  bookCount: number;   // 도서 개수
}

// --- 컴포넌트 속성(Props) 정의 ---
interface CurationEditorProps {
  curationId?: string; // 선택 사항: 기존 큐레이션을 편집할 경우 해당 큐레이션의 ID
  onSaveSuccess: () => void; // 큐레이션 저장 성공 후 호출될 콜백 함수
}

/**
 * @file curation-editor.tsx
 * @description '글:림' 관리자 대시보드에서 큐레이션을 생성하고 편집하는 컴포넌트.
 *              큐레이션 정보 입력, 인용구/도서 검색 및 추가, 항목 순서 변경 기능을 제공합니다.
 *              `app/page.tsx`에서 '큐레이션 편집' 섹션에 동적으로 로드되어 사용됩니다.
 *
 * @param {string} [curationId] - 편집할 기존 큐레이션 ID (선택 사항).
 * @param {() => void} onSaveSuccess - 저장 성공 후 호출될 콜백 함수.
 *
 * @backend_note
 * - 현재 데이터는 로컬 스토리지에서 로드/저장되며, 검색은 `/public` 폴더의 JSON 파일을 사용합니다.
 * - **향후 백엔드 연동 시, 데이터 로드/저장/검색 로직을 백엔드 API 호출로 변경해야 합니다.**
 *   (`onSubmit`, `useEffect` 내 데이터 로드, `fetchData`, `handleSearch` 함수 확인 필요)
 */

export function CurationEditor({ curationId, onSaveSuccess }: CurationEditorProps) {
  // 토스트 메시지를 사용하기 위한 훅
  const { toast } = useToast();
  // 현재 큐레이션의 ID를 관리합니다. 기존 큐레이션이면 해당 ID를 사용하고, 아니면 새 ID를 생성합니다.
  const [currentCurationId, setCurrentCurationId] = useState<string>(curationId || uuidv4());

  // 폼 상태 및 유효성 검사를 관리하는 훅
  const form = useForm<CurationFormValues>({
    resolver: zodResolver(curationFormSchema), // Zod 스키마를 사용하여 유효성 검사 규칙을 정의
    defaultValues: { // 폼의 초기값 설정
      title: "",
      description: "",
    },
  });

  // 큐레이션에 포함된 항목들의 목록을 관리하는 상태
  const [curationItems, setCurationItems] = useState<CurationItem[]>([]);
  // 검색 결과 목록을 관리하는 상태
  const [searchResults, setSearchResults] = useState<CurationItem[]>([]);
  // 검색어 입력 필드의 값을 관리하는 상태
  const [searchQuery, setSearchQuery] = useState<string>("");
  // 검색할 카테고리(인용구 또는 도서)를 관리하는 상태
  const [selectedCategory, setSelectedCategory] = useState<'quote' | 'book' | null>(null);

  // --- 큐레이션 데이터 로드 (컴포넌트 마운트 시 또는 curationId 변경 시) ---
  useEffect(() => {
    // curationId가 제공되면 (즉, 기존 큐레이션을 편집하는 경우)
    if (curationId) {
      // 로컬 스토리지에서 해당 ID의 큐레이션 데이터를 가져옵니다.
      const savedCuration = localStorage.getItem(`curation_${curationId}`);
      if (savedCuration) {
        // 데이터를 파싱하여 폼과 큐레이션 항목 상태를 업데이트합니다.
        const data: CurationData = JSON.parse(savedCuration);
        form.reset({ title: data.title, description: data.description }); // 폼 필드 초기화
        setCurationItems(data.items); // 큐레이션 항목 설정
      } else {
        // 큐레이션을 찾을 수 없을 경우 에러 토스트 메시지를 표시합니다.
        toast({
          title: "오류",
          description: "해당 큐레이션을 찾을 수 없습니다.",
          variant: "destructive",
        });
      }
    } else {
      // 새로운 큐레이션을 생성하는 경우, 초기 더미 데이터를 설정합니다.
      setCurationItems([
        { id: uuidv4(), type: "quote", content: "삶이 있는 한 희망은 있다.", author: "키케로", source: "투스쿨룸 담화" },
        { id: uuidv4(), type: "book", title: "데미안", author: "헤르만 헤세" },
        { id: uuidv4(), type: "quote", content: "나는 생각한다. 고로 존재한다.", author: "르네 데카르트", source: "방법서설" },
      ]);
    }
  }, [curationId, form, toast]); // curationId, form, toast가 변경될 때마다 이 효과를 다시 실행합니다.

  // --- 큐레이션 항목 관리 함수들 ---

  // 새 항목을 직접 생성하여 큐레이션 목록에 추가합니다. (검색을 통하지 않고)
  const handleAddNewItem = () => {
    const newItem: CurationItem = {
      id: uuidv4(), // 고유 ID 생성
      type: "quote", // 기본값은 인용구로 설정 (나중에 편집 모드에서 변경 가능)
      content: "새 인용구 내용",
      author: "새 작가",
      source: "새 출처",
    };
    setCurationItems((prev) => [...prev, newItem]); // 기존 목록에 새 항목 추가
    toast({
      title: "새 항목 추가",
      description: "새로운 인용구 카드가 추가되었습니다. 편집하여 내용을 변경하세요.",
    });
  };

  // 큐레이션 목록에서 기존 항목을 업데이트합니다.
  const handleUpdateItem = (updatedItem: CurationItem) => {
    setCurationItems((prev) =>
      prev.map((item) => (item.id === updatedItem.id ? updatedItem : item)) // ID가 일치하면 업데이트된 항목으로 교체
    );
  };

  // 큐레이션 목록에서 항목을 삭제합니다.
  const handleRemoveItem = (id: string) => {
    setCurationItems((prev) => prev.filter((item) => item.id !== id)); // 해당 ID의 항목을 제외하고 필터링
    toast({
      title: "항목 삭제",
      description: "선택한 항목이 삭제되었습니다.",
    });
  };

  // 검색 결과에서 선택된 항목을 큐레이션 목록에 추가합니다.
  const handleAddItemFromSearch = (item: CurationItem) => {
    // 이미 큐레이션에 추가된 항목인지 확인
    if (!curationItems.some(curationItem => curationItem.id === item.id)) {
      // 새 ID를 할당하여 추가 (원본 ID가 다른 타입 간에 고유하지 않을 경우 충돌 방지)
      setCurationItems((prev) => [...prev, { ...item, id: uuidv4() }]);
      toast({
        title: "항목 추가",
        description: `${item.type === "quote" ? "인용구" : "도서"}가 큐레이션에 추가되었습니다.`, 
      });
    } else {
      // 이미 추가된 항목일 경우 알림
      toast({
        title: "알림",
        description: "이미 큐레이션에 추가된 항목입니다.",
        variant: "destructive",
      });
    }
  };

  // --- 검색 기능 관련 함수들 ---

  // `/public` 폴더의 JSON 파일에서 데이터를 가져옵니다.
  const fetchData = async (category: 'quote' | 'book') => {
    const url = category === 'quote' ? '/available-quotes.json' : '/available-books.json';
    const response = await fetch(url);
    if (!response.ok) {
      throw new Error(`Failed to fetch ${category}s`);
    }
    return response.json();
  };

  // 검색 버튼 클릭 시 또는 Enter 키 입력 시 호출되는 검색 함수
  const handleSearch = async () => {
    // 카테고리가 선택되지 않았으면 알림
    if (!selectedCategory) {
      toast({ title: "카테고리를 먼저 선택해주세요.", variant: "destructive" });
      return;
    }
    // 검색어가 비어있으면 알림
    if (!searchQuery.trim()) {
      toast({ title: "검색어를 입력해주세요.", variant: "destructive" });
      return;
    }

    console.log("Searching for:", searchQuery, "in category:", selectedCategory);

    try {
      // 선택된 카테고리에 따라 데이터를 가져옵니다.
      const allItems: CurationItem[] = await fetchData(selectedCategory);
      // 검색어에 따라 결과를 필터링합니다.
      const filteredResults = allItems.filter(item => {
        if (selectedCategory === "quote" && "content" in item) {
          // 인용구의 내용 또는 작가로 검색
          return item.content.includes(searchQuery) || item.author.includes(searchQuery);
        } else if (selectedCategory === "book" && "title" in item) {
          // 도서의 제목 또는 작가로 검색
          return item.title.includes(searchQuery) || item.author.includes(searchQuery);
        }
        return false;
      });
      setSearchResults(filteredResults);
      if (filteredResults.length === 0) {
        toast({ title: "검색 결과 없음", description: "입력하신 검색어에 대한 결과가 없습니다." });
      }
    } catch (error) {
      console.error("Search failed:", error);
      toast({ title: "검색 오류", description: "데이터를 불러오는 데 실패했습니다.", variant: "destructive" });
    }
  };

  // --- 폼 제출 (큐레이션 저장) 함수 ---
  // 폼 제출 시 호출되며, 큐레이션 데이터를 로컬 스토리지에 저장합니다.
  function onSubmit(values: CurationFormValues) {
    // 현재 큐레이션의 전체 데이터를 구성합니다.
    const curationData: CurationData = {
      id: currentCurationId,
      title: values.title,
      description: values.description,
      items: curationItems,
    };

    // 개별 큐레이션 데이터를 로컬 스토리지에 저장합니다.
    localStorage.setItem(`curation_${currentCurationId}`, JSON.stringify(curationData));

    // 모든 큐레이션의 메타데이터 목록을 업데이트하거나 추가합니다.
    let allCurationsMetadata: CurationMetadataForSave[] = [];
    const savedMetadata = localStorage.getItem("curations_metadata");
    if (savedMetadata) {
      allCurationsMetadata = JSON.parse(savedMetadata);
    }

    // 미리보기 정보와 항목 개수를 추출합니다.
    const quotePreviews: QuotePreview[] = [];
    const bookPreviews: BookPreview[] = [];
    let quoteCount = 0;
    let bookCount = 0;

    curationItems.forEach(item => {
      if (item.type === "quote") {
        quoteCount++;
        // 미리보기는 최대 3개까지만 저장
        if (quotePreviews.length < 3) {
          quotePreviews.push({
            id: item.id,
            contentSnippet: item.content.substring(0, 50) + (item.content.length > 50 ? "..." : ""),
            author: item.author,
            source: item.source,
          });
        }
      } else if (item.type === "book") {
        bookCount++;
        // 미리보기는 최대 3개까지만 저장
        if (bookPreviews.length < 3) {
          bookPreviews.push({
            id: item.id,
            title: item.title,
            author: item.author,
            coverImage: item.coverImage || "/placeholder-book-cover.png", // 표지 이미지가 없으면 기본 이미지 사용
          });
        }
      }
    });

    // 기존 큐레이션 메타데이터를 찾아서 업데이트하거나, 새로 추가합니다.
    const existingIndex = allCurationsMetadata.findIndex(meta => meta.id === currentCurationId);
    if (existingIndex > -1) {
      allCurationsMetadata[existingIndex] = { 
        id: currentCurationId, 
        title: values.title, 
        description: values.description, 
        quotePreviews, 
        bookPreviews, 
        quoteCount, 
        bookCount 
      };
    } else {
      allCurationsMetadata.push({ 
        id: currentCurationId, 
        title: values.title, 
        description: values.description, 
        quotePreviews, 
        bookPreviews, 
        quoteCount, 
        bookCount 
      });
    }
    // 업데이트된 메타데이터 목록을 로컬 스토리지에 저장합니다.
    localStorage.setItem("curations_metadata", JSON.stringify(allCurationsMetadata));

    toast({
      title: "큐레이션 저장 완료",
      description: "데이터가 로컬 스토리지에 저장되었습니다.",
    });
    onSaveSuccess(); // 부모 컴포넌트에 저장 성공을 알립니다.
  }

  // --- 컴포넌트 렌더링 ---
  return (
    <div className="space-y-6 p-4">
      {/* 페이지 제목 및 설명 */}
      <h2 className="text-2xl font-bold tracking-tight">큐레이션 에디터</h2>
      <p className="text-muted-foreground">새로운 큐레이션을 생성하거나 기존 큐레이션을 편집합니다.</p>

      <Separator /> {/* 구분선 */}

      {/* 큐레이션 정보 입력 섹션 */}
      <Card>
        <CardHeader>
          <CardTitle>큐레이션 정보</CardTitle>
          <CardDescription>큐레이션의 제목과 소개글을 입력합니다.</CardDescription>
        </CardHeader>
        <CardContent>
          {/* React Hook Form을 사용하여 폼을 관리합니다. */}
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
              {/* 제목 입력 필드 */}
              <FormField
                control={form.control}
                name="title"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>제목</FormLabel>
                    <FormControl>
                      <Input placeholder="큐레이션 제목을 입력하세요" {...field} />
                    </FormControl>
                    <FormMessage /> {/* 유효성 검사 에러 메시지 */}
                  </FormItem>
                )}
              />
              {/* 소개글 입력 필드 */}
              <FormField
                control={form.control}
                name="description"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>소개글</FormLabel>
                    <FormControl>
                      <Textarea
                        placeholder="큐레이션 소개글을 입력하세요"
                        className="resize-y min-h-[100px]"
                        {...field}
                      />
                    </FormControl>
                    <FormMessage /> {/* 유효성 검사 에러 메시지 */}
                  </FormItem>
                )}
              />
            </form>
          </Form>
        </CardContent>
      </Card>

      <Separator /> {/* 구분선 */}

      {/* 큐레이션 항목 관리 섹션 */}
      <Card>
        <CardHeader>
          <CardTitle>큐레이션 항목</CardTitle>
          <CardDescription>
            큐레이션에 포함할 인용구와 도서를 관리하고 순서를 변경합니다.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* 왼쪽 컬럼: 선택된 큐레이션 항목 목록 */}
            <div className="space-y-4">
              <h3 className="text-lg font-semibold">선택된 큐레이션 항목</h3>
              <ScrollArea className="h-[500px] border rounded-md p-4 bg-gray-50 dark:bg-gray-900">
                {curationItems.length === 0 ? (
                  <p className="text-muted-foreground text-center py-10">
                    큐레이션 항목이 없습니다. 우측에서 추가하거나 직접 생성하세요.
                  </p>
                ) : (
                  // SortableListWrapper 컴포넌트를 사용하여 드래그 앤 드롭 정렬 가능한 목록을 렌더링
                  <SortableListWrapper
                    items={curationItems} // 현재 큐레이션 항목들
                    setItems={setCurationItems} // 항목 순서 변경 시 호출될 함수
                    onRemoveItem={handleRemoveItem} // 항목 삭제 시 호출될 함수
                    onUpdateItem={handleUpdateItem} // 항목 업데이트 시 호출될 함수
                  />
                )}
              </ScrollArea>
              <div className="mt-4 flex justify-center">
                <Button onClick={handleAddNewItem} variant="outline">
                  <PlusCircle className="h-4 w-4 mr-2" /> 새 항목 직접 생성
                </Button>
              </div>
            </div>

            {/* 오른쪽 컬럼: 인용구/도서 검색 및 추가 */}
            <div className="space-y-4">
              <h3 className="text-lg font-semibold">인용구/도서 검색 및 추가</h3>
              <div className="mb-4">
                <Label htmlFor="category">카테고리 선택</Label>
                <RadioGroup
                  defaultValue={selectedCategory || undefined}
                  onValueChange={(value: "quote" | "book") => {
                    setSelectedCategory(value); // 선택된 카테고리 업데이트
                    setSearchResults([]); // 카테고리 변경 시 검색 결과 초기화
                    setSearchQuery(""); // 카테고리 변경 시 검색어 초기화
                  }}
                  className="flex space-x-4 mt-2"
                >
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="quote" id="quote" />
                    <Label htmlFor="quote">인용구</Label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <RadioGroupItem value="book" id="book" />
                    <Label htmlFor="book">도서</Label>
                  </div>
                </RadioGroup>
              </div>

              <div className="flex space-x-2">
                <Input
                  placeholder="검색어 입력..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  onKeyPress={(e) => {
                    if (e.key === 'Enter') {
                      handleSearch(); // Enter 키 입력 시 검색 실행
                    }
                  }}
                  disabled={!selectedCategory} // 카테고리가 선택되지 않으면 비활성화
                />
                <Button onClick={handleSearch} disabled={!selectedCategory}>
                  <Search className="h-4 w-4 mr-2" /> 검색
                </Button>
              </div>
              <ScrollArea className="h-[500px] border rounded-md p-4">
                {selectedCategory === null ? (
                  <p className="text-muted-foreground text-center py-10">
                    카테고리를 선택하여 검색을 시작하세요.
                  </p>
                ) : searchResults.length === 0 && searchQuery === "" ? (
                  <p className="text-muted-foreground text-center py-10">
                    검색어를 입력하여 인용구/도서를 찾아보세요.
                  </p>
                ) : searchResults.length === 0 && searchQuery !== "" ? (
                  <p className="text-muted-foreground text-center py-10">
                    검색 결과가 없습니다.
                  </p>
                ) : (
                  <div className="space-y-3">
                    {searchResults.map((item) => (
                      <Card key={item.id} className="flex items-center p-3 shadow-sm">
                        <div className="flex-1">
                          {"content" in item ? (
                            <>
                              <p className="font-medium line-clamp-1">{item.content}</p>
                              <p className="text-sm text-muted-foreground">
                                {item.author} {item.source && `(${item.source})`}
                              </p>
                            </>
                          ) : (
                            <>
                              <p className="font-medium line-clamp-1">{item.title}</p>
                              <p className="text-sm text-muted-foreground">{item.author}</p>
                            </>
                          )}
                        </div>
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => handleAddItemFromSearch(item)}
                          className="ml-2"
                          disabled={curationItems.some(curationItem => curationItem.id === item.id)} // 이미 추가된 항목이면 비활성화
                        >
                          {curationItems.some(curationItem => curationItem.id === item.id) ? "추가됨" : "추가"}
                        </Button>
                      </Card>
                    ))}
                  </div>
                )}
              </ScrollArea>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* 전체 폼 제출 버튼 */}
      <div className="flex justify-end">
        <Button type="submit" onClick={form.handleSubmit(onSubmit)}>
          큐레이션 저장
        </Button>
      </div>
      <Toaster /> {/* 토스트 메시지가 표시될 위치 */}
    </div>
  );
}