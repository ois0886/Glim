'use client'

import type { Metadata } from 'next'
import { GeistSans } from 'geist/font/sans'
import { GeistMono } from 'geist/font/mono'
import './globals.css'
import { useEffect } from 'react'
import { usePathname, useRouter } from 'next/navigation'

// export const metadata: Metadata = {
//   title: '글림 관리자 페이지',
//   description: '',
//   generator: '',
// }

async function initMocks() {
  // 브라우저 환경에서만 실행되도록 확인합니다.
  if (typeof window !== 'undefined') {
    // mocks/browser 파일을 동적으로 불러옵니다.
    const { worker } = await import('../mocks/browser');
    // MSW 워커를 시작시킵니다.
    await worker.start();
  }
}

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode
}>) {
  const router = useRouter()
  const pathname = usePathname();

  useEffect(() => {
    // 개발 환경일 때만 MSW를 활성화합니다.
    if (process.env.NODE_ENV === 'development') {
      // initMocks(); // <--- 이 부분을 주석 처리하거나 아예 지워주세요.
    }
  }, []);

  useEffect(() => {
    const accessToken = localStorage.getItem('accessToken');
    if (!accessToken && pathname !== '/login') {
      router.push('/login');
    }
  }, [pathname, router]);

  return (
    <html lang="en" suppressHydrationWarning>
      <head>
        <style>{`
html {
  font-family: ${GeistSans.style.fontFamily};
  --font-sans: ${GeistSans.variable};
  --font-mono: ${GeistMono.variable};
}
        `}</style>
      </head>
      <body>{children}</body>
    </html>
  )
}