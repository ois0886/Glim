/**
 * src/utils/textToImage.ts
 *
 * 이 파일은 주어진 텍스트를 이미지로 변환하는 유틸리티 함수를 제공합니다.
 * HTML Canvas API를 사용하여 텍스트를 캔버스에 그린 후, 이를 이미지 데이터 URL로 변환합니다.
 *
 * 주요 기능:
 * 1. 텍스트 렌더링:
 *    - `textToImage` 함수는 텍스트, 이미지의 너비/높이, 폰트, 색상, 패딩 등을 인자로 받습니다.
 *    - 내부적으로 `HTMLCanvasElement`를 생성하고 2D 렌더링 컨텍스트를 가져옵니다.
 *    - 배경색을 채우고, 텍스트 속성을 설정한 후 텍스트를 캔버스에 그립니다.
 *
 * 2. 텍스트 줄바꿈 (Word Wrapping):
 *    - 주어진 너비 내에서 텍스트가 자동으로 줄바꿈되도록 처리합니다.
 *    - 각 단어의 길이를 측정하여 최대 너비를 초과하면 다음 줄로 넘깁니다.
 *
 * 3. 이미지 데이터 URL 반환:
 *    - 텍스트 렌더링이 완료된 캔버스를 `toDataURL()` 메소드를 사용하여
 *      Base64 인코딩된 이미지 데이터 URL (PNG 형식)로 변환하여 반환합니다.
 *    - 이 데이터 URL은 `<img>` 태그의 `src` 속성으로 직접 사용하거나,
 *      다른 이미지 처리 로직에 활용될 수 있습니다.
 *
 * 활용 예시:
 * - 동적으로 텍스트가 포함된 썸네일 이미지 생성.
 * - 서버 사이드 렌더링 없이 클라이언트에서 이미지 생성.
 */
export function textToImage(
  text: string,
  width: number,
  height: number,
  font: string = '48px Arial',
  textColor: string = '#000000',
  backgroundColor: string = '#ffffff',
  padding: number = 20
): string {
  const canvas = document.createElement('canvas');
  canvas.width = width;
  canvas.height = height;
  const ctx = canvas.getContext('2d');

  if (!ctx) {
    throw new Error('Failed to get 2D context');
  }

  // Fill background
  ctx.fillStyle = backgroundColor;
  ctx.fillRect(0, 0, width, height);

  // Set text properties
  ctx.font = font;
  ctx.fillStyle = textColor;
  ctx.textAlign = 'left'; // Align left for wrapping
  ctx.textBaseline = 'top'; // Align top for wrapping

  const words = text.split(' ');
  let line = '';
  const lines: string[] = [];
  const maxWidth = width - 2 * padding;
  const lineHeight = parseInt(font) * 1.2; // Estimate line height

  for (let n = 0; n < words.length; n++) {
    const testLine = line + words[n] + ' ';
    const metrics = ctx.measureText(testLine);
    const testWidth = metrics.width;
    if (testWidth > maxWidth && n > 0) {
      lines.push(line);
      line = words[n] + ' ';
    } else {
      line = testLine;
    }
  }
  lines.push(line); // Add the last line

  // Calculate total text height to center vertically
  const totalTextHeight = lines.length * lineHeight;
  let y = (height - totalTextHeight) / 2 + padding;

  for (let i = 0; i < lines.length; i++) {
    ctx.fillText(lines[i], padding, y);
    y += lineHeight;
  }

  return canvas.toDataURL();
}