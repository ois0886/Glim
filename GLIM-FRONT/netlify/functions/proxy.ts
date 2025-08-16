// netlify/functions/proxy.ts
import fetch from 'node-fetch';
import type { Handler, HandlerEvent } from '@netlify/functions';

const API_ENDPOINT = 'http://i13d202.p.ssafy.io:8080';

export const handler: Handler = async (event: HandlerEvent) => {
  // 들어온 요청 경로에서 /api/ 부분을 제거하여 실제 백엔드 경로를 만듭니다.
  const path = event.path.replace('/api/', '/');
  const url = `${API_ENDPOINT}${path}`;

  // 브라우저가 보낸 헤더에서 'host'와 'origin'을 삭제합니다.
  const headers = { ...event.headers };
  delete headers.host;
  delete headers.origin;
  // Netlify가 추가하는 관련 헤더도 제거해볼 수 있습니다.
  delete headers['x-forwarded-host'];
  delete headers['x-forwarded-proto'];
  delete headers['x-forwarded-for'];

  try {
    const response = await fetch(url, {
      method: event.httpMethod,
      body: event.body, // POST 요청 등의 body는 그대로 전달
      headers: {
        ...(headers as Record<string, string>), // 정리된 헤더를 전달
      },
    });

    // 백엔드에서 받은 응답을 그대로 클라이언트에게 전달
    const data = await response.text(); // json() 대신 text()로 원본을 그대로 받습니다.

    return {
      statusCode: response.status,
      body: data,
      headers: {
        'Content-Type': response.headers.get('Content-Type') || 'application/json',
      },
    };
  } catch (error) {
    console.error('Proxy Error:', error);
    return {
      statusCode: 500,
      body: JSON.stringify({ message: 'Proxy Internal Server Error' }),
    };
  }
};