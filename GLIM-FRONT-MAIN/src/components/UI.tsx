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
            <a href="#">아카이브</a>
            <a href="#">커뮤니티</a>
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