// /lib/api/books.ts
import axiosInstance from '@/lib/axiosInstance';
import { Book } from '@/types';

// 키워드로 도서 검색
export const searchBooksByKeyword = async (keyword: string, page: number = 1): Promise<Book[]> => {
  const response = await axiosInstance.get('/books', {
    params: { keyword, page }
  });
  return response.data;
};

// 인기 도서 검색
export const getPopularBooks = async (page: number = 1): Promise<Book[]> => {
    const response = await axiosInstance.get('/books/popular', {
        params: { page }
    });
    return response.data;
}
