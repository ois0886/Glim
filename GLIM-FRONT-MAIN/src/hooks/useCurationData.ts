/**
 * src/hooks/useCurationData.ts
 *
 * 이 훅은 메인 페이지에 표시될 큐레이션 데이터를 API로부터 가져오는 역할을 합니다.
 *
 * 주요 기능:
 * 1. 큐레이션 데이터 Fetching:
 *    - `/api/v1/curations/main` 엔드포인트로 GET 요청을 보내 큐레이션 목록을 가져옵니다.
 *
 * 2. 데이터 가공 및 변환:
 *    - API 응답은 `Curation[]` 형태이며, 각 Curation 객체 안에 `contents` 배열이 포함된 중첩 구조입니다.
 *    - `flatMap`과 `map`을 사용하여 이 중첩된 구조를 `Content[]` 형태의 1차원 배열로 평탄화하고,
 *      앱 내부에서 사용하는 `Content` 데이터 모델에 맞게 필드명을 맞추거나 기본값을 채워 넣습니다.
 *    - 예를 들어, API의 `imageName` 필드를 앱에서 사용하는 `quoteImage` 필드로 매핑합니다.
 *    - 이 변환 과정은 API 데이터 구조와 앱 내부 데이터 모델 간의 차이를 해결하는 중요한 역할을 합니다.
 *
 * 3. 상태 관리 및 반환:
 *    - `useApiData`와 유사하게 `data`, `loading`, `error` 상태를 관리하고 반환하여
 *      컴포넌트에서 비동기 데이터 로직을 쉽게 사용하도록 돕습니다.
 */
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
