// src/mocks/handlers.ts

import { http, HttpResponse } from 'msw';


const mockAdminDB = [
  {
    email: 'ee475320@gmail.com',
    password: 'Ee475320!', // 실제 비밀번호를 여기에 적어둡니다.
    // 기타 정보
    memberId: 31,
    nickname: '테스트관리자'
  }
];

// 가짜 유저 데이터 (이전에 만드신 User 타입을 활용합니다)
const mockUsers = [
  { memberId: 1, nickname: '개발자A', email: 'devA@test.com', birthDate: '1990-01-01', status: 'ACTIVE', gender: 'MALE' },
  { memberId: 2, nickname: '디자이너B', email: 'designB@test.com', birthDate: '1992-05-10', status: 'ACTIVE', gender: 'FEMALE' },
  { memberId: 3, nickname: '기획자C', email: 'planC@test.com', birthDate: '1988-11-20', status: 'INACTIVE', gender: 'MALE' },
];

export const handlers = [
  // --- ▼▼▼ 1. 기존 로그인 핸들러를 아래 코드로 교체합니다. ▼▼▼ ---
  http.post('/api/v1/auth/admin/login', async ({ request }) => {
    // 1. 요청에서 이메일과 비밀번호를 추출합니다.
    const credentials = await request.json() as { email?: string; password?: string };
    console.log('MSW: 로그인 시도 (업그레이드 버전)', credentials);

    // 2. 가짜 DB에서 이메일로 사용자를 찾습니다.
    const foundUser = mockAdminDB.find(user => user.email === credentials.email);

    // 3. 사용자를 찾을 수 없으면, 404 에러를 보냅니다.
    if (!foundUser) {
      return new HttpResponse('User not found', { status: 404 });
    }

    // 4. 비밀번호가 틀리면, 401 (Unauthorized) 에러를 보냅니다.
    if (foundUser.password !== credentials.password) {
      return new HttpResponse('Invalid credentials', { status: 401 });
    }

    // 5. 이메일과 비밀번호가 모두 맞으면, 200 성공과 함께 가짜 토큰을 보냅니다.
    return HttpResponse.json({
      accessToken: 'fake-jwt-token-from-smart-msw-67890',
      refreshToken: 'fake-refresh-token-from-smart-msw',
      memberEmail: foundUser.email,
      memberId: foundUser.memberId,
    });
  }),

  // 2. 사용자 목록 조회 API 모킹 (인증 제거)
  http.get('/api/v1/admin/members', ({ request }) => {
    console.log('MSW: 사용자 목록 조회 성공');
    return HttpResponse.json({
        data: mockUsers // API 응답 형식을 data로 통일
    });
  }),

  // 3. 명언/글귀 검색 API 모킹 (인증 제거)
  http.get('/api/v1/admin/search-keywords/quotes', ({ request }) => {
    console.log('MSW: 명언/글귀 목록 조회 성공');
    return HttpResponse.json({
      data: [
        { id: 1, content: '가장 큰 위험은 아무런 위험도 감수하지 않으려는 것이다.', author: '마크 저커버그' },
        { id: 2, content: '나는 실패한 게 아니다. 나는 잘 되지 않는 방법 1만 가지를 발견한 것이다.', author: '토머스 에디슨' },
        { id: 3, content: '성공의 비결은 단 한 가지, 잘할 수 있는 일에 광적으로 집중하는 것이다.', author: '톰 모나건' },
      ]
    });
  }),

  // 4. 메인 큐레이션 목록 조회 API 모킹 (인증 제거)
  http.get('/api/v1/admin/curations/main', ({ request }) => {
    console.log('MSW: 큐레이션 목록 조회 성공');
    return HttpResponse.json({
      data: [
        { curationId: 1, title: '오늘의 추천 큐레이션', description: '사용자들에게 영감을 주는 글귀 모음입니다.' },
        { curationId: 2, title: '성공에 대한 명언', description: '도전 정신을 일깨우는 명언들을 만나보세요.' },
      ]
    });
  }),
];
