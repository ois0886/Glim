/**
 * src/app/layout.tsx
 *
 * 이 파일은 Next.js 애플리케이션의 최상위 레이아웃을 정의합니다.
 * 모든 페이지에 공통으로 적용될 HTML의 기본 구조(<html>, <body>)와
 * 공통 컴포넌트(예: Header, 3D Scene)를 포함합니다.
 * ' suppressHydrationWarning'은 three.js와 같이 클라이언트 측에서만 렌더링되는 요소와
 * 서버 사이드 렌더링 간의 불일치 경고를 무시하기 위해 추가되었습니다.
 */
import type { Metadata } from "next";
import Header from '../components/Header'; // Import Header
import Scene from '../components/Scene'; // Import Scene
import "./globals.css"; // Ensure globals.css is imported

export const metadata: Metadata = {
  title: "GLIM",
  description: "문장의 우주",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" suppressHydrationWarning>
      <body>
        <Scene id="webgl-canvas" /> {/* Render Scene here with id */}
        <div className="ui-layer"> {/* UI Layer */}
          <Header /> {/* Header inside UI Layer */}
          {children}
        </div>
      </body>
    </html>
  );
}