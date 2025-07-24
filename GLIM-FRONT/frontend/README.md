# 글:림 관리자 대시보드

이 프로젝트는 Next.js와 shadcn/ui를 사용하여 구축된 '글:림' 서비스의 관리자 대시보드입니다.

## 🚀 시작하기

이 프로젝트를 로컬 환경에서 실행하기 위한 안내입니다.

### 사전 준비 (가상 환경 설정)

이 프로젝트는 특정 버전의 Node.js에서 실행되도록 구성되어 있습니다. 시스템에 여러 버전의 Node.js를 설치하고 프로젝트별로 버전을 쉽게 전환할 수 있도록 **Node.js 버전 관리자**를 사용하는 것을 강력히 권장합니다.

1.  **Node.js 버전 관리자 설치**
    -   **Windows**: nvm-windows를 다운로드하여 설치합니다. `nvm-setup.zip` 파일을 받아 설치를 진행하세요.
    -   **macOS / Linux**: 터미널에서 아래 명령어를 실행하여 nvm을 설치합니다.
        ```bash
        curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.7/install.sh | bash
        ```
    > 설치 후 터미널을 재시작해야 할 수 있습니다.

2.  **필요 도구**
    *   **Node.js**: `.nvmrc` 파일에 명시된 `v22.x` 버전을 사용합니다. (nvm이 자동으로 설치해줍니다.)
    *   **npm**: Node.js 설치 시 함께 설치됩니다.

### 설치 및 실행 방법

1.  **Node.js 버전 설정 (nvm 사용 시)**
    프로젝트 폴더로 이동한 후, 아래 명령어를 실행하여 `.nvmrc` 파일에 지정된 버전의 Node.js를 자동으로 설치하고 사용하도록 설정합니다.
    ```bash
    nvm install
    nvm use
    ```

2.  **환경 변수 설정 (`.env` 파일)**
    프로젝트 루트에 있는 `.env.example` 파일을 복사하여 `.env.local` 파일을 생성합니다. 이 파일에 API 키 등 필요한 환경 변수를 설정합니다.
    ```bash
    cp .env.example .env.local
    ```

2.  **의존성 라이브러리 설치**
    프로젝트에 필요한 모든 라이브러리(패키지)를 설치합니다.
    ```bash
    npm install
    ```

3.  **개발 서버 실행**
    개발용 서버를 실행하여 프로젝트를 확인합니다.
    ```bash
    npm run dev
    ```

4.  **브라우저에서 확인**
    서버가 성공적으로 실행되면, 웹 브라우저를 열고 다음 주소로 접속하세요:
    http://localhost:3000

## 📁 프로젝트 구조

프로젝트 폴더는 아래와 같은 구조로 이루어져 있습니다. 각 폴더와 파일이 어떤 역할을 하는지 알아두면 코드를 이해하는 데 큰 도움이 됩니다.

```
관리자 페이지_grok/
├── app/
│   ├── layout.tsx         # 전역 레이아웃, 폰트 및 메타데이터 설정
│   └── page.tsx           # 메인 대시보드 페이지 (각 섹션을 렌더링)
├── components/
│   ├── ui/                # 재사용 가능한 UI 컴포넌트 (shadcn/ui)
│   │   ├── calendar.tsx
│   │   ├── chart.tsx
│   │   ├── sidebar.tsx
│   │   └── ... (기타 UI 컴포넌트)
│   ├── app-sidebar.tsx      # 사이드바 메뉴 구성
│   ├── curation-editor.tsx  # 큐레이션 관리 섹션
│   ├── post-management.tsx  # 게시물 관리 섹션
│   ├── user-graphics.tsx# 사용자 인구통계 섹션
│   ├── user-management.tsx  # 사용자 관리 섹션
│   └── visitor-analytics.tsx# 방문자 분석 섹션
├── public/                # 정적 파일 (이미지, 폰트 등)
├── .nvmrc                 # Node.js 권장 버전 명시
├── package.json           # 프로젝트 의존성 및 스크립트 정의
├── tsconfig.json          # TypeScript 컴파일러 설정
└── README.md              # 프로젝트 설명 (현재 파일)
```


## 📂 주요 폴더

