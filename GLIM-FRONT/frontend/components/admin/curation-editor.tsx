'use client'

import { useState, useEffect, useMemo } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/admin/ui/card"
import { Button } from "@/components/admin/ui/button"
import { Input } from "@/components/admin/ui/input"
import { Textarea } from "@/components/admin/ui/textarea"
import { Checkbox } from "@/components/admin/ui/checkbox"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/admin/ui/select"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/admin/ui/tabs"
import { Search, Star, Pin, PinOff, ArrowUp, ArrowDown, Loader2 } from "lucide-react"

// 데이터 타입 정의
interface Quote {
  quoteId: number;
  bookTitle: string;
  author: string;
}

interface Book {
  itemId: number;
  title: string;
  author: string;
  cover: string;
}

// 제네릭 아이템 타입
type Item = Quote | Book;

export function CurationEditor() {
  // 상태 관리
  const [curationTitle, setCurationTitle] = useState("")
  const [curationContent, setCurationContent] = useState("")
  const [curationTarget, setCurationTarget] = useState("")
  const [isSaving, setIsSaving] = useState(false)
  const [searchTerm, setSearchTerm] = useState("")

  const [availableQuotes, setAvailableQuotes] = useState<Quote[]>([])
  const [featuredQuotes, setFeaturedQuotes] = useState<Quote[]>([])
  const [selectedQuotes, setSelectedQuotes] = useState<number[]>([])

  const [availableBooks, setAvailableBooks] = useState<Book[]>([])
  const [featuredBooks, setFeaturedBooks] = useState<Book[]>([])
  const [selectedBooks, setSelectedBooks] = useState<number[]>([])

  // 데이터 로딩
  useEffect(() => {
    const fetchData = async (url: string, setter: React.Dispatch<React.SetStateAction<any[]>>) => {
      try {
        const response = await fetch(url);
        if (!response.ok) throw new Error(`Failed to fetch ${url}`);
        const data = await response.json();
        setter(data);
      } catch (error) {
        console.error("데이터 로딩 오류:", error);
      }
    };
    fetchData('/available-quotes.json', setAvailableQuotes);
    fetchData('/available-books.json', setAvailableBooks);
  }, []);

  // 아이템 관리 핸들러 (제네릭)
  const handleSelection = (id: number, selected: number[], setSelected: React.Dispatch<React.SetStateAction<number[]>>) => {
    setSelected(prev => prev.includes(id) ? prev.filter(i => i !== id) : [...prev, id]);
  };

  const handleAddItem = <T extends Item>(
    available: T[], setAvailable: React.Dispatch<React.SetStateAction<T[]>>,
    featured: T[], setFeatured: React.Dispatch<React.SetStateAction<T[]>>,
    selected: number[], setSelected: React.Dispatch<React.SetStateAction<number[]>>,
    idKey: keyof T
  ) => {
    const toAdd = available.filter(item => selected.includes(item[idKey] as number));
    setFeatured(prev => [...prev, ...toAdd]);
    setAvailable(prev => prev.filter(item => !selected.includes(item[idKey] as number)));
    setSelected([]);
  };

  const handleRemoveItem = <T extends Item>(
    id: number,
    available: T[], setAvailable: React.Dispatch<React.SetStateAction<T[]>>,
    featured: T[], setFeatured: React.Dispatch<React.SetStateAction<T[]>>,
    idKey: keyof T
  ) => {
    const toRemove = featured.find(item => item[idKey] === id);
    if (toRemove) {
      setFeatured(prev => prev.filter(item => item[idKey] !== id));
      setAvailable(prev => [...prev, toRemove]);
    }
  };

  const moveItem = <T,>(items: T[], index: number, direction: 'up' | 'down'): T[] => {
    const newItems = [...items];
    const item = newItems[index];
    const swapIndex = direction === 'up' ? index - 1 : index + 1;
    if (swapIndex < 0 || swapIndex >= newItems.length) return newItems;
    [newItems[index], newItems[swapIndex]] = [newItems[swapIndex], item];
    return newItems;
  };

  // 큐레이션 저장
  const handleSaveCuration = () => {
    setIsSaving(true);
    const curationData = { curationTitle, curationContent, curationTarget, featuredQuotes, featuredBooks };
    console.log("--- 큐레이션 저장 데이터 ---", JSON.stringify(curationData, null, 2));
    setTimeout(() => {
      setIsSaving(false);
      alert("큐레이션이 성공적으로 저장되었습니다! (콘솔 확인)");
    }, 1500);
  };

  // 검색 필터링
  const filteredQuotes = useMemo(() => availableQuotes.filter(q => 
    q.bookTitle.toLowerCase().includes(searchTerm.toLowerCase()) || 
    q.author.toLowerCase().includes(searchTerm.toLowerCase())
  ), [availableQuotes, searchTerm]);

  const filteredBooks = useMemo(() => availableBooks.filter(b => 
    b.title.toLowerCase().includes(searchTerm.toLowerCase()) || 
    b.author.toLowerCase().includes(searchTerm.toLowerCase())
  ), [availableBooks, searchTerm]);


  return (
    <div className="space-y-6 p-4 md:p-6">
      <div className="flex items-center justify-between">
        <h2 className="text-3xl font-bold tracking-tight">통합 큐레이션 편집기</h2>
        <Button onClick={handleSaveCuration} disabled={isSaving}>
          {isSaving ? <Loader2 className="mr-2 h-4 w-4 animate-spin" /> : null}
          {isSaving ? '저장 중...' : '큐레이션 저장'}
        </Button>
      </div>

      <CurationInfoCard {...{ curationTitle, setCurationTitle, curationContent, setCurationContent, curationTarget, setCurationTarget }} />

      <Tabs defaultValue="quotes" className="space-y-4">
        <TabsList>
          <TabsTrigger value="quotes">추천 인용구 관리</TabsTrigger>
          <TabsTrigger value="books">추천 도서 관리</TabsTrigger>
        </TabsList>
        
        <ContentTab 
            type="quotes"
            featuredItems={featuredQuotes}
            availableItems={filteredQuotes}
            selectedItems={selectedQuotes}
            onSelectionChange={(id) => handleSelection(id, selectedQuotes, setSelectedQuotes)}
            onAdd={() => handleAddItem(availableQuotes, setAvailableQuotes, featuredQuotes, setFeaturedQuotes, selectedQuotes, setSelectedQuotes, 'quoteId')}
            onRemove={(id) => handleRemoveItem(id, availableQuotes, setAvailableQuotes, featuredQuotes, setFeaturedQuotes, 'quoteId')}
            onMove={(index, dir) => setFeaturedQuotes(moveItem(featuredQuotes, index, dir))}
            setSearchTerm={setSearchTerm}
            searchTerm={searchTerm}
        />
        <ContentTab 
            type="books"
            featuredItems={featuredBooks}
            availableItems={filteredBooks}
            selectedItems={selectedBooks}
            onSelectionChange={(id) => handleSelection(id, selectedBooks, setSelectedBooks)}
            onAdd={() => handleAddItem(availableBooks, setAvailableBooks, featuredBooks, setFeaturedBooks, selectedBooks, setSelectedBooks, 'itemId')}
            onRemove={(id) => handleRemoveItem(id, availableBooks, setAvailableBooks, featuredBooks, setFeaturedBooks, 'itemId')}
            onMove={(index, dir) => setFeaturedBooks(moveItem(featuredBooks, index, dir))}
            setSearchTerm={setSearchTerm}
            searchTerm={searchTerm}
        />
      </Tabs>

      <StatsCards featuredQuotesCount={featuredQuotes.length} featuredBooksCount={featuredBooks.length} />
    </div>
  )
}

