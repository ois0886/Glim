/**
 * @file post-management.tsx
 * @description '글:림' 관리자 대시보드에서 게시물(글귀)을 관리하는 컴포넌트.
 *              게시물 검색, 정렬, 상세 정보 편집, 삭제 기능을 제공합니다.
 *              `app/page.tsx`에서 '게시물 관리' 섹션에 동적으로 로드되어 사용됩니다.
 *
 * @backend_note
 * - 현재 게시물 데이터는 `/public/posts.json` 파일에서 로드됩니다.
 * - 게시물 삭제 및 편집 기능은 `http://localhost:50871/api/v1/quotes/{quoteId}` 엔드포인트를 호출합니다.
 * - **향후 백엔드 연동 시, 데이터 로드 (`useEffect` 내 `fetchQuotes`) 및 수정/삭제 (`handleDeleteClick`, `handleSaveEdit`) 로직을
 *   백엔드 API 호출로 변경해야 합니다.**
 */
'use client'

import React, { useState, useEffect } from 'react'
import { Search, Trash2, Edit } from "lucide-react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"

interface Quote {
  quoteId: number;
  quoteImageName: string;
  quoteViews: number | null;
  bookId: number;
  bookTitle: string;
  author: string;
  bookCoverUrl: string;
  content: string; // Added for detailed view
  date: string; // Added for detailed view
  status: "visible" | "hidden"; // Added for detailed view
}

