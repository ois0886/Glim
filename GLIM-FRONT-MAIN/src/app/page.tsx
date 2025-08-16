'use client';

import styled from 'styled-components';
import Scene from '../components/Scene';
import Header from '../components/Header';
import HeroContent from '../components/HeroContent';

import useApiData from '../hooks/useApiData';

const AppContainer = styled.div`
  width: 100%;
  height: 100%;
  background-color: var(--bg-color);
`;

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

const MessageContainer = styled.div`
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  font-size: 24px;
  font-family: 'Pretendard, sans-serif';
  text-align: center;
`;

export default function Home() {
  const { data, loading, error } = useApiData();

  if (loading) {
    return (
      <AppContainer>
        <MessageContainer>Loading Cosmos...</MessageContainer>
        <UILayer>
          <Header />
          <HeroContent />
        </UILayer>
      </AppContainer>
    );
  }

  if (error) {
    return (
      <AppContainer>
        <MessageContainer>Error fetching data. Please try again.</MessageContainer>
        <UILayer>
          <Header />
          <HeroContent />
        </UILayer>
      </AppContainer>
    );
  }

  return (
    <AppContainer>
      <Scene data={data} />
      <UILayer>
        <Header />
        <HeroContent />
      </UILayer>
    </AppContainer>
  );
}