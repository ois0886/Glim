import { useState, useCallback } from 'react';
import axios, { AxiosError } from 'axios';
import { GPTImageGenerationRequest } from '../types/api';

interface UseGPTImageReturn {
  imageUrl: string | null;
  loading: boolean;
  error: AxiosError | null;
  generateImage: (prompt: string) => Promise<void>;
}

const useGPTImage = (): UseGPTImageReturn => {
  const [imageUrl, setImageUrl] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<AxiosError | null>(null);

  const generateImage = useCallback(async (prompt: string) => {
    try {
      setLoading(true);
      setError(null);
      setImageUrl(null);

      const apiUrl = `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/v1/images`;
      const response = await axios.post<Blob>(
        apiUrl,
        { prompt } as GPTImageGenerationRequest,
        {
          headers: {
            'Content-Type': 'application/json',
          },
          responseType: 'blob', // Important for receiving image data
        }
      );

      const url = URL.createObjectURL(response.data);
      setImageUrl(url);
    } catch (err) {
      if (axios.isAxiosError(err)) {
        setError(err);
      } else {
        setError(new AxiosError('An unexpected error occurred'));
      }
      console.error("Error generating GPT image:", err);
    } finally {
      setLoading(false);
    }
  }, []);

  return { imageUrl, loading, error, generateImage };
};

export default useGPTImage;
