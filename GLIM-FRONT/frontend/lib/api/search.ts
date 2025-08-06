// lib/api/search.ts

import axiosInstance from '@/lib/axiosInstance';

// 도서 검색 API 응답 타입 (API 명세서 참고)
interface BookSearchResult {
  bookId: number;
  isbn13: string;
  title: string;
  author: string;
  coverUrl: string;
  // ... 기타 필요한 필드
}

// 글귀 검색 API 응답 타입 (API 명세서 참고)
interface QuoteSearchResult {
  quoteId: number;
  content: string;
  author: string;
  bookTitle: string;
  // ... 기타 필요한 필드
}

// 키워드로 도서 검색
export const searchBooks = async (keyword: string): Promise<BookSearchResult[]> => {
  const response = await axiosInstance.get('/api/v1/books', {
    params: { keyword }
  });
  return response.data;
};

// 키워드로 글귀 검색
export const searchQuotes = async (content: string): Promise<QuoteSearchResult[]> => {
  const response = await axiosInstance.get('/api/v1/quotes/by-content', {
    params: { content }
  });
  // API 응답이 { contents: [...] } 형태일 수 있으므로 실제 데이터 구조 확인 필요
  return response.data.contents || response.data; 
};


// Admin용 도서 검색
export const adminSearchBooks = async (keyword: string): Promise<BookSearchResult[]> => {
  const response = await axiosInstance.get('/api/v1/admin/search/book', {
    params: { keyword, page: 0, size: 10 }
  });
  return response.data.content || response.data;
};

// Admin용 글귀 검색
export const adminSearchQuotes = async (content: string): Promise<QuoteSearchResult[]> => {
  const response = await axiosInstance.get('/api/v1/admin/search/quote', {
    params: { content, page: 0, size: 10 }
  });
  return response.data.content || response.data;
};