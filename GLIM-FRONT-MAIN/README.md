# Project Glim (next-gongtong)

안녕하세요! 이 프로젝트는 Next.js와 Three.js를 사용하여 만들어진 인터랙티브 웹 애플리케이션입니다.
책과 관련된 이미지와 인용구를 3D 공간에 시각적으로 표현하는 것을 목표로 합니다.

## 시작하기

먼저, 개발 서버를 실행해 주세요:

```bash
npm run dev
```

브라우저에서 [http://localhost:3000](http://localhost:3000)을 열어 결과를 확인하세요.

`src/app/page.tsx` 파일을 수정하여 페이지를 편집할 수 있습니다. 파일은 수정 시 자동으로 업데이트됩니다.

## 주요 기술 스택

*   **Framework**: [Next.js](https://nextjs.org/)
*   **3D Graphics**: [Three.js](https://threejs.org/), [@react-three/fiber](https://docs.pmnd.rs/react-three-fiber/getting-started/introduction), [@react-three/drei](https://github.com/pmndrs/drei)
*   **Animation**: [GSAP](https://gsap.com/)
*   **Styling**: [Styled Components](https://styled-components.com/), CSS Modules
*   **Language**: [TypeScript](https://www.typescriptlang.org/)

## 디렉토리 및 파일 구조 설명

이 프로젝트는 다음과 같은 구조로 이루어져 있습니다. 각 파일과 폴더의 역할은 다음과 같습니다.

---

### 최상위 디렉토리 (`/`)

*   `.gitignore`: Git 버전 관리에서 제외할 파일 및 폴더 목록을 정의합니다. (예: `node_modules`)
*   `next.config.ts`: Next.js 프로젝트의 설정을 변경하는 파일입니다. (예: 이미지 호스트 설정)
*   `package.json`: 프로젝트의 이름, 버전, 의존성 라이브러리 목록 및 실행 스크립트를 정의합니다.
*   `package-lock.json`: 설치된 의존성 라이브러리의 정확한 버전을 기록하여 일관된 설치를 보장합니다.
*   `README.md`: 현재 보고 계신 파일로, 프로젝트에 대한 전반적인 설명을 담고 있습니다.
*   `tsconfig.json`: TypeScript 컴파일러의 설정을 정의합니다.
*   `eslint.config.mjs`: 코드 스타일과 문법 오류를 검사하는 ESLint의 설정 파일입니다.
*   `imageList.json`: 프로젝트에서 사용하는 이미지 파일들의 목록을 담고 있는 JSON 파일입니다.
*   `레퍼런스.html`, `오류.txt`, `GEMINI 답.html`: 개발 과정에서 참고한 자료나 발생한 오류, Gemini를 통해 얻은 답변을 기록한 파일입니다.

### `public/`

브라우저에서 직접 접근할 수 있는 정적 파일들을 보관하는 폴더입니다.

*   `images/`: 프로젝트에서 사용하는 이미지 파일들이 저장되어 있습니다.
    *   `books/`: 원본 책 이미지 파일들입니다.
    *   `placeholders/`: 저해상도의 플레이스홀더 이미지 파일들입니다.
*   `*.svg`: 아이콘과 같은 SVG 이미지 파일입니다.
*   `imageList.json`: 이미지 목록 데이터입니다.

### `src/`

프로젝트의 핵심 소스 코드가 위치하는 폴더입니다.

*   **`app/`**: Next.js의 App Router를 사용하는 기본 폴더입니다.
    *   `api/`: API 라우트를 정의하는 폴더입니다.
        *   `quotes/route.ts`: `/api/quotes` 엔드포인트에 대한 서버 로직을 처리합니다.
    *   `layout.tsx`: 모든 페이지에 공통으로 적용되는 기본 레이아웃 컴포넌트입니다.
    *   `page.tsx`: 메인 페이지 (`/`)를 구성하는 기본 페이지 컴포넌트입니다.
    *   `globals.css`: 전역적으로 적용되는 CSS 스타일을 정의합니다.
    *   `page.module.css`: 메인 페이지에만 적용되는 CSS 모듈 스타일입니다.

*   **`components/`**: 재사용 가능한 React 컴포넌트들을 모아놓은 폴더입니다.
    *   `Header.tsx`: 웹사이트의 헤더(머리말) 부분을 담당하는 컴포넌트입니다.
    *   `HeroContent.tsx`: 메인 페이지의 핵심 콘텐츠를 보여주는 컴포넌트입니다.
    *   `Scene.tsx`: Three.js를 사용하여 3D 장면을 렌더링하는 핵심 컴포넌트입니다.
    *   `UI.tsx`: 사용자 인터페이스(UI) 요소들을 모아놓은 컴포넌트입니다.

*   **`hooks/`**: 반복되는 로직을 재사용하기 위한 React Custom Hook들을 모아놓은 폴더입니다.
    *   `useApiData.ts`, `useCurationData.ts`, `useImageListData.ts`, `useQuoteDetail.ts`, `useQuoteList.ts`: 다양한 종류의 데이터를 가져오고 관리하는 훅입니다.
    *   `useGPTImage.ts`: GPT와 관련된 이미지 처리 로직을 담고 있는 훅으로 추정됩니다.

*   **`lib/`**: 특정 프레임워크에 종속되지 않는 라이브러리나 유틸리티 함수를 모아놓은 폴더입니다.
    *   `registry.tsx`: Styled Components와 같은 라이브러리 설정을 위한 파일일 수 있습니다.

*   **`utils/`**: 프로젝트 전반에서 사용되는 유틸리티 함수들을 모아놓은 폴더입니다.
    *   `textToImage.ts`: 텍스트를 이미지로 변환하거나 관련 처리를 하는 유틸리티 함수로 추정됩니다.

### `scripts/`

개발 과정에서 필요한 스크립트 파일들을 보관하는 폴더입니다.

*   `1-fetch-image-list.js`: 이미지 목록을 가져오는 스크립트입니다.
*   `2-process-images.js`: 가져온 이미지를 처리(예: 리사이징, 플레이스홀더 생성)하는 스크립트입니다.