"use client"

import { useState } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Input } from "@/components/ui/input"
import { Checkbox } from "@/components/ui/checkbox"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Search, Star, Heart, TrendingUp, Pin, PinOff, ArrowUp, ArrowDown } from "lucide-react"

const featuredQuotes = [
  {
    id: "F001",
    content: "인생은 책과 같다. 바보들은 그것을 훑어보고, 현명한 사람들은 그것을 주의 깊게 읽는다.",
    author: "장 폴 리히터",
    book: "인생론",
    likes: 245,
    isPinned: true,
    order: 1,
  },
  {
    id: "F002",
    content: "독서는 완성된 사람을 만들고, 담화는 재빠른 사람을 만들며, 쓰기는 정확한 사람을 만든다.",
    author: "프랜시스 베이컨",
    book: "수필집",
    likes: 189,
    isPinned: true,
    order: 2,
  },
  {
    id: "F003",
    content: "좋은 책을 읽는 것은 과거 몇 세기의 가장 훌륭한 사람들과 대화하는 것과 같다.",
    author: "르네 데카르트",
    book: "방법서설",
    likes: 156,
    isPinned: true,
    order: 3,
  },
]

const availableQuotes = [
  {
    id: "Q001",
    content: "책을 읽지 않는 사람은 읽을 줄 모르는 사람보다 나을 것이 없다.",
    author: "마크 트웨인",
    book: "톰 소여의 모험",
    likes: 123,
    isPinned: false,
  },
  {
    id: "Q002",
    content: "책은 청춘에게는 음식이요, 노년에게는 오락이며, 잠자리에서는 장식이요, 고독할 때는 동반자이다.",
    author: "키케로",
    book: "키케로 명언집",
    likes: 198,
    isPinned: false,
  },
  {
    id: "Q003",
    content: "진정한 대학은 책들의 집합체이다.",
    author: "토마스 칼라일",
    book: "영웅숭배론",
    likes: 87,
    isPinned: false,
  },
  {
    id: "Q004",
    content: "책은 인류가 남긴 가장 조용하고 지속적인 친구이다.",
    author: "찰스 W. 엘리엇",
    book: "교육론",
    likes: 145,
    isPinned: false,
  },
]

const featuredBooks = [
  {
    id: "B001",
    title: "데미안",
    author: "헤르만 헤세",
    description: "성장과 자아 발견의 고전 소설",
    isPinned: true,
    order: 1,
  },
  {
    id: "B002",
    title: "어린 왕자",
    author: "생텍쥐페리",
    description: "순수함과 사랑에 대한 철학적 동화",
    isPinned: true,
    order: 2,
  },
]

const availableBooks = [
  {
    id: "B003",
    title: "1984",
    author: "조지 오웰",
    description: "디스토피아 사회를 그린 정치 소설",
    isPinned: false,
  },
  {
    id: "B004",
    title: "위대한 개츠비",
    author: "F. 스콧 피츠제럴드",
    description: "아메리칸 드림의 허상을 다룬 작품",
    isPinned: false,
  },
  {
    id: "B005",
    title: "호밀밭의 파수꾼",
    author: "J.D. 샐린저",
    description: "청소년의 방황과 성장을 그린 소설",
    isPinned: false,
  },
]

export function CurationEditor() {
  const [searchTerm, setSearchTerm] = useState("")
  const [selectedQuotes, setSelectedQuotes] = useState<string[]>([])
  const [selectedBooks, setSelectedBooks] = useState<string[]>([])

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
                          <Button variant="ghost" size="sm">
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
                  <Button disabled={selectedQuotes.length === 0} className="flex-1">
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
                          <Button variant="ghost" size="sm">
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
                  <Button disabled={selectedBooks.length === 0} className="flex-1">
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
