// lib/imageUrl.ts
// 배포에서는 NEXT_PUBLIC_IMAGE_BASE(= 사이트 도메인) → https 로 나가고
// 로컬에서는 기본값(백엔드 HTTP)로 떨어지게 함
export const IMG_BASE =
  process.env.NEXT_PUBLIC_IMAGE_BASE ?? 'http://i13d202.p.ssafy.io:8080';

export function quoteImageUrl(fileName?: string) {
  if (!fileName) return '';
  // 백엔드 실제 경로 프리픽스에 맞춰서 조립
  return `${IMG_BASE}/images/${fileName}`;
}