export function PostManagement() {
  const [searchTerm, setSearchTerm] = useState("")
  const [sortOrder, setSortOrder] = useState("views,desc")
  const [quotes, setQuotes] = useState<Quote[]>([])
  const [isDetailsDialogOpen, setIsDetailsDialogOpen] = useState(false) // New state for details dialog
  const [editedQuote, setEditedQuote] = useState<Quote | null>(null) // New state for selected quote

  useEffect(() => {
    const fetchQuotes = async () => {
      try {
        const response = await fetch('/posts.json');
        if (!response.ok) {
          throw new Error('Failed to load posts.json');
        }
        const jsonData = await response.json();
        const mappedData: Quote[] = jsonData.map((item: any) => ({
          quoteId: parseInt(item.id.replace('P', '')),
          quoteImageName: '', // posts.json에 없음
          quoteViews: item.likes,
          bookId: 0, // posts.json에 없음
          bookTitle: item.book,
          author: item.bookAuthor,
          bookCoverUrl: '', // posts.json에 없음
          content: item.content, // Mapped from posts.json
          date: item.date, // Mapped from posts.json
          status: item.status, // Mapped from posts.json
        }));
        setQuotes(mappedData);
      } catch (error) {
        console.error("Failed to fetch quotes:", error);
      }
    };

    fetchQuotes();
  }, [sortOrder]);

  const handleDeleteClick = async (quoteId: number) => {
    if (window.confirm("이 글귀를 삭제하시겠습니까?")) {
      try {
        // This part still points to the backend API, which is fine for now as per user's request to not modify backend files.
        // If backend is not running, this will fail silently or log an error.
        const response = await fetch(`http://localhost:50871/api/v1/quotes/${quoteId}`, {
          method: 'DELETE',
        });
        if (!response.ok) {
          throw new Error('Failed to delete quote');
        }
        setQuotes(quotes.filter((quote) => quote.quoteId !== quoteId));
      } catch (error) {
        console.error("Failed to delete quote:", error);
      }
    }
  };

  const handleEditClick = (quote: Quote) => {
    setEditedQuote({ ...quote });
    setIsDetailsDialogOpen(true);
  };

  const handleSaveEdit = () => {
    if (editedQuote) {
      setQuotes(quotes.map((q) => (q.quoteId === editedQuote.quoteId ? editedQuote : q)));
      setIsDetailsDialogOpen(false);
      setEditedQuote(null);
    }
  };

  const filteredQuotes = quotes.filter((quote) =>
    quote.bookTitle.toLowerCase().includes(searchTerm.toLowerCase()) ||
    quote.author.toLowerCase().includes(searchTerm.toLowerCase())
  )

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold tracking-tight">게시물 관리</h2>
        <p className="text-muted-foreground">플랫폼의 모든 게시물과 인용구를 관리합니다.</p>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>게시물 목록</CardTitle>
          <div className="flex gap-4">
            <div className="relative flex-1">
              <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
              <Input
                placeholder="책 제목, 저자로 검색..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="pl-8"
              />
            </div>
            <Select value={sortOrder} onValueChange={setSortOrder}>
              <SelectTrigger className="w-[180px]">
                <SelectValue placeholder="정렬 기준" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="views,desc">조회수 높은 순</SelectItem>
                <SelectItem value="createdDate,desc">최신 순</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>글귀 ID</TableHead>
                <TableHead>글귀 이미지</TableHead>
                <TableHead>책 정보</TableHead>
                <TableHead>조회수</TableHead>
                <TableHead>상태</TableHead> {/* Added status column */}
                <TableHead>작업</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {filteredQuotes.map((quote) => (
                <React.Fragment key={quote.quoteId}>
                  <TableRow>
                    <TableCell className="font-mono">{quote.quoteId}</TableCell>
                    <TableCell>
                      {quote.quoteImageName ? `이미지: ${quote.quoteImageName}` : '이미지 없음'}
                    </TableCell>
                    <TableCell>
                      <div className="font-bold text-foreground">{quote.bookTitle}</div>
                      <div className="text-xs">{quote.author}</div>
                    </TableCell>
                    <TableCell>{quote.quoteViews ?? 0}</TableCell>
                    <TableCell>
                      <Badge variant={quote.status === "visible" ? "default" : "destructive"}>
                        {quote.status === "visible" ? "표시" : "숨김"}
                      </Badge>
                    </TableCell>
                    <TableCell>
                      <div className="flex gap-2">
                        <Button variant="outline" size="sm" onClick={() => handleEditClick(quote)}>
                          <Edit className="w-4 h-4" />
                        </Button>
                        <Button variant="outline" size="sm" onClick={() => handleDeleteClick(quote.quoteId)}>
                          <Trash2 className="w-4 h-4" />
                        </Button>
                      </div>
                    </TableCell>
                  </TableRow>
                </React.Fragment>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>

      {/* Post Details Dialog */}
      <Dialog open={isDetailsDialogOpen} onOpenChange={setIsDetailsDialogOpen}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>게시물 상세 정보</DialogTitle>
          </DialogHeader>
          {editedQuote && (
            <div className="space-y-4">
              <div>
                <p className="text-sm font-medium">글귀 ID</p>
                <p className="text-muted-foreground">{editedQuote.quoteId}</p>
              </div>
              <div>
                <p className="text-sm font-medium">책 제목</p>
                <p className="text-muted-foreground">{editedQuote.bookTitle}</p>
              </div>
              <div>
                <p className="text-sm font-medium">저자</p>
                <p className="text-muted-foreground">{editedQuote.author}</p>
              </div>
              <div>
                <p className="text-sm font-medium">내용</p>
                <p className="text-muted-foreground">{editedQuote.content}</p>
              </div>
              <div>
                <p className="text-sm font-medium">작성일</p>
                <p className="text-muted-foreground">{editedQuote.date}</p>
              </div>
              <div>
                <p className="text-sm font-medium">조회수</p>
                <p className="text-muted-foreground">{editedQuote.quoteViews ?? 0}</p>
              </div>
              <div>
                <p className="text-sm font-medium">상태</p>
                <Select
                  value={editedQuote.status}
                  onValueChange={(value) => setEditedQuote({ ...editedQuote, status: value as "visible" | "hidden" })}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="상태 선택" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="visible">표시</SelectItem>
                    <SelectItem value="hidden">숨김</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              <div className="flex justify-end gap-2">
                <Button variant="outline" onClick={() => setIsDetailsDialogOpen(false)}>
                  취소
                </Button>
                <Button onClick={handleSaveEdit}>저장</Button>
              </div>
            </div>
          )}
        </DialogContent>
      </Dialog>
    </div>
  )
}