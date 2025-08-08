// /lib/axiosInstance.ts

import axios from 'axios';

/**
 * 프로젝트 전역에서 사용할 axios 인스턴스입니다.
 * 모든 API 요청은 이 인스턴스를 통해 이루어집니다.
 */
const axiosInstance = axios.create({
  /**
   * API 서버의 기본 URL 주소입니다.
   * 이 값은 .env.local 파일에 정의된 NEXT_PUBLIC_API_URL 환경 변수에서 가져옵니다.
   * 이렇게 하면 실제 주소가 코드에 노출되지 않아 안전합니다.
   */
  baseURL: '/api',

  // 요청이 5초 이상 걸리면 타임아웃 처리합니다.
  timeout: 5000,

  // 모든 요청에 기본적으로 포함될 헤더입니다.
  headers: {
    'Content-Type': 'application/json',
  },
});

/**
 * 요청 인터셉터 (Request Interceptor)
 *
 * 모든 API 요청이 백엔드 서버로 전송되기 직전에 이 함수를 거칩니다.
 * 여기에 '인증 토큰'을 자동으로 헤더에 추가하는 로직을 넣으면,
 * 매번 API를 호출할 때마다 토큰을 직접 넣어줄 필요가 없어 매우 편리합니다.
 */
axiosInstance.interceptors.request.use(
  (config) => {
    // 1. 브라우저의 localStorage에서 저장된 'accessToken'을 가져옵니다.
    //    (로그인 성공 시 토큰을 localStorage에 저장해야 합니다.)
    const token = typeof window !== 'undefined' ? localStorage.getItem('accessToken') : null;

    // 2. 토큰이 존재한다면...
    if (token) {
      // ...모든 요청의 헤더(headers)에 'Authorization' 항목을 추가합니다.
      // 'Bearer'는 토큰의 종류를 나타내는 표준적인 접두사입니다.
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    // 3. 수정된 설정(config)으로 요청을 계속 진행시킵니다.
    return config;
  },
  (error) => {
    // 요청 설정 중 에러가 발생하면 여기서 처리합니다.
    return Promise.reject(error);
  }
);

export default axiosInstance;