package com.lovedbug.geulgwi.core.domain.book;

import java.util.List;
import java.util.Optional;

import com.lovedbug.geulgwi.core.domain.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findBookByIsbn(String isbn);

    Optional<Book> findBookByBookId(long bookId);

    boolean existsByIsbn(String isbn);

    List<Book> findTop10ByOrderByViewsDesc();

    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
