// /lib/api/admin.ts

import axiosInstance from '../axiosInstance';

// --- 타입 정의 ---
export interface User {
  memberId: number;
  nickname: string;
  email: string;
  birthDate: string;
  status: "ACTIVE" | "INACTIVE";
  gender: "MALE" | "FEMALE";
}

// [추가] 서버가 페이지네이션 응답을 줄 경우를 대비한 타입
export interface UserApiResponse {
  content: User[];
  // 필요 시 다른 페이지네이션 필드 추가
}

export interface UserUpdatePayload {
  nickname: string | null;
  birthDate: number[] | null;
  gender: "MALE" | "FEMALE" | null;
  password?: string | null;
}

/**
 * [수정] 관리자: 사용자 목록을 조회합니다.
 * - 서버가 'page', 'size'를 필수로 요구하는 상황으로 판단하고, 해당 파라미터를 요청에 포함시킵니다.
 * - 서버 응답이 User[]일 수도, { content: User[] } 형태일 수도 있으므로 둘 다 처리합니다.
 */
export const getUsers = async (): Promise<User[]> => {
  try {
    // 서버가 필수적으로 요구할 것으로 추정되는 기본 페이지네이션 파라미터
    const params = {
      page: 0,
      size: 20, // 넉넉하게 20개씩 조회
    };

    // [핵심] 다른 Admin API들처럼 search-keywords 경로를 사용하는 것이 더 정확할 수 있습니다.
    // 우선 members 경로로 시도하고, 안될 경우 아래 주석 처리된 경로로 변경해야 합니다.
    const response = await axiosInstance.get('/api/v1/admin/members', { params });
    // const response = await axiosInstance.get('/api/v1/admin/search-keywords/members', { params });

    const data = response.data;

    // 서버 응답이 { content: [...] } 형태인지, 아니면 그냥 [...] 형태인지 확인 후 반환
    if (data && Array.isArray(data.content)) {
      return data.content;
    }
    if (Array.isArray(data)) {
      return data;
    }
    
    return []; // 예상치 못한 구조일 경우 빈 배열 반환
  } catch (error) {
    console.error("Failed to fetch users with params:", error);
    return [];
  }
};

export const updateUser = async (memberId: number, userData: UserUpdatePayload): Promise<User> => {
  const response = await axiosInstance.put<User>(`/api/v1/admin/members/${memberId}`, userData);
  return response.data;
};

export const deleteUser = async (memberId: number): Promise<User> => {
  const response = await axiosInstance.patch<User>(`/api/v1/admin/members/${memberId}/status`);
  return response.data;
};