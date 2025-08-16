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
