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

// [수정됨] 특정 큐레이션 상세 조회
export const getCurationById = async (id: string): Promise<ApiCuration> => {
  console.log(`Attempting to find curation with ID: ${id}`);
  
  // 1. API 명세서에 명시된 '전체 목록 조회' API를 호출합니다.
  const allCurations = await getCurations();

  // 2. 클라이언트에서 ID가 일치하는 큐레이션을 찾습니다.
  // API 응답의 curationItemId는 number 타입이므로, 파라미터로 받은 id(string)를 숫자로 변환하여 비교합니다.
  const numericId = parseInt(id, 10);
  const curation = allCurations.find(c => c.curationItemId === numericId);

  // 3. 만약 해당하는 큐레이션이 없다면 에러를 발생시킵니다.
  if (!curation) {
    throw new Error(`Curation with ID ${id} not found.`);
  }

  console.log("Found curation:", curation);
  return curation;
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

// 큐레이션 순서 변경
export const updateCurationOrder = async (orderedIds: number[]): Promise<void> => {
  // 메인 큐레이션 목록의 순서를 업데이트합니다.
  await axiosInstance.put('/api/v1/admin/curations/order', { orderedIds });
};

// 큐레이션 삭제
export const deleteCuration = async (itemId: number): Promise<void> => {
  await axiosInstance.delete(`/api/v1/admin/curations/items/${itemId}`);
};