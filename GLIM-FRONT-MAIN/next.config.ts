/**
 * next.config.ts
 *
 * 이 파일은 Next.js 애플리케이션의 전반적인 빌드 및 런타임 동작을 설정합니다.
 * Next.js 프로젝트의 핵심 설정 파일 중 하나입니다.
 *
 * 주요 설정:
 * 1. `transpilePackages`:
 *    - 특정 npm 패키지(여기서는 `three`, `gsap`)를 Next.js가 빌드 과정에서 트랜스파일하도록 지시합니다.
 *    - 이는 해당 패키지들이 ES6 모듈 문법을 사용하거나, 특정 환경에서 호환성 문제가 발생할 때 유용합니다.
 *
 * 2. `compiler.styledComponents`:
 *    - `styled-components` 라이브러리를 Next.js 빌드 시스템과 통합하여
 *      서버 사이드 렌더링(SSR) 및 개발 환경에서 스타일이 올바르게 적용되도록 합니다.
 *    - `true`로 설정하면 `styled-components`의 Babel 플러그인이 활성화됩니다.
 *
 * 3. `webpack`:
 *    - Next.js의 내부 Webpack 설정을 커스터마이징할 수 있는 함수입니다.
 *    - 여기서는 클라이언트 사이드 빌드(`!isServer`) 시 `@styled-components/styled-components`
 *      별칭을 `node_modules/styled-components`로 매핑하여,
 *      `styled-components`의 중복 로딩 문제를 방지하거나 특정 버전을 강제하는 데 사용될 수 있습니다.
 *
 * 4. `rewrites`:
 *    - 들어오는 요청 경로를 다른 경로로 다시 작성(rewrite)하는 기능을 정의합니다.
 *    - 여기서는 `/api/:path*`로 들어오는 모든 요청을 백엔드 서버(`http://i13d202.p.ssafy.io:8080/api/:path*`)로
 *      프록시(proxy)하도록 설정합니다.
 *    - 이는 클라이언트에서 백엔드 API를 호출할 때 CORS(Cross-Origin Resource Sharing) 문제를 피하고,
 *      API 엔드포인트를 숨기는 데 유용합니다.
 */
import type { NextConfig } from "next";
import path from "path";

const nextConfig: NextConfig = {
  transpilePackages: ['three', 'gsap'],
  compiler: {
    styledComponents: true,
  },
  webpack: (config, { isServer }) => {
    if (!isServer) {
      config.resolve.alias['@styled-components/styled-components'] = path.resolve(
        __dirname,
        'node_modules/styled-components'
      );
    }
    return config;
  },
  async rewrites() {
    return [
      {
        source: '/api/:path*',
        destination: `http://i13d202.p.ssafy.io:8080/api/:path*`, // Proxy to Backend
      },
    ];
  },
};

export default nextConfig;
