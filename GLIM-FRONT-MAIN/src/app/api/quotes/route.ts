/**
 * src/app/api/quotes/route.ts
 *
 * 이 파일은 Next.js의 API 라우트를 정의합니다.
 * 클라이언트의 '/api/quotes' 경로로 들어오는 GET 요청을 처리합니다.
 *
 * 내부적으로 외부 백엔드 API(process.env.BASE_URL)에 다시 요청을 보내어
 * 명언(quotes) 데이터를 가져온 후, 클라이언트에게 JSON 형태로 응답합니다.
 *
 * 이 방식을 사용하면 외부 API의 주소나 인증 키를 클라이언트에게 노출하지 않고
 * 안전하게 데이터를 중계할 수 있는 장점이 있습니다.
 */
import { NextResponse } from 'next/server';
import axios from 'axios';

export async function GET() {
  try {
    const API_BASE_URL = process.env.BASE_URL; // Access server-side env var
    const API_ENDPOINT = `/api/v1/quotes?page=0&size=10&sort=views%2Cdesc`;
    const apiUrl = `${API_BASE_URL}${API_ENDPOINT}`;

    const response = await axios.get(apiUrl);
    return NextResponse.json(response.data);
  } catch (error) {
    console.error("Error in API route:", error);
    if (axios.isAxiosError(error)) {
      return NextResponse.json({ message: error.message, details: error.response?.data }, { status: error.response?.status || 500 });
    } else {
      return NextResponse.json({ message: 'An unexpected error occurred' }, { status: 500 });
    }
  }
}