// --- 하위 컴포넌트들 ---

const CurationInfoCard = ({ curationTitle, setCurationTitle, curationContent, setCurationContent, curationTarget, setCurationTarget }) => (
  <Card>
    <CardHeader><CardTitle>큐레이션 정보</CardTitle></CardHeader>
    <CardContent className="space-y-4">
      <div>
        <label htmlFor="curationTitle" className="block text-sm font-medium text-gray-700 mb-1">큐레이션 제목</label>
        <Input id="curationTitle" placeholder="큐레이션의 제목을 입력하세요..." value={curationTitle} onChange={(e) => setCurationTitle(e.target.value)} />
      </div>
      <div>
        <label htmlFor="curationContent" className="block text-sm font-medium text-gray-700 mb-1">큐레이션 소개글</label>
        <Textarea id="curationContent" placeholder="독자들을 위한 소개글을 작성하세요..." value={curationContent} onChange={(e) => setCurationContent(e.target.value)} rows={4} />
      </div>
      <div>
        <label htmlFor="curationTarget" className="block text-sm font-medium text-gray-700 mb-1">발행 위치</label>
        <Select onValueChange={setCurationTarget} value={curationTarget}>
          <SelectTrigger className="w-full md:w-[240px]"><SelectValue placeholder="발행할 위치를 선택하세요" /></SelectTrigger>
          <SelectContent>
            <SelectItem value="main_feed">메인 피드</SelectItem>
            <SelectItem value="newsletter">뉴스레터</SelectItem>
            <SelectItem value="social_media">소셜 미디어</SelectItem>
          </SelectContent>
        </Select>
      </div>
    </CardContent>
  </Card>
);

const ContentTab = ({ type, featuredItems, availableItems, selectedItems, onSelectionChange, onAdd, onRemove, onMove, setSearchTerm, searchTerm }) => {
    const typeText = type === 'quotes' ? '인용구' : '도서';
    return (
        <TabsContent value={type} className="space-y-4">
            <div className="grid gap-6 md:grid-cols-2">
            <FeaturedItemsList type={type} items={featuredItems} onRemove={onRemove} onMove={onMove} typeText={typeText} />
            <AvailableItemsList type={type} items={availableItems} selectedItems={selectedItems} onSelectionChange={onSelectionChange} onAdd={onAdd} setSearchTerm={setSearchTerm} searchTerm={searchTerm} typeText={typeText} />
            </div>
        </TabsContent>
    );
};

