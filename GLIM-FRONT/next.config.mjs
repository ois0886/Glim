/** @type {import('next').NextConfig} */
const nextConfig = {
  eslint: { ignoreDuringBuilds: true },
  typescript: { ignoreBuildErrors: true },
  images: {
    unoptimized: false,   // ✅ 여기 false로 돌려야 Next가 최적화/프록시 수행
    remotePatterns: [
      {
        protocol: 'http',
        hostname: 'i13d202.p.ssafy.io',
        port: '8080',
        pathname: '/**',
      },
    ],
  },
  async rewrites() {
    return [
      {
        source: '/api/:path*',
        destination: 'http://i13d202.p.ssafy.io:8080/api/:path*',
      },
    ];
  },
}

export default nextConfig
