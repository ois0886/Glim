'use client'

import { useState, useEffect } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/admin/ui/card"
import { Button } from "@/components/admin/ui/button"
import { Badge } from "@/components/admin/ui/badge"
import { Input } from "@/components/admin/ui/input"
import { Checkbox } from "@/components/admin/ui/checkbox"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/admin/ui/tabs"
import { Search, Star, Heart, TrendingUp, Pin, PinOff, ArrowUp, ArrowDown } from "lucide-react"

// API 데이터 구조에 기반한 인터페이스 정의
interface Quote {
  quoteId: number;
  bookTitle: string;
  author: string;
  quoteViews: number | null;
  isPinned?: boolean;
  order?: number;
}

interface Book {
  itemId: number;
  title: string;
  author: string;
  cover: string;
  isPinned?: boolean;
  order?: number;
}

export function CurationEditor() {
  const [searchTerm, setSearchTerm] = useState("")
  const [availableQuotes, setAvailableQuotes] = useState<Quote[]>([])
  const [featuredQuotes, setFeaturedQuotes] = useState<Quote[]>([])
  const [selectedQuotes, setSelectedQuotes] = useState<number[]>([])

  const [availableBooks, setAvailableBooks] = useState<Book[]>([])
  const [featuredBooks, setFeaturedBooks] = useState<Book[]>([])
  const [selectedBooks, setSelectedBooks] = useState<number[]>([])

  // 글귀 및 도서 데이터 로드
  useEffect(() => {
    const fetchQuotes = async () => {
      try {
        const response = await fetch('http://localhost:50871/api/v1/quotes');
        const data = await response.json();
        setAvailableQuotes(data);
      } catch (error) {
        console.error("Error fetching quotes:", error);
      }
    };

    const fetchBooks = async () => {
      try {
        const response = await fetch('http://localhost:50858/api/v1/books/popular');
        const data = await response.json();
        setAvailableBooks(data);
      } catch (error) {
        console.error("Error fetching books:", error);
      }
    };

    fetchQuotes();
    fetchBooks();
  }, []);

  // 검색 필터링 로직
  const filteredQuotes = availableQuotes.filter(
    (quote) =>
      quote.bookTitle.toLowerCase().includes(searchTerm.toLowerCase()) ||
      quote.author.toLowerCase().includes(searchTerm.toLowerCase()),
  )

  const filteredBooks = availableBooks.filter(
    (book) =>
      book.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
      book.author.toLowerCase().includes(searchTerm.toLowerCase()),
  )

  // 글귀 선택 및 추천 로직
  const handleQuoteSelection = (quoteId: number) => {
    setSelectedQuotes((prev) => (prev.includes(quoteId) ? prev.filter((id) => id !== quoteId) : [...prev, quoteId]))
  }

  const handlePinQuotes = () => {
    const quotesToPin = availableQuotes.filter((quote) => selectedQuotes.includes(quote.quoteId));
    const newFeaturedQuotes = [...featuredQuotes, ...quotesToPin.map(q => ({...q, isPinned: true, order: featuredQuotes.length + 1}))];
    setFeaturedQuotes(newFeaturedQuotes.sort((a, b) => a.order! - b.order!));
    setAvailableQuotes(availableQuotes.filter((quote) => !selectedQuotes.includes(quote.quoteId)));
    setSelectedQuotes([]);
  };

  const handleUnpinQuote = (quoteId: number) => {
    const quoteToUnpin = featuredQuotes.find(q => q.quoteId === quoteId);
    if(quoteToUnpin) {
        setAvailableQuotes([...availableQuotes, {...quoteToUnpin, isPinned: false, order: undefined}]);
        setFeaturedQuotes(featuredQuotes.filter(q => q.quoteId !== quoteId));
    }
  };

  // 도서 선택 및 추천 로직
  const handleBookSelection = (bookId: number) => {
    setSelectedBooks((prev) => (prev.includes(bookId) ? prev.filter((id) => id !== bookId) : [...prev, bookId]))
  }

  const handlePinBooks = () => {
    const booksToPin = availableBooks.filter((book) => selectedBooks.includes(book.itemId));
    const newFeaturedBooks = [...featuredBooks, ...booksToPin.map(b => ({...b, isPinned: true, order: featuredBooks.length + 1}))];
    setFeaturedBooks(newFeaturedBooks.sort((a, b) => a.order! - b.order!));
    setAvailableBooks(availableBooks.filter((book) => !selectedBooks.includes(book.itemId)));
    setSelectedBooks([]);
  };

  const handleUnpinBook = (bookId: number) => {
    const bookToUnpin = featuredBooks.find(b => b.itemId === bookId);
    if(bookToUnpin) {
        setAvailableBooks([...availableBooks, {...bookToUnpin, isPinned: false, order: undefined}]);
        setFeaturedBooks(featuredBooks.filter(b => b.itemId !== bookId));
    }
  };
  
  // 순서 변경 로직
  const moveFeaturedItem = <T extends { order?: number }>(items: T[], index: number, direction: 'up' | 'down'): T[] => {
    const newItems = [...items];
    const item = newItems[index];
    const swapIndex = direction === 'up' ? index - 1 : index + 1;

    if (swapIndex < 0 || swapIndex >= newItems.length) return newItems;

    const swapItem = newItems[swapIndex];
    [newItems[index], newItems[swapIndex]] = [swapItem, item]; // Swap positions
    
    // Update order property
    newItems[index].order = index + 1;
    newItems[swapIndex].order = swapIndex + 1;

    return newItems.sort((a, b) => a.order! - b.order!); // Re-sort to ensure correctness
  };

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold tracking-tight">큐레이션 편집</h2>
        <p className="text-muted-foreground">메인 앱 피드에 표시될 추천 인용구와 책을 선택하고 관리합니다.</p>
      </div>

      <Tabs defaultValue="quotes" className="space-y-4">
        <TabsList>
          <TabsTrigger value="quotes">추천 인용구</TabsTrigger>
          <TabsTrigger value="books">추천 도서</TabsTrigger>
        </TabsList>

        <TabsContent value="quotes" className="space-y-4">
          <div className="grid gap-4 md:grid-cols-2">
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Pin className="w-5 h-5" />
                  현재 추천 인용구
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {featuredQuotes.map((quote, index) => (
                    <div key={quote.quoteId} className="p-4 border rounded-lg bg-muted/50">
                      <div className="flex items-start justify-between mb-2">
                        <Badge variant="outline" className="bg-blue-50 text-blue-700">
                          #{quote.order}
                        </Badge>
                        <div className="flex gap-1">
                          <Button variant="ghost" size="sm" onClick={() => setFeaturedQuotes(moveFeaturedItem(featuredQuotes, index, 'up'))}><ArrowUp className="w-4 h-4" /></Button>
                          <Button variant="ghost" size="sm" onClick={() => setFeaturedQuotes(moveFeaturedItem(featuredQuotes, index, 'down'))}><ArrowDown className="w-4 h-4" /></Button>
                          <Button variant="ghost" size="sm" onClick={() => handleUnpinQuote(quote.quoteId)}><PinOff className="w-4 h-4" /></Button>
                        </div>
                      </div>
                      <p className="text-sm leading-relaxed mb-2">{quote.bookTitle}</p>
                      <div className="flex items-center justify-between text-xs text-muted-foreground">
                        <span>{quote.author}</span>
                        <div className="flex items-center gap-1">
                          <Heart className="w-3 h-3 text-red-500" />
                          {quote.quoteViews}
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle>인용구 선택</CardTitle>
                <div className="relative">
                  <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
                  <Input
                    placeholder="인용구, 저자, 책 제목으로 검색..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="pl-8"
                  />
                </div>
              </CardHeader>
              <CardContent>
                <div className="space-y-3 max-h-96 overflow-y-auto">
                  {filteredQuotes.map((quote) => (
                    <div key={quote.quoteId} className="flex items-start gap-3 p-3 border rounded-lg hover:bg-muted/50">
                      <Checkbox
                        checked={selectedQuotes.includes(quote.quoteId)}
                        onCheckedChange={() => handleQuoteSelection(quote.quoteId)}
                      />
                      <div className="flex-1 min-w-0">
                        <p className="text-sm leading-relaxed mb-1">{quote.bookTitle}</p>
                        <div className="flex items-center justify-between text-xs text-muted-foreground">
                          <span>{quote.author}</span>
                          <div className="flex items-center gap-1">
                            <Heart className="w-3 h-3 text-red-500" />
                            {quote.quoteViews}
                          </div>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
                <div className="flex gap-2 mt-4">
                  <Button onClick={handlePinQuotes} disabled={selectedQuotes.length === 0} className="flex-1">
                    <Pin className="w-4 h-4 mr-2" />
                    선택한 인용구 추가 ({selectedQuotes.length})
                  </Button>
                </div>
              </CardContent>
            </Card>
          </div>
        </TabsContent>

        <TabsContent value="books" className="space-y-4">
          <div className="grid gap-4 md:grid-cols-2">
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Star className="w-5 h-5" />
                  현재 추천 도서
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {featuredBooks.map((book, index) => (
                    <div key={book.itemId} className="p-4 border rounded-lg bg-muted/50">
                      <div className="flex items-start justify-between mb-2">
                        <Badge variant="outline" className="bg-green-50 text-green-700">
                          #{book.order}
                        </Badge>
                        <div className="flex gap-1">
                          <Button variant="ghost" size="sm" onClick={() => setFeaturedBooks(moveFeaturedItem(featuredBooks, index, 'up'))}><ArrowUp className="w-4 h-4" /></Button>
                          <Button variant="ghost" size="sm" onClick={() => setFeaturedBooks(moveFeaturedItem(featuredBooks, index, 'down'))}><ArrowDown className="w-4 h-4" /></Button>
                          <Button variant="ghost" size="sm" onClick={() => handleUnpinBook(book.itemId)}><PinOff className="w-4 h-4" /></Button>
                        </div>
                      </div>
                      <div className="flex items-center gap-4">
                        <img src={book.cover} alt={book.title} className="w-16 h-auto rounded-md"/>
                        <div>
                            <h4 className="font-medium mb-1">{book.title}</h4>
                            <p className="text-sm text-muted-foreground">{book.author}</p>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle>도서 선택</CardTitle>
                <div className="relative">
                  <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
                  <Input
                    placeholder="책 제목, 저자로 검색..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="pl-8"
                  />
                </div>
              </CardHeader>
              <CardContent>
                <div className="space-y-3 max-h-96 overflow-y-auto">
                  {filteredBooks.map((book) => (
                    <div key={book.itemId} className="flex items-start gap-3 p-3 border rounded-lg hover:bg-muted/50">
                      <Checkbox
                        checked={selectedBooks.includes(book.itemId)}
                        onCheckedChange={() => handleBookSelection(book.itemId)}
                      />
                      <div className="flex-1 min-w-0 flex items-center gap-4">
                        <img src={book.cover} alt={book.title} className="w-12 h-auto rounded-md"/>
                        <div>
                            <h4 className="font-medium">{book.title}</h4>
                            <p className="text-sm text-muted-foreground">{book.author}</p>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
                <div className="flex gap-2 mt-4">
                  <Button onClick={handlePinBooks} disabled={selectedBooks.length === 0} className="flex-1">
                    <Star className="w-4 h-4 mr-2" />
                    선택한 도서 추가 ({selectedBooks.length})
                  </Button>
                </div>
              </CardContent>
            </Card>
          </div>
        </TabsContent>
      </Tabs>

      <div className="grid gap-4 md:grid-cols-3">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">추천 인용구</CardTitle>
            <Star className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{featuredQuotes.length}</div>
            <p className="text-xs text-muted-foreground">현재 활성화됨</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">추천 도서</CardTitle>
            <TrendingUp className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{featuredBooks.length}</div>
            <p className="text-xs text-muted-foreground">현재 활성화됨</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">평균 참여도</CardTitle>
            <Heart className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">85.2%</div>
            <p className="text-xs text-muted-foreground">
              <span className="text-green-600">+5.1%</span> 지난 주 대비
            </p>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
