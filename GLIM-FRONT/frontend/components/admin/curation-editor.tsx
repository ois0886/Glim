/**
 * @file curation-editor.tsx
 * @description '글:림' 관리자 대시보드에서 큐레이션을 생성하고 편집하는 컴포넌트.
 *              API와 연동하여 큐레이션 데이터를 로드, 생성, 수정합니다.
 */
"use client";

import { useState, useEffect } from "react";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { v4 as uuidv4 } from 'uuid';
import dynamic from 'next/dynamic';

// --- API 호출 함수 임포트 ---
import { getCurationById, createCuration, updateCuration, CurationMutationPayload } from "@/lib/api/curations";
import { adminSearchBooks, adminSearchQuotes } from "@/lib/api/search";

// --- UI 컴포넌트 임포트 ---
import { Button } from "@/components/ui/button";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Separator } from "@/components/ui/separator";
import { useToast } from "@/components/ui/use-toast";
import { Toaster } from "@/components/ui/toaster";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { Label } from "@/components/ui/label";
import { PlusCircle, Search } from "lucide-react";

const SortableListWrapper = dynamic(
  () => import('@/components/admin/sortable-list-wrapper').then(mod => mod.SortableListWrapper),
  { ssr: false }
);

// --- 데이터 스키마 및 타입 정의 ---
const curationFormSchema = z.object({
  title: z.string().min(2, { message: "제목은 2자 이상이어야 합니다." }),
  description: z.string().min(10, { message: "소개글은 10자 이상이어야 합니다." }),
});

type CurationFormValues = z.infer<typeof curationFormSchema>;

interface Quote {
  id: string; // quoteId를 문자열로 저장
  type: "quote";
  content: string;
  source?: string;
}

interface Book {
  id: string; // bookId를 문자열로 저장
  type: "book";
  title: string;
  author: string;
  coverImage?: string;
}

export type CurationItem = Quote | Book;

interface CurationEditorProps {
  curationId?: string;
  onSaveSuccess: () => void;
  onGoBack: () => void;
}

