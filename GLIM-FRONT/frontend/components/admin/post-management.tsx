// /frontend/components/admin/post-management.tsx

"use client";

import React, { useState, useEffect } from 'react';
import { Search, Trash2, Edit } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { getQuotes, deleteQuote } from '@/lib/api/quotes'; // 위에서 만든 API 함수 import

interface Quote {
  quoteId: number;
  quoteImageName: string;
  quoteViews: number | null;
  page: number;
  bookId: number;
  bookTitle: string;
  author: string;
  publisher: string;
  bookCoverUrl: string;
  likeCount: number;
  liked: boolean;
}


export function PostManagement() {
  const [searchTerm, setSearchTerm] = useState("");
  const [sortOrder, setSortOrder] = useState("views,desc");
  const [quotes, setQuotes] = useState<Quote[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  
  // 이미지 URL을 만들기 위한 백엔드 API 기본 주소
  const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL;

  // 데이터 로딩 함수
  const fetchQuotesData = async () => {
    setIsLoading(true);
    setError(null);
    try {
      // 페이지네이션을 위해 page, size 전달 (예: 0페이지, 100개)
      const fetchedQuotes = await getQuotes(0, 100, sortOrder);
      setQuotes(fetchedQuotes);
    } catch (err) {
      setError("게시물 데이터를 불러오는 데 실패했습니다. 서버 상태를 확인해주세요.");
    } finally {
      setIsLoading(false);
    }
  };

  // 컴포넌트가 처음 로드되거나, 정렬 순서가 바뀔 때 데이터를 다시 가져옴
  useEffect(() => {
    console.log("API_BASE_URL for images:", API_BASE_URL);
    fetchQuotesData();
  }, [sortOrder]);

  // 삭제 버튼 클릭 핸들러
  const handleDeleteClick = async (quoteId: number) => {
    if (window.confirm(`글귀 ID ${quoteId}를 정말 삭제하시겠습니까?`)) {
      try {
        await deleteQuote(quoteId);
        // 삭제 성공 시, UI에서도 해당 항목을 즉시 제거
        setQuotes(prevQuotes => prevQuotes.filter(quote => quote.quoteId !== quoteId));
        alert("성공적으로 삭제되었습니다.");
      } catch (err) {
        alert("삭제에 실패했습니다. 다시 시도해주세요.");
      }
    }
  };

  const filteredQuotes = quotes.filter(quote => {
    const titleMatch = quote.bookTitle && quote.bookTitle.toLowerCase().includes(searchTerm.toLowerCase());
    const authorMatch = quote.author && quote.author.toLowerCase().includes(searchTerm.toLowerCase());
    return titleMatch || authorMatch;
  });

  return (
    <div className="space-y-6">
      <Card>
        <CardHeader>
          <CardTitle>글귀 관리</CardTitle>
          <CardDescription>플랫폼의 모든 글귀(게시물)을 관리합니다.</CardDescription>
          <div className="flex gap-4 pt-4">
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
                <TableHead>ID</TableHead>
                <TableHead>글귀 이미지</TableHead>
                <TableHead>책 정보</TableHead>
                <TableHead>조회수</TableHead>
                <TableHead>작업</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {isLoading ? (
                <TableRow><TableCell colSpan={5} className="text-center">데이터를 불러오는 중입니다...</TableCell></TableRow>
              ) : error ? (
                <TableRow><TableCell colSpan={5} className="text-center text-red-500">{error}</TableCell></TableRow>
              ) : filteredQuotes.length > 0 ? (
                filteredQuotes.map((quote) => (
                  <TableRow key={quote.quoteId}>
                    <TableCell className="font-mono">{quote.quoteId}</TableCell>
                    <TableCell>
                      {/* 백엔드 주소와 이미지 이름을 조합하여 완전한 URL 생성 */}
                      <img
                        src={`http://i13d202.p.ssafy.io:8080/images/${quote.quoteImageName}`}
                        alt={`글귀 ${quote.quoteId}`}
                        className="w-45 h-80 object-cover rounded-md bg-gray-200"
                        // 이미지 로딩 실패 시 대체 이미지 표시
                        onError={(e) => { e.currentTarget.src = '/placeholder.svg'; }}
                      />
                    </TableCell>
                    <TableCell>
                      <div className="font-bold">{quote.bookTitle}</div>
                      <div>{quote.author}</div>
                    </TableCell>
                    <TableCell>{quote.quoteViews ?? 'N/A'}</TableCell>
                    <TableCell>
                      <div className="flex gap-2">
                        <Button variant="outline" size="sm" disabled> {/* 수정 기능은 추후 구현 */}
                          <Edit className="w-4 h-4" />
                        </Button>
                        <Button variant="outline" size="sm" onClick={() => handleDeleteClick(quote.quoteId)}>
                          <Trash2 className="w-4 h-4" />
                        </Button>
                      </div>
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow><TableCell colSpan={5} className="text-center">표시할 게시물이 없습니다.</TableCell></TableRow>
              )}
            </TableBody>
          </Table>
        </CardContent>
      </Card>
    </div>
  );
}