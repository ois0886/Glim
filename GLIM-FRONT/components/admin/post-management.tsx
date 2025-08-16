/**
 * @file post-management.tsx
 * @description 글귀 관리 페이지.
 * @known_issue
 * 백엔드 API가 페이지네이션/검색/정렬을 지원하지 않아 모든 데이터를 한 번에 가져와 클라이언트에서 처리합니다.
 * 데이터가 수백 개 이상으로 늘어날 경우 심각한 성능 저하가 발생할 수 있습니다.
 * -> 해결책: 백엔드 API가 페이지네이션, 서버사이드 검색/정렬을 지원하도록 수정하는 것이 근본적인 해결책입니다.
 */
"use client";

import React, { useState, useEffect, useMemo } from 'react';
import { Search, Trash2 } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Button } from "@/components/ui/button";
import { getQuotes, deleteQuote } from '@/lib/api/quotes';

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
  const [allQuotes, setAllQuotes] = useState<Quote[]>([]); // [수정됨] 원본 데이터 저장용
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  
  // [수정됨] .env 파일의 NEXT_PUBLIC_API_URL을 사용해야 합니다.
  const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL;

  // [수정됨] 데이터 로딩은 컴포넌트 마운트 시 한 번만 실행
  useEffect(() => {
    const fetchQuotesData = async () => {
      setIsLoading(true);
      setError(null);
      try {
        // 백엔드 제약으로 인해, 일단 가능한 모든 데이터를 가져옵니다 (API가 최대 100개로 제한하는 것으로 가정).
        const fetchedQuotes = await getQuotes("", 0, 100, "views,desc");
        const mappedQuotes: Quote[] = fetchedQuotes.map((quote: any) => ({
          quoteId: quote.quoteId,
          content: quote.content,
          quoteImageName: quote.quoteImage,
          quoteViews: quote.views,
          page: quote.page,
          bookId: quote.bookId || 0,
          bookTitle: quote.bookTitle || '',
          author: quote.author || '',
          publisher: quote.publisher || '',
          bookCoverUrl: quote.bookCoverUrl || '',
          likeCount: quote.likeCount || 0,
          liked: quote.liked || false,
        }));
        setAllQuotes(mappedQuotes);
      } catch (err) {
        setError("게시물 데이터를 불러오는 데 실패했습니다. 서버 상태를 확인해주세요.");
      } finally {
        setIsLoading(false);
      }
    };
    fetchQuotesData();
  }, []);

  // [수정됨] 필터링과 정렬 로직을 useMemo로 감싸 성능 최적화
  const processedQuotes = useMemo(() => {
    if (isLoading || error) return [];

    let quotesToProcess = [...allQuotes];

    // 클라이언트 측 검색
    if (searchTerm) {
      const lowercasedSearchTerm = searchTerm.toLowerCase();
      quotesToProcess = quotesToProcess.filter(quote =>
        (quote.bookTitle && quote.bookTitle.toLowerCase().includes(lowercasedSearchTerm)) ||
        (quote.author && quote.author.toLowerCase().includes(lowercasedSearchTerm)) ||
        (quote.content && quote.content.toLowerCase().includes(lowercasedSearchTerm))
      );
    }
    
    // 클라이언트 측 정렬
    if (sortOrder === "quoteId,desc") {
      quotesToProcess.sort((a, b) => b.quoteId - a.quoteId);
    } else if (sortOrder === "quoteId,asc") {
      quotesToProcess.sort((a, b) => a.quoteId - b.quoteId);
    } else if (sortOrder === "views,desc") {
      quotesToProcess.sort((a, b) => (b.quoteViews || 0) - (a.quoteViews || 0));
    }
    
    return quotesToProcess;
  }, [allQuotes, searchTerm, sortOrder, isLoading, error]);

  // 삭제 버튼 클릭 핸들러
  const handleDeleteClick = async (quoteId: number) => {
    if (window.confirm(`글귀 ID ${quoteId}를 정말 삭제하시겠습니까?`)) {
      try {
        await deleteQuote(quoteId);
        setAllQuotes(prevQuotes => prevQuotes.filter(quote => quote.quoteId !== quoteId));
        alert("성공적으로 삭제되었습니다.");
      } catch (err) {
        alert("삭제에 실패했습니다. 다시 시도해주세요.");
      }
    }
  };

  // 페이지네이션 로직
  const indexOfLastQuote = currentPage * itemsPerPage;
  const indexOfFirstQuote = indexOfLastQuote - itemsPerPage;
  const currentQuotes = processedQuotes.slice(indexOfFirstQuote, indexOfLastQuote);
  const totalPages = Math.ceil(processedQuotes.length / itemsPerPage);

  const handlePageChange = (pageNumber: number) => {
    if (pageNumber < 1 || pageNumber > totalPages) return;
    setCurrentPage(pageNumber);
  };

  const handleItemsPerPageChange = (value: string) => {
    setItemsPerPage(Number(value));
    setCurrentPage(1);
  };

  return (
    <div className="space-y-6">
      <Card>
        <CardHeader>
          <CardTitle>글귀 관리 ({isLoading ? '...' : processedQuotes.length}개)</CardTitle>
          <CardDescription>플랫폼의 모든 글귀(게시물)을 관리합니다.</CardDescription>
          <div className="flex flex-wrap gap-4 pt-4">
            <div className="relative flex-1 min-w-[200px]">
              <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
              <Input
                placeholder="책 제목, 저자, 내용으로 검색..."
                value={searchTerm}
                onChange={(e) => {
                  setSearchTerm(e.target.value);
                  setCurrentPage(1); // 검색 시 첫 페이지로 이동
                }}
                className="pl-8"
              />
            </div>
            <Select value={sortOrder} onValueChange={(value) => {
              setSortOrder(value);
              setCurrentPage(1); // 정렬 변경 시 첫 페이지로 이동
            }}>
              <SelectTrigger className="w-[180px]">
                <SelectValue placeholder="정렬 기준" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="views,desc">조회수 높은 순</SelectItem>
                <SelectItem value="quoteId,desc">최신 순 (ID)</SelectItem>
                <SelectItem value="quoteId,asc">오래된 순 (ID)</SelectItem>
              </SelectContent>
            </Select>
            <Select value={String(itemsPerPage)} onValueChange={handleItemsPerPageChange}>
              <SelectTrigger className="w-[120px]">
                <SelectValue placeholder="페이지당" />
              </SelectTrigger>
              <SelectContent>
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
                <TableHead>글귀 내용</TableHead>
                <TableHead>책 정보</TableHead>
                <TableHead className="w-[80px]">조회수</TableHead>
                <TableHead className="w-[80px]">작업</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {isLoading ? (
                <TableRow><TableCell colSpan={6} className="text-center h-24">데이터를 불러오는 중입니다...</TableCell></TableRow>
              ) : error ? (
                <TableRow><TableCell colSpan={6} className="text-center h-24 text-red-500">{error}</TableCell></TableRow>
              ) : currentQuotes.length > 0 ? (
                currentQuotes.map((quote) => (
                  <TableRow key={quote.quoteId}>
                    <TableCell className="font-mono">{quote.quoteId}</TableCell>
                    <TableCell>
                      {/* [수정됨] 이미지 URL에서 /api 경로를 제거하여 올바른 주소 생성 */}
                      <img
                        src={API_BASE_URL ? `${API_BASE_URL.replace('/api', '')}/images/${quote.quoteImageName}` : '/placeholder.svg'}
                        alt={`글귀 ${quote.quoteId}`}
                        className="w-full h-auto object-cover rounded-md bg-gray-200"
                        onError={(e) => { e.currentTarget.src = '/placeholder.svg'; }}
                      />
                    </TableCell>
                    <TableCell className="min-w-[250px]">{quote.content}</TableCell>
                    <TableCell>
                      <div className="font-bold">{quote.bookTitle}</div>
                      <div>{quote.author}</div>
                    </TableCell>
                    <TableCell>{quote.quoteViews ?? 'N/A'}</TableCell>
                    <TableCell>
                      <Button variant="outline" size="icon" onClick={() => handleDeleteClick(quote.quoteId)}>
                          <Trash2 className="w-4 h-4" />
                      </Button>
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow><TableCell colSpan={6} className="text-center h-24">표시할 게시물이 없습니다.</TableCell></TableRow>
              )}
            </TableBody>
          </Table>
          <div className="flex items-center justify-between space-x-2 py-4">
            <div className="flex-1 text-sm text-muted-foreground">
              총 {processedQuotes.length}개 중 {currentQuotes.length > 0 ? indexOfFirstQuote + 1 : 0}-{Math.min(indexOfLastQuote, processedQuotes.length)}개 표시.
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
              <span className="text-sm">{currentPage} / {totalPages > 0 ? totalPages : 1}</span>
              <Button
                variant="outline"
                size="sm"
                onClick={() => handlePageChange(currentPage + 1)}
                disabled={currentPage === totalPages || totalPages === 0}
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