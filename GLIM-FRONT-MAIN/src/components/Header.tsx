"use client";

import React from 'react';
import styled from 'styled-components';

// 헤더 전체를 감싸는 컨테이너입니다.
// position: fixed와 z-index를 추가하여 화면 상단에 고정시켰습니다.
const HeaderContainer = styled.header`
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  padding: 0 40px;
  pointer-events: all;
  z-index: 100;
`;

// 로고와 내비게이션 메뉴를 담는 영역입니다.
// 기존 코드의 className="container" 부분을 제거하여 styled-component가 올바르게 적용되도록 했습니다.
const HeaderContent = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 80px;
  border-bottom: 1px solid var(--border-color); /* GlobalStyles.ts에 정의된 변수를 사용합니다. */
  max-width: 1100px; /* 콘텐츠가 너무 넓어지지 않도록 최대 너비를 설정합니다. */
  margin: 0 auto; /* 중앙 정렬 */
`;

// 로고 스타일입니다.
const Logo = styled.a`
  font-weight: 700;
  font-size: 24px;
  text-decoration: none;
  color: var(--primary-text-color);
`;

// 내비게이션 메뉴 스타일입니다.
// 각 a 태그에 margin을 추가하여 링크 사이에 간격을 만들었습니다.
const Nav = styled.nav`
  a {
    margin: 0 16px; /* 링크 좌우에 16px의 간격을 줍니다. */
    text-decoration: none;
    color: var(--secondary-text-color);
    font-weight: 500;
    font-size: 15px;
    transition: color 0.3s ease;

    &:hover {
      color: var(--primary-text-color);
    }
  }
`;

const Header: React.FC = () => {
  return (
    <HeaderContainer>
      <HeaderContent>
        <Logo href="#">GLIM</Logo>
        <Nav>
          {/* 요청하신 대로 내비게이션 메뉴를 추가했습니다. */}
          <a href="#">다운로드</a>
        </Nav>
      </HeaderContent>
    </HeaderContainer>
  );
};

export default Header;
