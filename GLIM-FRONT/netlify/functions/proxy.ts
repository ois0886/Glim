import type { Handler, HandlerEvent } from '@netlify/functions';

const API_ENDPOINT = process.env.BACKEND_API_URL;

const cors = (origin?: string) => ({
  'Access-Control-Allow-Origin': origin ?? '*',
  'Access-Control-Allow-Credentials': 'true',
  'Access-Control-Allow-Headers': 'Content-Type, Authorization, X-Requested-With',
  'Access-Control-Allow-Methods': 'GET,POST,PUT,PATCH,DELETE,OPTIONS',
  'Vary': 'Origin',
});

export const handler: Handler = async (event: HandlerEvent) => {
  if (!API_ENDPOINT) {
    return {
      statusCode: 500,
      body: JSON.stringify({ message: 'Server configuration error: BACKEND_API_URL is not set.' }),
    };
  }

  const origin = event.headers.origin;

  // 1) OPTIONS 프리플라이트
  if (event.httpMethod.toUpperCase() === 'OPTIONS') {
    return { statusCode: 204, body: '', headers: { ...cors(origin) } };
  }

  // 2) 함수 경로에서 원래 API 경로 복구
  //    - /api/* → /.netlify/functions/proxy/:splat  => incomingPath = "v1/..."
  //    - /v1/*  → /.netlify/functions/proxy/api/:splat => incomingPath = "api/v1/..."
  const prefix = '/.netlify/functions/proxy/';
  const incomingPath = event.path.startsWith(prefix)
    ? event.path.slice(prefix.length)
    : event.path.replace(/^\/api\//, '');

  // 3) 최종 백엔드 URL (쿼리 유지)
  const url = new URL(API_ENDPOINT.replace(/\/+$/, ''));
  // 안전 조인
  const join = (...parts: string[]) =>
    parts
      .map((p) => p.replace(/^\/+|\/+$/g, ''))
      .filter(Boolean)
      .join('/');

  url.pathname = '/' + join(url.pathname, incomingPath);
  if (event.queryStringParameters && Object.keys(event.queryStringParameters).length > 0) {
    url.search = new URLSearchParams(event.queryStringParameters as Record<string, string>).toString();
  }

  // 4) 전달할 헤더 (화이트리스트)
  const headers: Record<string, string> = {};
  const ct = event.headers['content-type'] || event.headers['Content-Type'];
  if (ct) headers['Content-Type'] = ct;
  const auth = event.headers['authorization'] || event.headers['Authorization'];
  if (auth) headers['Authorization'] = auth;
  const cookie = event.headers['cookie'] || event.headers['Cookie'];
  if (cookie) headers['Cookie'] = cookie;

  // 5) 요청 옵션
  const method = event.httpMethod.toUpperCase();
  const hasBody = !!event.body && !['GET', 'HEAD'].includes(method);
  const init: RequestInit = {
    method,
    headers,
    body: hasBody ? (event.isBase64Encoded ? Buffer.from(event.body, 'base64') : event.body) : undefined,
  };

  try {
    const resp = await fetch(url.toString(), init);
    const text = await resp.text();

    // 6) Set-Cookie 패스스루 (undici/node-fetch 호환)
    // @ts-ignore
    const setCookie = resp.headers.getSetCookie?.() || resp.headers.raw?.()['set-cookie'] || [];

    const out: Record<string, string | string[]> = {
      ...cors(origin),
      'Content-Type': resp.headers.get('Content-Type') || 'application/json',
    };
    if (setCookie.length > 0) out['Set-Cookie'] = setCookie;

    return { statusCode: resp.status, body: text, headers: out };
  } catch (e) {
    return {
      statusCode: 500,
      body: JSON.stringify({
        message: 'Proxy Internal Server Error',
        error: e instanceof Error ? e.message : String(e),
      }),
      headers: { ...cors(origin), 'Content-Type': 'application/json' },
    };
  }
};