- **.next/**  
  Next.js가 내부적으로 사용하는 빌드 결과 폴더 (신경 안 써도 됨)

- **app/**  
  웹사이트의 실제 페이지와 전체 스타일, 레이아웃을 담당하는 폴더
    - **globals.css** : 모든 페이지에 공통 적용되는 CSS(스타일)
    - **layout.tsx** : 전체 페이지 뼈대(레이아웃), 헤더/푸터/폰트 등 설정
    - **loading.tsx** : 페이지 로딩(대기) 중 보여줄 UI
    - **page.tsx** : 사이트의 메인 대시보드(첫 화면) 구성

- **components/**  
  자주 쓰는 UI 부품(컴포넌트) 모음
    - **ui/** : 버튼, 차트 등 자잘한 기본 UI 컴포넌트
    - **app-sidebar.tsx** : 왼쪽 메뉴(사이드바)
    - **curation-editor.tsx** : 큐레이션(추천글/컨텐츠) 관리 UI
    - **post-management.tsx** : 게시글 목록, 관리 기능
    - **theme-provider.tsx** : 다크모드 등 테마 설정
    - **user-graphics.tsx** : 사용자 통계/그래프
    - **user-management.tsx** : 사용자 목록, 관리 기능
    - **visitor-analytics.tsx** : 방문자 분석/통계

- **hooks/**  
  자주 쓰는 ‘기능 코드’(React의 Hook)
    - **use-mobile.tsx** : 모바일 환경 감지
    - **use-toast.ts** : 알림(토스트) 기능

- **lib/**  
  프로젝트 전반에 쓰는 공용 함수 모음
    - **utils.ts** : 유틸리티 함수(여러 곳에서 쓸만한 도우미 코드)

- **node_modules/**  
  설치한 외부 라이브러리들이 저장되는 폴더 (직접 건드릴 필요 없음)

- **public/**  
  이미지, 아이콘, 샘플 데이터 등 외부에서 직접 접근 가능한 파일 보관
    - **placeholder-*.png/svg/jpg** : 샘플(임시) 이미지, 아이콘
    - **users.json** : 샘플 사용자 데이터

- **styles/**  
  CSS, 스타일 관련 파일 관리

---

## 🗂️ 주요 파일

- **.gitignore** : git(버전 관리)로 저장하지 않을 파일/폴더 목록  
- **API 명세서.html** : API(데이터 통신 방식) 설명 문서  
- **components.json** : 컴포넌트 관련 정보  
- **GEMINI.md** : Gemini(구글 AI) 관련 참고/설명 문서  
- **next-env.d.ts, next.config.mjs** : Next.js 동작 환경, 설정파일  
- **package.json** : 프로젝트 정보, 설치된 라이브러리, 실행 명령어 정의  
- **package-lock.json, pnpm-lock.yaml** : 설치된 라이브러리(버전) 잠금 정보 (자동 생성)  
- **postcss.config.mjs** : CSS(스타일) 전처리 설정  
- **README.md** : 이 프로젝트 설명서  
- **tailwind.config.ts** : TailwindCSS(디자인 프레임워크) 설정  
- **tsconfig.json** : TypeScript(타입 안전한 JS) 설정

---

# ✅ 정말 쉬운 요약

- **app/** : “실제 화면, 페이지 뼈대”
- **components/** : “각 화면을 만드는 작은 부품”
- **public/** : “이미지, 샘플 데이터”
- **hooks/** : “특별 기능(모바일 감지, 알림 등)”
- **lib/** : “여러 군데서 쓰는 함수 모음”
- **styles/** : “추가 스타일”

> ⚡️ **딱히 직접 손댈 건 거의 없고, 보통은 `app/`, `components/`, `public/`만 다루면 됨!**  
> 궁금한 파일/폴더 있으면 언제든 질문해도 OK!





## 📜 사용 가능한 스크립트

*   `npm run dev`: 개발 모드로 Next.js 애플리케이션을 실행합니다.
*   `npm run build`: 프로덕션(배포)용으로 애플리케이션을 빌드합니다.
*   `npm run start`: 빌드된 프로덕션 애플리케이션을 실행합니다.
*   `npm run lint`: ESLint를 사용하여 코드 스타일을 검사하고 문제를 찾습니다.