/**
 * src/components/Header.tsx
 *
 * 이 파일은 웹사이트의 헤더(머리말) 영역을 정의하는 React 컴포넌트입니다.
 * 로고와 네비게이션 링크(예: 다운로드)를 포함하고 있습니다.
 * 'use client' 지시어는 이 컴포넌트가 클라이언트 사이드에서 렌더링되도록 합니다.
 * 다른 컴포넌트에서 재사용할 수 있도록 만들어졌습니다.
 */
"use client";

import React from 'react';

const Header: React.FC = () => {
  return (
    <header>
      <div className="header-content">
        <a href="#" className="logo">GLIM</a>
        <nav>
          <a href="https://m.onestore.co.kr/v2/ko-kr/app/0001001790">다운로드</a>
        </nav>
      </div>
    </header>
  );
};

export default Header;