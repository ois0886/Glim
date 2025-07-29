package com.lovedbug.geulgwi.core.domain.book;

import java.util.Optional;
import com.lovedbug.geulgwi.core.domain.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findBookByIsbn(String isbn);
}
