import { useState, useEffect } from 'react';
import axios, { AxiosError } from 'axios';
// API 명세에 맞게 정의한 Curation, CurationContent 타입을 가져옵니다.
import { Curation, CurationContent, Content } from '../types/api';

interface UseApiDataReturn {
  data: Content[];
  loading: boolean;
  error: AxiosError | null;
}

const useCurationData = (): UseApiDataReturn => {
  const [data, setData] = useState<Content[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<AxiosError | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const apiUrl = `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/v1/curations/main`;
        // API 응답 타입은 Curation[] 입니다.
        const response = await axios.get<Curation[]>(apiUrl);

        // API 응답 데이터(CurationContent)를 프론트엔드용 데이터(Content)로 변환합니다.
        const allContents: Content[] = response.data.flatMap((curation: Curation) =>
          (curation.contents || []).map((apiContent: CurationContent) => {
            // Scene.tsx가 요구하는 Content 타입 객체를 여기서 직접 만듭니다.
            return {
              // --- API에서 받은 값을 채워 넣습니다 ---
              quoteId: apiContent.quoteId || 0, // null일 경우를 대비해 기본값 0을 설정합니다.
              bookId: apiContent.bookId || 0, // null일 경우를 대비해 기본값 0을 설정합니다.
              bookTitle: apiContent.bookTitle,
              author: apiContent.author,
              publisher: apiContent.publisher,
              bookCoverUrl: apiContent.bookCoverUrl,

              // --- 필드명을 변환합니다 ---
              // API 응답의 `imageName`을 컴포넌트가 사용하는 `quoteImage`로 변경합니다.
              quoteImage: apiContent.imageName || '',

              // --- 없는 값은 기본값으로 채워 넣습니다 (오류 해결의 핵심) ---
              quoteViews: 0,
              page: 0,
              likeCount: 0,
              liked: false,
            };
          })
        );

        setData(allContents);
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

export default useCurationData;