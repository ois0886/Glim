ALTER TABLE quote
    ADD COLUMN author TEXT;

UPDATE quote
SET author = b.author
    FROM book b
WHERE quote.book_id = b.book_id;