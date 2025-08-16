"use client";

import React, { useEffect, useRef } from 'react';
import styled from 'styled-components';
import { gsap } from 'gsap';

// HeroContentContainer 스타일 수정
const HeroContentContainer = styled.div`
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  opacity: 1;
  width: 100%; /* 컨테이너가 화면 전체 너비를 차지하도록 설정 */
  padding: 0 20px; /* 좌우 여백 추가 */
  transition: opacity 1s ease;

  &.hidden {
    opacity: 0;
  }
`;

const MainTitle = styled.h1`
  font-size: clamp(3rem, 8vw, 6rem);
  font-weight: 700;
  line-height: 1.1;
  letter-spacing: -0.04em;
  margin-bottom: 24px;
`;

// MainSubtitle 스타일 수정
const MainSubtitle = styled.p`
  font-size: 18px;
  max-width: 600px;
  color: var(--secondary-text-color);
  margin: 0 auto; /* p 태그 블록 자체를 가운데 정렬 */
`;

const HeroContent: React.FC = () => {
  const heroRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    // 4초 후에 서서히 사라지는 애니메이션
    if (heroRef.current) {
      gsap.to(heroRef.current, { opacity: 0, delay: 4, duration: 1, ease: 'power3.out' });
    }
  }, []);

  return (
    <HeroContentContainer ref={heroRef}>
      <MainTitle>Cosmos of Sentences</MainTitle>
      <MainSubtitle>클릭하여 집중하고, 드래그하여 회전하세요.</MainSubtitle>
    </HeroContentContainer>
  );
};

export default HeroContent;
