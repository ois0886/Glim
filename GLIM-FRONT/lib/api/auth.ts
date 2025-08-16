// /lib/api/auth.ts

import axiosInstance from '@/lib/axiosInstance';

export const login = async (credentials: { email: string; password: string }) => {
  try {
    // ▼▼▼ headers 부분을 완전히 삭제합니다 ▼▼▼
    const response = await axiosInstance.post('/api/v1/auth/admin/login', credentials);
    
    // ... 이하 코드는 그대로 둡니다 ...
    const { accessToken, refreshToken, memberEmail, memberId } = response.data;

    if (accessToken) {
      localStorage.setItem('accessToken', accessToken);
      if (refreshToken) {
        localStorage.setItem('refreshToken', refreshToken);
      }
      const userInfo = { email: memberEmail, id: memberId };
      localStorage.setItem('userInfo', JSON.stringify(userInfo));
    } else {
      throw new Error("Login response did not contain an access token.");
    }
    
    return response.data;

  } catch (error) {
    console.error("Login API call failed:", error);
    throw error;
  }
};