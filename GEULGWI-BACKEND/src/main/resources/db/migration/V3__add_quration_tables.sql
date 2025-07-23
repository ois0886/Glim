-- MainCuration 테이블
CREATE TABLE main_curation
(
    main_curation_id SERIAL PRIMARY KEY,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- CurationItem 테이블
CREATE TABLE curation_item
(
    curation_item_id SERIAL PRIMARY KEY,
    title            TEXT         NOT NULL,
    description      TEXT,
    curation_type    VARCHAR(10) NOT NULL,
    main_curation_id BIGINT,
    sequence         INTEGER,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_main_curation
        FOREIGN KEY (main_curation_id)
            REFERENCES main_curation (main_curation_id)
            ON DELETE SET NULL
);

-- CurationItemBook 테이블
CREATE TABLE curation_item_book
(
    curation_item_book_id SERIAL PRIMARY KEY,
    curation_item_id      BIGINT NOT NULL,
    book_id               BIGINT NOT NULL,
    created_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_curation_item_book_curation_item
        FOREIGN KEY (curation_item_id)
            REFERENCES curation_item (curation_item_id)
            ON DELETE CASCADE,
    CONSTRAINT fk_curation_item_book_book
        FOREIGN KEY (book_id)
            REFERENCES book (book_id)
);

-- CurationItemQuote 테이블
CREATE TABLE curation_item_quote
(
    curation_item_quote_id SERIAL PRIMARY KEY,
    curation_item_id       BIGINT NOT NULL,
    quote_id               BIGINT NOT NULL,
    created_at             TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at             TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_curation_item_quote_curation_item
        FOREIGN KEY (curation_item_id)
            REFERENCES curation_item (curation_item_id)
            ON DELETE CASCADE,
    CONSTRAINT fk_curation_item_quote_quote
        FOREIGN KEY (quote_id)
            REFERENCES quote (quote_id)
);


