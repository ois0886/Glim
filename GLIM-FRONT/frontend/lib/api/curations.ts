import axiosInstance from '@/lib/axiosInstance';

// --- API 응답을 위한 타입 정의 ---
interface CurationContentItem {
  bookId: number | null;
  bookTitle: string;
  author: string;
  publisher: string;
  bookCoverUrl: string | null;
  quoteId: number | null;
  imageName: string | null;
  content?: string; 
}

export interface ApiCuration {
  curationItemId: number;
  title: string;
  description: string;
  curationType: string;
  contents: CurationContentItem[];
}

// --- Curation Create/Update Payloads ---
export interface CurationMutationPayload {
  name: string;
  description: string;
  curationType: 'BOOK' | 'QUOTE';
  bookIds: number[];
  quoteIds: number[];
}

// --- API 호출 함수 ---

// Admin 메인 큐레이션 목록 조회
export const getCurations = async (): Promise<ApiCuration[]> => {
  const response = await axiosInstance.get('/api/v1/admin/curations/main');
  return response.data;
};

// 특정 큐레이션 상세 조회
export const getCurationById = async (id: string): Promise<ApiCuration> => {
  // Admin용 상세 조회 엔드포인트는 /items/{id} 일 가능성이 높습니다.
  const response = await axiosInstance.get(`/api/v1/admin/curations/items/${id}`); 
  return response.data;
};

// 큐레이션 생성
export const createCuration = async (payload: CurationMutationPayload): Promise<any> => {
  const response = await axiosInstance.post('/api/v1/admin/curations', payload);
  return response.data;
};

// 큐레이션 수정 
export const updateCuration = async (itemId: string, payload: CurationMutationPayload): Promise<void> => {
  await axiosInstance.put(`/api/v1/admin/curations/items/${itemId}`, payload);
};
// 큐레이션 삭제
export const deleteCuration = async (itemId: number): Promise<void> => {
  await axiosInstance.delete(`/api/v1/admin/curations/items/${itemId}`);
};