export function CurationEditor({ curationId, onSaveSuccess, onGoBack }: CurationEditorProps) {
  const { toast } = useToast();
  const [isSubmitting, setIsSubmitting] = useState(false);
  
  const form = useForm<CurationFormValues>({
    resolver: zodResolver(curationFormSchema),
    defaultValues: { title: "", description: "" },
  });

  const [curationItems, setCurationItems] = useState<CurationItem[]>([]);
  const [searchResults, setSearchResults] = useState<CurationItem[]>([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedCategory, setSelectedCategory] = useState<'quote' | 'book' | null>(null);

  useEffect(() => {
    const fetchCurationData = async (id: string) => {
      try {
        const data = await getCurationById(id);
        const itemsSource = data.contents || [];
        const formattedItems: CurationItem[] = itemsSource.map((item) => {
          if (item.bookId) {
            return {
              id: String(item.bookId),
              type: 'book',
              title: item.bookTitle,
              author: item.author,
              coverImage: item.bookCoverUrl,
            };
          }
          if (item.quoteId) {
            return {
              id: String(item.quoteId),
              type: 'quote',
              content: item.content || `글귀 ID: ${item.quoteId}`,
              source: item.bookTitle,
            };
          }
          return null;
        }).filter((item): item is CurationItem => item !== null);

        form.reset({ title: data.title, description: data.description });
        setCurationItems(formattedItems);

      } catch (error) {
        console.error(`Failed to fetch curation with id ${id}:`, error);
        toast({
          title: "오류",
          description: "큐레이션 정보를 불러오는 데 실패했습니다.",
          variant: "destructive",
        });
      }
    };

    if (curationId) {
      fetchCurationData(curationId);
    } else {
      form.reset({ title: "", description: "" });
      setCurationItems([]);
    }
  }, [curationId, form, toast]);

  const handleAddItemFromSearch = (item: CurationItem) => {
    if (!curationItems.some(curationItem => curationItem.id === item.id)) {
      setCurationItems((prev) => [...prev, item]);
      toast({ title: "항목 추가", description: `${item.type === "quote" ? "글귀" : "도서"}가 추가되었습니다.` });
    } else {
      toast({ title: "알림", description: "이미 추가된 항목입니다.", variant: "destructive" });
    }
  };

  const handleSearch = async () => {
    if (!selectedCategory) {
      toast({ title: "카테고리를 먼저 선택해주세요.", variant: "destructive" });
      return;
    }

    try {
      let results: CurationItem[] = [];
      if (selectedCategory === 'book') {
        const bookResults = await adminSearchBooks(searchQuery);
        results = (bookResults || []).map(book => ({
          id: String(book.bookId),
          type: 'book',
          title: book.title,
          author: book.author,
          coverImage: book.coverUrl,
        }));
      } else {
        const quoteResults = await adminSearchQuotes(searchQuery);
        results = (quoteResults || []).map(quote => ({
          id: String(quote.quoteId),
          type: 'quote',
          content: quote.content,
          source: quote.bookTitle,
        }));
      }

      setSearchResults(results);
      if (results.length === 0) {
        toast({ title: "검색 결과 없음" });
      }
    } catch (error) {
      console.error("Search failed:", error);
      toast({ title: "검색 오류", description: "데이터를 불러오는 데 실패했습니다.", variant: "destructive" });
    }
  };
  
  async function onSubmit(values: CurationFormValues) {
    setIsSubmitting(true);
    
    const bookIds: number[] = curationItems
      .filter(item => item.type === 'book')
      .map(item => parseInt(item.id, 10))
      .filter(id => !isNaN(id));

    const quoteIds: number[] = curationItems
      .filter(item => item.type === 'quote')
      .map(item => parseInt(item.id, 10))
      .filter(id => !isNaN(id));

    let curationType: 'BOOK' | 'QUOTE' | undefined;
    if (bookIds.length > 0) {
      curationType = 'BOOK';
    } else if (quoteIds.length > 0) {
      curationType = 'QUOTE';
    } else {
      curationType = undefined;
    }

    const payload: CurationMutationPayload = {
      name: values.title,
      description: values.description,
      ...(curationType && { curationType }),
      bookIds,
      quoteIds,
    };

    try {
      if (curationId) {
        await updateCuration(curationId, payload);
        toast({ title: "✅ 큐레이션 수정 완료" });
      } else {
        await createCuration(payload);
        toast({ title: "✅ 큐레이션 생성 완료" });
      }
      onSaveSuccess();
    } catch (error) {
      console.error("Failed to save curation:", error);
      toast({ title: "❌ 저장 실패", description: "서버 오류가 발생했습니다. 콘솔을 확인하세요.", variant: "destructive" });
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <div className="space-y-6 p-4">
      <h2 className="text-2xl font-bold tracking-tight">큐레이션 에디터</h2>
      <p className="text-muted-foreground">큐레이션을 생성하거나 기존 큐레이션을 편집합니다.</p>
      <Separator />

      <Card>
        <CardHeader>
          <CardTitle>큐레이션 정보</CardTitle>
          <CardDescription>큐레이션의 제목과 소개글을 입력합니다.</CardDescription>
        </CardHeader>
        <CardContent>
          <Form {...form}>
            <div className="space-y-8">
              <FormField
                control={form.control}
                name="title"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>제목</FormLabel>
                    <FormControl>
                      <Input placeholder="큐레이션 제목을 입력하세요" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="description"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>소개글</FormLabel>
                    <FormControl>
                      <Textarea placeholder="큐레이션 소개글을 입력하세요" className="resize-y min-h-[100px]" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>
          </Form>
        </CardContent>
      </Card>

      <Separator />

      <Card>
        <CardHeader>
          <CardTitle>큐레이션 항목</CardTitle>
          <CardDescription>포함할 글귀와 도서를 관리하고 순서를 변경합니다.</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="space-y-4">
              <h3 className="text-lg font-semibold">선택된 큐레이션 항목</h3>
              <ScrollArea className="h-[500px] border rounded-md p-4 bg-gray-50 dark:bg-gray-900">
                {curationItems.length === 0 ? (
                  <p className="text-muted-foreground text-center py-10">항목이 없습니다. 우측에서 추가하세요.</p>
                ) : (
                  <SortableListWrapper
                    items={curationItems}
                    setItems={setCurationItems}
                    onUpdateItem={() => {}}
                    onRemoveItem={(idToRemove) => {
                      setCurationItems(prevItems => prevItems.filter(item => item.id !== idToRemove));
                      toast({ title: "항목 삭제", description: "큐레이션 항목이 삭제되었습니다." });
                    }}
                  />
                )}
              </ScrollArea>
            </div>

            <div className="space-y-4">
              <h3 className="text-lg font-semibold">글귀/도서 검색 및 추가</h3>
              <RadioGroup
                value={selectedCategory || ""}
                onValueChange={(value: "quote" | "book") => setSelectedCategory(value)}
                className="flex space-x-4"
              >
                <div className="flex items-center space-x-2">
                  <RadioGroupItem value="book" id="book" />
                  <Label htmlFor="book">도서</Label>
                </div>
                <div className="flex items-center space-x-2">
                  <RadioGroupItem value="quote" id="quote" />
                  <Label htmlFor="quote">글귀</Label>
                </div>
              </RadioGroup>

              <div className="flex space-x-2">
                <Input
                  placeholder="검색어 입력..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  onKeyPress={(e) => { if (e.key === 'Enter') handleSearch(); }}
                  disabled={!selectedCategory}
                />
                <Button onClick={handleSearch} disabled={!selectedCategory}>
                  <Search className="h-4 w-4 mr-2" /> 검색
                </Button>
              </div>
              <ScrollArea className="h-[500px] border rounded-md p-4">
                {searchResults.length > 0 ? (
                  <div className="space-y-3">
                    {searchResults.map((item) => (
                      <Card key={item.id} className="flex items-center p-3 shadow-sm">
                        <div className="flex-1">
                          {item.type === 'quote' ? (
                            <>
                              <p className="font-medium line-clamp-2 italic">"{item.content}"</p>
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
                          variant="outline"
                          size="sm"
                          onClick={() => handleAddItemFromSearch(item)}
                          className="ml-2"
                          disabled={curationItems.some(ci => ci.id === item.id)}
                        >
                          {curationItems.some(ci => ci.id === item.id) ? "추가됨" : "추가"}
                        </Button>
                      </Card>
                    ))}
                  </div>
                ) : (
                  <p className="text-muted-foreground text-center py-10">검색 결과가 없습니다.</p>
                )}
              </ScrollArea>
            </div>
          </div>
        </CardContent>
      </Card>

      <div className="flex justify-end space-x-2">
        <Button type="button" variant="outline" onClick={onGoBack}>뒤로가기</Button>
        <Button type="button" onClick={form.handleSubmit(onSubmit)} disabled={isSubmitting || curationItems.length === 0}>
          {isSubmitting ? "저장 중..." : "큐레이션 저장"}
        </Button>
      </div>
      <Toaster />
    </div>
  );
}
