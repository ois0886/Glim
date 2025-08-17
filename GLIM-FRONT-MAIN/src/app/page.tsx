/**
 * src/app/page.tsx
 *
 * 이 파일은 웹사이트의 메인 페이지('/')를 정의하는 컴포넌트입니다.
 * 'use client' 지시어는 이 컴포넌트가 클라이언트 사이드에서 렌더링되도록 설정합니다.
 * 이는 사용자와의 상호작용(예: 마우스 드래그, 휠)이 필요하기 때문입니다.
 *
 * 현재는 Scene 컴포넌트와 UI 레이어를 직접 포함하고 있지만,
 * layout.tsx에서 이미 Scene과 Header를 렌더링하고 있으므로 중복될 수 있습니다.
 * HeroContent 컴포넌트를 사용하여 콘텐츠를 분리하는 것이 좋습니다.
 */
'use client';

import React from 'react';
import Scene from '../components/Scene';

export default function Home() {
  return (
    <main>
      <div className="ui-layer">
        <header>
            <div className="header-content">
                <a href="#" className="logo">GLIM</a>
                <nav>
                    <a href="https://m.onestore.co.kr/v2/ko-kr/app/0001001790">다운로드</a>

                </nav>
            </div>
        </header>

        <div className="hero-content">
            <h1>문장의 우주</h1>
            <p>마우스를 드래그하여 회전하고, 휠로 확대/축소하며 문장들의 우주를 탐험하세요.</p>
        </div>
      </div>
    </main>
  );
}