export interface Quote {
  id: string;
  type: "quote";
  content: string;
  source?: string;
}

export interface Book {
  id: string;
  type: "book";
  title: string;
  author: string;
  coverImage?: string;
}

export type CurationItem = Quote | Book;
