/**
 * src/hooks/useGPTImage.ts
 *
 * 이 훅은 텍스트 프롬프트를 기반으로 DALL-E와 같은 AI 이미지 생성 모델을 사용하여
 * 이미지를 생성하는 API를 호출하는 기능을 제공합니다.
 *
 * 주요 기능:
 * 1. 이미지 생성 요청:
 *    - `generateImage` 함수는 문자열 `prompt`를 인자로 받습니다.
 *    - 이 함수가 호출되면 `/api/v1/images` 엔드포인트에 POST 요청을 보내
 *      AI 모델에게 이미지 생성을 요청합니다.
 *
 * 2. Blob 데이터 처리:
 *    - API는 생성된 이미지를 JSON이 아닌 `blob` (Binary Large Object) 형태로 반환합니다.
 *    - `responseType: 'blob'` 옵션을 사용하여 바이너리 데이터를 직접 받습니다.
 *    - `URL.createObjectURL()`을 사용하여 받은 blob 데이터를 브라우저에서 표시할 수 있는
 *      임시 URL로 변환합니다.
 *
 * 3. 상태 관리 및 반환:
 *    - `imageUrl`: 생성된 이미지의 임시 URL 주소.
 *    - `loading`: 이미지 생성 요청이 진행 중인지 여부.
 *    - `error`: 요청 중 발생한 에러.
 *    - `generateImage`: 이미지 생성을 트리거하는 함수.
 *    - 이들을 객체로 묶어 반환하여 컴포넌트에서 쉽게 사용할 수 있게 합니다.
 */
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