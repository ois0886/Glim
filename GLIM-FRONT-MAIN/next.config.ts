import type { NextConfig } from "next";
import path from "path";

const nextConfig: NextConfig = {
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
      {
        source: '/images/:path*',
        destination: `http://i13d202.p.ssafy.io:8080/images/:path*`, // Proxy for images
      },
    ];
  },
};

export default nextConfig;