"use client"

import { useState, useEffect } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Input } from "@/components/ui/input"
import { Checkbox } from "@/components/ui/checkbox"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Textarea } from "@/components/ui/textarea"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Search, Star, Heart, TrendingUp, Pin, PinOff, ArrowUp, ArrowDown, Loader2 } from "lucide-react"

interface Quote {
  id: string
  content: string
  author: string
  book: string
  likes: number
  isPinned: boolean
  order?: number
}

interface Book {
  id: string
  title: string
  author: string
  description: string
  isPinned: boolean
  order?: number
}

export function CurationEditor() {
  const [searchTerm, setSearchTerm] = useState("")
  const [selectedQuotes, setSelectedQuotes] = useState<string[]>([])
  const [selectedBooks, setSelectedBooks] = useState<string[]>([])
  const [curationTitle, setCurationTitle] = useState("이번 주, 당신의 마음을 울릴 문장들")
  const [curationContent, setCurationContent] = useState(
    "지친 일상에 위로와 영감을 선사할 책 속의 문장과 특별한 도서들을 만나보세요.",
  )
  const [curationDestination, setCurationDestination] = useState("main")

  const [featuredQuotes, setFeaturedQuotes] = useState<Quote[]>([])
  const [availableQuotes, setAvailableQuotes] = useState<Quote[]>([])
  const [featuredBooks, setFeaturedBooks] = useState<Book[]>([])
  const [availableBooks, setAvailableBooks] = useState<Book[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await fetch("/data/curation.json")
        if (!response.ok) throw new Error("데이터를 불러오지 못했습니다.")
        const data = await response.json()
        setFeaturedQuotes(data.featuredQuotes)
        setAvailableQuotes(data.availableQuotes)
        setFeaturedBooks(data.featuredBooks)
        setAvailableBooks(data.availableBooks)
      } catch (err) {
        setError(err instanceof Error ? err.message : "알 수 없는 오류가 발생했습니다.")
      } finally {
        setIsLoading(false)
      }
    }
    fetchData()
  }, [])

  const filteredQuotes = availableQuotes.filter(
    (quote) =>
      quote.content.toLowerCase().includes(searchTerm.toLowerCase()) ||
      quote.author.toLowerCase().includes(searchTerm.toLowerCase()) ||
      quote.book.toLowerCase().includes(searchTerm.toLowerCase()),
  )

  const filteredBooks = availableBooks.filter(
    (book) =>
      book.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
      book.author.toLowerCase().includes(searchTerm.toLowerCase()) ||
      book.description.toLowerCase().includes(searchTerm.toLowerCase()),
  )

  const handleQuoteSelection = (quoteId: string) => {
    setSelectedQuotes((prev) => (prev.includes(quoteId) ? prev.filter((id) => id !== quoteId) : [...prev, quoteId]))
  }

  const handleBookSelection = (bookId: string) => {
    setSelectedBooks((prev) => (prev.includes(bookId) ? prev.filter((id) => id !== bookId) : [...prev, bookId]))
  }

  const handleAddQuotes = () => {
    const quotesToAdd = availableQuotes.filter((q) => selectedQuotes.includes(q.id))
    if (quotesToAdd.length === 0) return

    const newFeaturedQuotes = [
      ...featuredQuotes,
      ...quotesToAdd.map((q, index) => ({
        ...q,
        isPinned: true,
        order: featuredQuotes.length + index + 1,
      })),
    ]
    setFeaturedQuotes(newFeaturedQuotes.sort((a, b) => (a.order ?? 0) - (b.order ?? 0)))

    const newAvailableQuotes = availableQuotes.filter((q) => !selectedQuotes.includes(q.id))
    setAvailableQuotes(newAvailableQuotes)

    setSelectedQuotes([])
  }

  const handleRemoveQuote = (quoteId: string) => {
    const quoteToRemove = featuredQuotes.find((q) => q.id === quoteId)
    if (!quoteToRemove) return

    const newFeaturedQuotes = featuredQuotes.filter((q) => q.id !== quoteId).map((q, index) => ({ ...q, order: index + 1 }))
    setFeaturedQuotes(newFeaturedQuotes)

    setAvailableQuotes([...availableQuotes, { ...quoteToRemove, isPinned: false, order: undefined }])
  }

  const handleAddBooks = () => {
    const booksToAdd = availableBooks.filter((b) => selectedBooks.includes(b.id))
    if (booksToAdd.length === 0) return

    const newFeaturedBooks = [
      ...featuredBooks,
      ...booksToAdd.map((b, index) => ({
        ...b,
        isPinned: true,
        order: featuredBooks.length + index + 1,
      })),
    ]
    setFeaturedBooks(newFeaturedBooks.sort((a, b) => (a.order ?? 0) - (b.order ?? 0)))

    setAvailableBooks(availableBooks.filter((b) => !selectedBooks.includes(b.id)))
    setSelectedBooks([])
  }

  const handleRemoveBook = (bookId: string) => {
    const bookToRemove = featuredBooks.find((b) => b.id === bookId)
    if (!bookToRemove) return

    const newFeaturedBooks = featuredBooks.filter((b) => b.id !== bookId).map((b, index) => ({ ...b, order: index + 1 }))
    setFeaturedBooks(newFeaturedBooks)

    setAvailableBooks([...availableBooks, { ...bookToRemove, isPinned: false, order: undefined }])
  }

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-96">
        <Loader2 className="w-8 h-8 animate-spin text-muted-foreground" />
        <span className="ml-2">데이터를 불러오는 중...</span>
      </div>
    )
  }

  if (error) {
    return <div className="text-center text-red-500">오류: {error}</div>
  }

  return (
    <div className="space-y-6">
      <Card>
        <CardHeader>
          <CardTitle>큐레이션 정보</CardTitle>
          <p className="text-muted-foreground">큐레이션의 제목, 소개글, 발행 위치를 편집합니다.</p>
        </CardHeader>
        <CardContent className="space-y-4">
          <div>
            <label htmlFor="curation-title" className="block text-sm font-medium mb-1">
              큐레이션 제목
            </label>
            <Input
              id="curation-title"
              value={curationTitle}
              onChange={(e) => setCurationTitle(e.target.value)}
              placeholder="예: 이번 주를 위한 문장들"
            />
          </div>
          <div>
            <label htmlFor="curation-content" className="block text-sm font-medium mb-1">
              큐레이션 소개글
            </label>
            <Textarea
              id="curation-content"
              value={curationContent}
              onChange={(e) => setCurationContent(e.target.value)}
              placeholder="큐레이션에 대한 설명을 입력하세요."
              rows={3}
            />
          </div>
          <div>
            <label htmlFor="curation-destination" className="block text-sm font-medium mb-1">
              발행 위치
            </label>
            <Select value={curationDestination} onValueChange={setCurationDestination}>
              <SelectTrigger id="curation-destination">
                <SelectValue placeholder="발행 위치를 선택하세요" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="main">메인 페이지</SelectItem>
                <SelectItem value="social">소셜 미디어</SelectItem>
                <SelectItem value="newsletter">뉴스레터</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </CardContent>
      </Card>

      <div>
        <h2 className="text-2xl font-bold tracking-tight">큐레이션 콘텐츠 편집</h2>
        <p className="text-muted-foreground">큐레이션에 포함될 추천 인용구와 책을 선택하고 관리합니다.</p>
      </div>

      <Tabs defaultValue="quotes" className="space-y-4">
        <TabsList>
          <TabsTrigger value="quotes">추천 인용구</TabsTrigger>
          <TabsTrigger value="books">추천 도서</TabsTrigger>
        </TabsList>

        <TabsContent value="quotes" className="space-y-4">
          <div className="grid gap-4 md:grid-cols-2">
            {/* 현재 추천 인용구 */}
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Pin className="w-5 h-5" />
                  현재 추천 인용구
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {featuredQuotes.map((quote) => (
                    <div key={quote.id} className="p-4 border rounded-lg bg-muted/50">
                      <div className="flex items-start justify-between mb-2">
                        <Badge variant="outline" className="bg-blue-50 text-blue-700">
                          #{quote.order}
                        </Badge>
                        <div className="flex gap-1">
                          <Button variant="ghost" size="sm">
                            <ArrowUp className="w-4 h-4" />
                          </Button>
                          <Button variant="ghost" size="sm">
                            <ArrowDown className="w-4 h-4" />
                          </Button>
                          <Button variant="ghost" size="sm" onClick={() => handleRemoveQuote(quote.id)}>
                            <PinOff className="w-4 h-4" />
                          </Button>
                        </div>
                      </div>
                      <p className="text-sm leading-relaxed mb-2">{quote.content}</p>
                      <div className="flex items-center justify-between text-xs text-muted-foreground">
                        <span>
                          {quote.author} - {quote.book}
                        </span>
                        <div className="flex items-center gap-1">
                          <Heart className="w-3 h-3 text-red-500" />
                          {quote.likes}
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>

            {/* 인용구 선택 */}
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
                    <div key={quote.id} className="flex items-start gap-3 p-3 border rounded-lg hover:bg-muted/50">
                      <Checkbox
                        checked={selectedQuotes.includes(quote.id)}
                        onCheckedChange={() => handleQuoteSelection(quote.id)}
                      />
                      <div className="flex-1 min-w-0">
                        <p className="text-sm leading-relaxed mb-1">{quote.content}</p>
                        <div className="flex items-center justify-between text-xs text-muted-foreground">
                          <span>
                            {quote.author} - {quote.book}
                          </span>
                          <div className="flex items-center gap-1">
                            <Heart className="w-3 h-3 text-red-500" />
                            {quote.likes}
                          </div>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
                <div className="flex gap-2 mt-4">
                  <Button onClick={handleAddQuotes} disabled={selectedQuotes.length === 0} className="flex-1">
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
            {/* 현재 추천 도서 */}
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Star className="w-5 h-5" />
                  현재 추천 도서
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {featuredBooks.map((book) => (
                    <div key={book.id} className="p-4 border rounded-lg bg-muted/50">
                      <div className="flex items-start justify-between mb-2">
                        <Badge variant="outline" className="bg-green-50 text-green-700">
                          #{book.order}
                        </Badge>
                        <div className="flex gap-1">
                          <Button variant="ghost" size="sm">
                            <ArrowUp className="w-4 h-4" />
                          </Button>
                          <Button variant="ghost" size="sm">
                            <ArrowDown className="w-4 h-4" />
                          </Button>
                          <Button variant="ghost" size="sm" onClick={() => handleRemoveBook(book.id)}>
                            <PinOff className="w-4 h-4" />
                          </Button>
                        </div>
                      </div>
                      <h4 className="font-medium mb-1">{book.title}</h4>
                      <p className="text-sm text-muted-foreground mb-2">{book.author}</p>
                      <p className="text-xs text-muted-foreground">{book.description}</p>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>

            {/* 도서 선택 */}
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
                    <div key={book.id} className="flex items-start gap-3 p-3 border rounded-lg hover:bg-muted/50">
                      <Checkbox
                        checked={selectedBooks.includes(book.id)}
                        onCheckedChange={() => handleBookSelection(book.id)}
                      />
                      <div className="flex-1 min-w-0">
                        <h4 className="font-medium mb-1">{book.title}</h4>
                        <p className="text-sm text-muted-foreground mb-1">{book.author}</p>
                        <p className="text-xs text-muted-foreground">{book.description}</p>
                      </div>
                    </div>
                  ))}
                </div>
                <div className="flex gap-2 mt-4">
                  <Button onClick={handleAddBooks} disabled={selectedBooks.length === 0} className="flex-1">
                    <Star className="w-4 h-4 mr-2" />
                    선택한 도서 추가 ({selectedBooks.length})
                  </Button>
                </div>
              </CardContent>
            </Card>
          </div>
        </TabsContent>
      </Tabs>

      {/* 큐레이션 통계 */}
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