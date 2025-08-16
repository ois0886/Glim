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

