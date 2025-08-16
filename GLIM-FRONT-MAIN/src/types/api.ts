// src/types/api.ts

// ===================================================================
// API 명세서의 실제 응답 구조를 정의하는 타입들
// ===================================================================

/**
 * '메인 큐레이션 조회' API 응답의 `contents` 배열 안 객체 타입
 */
export interface CurationContent {
  bookId: number | null;
  bookTitle: string;
  author: string;
  publisher: string | null;
  bookCoverUrl: string | null;
  quoteId: number | null;
  imageName: string | null; // API는 `imageName`을 반환
}

/**
 * '메인 큐레이션 조회' API 응답의 최상위 객체 타입
 */
export interface Curation {
  curationItemId: number | null;
  title: string;
  description: string;
  curationType: 'QUOTE' | 'BOOK';
  contents: CurationContent[];
}

/**
 * '글귀 목록 조회' API 응답의 객체 타입
 */
export interface QuoteListApiResponse {
    quoteId: number;
    quoteImageName: string; // API는 `quoteImageName`을 반환
    quoteViews: number;
    page: number;
    bookId: number;
    bookTitle: string;
    author: string;
    publisher: string | null;
    bookCoverUrl: string;
    likeCount: number;
    liked: boolean;
  }

// ===================================================================
// 프론트엔드 컴포넌트에서 최종적으로 사용하는 데이터 타입
// ===================================================================

/**
 * Scene, Book 등 프론트엔드 컴포넌트가 사용하는 최종 데이터 형태
 * API 응답들을 이 형태로 변환해서 사용합니다.
 */
export interface Content {
  quoteId: number;
  quoteImage: string; // 컴포넌트는 `quoteImage`를 사용
  quoteViews: number;
  page: number;
  bookId: number;
  bookTitle: string;
  author: string;
  publisher: string | null;
  bookCoverUrl: string | null;
  likeCount: number;
  liked: boolean;
}


// ===================================================================
// 기타 API 요청/응답 타입들
// ===================================================================

/**
 * GPT 이미지 생성을 요청할 때 사용하는 데이터 타입
 * 이 타입이 없어서 마지막 오류가 발생했습니다.
 */
export interface GPTImageGenerationRequest {
  prompt: string;
}

/**
 * '글귀 id로 글귀 조회' API 응답 타입
 */
export interface QuoteDetail {
  quoteId: number;
  quoteImageName: string; // API 응답에는 이 필드가 있음
  quoteViews: number;
  page: number;
  bookId: number;
  bookTitle: string;
  author: string;
  publisher: string | null;
  bookCoverUrl: string;
  likeCount: number;
  liked: boolean;
}

export type QuoteDetailResponse = QuoteDetail;
export type QuoteListResponse = Content[];
