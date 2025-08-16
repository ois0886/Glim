// /lib/authUtils.ts (최종 추천 버전)

/**
 * 클라이언트 측 로그아웃을 위해 로컬 스토리지에서 모든 인증 정보를 제거합니다.
 * 이 함수는 데이터 삭제 책임만 가집니다.
 * 페이지 이동(리다이렉트)은 이 함수를 호출하는 컴포넌트에서 담당해야 합니다.
 */
export const clearAuthData = () => { // ✅ 함수 이름을 역할에 맞게 변경: logout -> clearAuthData
  if (typeof window === 'undefined') {
    return;
  }
  
  console.log("Clearing authentication data from local storage.");

  // 로컬 스토리지에서 저장했던 모든 키들을 삭제
  localStorage.removeItem('accessToken');
  localStorage.removeItem('refreshToken');
  localStorage.removeItem('userInfo');
};