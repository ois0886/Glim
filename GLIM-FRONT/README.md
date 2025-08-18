# 글림 관리자 페이지 (Glim Admin) - 프론트엔드 설정 가이드

이 문서는 글림 관리자 페이지 프론트엔드 프로젝트의 설정 방법과 주요 아키텍처 결정 사항을 안내합니다.

## 1. 시작하기 (Getting Started)

### 1.1. 패키지 매니저

이 프로젝트는 **`pnpm`**을 사용하여 의존성을 관리합니다. `npm`이나 `yarn`을 사용할 경우 예기치 않은 오류가 발생할 수 있으니 반드시 `pnpm`을 사용해주세요.

```bash
# pnpm이 설치되어 있지 않다면 전역으로 설치합니다.
npm install -g pnpm
```

### 1.2. 프로젝트 설치 및 실행

```bash
# 1. 프로젝트에 필요한 모든 패키지를 설치합니다.
pnpm install

# 2. 로컬 개발 서버를 실행합니다.
pnpm run dev
```
서버가 시작되면 `http://localhost:3000` 주소로 접속할 수 있습니다.

## 2. 주요 아키텍처 및 문제 해결 기록

이 프로젝트는 배포 및 개발 과정에서 몇 가지 주요 이슈를 해결했으며, 그 결과가 현재 아키텍처에 반영되어 있습니다.

### 2.1. 백엔드 API 통신 및 CORS 이슈 해결(403)

-   **문제:** Netlify 배포 환경에서 백엔드 API 호출 시, 브라우저가 보내는 `Origin` 헤더로 인해 `403 Forbidden` 에러가 발생했습니다. Postman과 같이 `Origin` 헤더를 보내지 않는 환경에서는 정상적으로 통신이 가능했습니다.

-   **해결:** Netlify의 기본 프록시(`redirects`) 대신, **Netlify Function을 이용한 고급 프록시**를 구현하여 이 문제를 해결했습니다. 이 함수는 프론트엔드 앱으로부터 요청을 받아 백엔드로 전달하기 직전에, 문제를 일으키는 `Origin` 헤더를 강제로 삭제합니다.

    -   **관련 파일:**
        -   `netlify.toml`: 모든 `/api/*` 요청을 프록시 함수로 전달하도록 설정합니다.
        -   `netlify/functions/proxy.ts`: 헤더를 수정하고 실제 백엔드로 요청을 중계하는 로직이 담겨있습니다.

### 2.2. API 인증 키 관리

-   **문제:** 로그인 API 호출 시 특정 `Authorization` 헤더(고정 API 키)가 필요할 경우, 이를 소스 코드에 직접 작성(하드코딩)하는 것은 보안에 매우 취약합니다.

-   **해결:** **환경 변수(Environment Variables)**를 사용하여 문제를 해결했습니다.
    -   **로컬 개발:** 프로젝트 루트의 `.env.local` 파일에 키를 정의하여 사용합니다. 이 파일은 `.gitignore`에 포함되어 Git 저장소에 올라가지 않습니다.
      ```env
      # .env.local
      NEXT_PUBLIC_ADMIN_API_KEY=your_secret_api_key_here
      ```
    -   **Netlify 배포:** Netlify 대시보드의 `Site settings > Build & deploy > Environment` 메뉴에 동일한 이름(`NEXT_PUBLIC_ADMIN_API_KEY`)과 값으로 환경 변수를 설정해야 합니다.

### 2.3. 백엔드 독립적인 프론트엔드 개발 환경

-   **문제:** 주말 동안 백엔드 서버에 `500 Internal Server Error`가 발생하여 API 통신이 불가능했고, 이로 인해 프론트엔드 개발 및 테스트가 중단될 위기에 처했습니다.

-   **해결:** **Mock Service Worker (MSW)**를 도입하여 백엔드 서버의 상태와 관계없이 프론트엔드 개발을 계속할 수 있는 환경을 구축했습니다.
    -   **실행:** 개발 모드(`pnpm run dev`)에서는 MSW가 자동으로 실행되어, 실제 API와 동일한 경로 및 데이터 형식의 가짜 응답을 반환합니다.
    -   **활성화/비활성화:** `app/layout.tsx` 파일에서 `initMocks()` 함수의 호출부를 주석 처리하여 MSW를 쉽게 끄고 켤 수 있습니다. 이를 통해 실제 백엔드와의 통신 테스트와 Mock 환경에서의 UI 테스트를 자유롭게 전환할 수 있습니다.
    -   **관련 파일:**
        -   `mocks/handlers.ts`: 가짜 API의 응답을 정의하는 곳입니다.
        -   `mocks/browser.ts`: 브라우저에서 MSW를 설정하는 파일입니다.
        -   `public/mockServiceWorker.js`: MSW 서비스 워커 파일입니다.

---

이 문서는 2025년 8월 16일 토요일, 구미에서의 긴 디버깅 여정을 통해 완성되었습니다.