/**
 * src/hooks/useImageListData.ts
 *
 * 이 훅은 `public/imageList.json` 파일로부터 이미지 목록 데이터를 가져오는 역할을 합니다.
 * 이 JSON 파일은 프로젝트에서 사용되는 이미지들의 ID, 원본 경로, 플레이스홀더 경로를 포함합니다.
 *
 * 주요 기능:
 * 1. 로컬 JSON 데이터 Fetching:
 *    - `fetch` API를 사용하여 `public` 디렉토리에 있는 `imageList.json` 파일을 비동기적으로 읽어옵니다.
 *    - 이 방식은 외부 API 호출 없이 정적 데이터를 로드할 때 유용합니다.
 *
 * 2. 데이터 구조:
 *    - `ImageItem` 인터페이스는 각 이미지 항목의 구조를 정의합니다.
 *      - `id`: 이미지의 고유 식별자.
 *      - `src`: 원본 이미지 파일의 경로.
 *      - `placeholderSrc`: 로딩 중이나 미리보기에 사용될 저해상도 이미지의 경로.
 *
 * 3. 상태 관리 및 반환:
 *    - `images`: 로드된 이미지 항목들의 배열.
 *    - `loading`: 데이터 로딩 중인지 여부.
 *    - `error`: 로딩 중 발생한 에러 객체.
 *    - 이들을 객체로 묶어 반환하여 컴포넌트에서 이미지 목록을 쉽게 사용할 수 있게 합니다.
 */
import { useState, useEffect } from 'react';

interface ImageItem {
  id: string;
  src: string;
  placeholderSrc: string;
}

interface UseImageListDataReturn {
  images: ImageItem[];
  loading: boolean;
  error: Error | null;
}

const useImageListData = (): UseImageListDataReturn => {
  const [images, setImages] = useState<ImageItem[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<Error | null>(null);

  useEffect(() => {
    const fetchImages = async () => {
      try {
        setLoading(true);
        // Assuming imageList.json is in the public directory and accessible directly
        const response = await fetch('/imageList.json');
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data: ImageItem[] = await response.json();
        setImages(data);
      } catch (err) { // 'any' 타입을 제거하면 기본적으로 'unknown'으로 처리됩니다.
        if (err instanceof Error) {
          setError(err);
        } else {
          // err이 Error 객체가 아닐 경우를 대비해 새로운 Error 객체를 생성합니다.
          setError(new Error(String(err)));
        }
        console.error("Error fetching image list:", err);
      }
    };

    fetchImages();
  }, []);

  return { images, loading, error };
};

export default useImageListData;