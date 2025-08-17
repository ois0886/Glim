/**
 * src/components/UI.tsx
 *
 * 이 파일은 `styled-components`를 사용하여 UI 요소들의 스타일을 정의하고,
 * 이들을 조합하여 전체 UI 레이어를 구성하는 React 컴포넌트입니다.
 *
 * 주요 기능 및 구조:
 *
 * 1. Styled Components:
 *    - `UILayer`: 3D 캔버스 위에 UI 요소들을 배치하기 위한 최상위 컨테이너입니다.
 *                 `pointer-events: none;`으로 설정하여 3D 씬의 마우스 이벤트를 방해하지 않도록 합니다.
 *    - `Header`, `HeaderContent`, `Logo`, `Nav`: 헤더 영역의 스타일을 정의합니다.
 *    - `HeroContent`, `H1`, `P`: 메인 콘텐츠(제목, 설명)의 스타일을 정의합니다.
 *
 * 2. UI 컴포넌트:
 *    - 위에서 정의한 스타일 컴포넌트들을 조합하여 최종 UI 레이아웃을 만듭니다.
 *    - 이 컴포넌트는 `Header`와 `HeroContent`를 포함하며,
 *      `layout.tsx`나 `page.tsx`에서 사용될 수 있습니다.
 *    - 현재는 `Header.tsx`와 `HeroContent.tsx` 컴포넌트와 유사한 구조를 가지고 있어,
 *      프로젝트 리팩토링 시 역할 구분이 필요할 수 있습니다.
 */
import React from 'react';
import styled from 'styled-components';

const UILayer = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none; 
  display: flex;
  flex-direction: column;
  justify-content: space-between;
`;

const Header = styled.header`
  width: 100%;
  padding: 0 40px;
  pointer-events: all;
`;

const HeaderContent = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 80px;
  border-bottom: 1px solid var(--border-color);
`;

const Logo = styled.a`
  font-weight: 700;
  font-size: 24px;
`;

const Nav = styled.nav`
  a {
    margin: 0 16px;
    color: var(--secondary-text-color);
    font-weight: 500;
    font-size: 15px;
    transition: color 0.3s ease;

    &:hover {
        color: var(--primary-text-color);
    }
  }
`;

const HeroContent = styled.div`
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
`;

const H1 = styled.h1`
  font-size: clamp(3rem, 8vw, 6rem);
  font-weight: 700;
  line-height: 1.1;
  letter-spacing: -0.04em;
  margin-bottom: 24px;
`;

const P = styled.p`
  font-size: 18px;
  max-width: 550px;
  color: var(--secondary-text-color);
`;

const UI: React.FC = () => {
  return (
    <UILayer>
      <Header>
        <HeaderContent>
          <Logo href="#">GLIM</Logo>
          <Nav>
            <a href="https://m.onestore.co.kr/v2/ko-kr/app/0001001790">다운로드</a>
          </Nav>
        </HeaderContent>
      </Header>

      <HeroContent>
        <H1>Cosmos of Sentences</H1>
        <P>마우스를 드래그하여 회전하고, 휠로 확대/축소하며 문장들의 우주를 탐험하세요.</P>
      </HeroContent>
    </UILayer>
  );
};

export default UI;