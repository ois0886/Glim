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
        const apiUrl = `/api/v1/admin/search-keywords/quotes?keyword=&page=0&size=100&sort=views%2Cdesc`;
        
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