/**
 * src/hooks/useQuoteList.ts
 *
 * 이 훅은 명언(quote) 목록 데이터를 API로부터 가져오는 역할을 합니다.
 * `useApiData.ts`와 유사하지만, 특정 API 엔드포인트와 데이터 변환 로직에 특화되어 있습니다.
 *
 * 주요 기능:
 * 1. 명언 목록 Fetching:
 *    - `/api/v1/quotes` 엔드포인트로 GET 요청을 보내 명언 목록을 가져옵니다.
 *    - 페이지네이션(`page`, `size`) 및 정렬(`sort`) 파라미터를 포함하여 요청합니다.
 *
 * 2. 데이터 변환:
 *    - API 응답(`QuoteListApiResponse[]`)을 앱 내부에서 사용하는 `Content[]` 타입으로 변환합니다.
 *    - 이 과정에서 `quoteImageName` 필드를 `quoteImage` 필드로 매핑하는 등,
 *      API와 앱 간의 데이터 구조 차이를 해결합니다.
 *
 * 3. 상태 관리 및 반환:
 *    - `quotes`: 가져온 명언 목록 데이터 배열.
 *    - `loading`: 데이터 로딩 중인지 여부.
 *    - `error`: 로딩 중 발생한 에러 객체.
 *    - 이들을 객체로 묶어 반환하여 컴포넌트에서 명언 목록을 쉽게 사용할 수 있게 합니다.
 */
// src/hooks/useQuoteList.ts

import { useState, useEffect } from 'react';
import axios, { AxiosError } from 'axios';
// QuoteListItem 대신, 프로젝트 전반에서 사용하는 Content 타입을 가져옵니다.
import { Content } from '../types/api';

// API가 실제로 반환하는 데이터의 타입을 명시적으로 정의합니다.
// 이 타입은 types/api.ts에 이미 QuoteListApiResponse로 정의되어 있습니다.
interface QuoteListApiResponse {
  quoteId: number;
  quoteImageName: string; // API는 quoteImageName을 반환합니다.
  quoteViews: number;
  page: number;
  bookId: number;
  bookTitle: string;
  author: string;
  publisher: string | null;
  bookCoverUrl: string;
  likeCount: number;
  liked: boolean;
}

interface UseQuoteListReturn {
  quotes: Content[]; // 최종 반환 타입은 Content[] 입니다.
  loading: boolean;
  error: AxiosError | null;
}

const useQuoteList = (): UseQuoteListReturn => {
  const [quotes, setQuotes] = useState<Content[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<AxiosError | null>(null);

  useEffect(() => {
    const fetchQuotes = async () => {
      try {
        setLoading(true);
        const apiUrl = `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/v1/quotes?page=0&size=200&sort=views%2Cdesc`;
        
        // axios를 사용하고, 응답 타입을 위에서 정의한 것으로 지정합니다.
        const response = await axios.get<QuoteListApiResponse>(apiUrl);

        // API 응답 데이터를 프론트엔드용 Content 타입으로 변환합니다.
        const transformedQuotes: Content[] = response.data.map(item => ({
          ...item, // 대부분의 속성은 그대로 사용하고,
          quoteImage: item.quoteImageName, // quoteImageName을 quoteImage로 변경해줍니다.
        }));
        
        setQuotes(transformedQuotes);
        setError(null);
      } catch (err) {
        if (axios.isAxiosError(err)) {
          setError(err);
        } else {
          setError(new AxiosError('An unexpected error occurred'));
        }
        console.error("Error fetching quote list:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchQuotes();
  }, []);

  return { quotes, loading, error };
};

export default useQuoteList;
