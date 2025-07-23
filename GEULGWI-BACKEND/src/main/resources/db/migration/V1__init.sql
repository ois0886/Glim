-- <회원>
CREATE TABLE member (
    member_id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE,
    password TEXT,
    nick_name VARCHAR(100) NOT NULL,
    profile_url TEXT,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    role VARCHAR(20) DEFAULT 'USER',
    provider VARCHAR(50),
    provider_id VARCHAR(255),
    CHECK (status IN ('ACTIVE', 'INACTIVE', 'BANNED')),
    CHECK (role IN ('USER', 'ADMIN')),
    CONSTRAINT unique_provider_user UNIQUE (provider, provider_id)
);


-- <도서>

CREATE TABLE book (
      book_id BIGSERIAL PRIMARY KEY,
      title VARCHAR(255) NOT NULL,
      price INTEGER,

      author VARCHAR(255),
      translator VARCHAR(255),

      category VARCHAR(100),
      category_id INTEGER,

      publisher VARCHAR(255),
      description TEXT,

      isbn VARCHAR(50),
      isbn13 VARCHAR(50),

      published_date DATE,

      cover_url TEXT,
      link_url TEXT,

      views INTEGER DEFAULT 0
);

-- <글귀>
CREATE TABLE quote (
   quote_id BIGSERIAL PRIMARY KEY,
   book_id BIGINT REFERENCES book(book_id) ON DELETE CASCADE,
   member_id BIGINT REFERENCES member(member_id) ON DELETE CASCADE,

   image_path TEXT,             -- 이미지 저장 경로 (예: /images/quotes/)
   image_name VARCHAR(255), -- 이미지 파일명 (예: quote123.png)

   book_title TEXT,
   content TEXT,
   visibility VARCHAR(10) DEFAULT 'PUBLIC',
   views INTEGER DEFAULT 0,
   page INTEGER DEFAULT 0,
   CHECK (visibility IN ('PUBLIC', 'PRIVATE'))
);

-- <사용자와 글귀 관계>
CREATE TABLE member_quote (
  member_quote_id BIGSERIAL PRIMARY KEY,
  member_id BIGINT REFERENCES member(member_id) ON DELETE CASCADE,
  quote_id BIGINT REFERENCES quote(quote_id) ON DELETE CASCADE
);

-- <사용자 책 좋아요>
CREATE TABLE member_like_book (
  member_like_book_id BIGSERIAL PRIMARY KEY,
  member_id BIGINT REFERENCES member(member_id) ON DELETE CASCADE,
  book_id BIGINT REFERENCES book(book_id) ON DELETE CASCADE,
  UNIQUE (member_id, book_id) -- 중복 좋아요 방지
);

-- <사용자 글귀 좋아요>
CREATE TABLE member_like_quote (
   member_like_quote_id BIGSERIAL PRIMARY KEY,
   member_id BIGINT REFERENCES member(member_id) ON DELETE CASCADE,
   quote_id BIGINT REFERENCES quote(quote_id) ON DELETE CASCADE,
   UNIQUE (member_id, quote_id) -- 중복 좋아요 방지
);
