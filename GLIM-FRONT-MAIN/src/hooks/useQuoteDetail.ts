/**
 * src/hooks/useQuoteDetail.ts
 *
 * 이 훅은 특정 명언(quote)의 상세 정보를 API로부터 가져오는 역할을 합니다.
 * 명언의 고유 ID를 기반으로 상세 데이터를 요청하고 관리합니다.
 *
 * 주요 기능:
 * 1. 명언 상세 정보 Fetching:
 *    - `quoteId`를 인자로 받아 해당 ID에 해당하는 명언의 상세 정보를 요청합니다.
 *    - `quoteId`가 유효할 때만 API 호출을 수행합니다.
 *    - `/api/v1/quotes/{quoteId}` 엔드포인트로 GET 요청을 보냅니다.
 *
 * 2. 상태 관리 및 반환:
 *    - `quote`: 가져온 명언 상세 정보 객체 (`QuoteDetailResponse` 타입).
 *    - `loading`: 데이터 로딩 중인지 여부.
 *    - `error`: 로딩 중 발생한 에러 객체.
 *    - 이들을 객체로 묶어 반환하여 컴포넌트에서 명언 상세 정보를 쉽게 사용할 수 있게 합니다.
 *
 * 사용 예시:
 * const { quote, loading, error } = useQuoteDetail(selectedQuoteId);
 */
import { useState, useEffect } from 'react';
import axios, { AxiosError } from 'axios';
import { QuoteDetailResponse } from '../types/api';

interface UseQuoteDetailReturn {
  quote: QuoteDetailResponse | null;
  loading: boolean;
  error: AxiosError | null;
}

const useQuoteDetail = (quoteId: number | null): UseQuoteDetailReturn => {
  const [quote, setQuote] = useState<QuoteDetailResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<AxiosError | null>(null);

  useEffect(() => {
    if (!quoteId) {
      setLoading(false);
      return;
    }

    const fetchQuoteDetail = async () => {
      try {
        setLoading(true);
        const apiUrl = `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/v1/quotes/${quoteId}`;
        const response = await axios.get<QuoteDetailResponse>(apiUrl);
        setQuote(response.data);
        setError(null);
      } catch (err) {
        if (axios.isAxiosError(err)) {
          setError(err);
        } else {
          setError(new AxiosError('An unexpected error occurred'));
        }
        console.error(`Error fetching quote detail for ID ${quoteId}:`, err);
      } finally {
        setLoading(false);
      }
    };

    fetchQuoteDetail();
  }, [quoteId]);

  return { quote, loading, error };
};

export default useQuoteDetail;