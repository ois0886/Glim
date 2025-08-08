// /frontend/components/admin/post-management.tsx

"use client";

import React, { useState, useEffect } from 'react';
import { Search, Trash2 } from "lucide-react";
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
  content: string;
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
  const [totalQuotes, setTotalQuotes] = useState<number>(0);
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(10); // 기본값 10개
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
      const fetchedQuotes = await getQuotes(searchTerm, 0, 100, sortOrder);
      // API 응답 필드 이름을 Quote 인터페이스에 맞게 매핑
      const mappedQuotes: Quote[] = fetchedQuotes.map((quote: any) => ({
        quoteId: quote.quoteId,
        content: quote.content,
        quoteImageName: quote.quoteImage, // quoteImage -> quoteImageName
        quoteViews: quote.views, // views -> quoteViews
        page: quote.page,
        bookId: quote.bookId || 0, // bookId가 없을 경우 기본값 설정
        bookTitle: quote.bookTitle || '', // bookTitle이 없을 경우 기본값 설정
        author: quote.author || '', // author가 없을 경우 기본값 설정
        publisher: quote.publisher || '', // publisher가 없을 경우 기본값 설정
        bookCoverUrl: quote.bookCoverUrl || '', // bookCoverUrl이 없을 경우 기본값 설정
        likeCount: quote.likeCount || 0, // likeCount가 없을 경우 기본값 설정
        liked: quote.liked || false, // liked가 없을 경우 기본값 설정
      }));

      // 클라이언트 측 정렬 로직
      let sortedQuotes = [...mappedQuotes];
      if (sortOrder === "quoteId,desc") {
        sortedQuotes.sort((a, b) => b.quoteId - a.quoteId);
      } else if (sortOrder === "quoteId,asc") {
        sortedQuotes.sort((a, b) => a.quoteId - b.quoteId);
      } else if (sortOrder === "views,desc") {
        sortedQuotes.sort((a, b) => (b.quoteViews || 0) - (a.quoteViews || 0));
      }

      setQuotes(sortedQuotes);
      setTotalQuotes(sortedQuotes.length);
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
  }, [sortOrder, searchTerm]); // searchTerm이 변경될 때도 데이터를 다시 가져오도록 추가

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

  // filteredQuotes 로직은 이제 필요 없음 (API에서 필터링하므로)
  // const filteredQuotes = quotes.filter(quote => {
  //   const titleMatch = quote.bookTitle && quote.bookTitle.toLowerCase().includes(searchTerm.toLowerCase());
  //   const authorMatch = quote.author && quote.author.toLowerCase().includes(searchTerm.toLowerCase());
  //   const contentMatch = quote.content && quote.content.toLowerCase().includes(searchTerm.toLowerCase());
  //   return titleMatch || authorMatch || contentMatch;
  // });

  // 페이지네이션 로직
  const filteredQuotes = quotes; // API에서 이미 필터링된 데이터를 가져오므로, 여기서는 전체 quotes를 사용
  const indexOfLastQuote = currentPage * itemsPerPage;
  const indexOfFirstQuote = indexOfLastQuote - itemsPerPage;
  const currentQuotes = filteredQuotes.slice(indexOfFirstQuote, indexOfLastQuote);
  const totalPages = Math.ceil(filteredQuotes.length / itemsPerPage);

  const handlePageChange = (pageNumber: number) => {
    setCurrentPage(pageNumber);
  };

  const handleItemsPerPageChange = (value: string) => {
    setItemsPerPage(Number(value));
    setCurrentPage(1); // 항목 수 변경 시 첫 페이지로 이동
  };

  return (
    <div className="space-y-6">
      <Card>
        <CardHeader>
          <CardTitle>글귀 관리 ({totalQuotes}개)</CardTitle>
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
                <SelectItem value="quoteId,desc">최신 순 (ID)</SelectItem>
                <SelectItem value="quoteId,asc">오래된 순 (ID)</SelectItem>
                <SelectItem value="views,desc">조회수 높은 순</SelectItem>
              </SelectContent>
            </Select>
            {/* 페이지당 항목 수 선택 드롭다운 */}
            <Select value={String(itemsPerPage)} onValueChange={handleItemsPerPageChange}>
              <SelectTrigger className="h-10 w-[120px]">
                <SelectValue placeholder="페이지당" />
              </SelectTrigger>
              <SelectContent side="top">
                <SelectItem value="10">10개</SelectItem>
                <SelectItem value="15">15개</SelectItem>
                <SelectItem value="20">20개</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead className="w-[50px]">ID</TableHead>
                <TableHead className="w-[150px]">글귀 이미지</TableHead>
                <TableHead className="w-[250px]">글귀 내용</TableHead>
                <TableHead className="w-[150px]">책 정보</TableHead>
                <TableHead className="w-[80px]">조회수</TableHead>
                <TableHead className="w-[80px]">작업</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {isLoading ? (
                <TableRow><TableCell colSpan={6} className="text-center">데이터를 불러오는 중입니다...</TableCell></TableRow>
              ) : error ? (
                <TableRow><TableCell colSpan={6} className="text-center text-red-500">{error}</TableCell></TableRow>
              ) : quotes.length > 0 ? (
                currentQuotes.map((quote) => (
                  <TableRow key={quote.quoteId}>
                    <TableCell className="font-mono w-[50px]">{quote.quoteId}</TableCell>
                    <TableCell className="w-[150px]">
                      {/* 백엔드 주소와 이미지 이름을 조합하여 완전한 URL 생성 */}
                      <img
                        src={`http://i13d202.p.ssafy.io:8080/images/${quote.quoteImageName}`}
                        alt={`글귀 ${quote.quoteId}`}
                        className="w-full h-auto object-cover rounded-md bg-gray-200"
                        // 이미지 로딩 실패 시 대체 이미지 표시
                        onError={(e) => { e.currentTarget.src = '/placeholder.svg'; }}
                      />
                    </TableCell>
                    <TableCell className="max-w-xs overflow-hidden text-ellipsis whitespace-nowrap w-[250px]">
                      {quote.content}
                    </TableCell>
                    <TableCell className="w-[150px]">
                      <div className="font-bold">{quote.bookTitle}</div>
                      <div>{quote.author}</div>
                    </TableCell>
                    <TableCell className="w-[80px]">{quote.quoteViews ?? 'N/A'}</TableCell>
                    <TableCell className="w-[80px]">
                      <Button variant="outline" size="sm" onClick={() => handleDeleteClick(quote.quoteId)}>
                          <Trash2 className="w-4 h-4" />
                        </Button>
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow><TableCell colSpan={6} className="text-center">표시할 게시물이 없습니다.</TableCell></TableRow>
              )}
            </TableBody>
          </Table>
          <div className="flex items-center justify-between space-x-2 py-4">
            <div className="flex-1 text-sm text-muted-foreground">
              총 {filteredQuotes.length}개 중 {indexOfFirstQuote + 1}-{Math.min(indexOfLastQuote, filteredQuotes.length)}개 표시.
            </div>
            <div className="flex items-center space-x-2">
              <Button
                variant="outline"
                size="sm"
                onClick={() => handlePageChange(currentPage - 1)}
                disabled={currentPage === 1}
              >
                이전
              </Button>
              {[...Array(totalPages)].map((_, index) => (
                <Button
                  key={index + 1}
                  variant={currentPage === index + 1 ? "default" : "outline"}
                  size="sm"
                  onClick={() => handlePageChange(index + 1)}
                >
                  {index + 1}
                </Button>
              ))}
              <Button
                variant="outline"
                size="sm"
                onClick={() => handlePageChange(currentPage + 1)}
                disabled={currentPage === totalPages}
              >
                다음
              </Button>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}