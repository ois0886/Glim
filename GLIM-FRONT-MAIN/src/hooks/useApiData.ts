/**
 * src/hooks/useApiData.ts
 *
 * 이 훅은 외부 API로부터 명언(quote) 데이터를 가져와 처리하는 커스텀 훅입니다.
 * 컴포넌트에서 데이터 로딩, 상태 관리, 에러 처리 로직을 분리하여 재사용성을 높입니다.
 *
 * 주요 기능:
 * 1. 비동기 데이터 Fetching:
 *    - `useEffect` 훅을 사용하여 컴포넌트가 마운트될 때 한 번만 데이터를 가져옵니다.
 *    - `axios`를 사용하여 지정된 API 엔드포인트에 GET 요청을 보냅니다.
 *
 * 2. 데이터 변환 (Transformation):
 *    - API로부터 받은 원본 데이터(`AdminQuoteSearchResult`)의 구조와
 *      프로젝트 내부에서 사용하는 데이터 구조(`Content`)가 다를 수 있습니다.
 *    - `.map()` 메소드를 사용하여 원본 데이터를 `Content` 타입 배열로 일관성 있게 변환합니다.
 *    - 이 과정에서 API 응답에 없는 필드(예: `author`, `bookId`)에 기본값을 채워 넣어,
 *      데이터 구조 차이로 인한 오류를 방지합니다.
 *
 * 3. 상태 관리:
 *    - `useState`를 사용하여 세 가지 상태를 관리합니다.
 *      - `data`: 성공적으로 가져와 변환된 데이터 배열
 *      - `loading`: 데이터 로딩 중인지 여부 (true/false)
 *      - `error`: 데이터 로딩 중 발생한 에러 객체
 *
 * 4. 반환 값:
 *    - 컴포넌트에서 현재 상태를 쉽게 사용할 수 있도록 `{ data, loading, error }` 객체를 반환합니다.
 */
import { useState, useEffect } from 'react';
import axios, { AxiosError } from 'axios';
import { Content } from '../types/api';

// 1. API가 실제로 반환하는 데이터의 타입을 먼저 정의합니다.
interface AdminQuoteSearchResult {
  quoteId: number;
  content: string; // API 응답에는 `content` 필드가 있습니다.
  views: number;
  page: number;
  quoteImage: string;
  bookTitle: string;
}

interface UseApiDataReturn {
  data: Content[];
  loading: boolean;
  error: AxiosError | null;
}

const useApiData = (): UseApiDataReturn => {
  const [data, setData] = useState<Content[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<AxiosError | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const apiUrl = `https://glim-main.netlify.app/api/v1/admin/search-keywords/quotes?keyword=&page=0&size=300&sort=views%2Cdesc`;
        
        // 2. API 응답 타입을 위에서 정의한 AdminQuoteSearchResult[]로 지정합니다.
        const response = await axios.get<AdminQuoteSearchResult[]>(apiUrl);
        
        // 3. API 응답 배열을 .map()을 통해 Content[] 타입으로 변환합니다.
        const transformedData: Content[] = response.data.map(item => ({
          // --- 있는 값은 그대로 또는 이름만 바꿔서 매핑 ---
          quoteId: item.quoteId,
          quoteImage: item.quoteImage,
          quoteViews: item.views, // 'views'를 'quoteViews'로 변경
          page: item.page,
          bookTitle: item.bookTitle,
          
          // --- 없는 값은 기본값으로 채워주기 (가장 중요) ---
          bookId: 0, // bookId가 없으므로 기본값 처리
          author: '작자 미상', // author가 없으므로 기본값 처리
          publisher: null, // publisher가 없으므로 null 처리
          bookCoverUrl: null, // bookCoverUrl이 없으므로 null 처리
          likeCount: 0, // likeCount가 없으므로 0 처리
          liked: false, // liked가 없으므로 false 처리
        }));

        setData(transformedData);
        setError(null);
      } catch (err) {
        if (axios.isAxiosError(err)) {
            setError(err);
        } else {
            setError(new AxiosError('An unexpected error occurred'));
        }
        console.error("Error fetching API data:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  return { data, loading, error };
};

export default useApiData;