/**
 * src/lib/registry.tsx
 *
 * 이 파일은 Next.js 환경에서 `styled-components` 라이브러리를 서버 사이드 렌더링(SSR)과
 * 클라이언트 사이드 렌더링(CSR) 모두에서 올바르게 작동하도록 설정하는 역할을 합니다.
 *
 * 주요 기능:
 * 1. 스타일 주입 (Server-Side Rendering):
 *    - `ServerStyleSheet`를 사용하여 서버에서 렌더링될 때 생성되는 스타일을 수집합니다.
 *    - `useServerInsertedHTML` 훅을 통해 서버에서 렌더링된 HTML에 수집된 스타일을 `<style>` 태그로 주입합니다.
 *    - 이를 통해 초기 로드 시 스타일이 적용되지 않아 깜빡이는 현상(FOUC: Flash Of Unstyled Content)을 방지합니다.
 *
 * 2. 스타일 관리 (Client-Side Rendering):
 *    - 브라우저 환경(`typeof window !== 'undefined'`)에서는 별도의 스타일 수집 없이 자식 컴포넌트를 직접 렌더링합니다.
 *    - `StyleSheetManager`는 `styled-components`가 스타일을 관리하는 데 필요한 컨텍스트를 제공합니다.
 *
 * 3. 재사용성:
 *    - 이 컴포넌트는 Next.js의 `RootLayout` (예: `src/app/layout.tsx`)과 같은 최상위 컴포넌트에서
 *      자식 컴포넌트들을 감싸는 형태로 사용되어야 합니다.
 *    - 이를 통해 프로젝트 전체에서 `styled-components`가 원활하게 작동하도록 보장합니다.
 */
'use client';

import React, { useState } from 'react';
import { useServerInsertedHTML } from 'next/navigation';
import { ServerStyleSheet, StyleSheetManager } from 'styled-components';

export default function StyledComponentsRegistry({
  children,
}: { children: React.ReactNode }) {
  // Only create stylesheet once per request
  const [styledComponentsStyleSheet] = useState(() => new ServerStyleSheet());

  useServerInsertedHTML(() => {
    const styles = styledComponentsStyleSheet.getStyleElement();
    styledComponentsStyleSheet.instance.clearTag();
    return <>{styles}</>;
  });

  if (typeof window !== 'undefined') return <>{children}</>;

  return (
    <StyleSheetManager sheet={styledComponentsStyleSheet.instance}>
      {children}
    </StyleSheetManager>
  );
}