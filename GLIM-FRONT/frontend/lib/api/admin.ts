// /lib/api/admin.ts

import axiosInstance from '@/lib/axiosInstance';
import { User } from '@/types'; // 아래 2단계에서 만들 타입 정의

/**
 * [관리자] 모든 사용자 목록을 조회하는 API
 * GET /api/admin/members (백엔드와 엔드포인트 확인 필요!)
 */
export const getAllUsers = async (): Promise<User[]> => {
  try {
    const response = await axiosInstance.get('/admin/members');
    // 백엔드 응답 데이터 구조에 따라 .data.content 등으로 접근해야 할 수 있습니다.
    return response.data;
  } catch (error) {
    console.error("Failed to fetch all users:", error);
    // 에러 발생 시 빈 배열을 반환하여 페이지가 깨지지 않도록 합니다.
    return [];
  }
};