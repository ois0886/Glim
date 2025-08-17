/**
 * src/components/HeroContent.tsx
 *
 * 이 파일은 메인 페이지의 핵심 콘텐츠를 표시하는 React 컴포넌트입니다.
 * '문장의 우주'라는 제목과 설명, 그리고 여러 책 이미지를 그리드 형태로 보여줍니다.
 *
 * 주요 기능:
 * 1. 이미지 목록을 받아와서 그리드 레이아웃으로 렌더링합니다.
 * 2. 처음에는 저화질의 플레이스홀더 이미지를 보여주고, 이미지를 클릭하면
 *    고화질의 원본 이미지로 부드럽게 교체되는 'Lazy Loading'과 유사한 기능을 구현했습니다.
 *    (정확히는 클릭 시 로딩이므로 'Click-to-load'에 가깝습니다.)
 * 3. 이미지 로딩 상태를 'loadedImages'라는 state로 관리합니다.
 */
import React, { useState } from 'react';

interface ImageItem {
  id: string;
  src: string;
  placeholderSrc: string;
}

interface HeroContentProps {
  images: ImageItem[];
}

const HeroContent: React.FC<HeroContentProps> = ({ images }) => {
  const [loadedImages, setLoadedImages] = useState<Set<string>>(new Set());

  const handleImageClick = (id: string) => {
    setLoadedImages(prev => new Set(prev).add(id));
  };

  return (
    <div className="hero-content" style={{ padding: '20px', textAlign: 'center' }}>
      <h1>Cosmos of Sentences</h1>
      <p>마우스를 드래그하여 회전하고, 휠로 확대/축소하며 문장들의 우주를 탐험하세요.</p>

      <div style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fill, minmax(150px, 1fr))',
        gap: '15px',
        marginTop: '30px',
        maxWidth: '1200px',
        margin: '30px auto',
      }}>
        {images.map(image => (
          <div key={image.id} style={{
            border: '1px solid #333',
            borderRadius: '8px',
            overflow: 'hidden',
            backgroundColor: '#222',
            cursor: 'pointer',
            boxShadow: '0 4px 8px rgba(0,0,0,0.2)',
            transition: 'transform 0.2s',
          }} onClick={() => handleImageClick(image.id)}>
            <img
              src={loadedImages.has(image.id) ? image.src : image.placeholderSrc}
              alt={`Book ${image.id}`}
              style={{
                width: '100%',
                height: '200px',
                objectFit: 'cover',
                display: 'block',
                filter: loadedImages.has(image.id) ? 'none' : 'blur(5px)',
                transition: 'filter 0.5s ease-out',
              }}
            />
          </div>
        ))}
      </div>
    </div>
  );
};

export default HeroContent;