// /frontend/lib/api/quotes.ts

import axiosInstance from '@/lib/axiosInstance';
import { Quote } from '@/types';

/**
 * 글귀(게시물) 목록을 조회하는 API
 * (GET /)
 * @param page 페이지 번호 (0부터 시작)
 * @param size 페이지 크기
 * @param sort 정렬 기준 (예: 'views,desc')
 * @returns 글귀(게시물) 배열
 */
export const getQuotes = async (page: number, size: number, sort: string): Promise<Quote[]> => {
  try {
    const response = await axiosInstance.get('/api/v1/quotes', {
      params: { page, size, sort }
    });
    return response.data;
  } catch (error) {
    console.error('글귀 목록 조회 API 호출 실패:', error);
    throw error;
  }
};

/**
 * [관리자] 특정 글귀(게시물)를 삭제하는 API
 * (DELETE /api/v1/admin/quotes/{quoteId})
 * @param quoteId 삭제할 글귀의 ID
 */
export const deleteQuote = async (quoteId: number): Promise<void> => {
    try {
        await axiosInstance.delete(`/api/v1/admin/quotes/${quoteId}`);
    } catch (error) {
        console.error(`[관리자] 글귀(ID: ${quoteId}) 삭제 API 호출 실패:`, error);
        throw error;
    }
};

// 글귀(게시물) 수정 API 함수 (백엔드에 API가 추가되면 여기에 작성)
// export const updateQuote = async (quoteId: number, data: Partial<Quote>) => { ... }