const FeaturedItemsList = ({ type, items, onRemove, onMove, typeText }) => (
  <Card>
    <CardHeader><CardTitle className="flex items-center gap-2">{type === 'quotes' ? <Pin className="w-5 h-5" /> : <Star className="w-5 h-5" />} 선택된 {typeText} ({items.length})</CardTitle></CardHeader>
    <CardContent className="space-y-3 max-h-[500px] overflow-y-auto">
      {items.length === 0 && <p className="text-center text-muted-foreground py-4">오른쪽에서 {typeText}를 추가하세요.</p>}
      {items.map((item, index) => (
        <div key={type === 'quotes' ? item.quoteId : item.itemId} className="p-3 border rounded-lg bg-muted/40 flex items-center justify-between">
          <div className="flex-1 min-w-0">
            {type === 'quotes' ? <p className='font-semibold'>{`"${item.bookTitle}"`}</p> : <h4 className="font-semibold">{item.title}</h4>}
            <p className="text-sm text-muted-foreground">- {item.author}</p>
          </div>
          <div className="flex gap-1 ml-2">
            <Button variant="ghost" size="icon" onClick={() => onMove(index, 'up')} disabled={index === 0}><ArrowUp className="w-4 h-4" /></Button>
            <Button variant="ghost" size="icon" onClick={() => onMove(index, 'down')} disabled={index === items.length - 1}><ArrowDown className="w-4 h-4" /></Button>
            <Button variant="outline" size="icon" onClick={() => onRemove(type === 'quotes' ? item.quoteId : item.itemId)}><PinOff className="w-4 h-4 text-red-500" /></Button>
          </div>
        </div>
      ))}
    </CardContent>
  </Card>
);

const AvailableItemsList = ({ type, items, selectedItems, onSelectionChange, onAdd, setSearchTerm, searchTerm, typeText }) => (
  <Card>
    <CardHeader>
      <CardTitle>{typeText} 검색 및 추가</CardTitle>
      <div className="relative mt-2">
        <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
        <Input placeholder={`제목, 저자로 검색...`} value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} className="pl-9" />
      </div>
    </CardHeader>
    <CardContent>
      <div className="space-y-3 max-h-[420px] overflow-y-auto pr-2">
        {items.map(item => (
          <div key={type === 'quotes' ? item.quoteId : item.itemId} className="flex items-start gap-3 p-3 border rounded-lg hover:bg-muted/50 transition-colors">
            <Checkbox 
              id={`${type}-${type === 'quotes' ? item.quoteId : item.itemId}`}
              checked={selectedItems.includes(type === 'quotes' ? item.quoteId : item.itemId)}
              onCheckedChange={() => onSelectionChange(type === 'quotes' ? item.quoteId : item.itemId)}
            />
            <label htmlFor={`${type}-${type === 'quotes' ? item.quoteId : item.itemId}`} className="flex-1 min-w-0 cursor-pointer">
              {type === 'quotes' ? <p className='font-semibold'>{`"${item.bookTitle}"`}</p> : <h4 className="font-semibold">{item.title}</h4>}
              <p className="text-sm text-muted-foreground">- {item.author}</p>
            </label>
          </div>
        ))}
      </div>
      <Button onClick={onAdd} disabled={selectedItems.length === 0} className="w-full mt-4">
        {type === 'quotes' ? <Pin className="w-4 h-4 mr-2" /> : <Star className="w-4 h-4 mr-2" />}
        선택한 {typeText} 추가 ({selectedItems.length})
      </Button>
    </CardContent>
  </Card>
);

const StatsCards = ({ featuredQuotesCount, featuredBooksCount }) => (
  <div className="grid gap-4 md:grid-cols-2">
    <Card>
      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
        <CardTitle className="text-sm font-medium">추천 인용구 수</CardTitle>
        <Pin className="h-4 w-4 text-muted-foreground" />
      </CardHeader>
      <CardContent>
        <div className="text-2xl font-bold">{featuredQuotesCount}개</div>
        <p className="text-xs text-muted-foreground">현재 큐레이션에 포함됨</p>
      </CardContent>
    </Card>
    <Card>
      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
        <CardTitle className="text-sm font-medium">추천 도서 수</CardTitle>
        <Star className="h-4 w-4 text-muted-foreground" />
      </CardHeader>
      <CardContent>
        <div className="text-2xl font-bold">{featuredBooksCount}권</div>
        <p className="text-xs text-muted-foreground">현재 큐레이션에 포함됨</p>
      </CardContent>
    </Card>
  </div>
);