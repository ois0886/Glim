// /lib/api/curations.ts
import axiosInstance from '@/lib/axiosInstance';
import { CurationData } from '@/types'; // 나중에 타입을 모아둘 파일을 만들면 좋습니다.

export const getMainCuration = async (): Promise<CurationData> => {
  const response = await axiosInstance.get('/curations/main');
  return response.data;
};