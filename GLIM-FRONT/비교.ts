// /lib/axiosInstance.ts

import axios from 'axios';

const axiosInstance = axios.create({
  // .env.local 파일에서 백엔드 API 서버의 기본 주소를 읽어옵니다.
  baseURL: process.env.NEXT_PUBLIC_API_URL,
  timeout: 5000, // 5초 이상 응답이 없으면 요청 실패 처리
  headers: {
    'Content-Type': 'application/json',
  },
});

// 요청 인터셉터: 모든 요청이 보내지기 전에 이 코드를 거칩니다.
axiosInstance.interceptors.request.use(
  (config) => {
    // 브라우저의 localStorage에서 'accessToken'을 가져옵니다.
    const token = typeof window !== 'undefined' ? localStorage.getItem('accessToken') : null;

    // 토큰이 존재하면, 모든 요청 헤더에 Authorization을 추가합니다.
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    return config; // 수정된 설정으로 요청을 계속 진행
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default axiosInstance;