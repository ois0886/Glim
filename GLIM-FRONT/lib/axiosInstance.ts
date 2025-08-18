// /lib/axiosInstance.ts

import axios from 'axios';
import { clearAuthData } from './authUtils'; // 이 파일이 있다면 그대로 둡니다.

const axiosInstance = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL,
  timeout: 5000,
  headers: {
    'Content-Type': 'application/json',
  },
});

axiosInstance.interceptors.request.use(
  (config) => {
    // ▼▼▼ 4. 다음 API 요청을 가로챘음을 알리는 로그 ▼▼▼
    console.log('--- 4. API 요청 가로채기 성공 (요청 주소:', config.url, ') ---');
    
    const token = typeof window !== 'undefined' ? localStorage.getItem('accessToken') : null;
    
    // ▼▼▼ 5. localStorage에서 토큰을 꺼내왔는지 확인하는 로그 ▼▼▼
    console.log('--- 5. localStorage에서 "accessToken" 이름으로 토큰 인출 시도 ---', token);
    
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
      // ▼▼▼ 6. 헤더에 토큰을 성공적으로 장착했음을 알리는 로그 ▼▼▼
      console.log('--- 6. 헤더에 토큰 장착 완료 ---');
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default axiosInstance;