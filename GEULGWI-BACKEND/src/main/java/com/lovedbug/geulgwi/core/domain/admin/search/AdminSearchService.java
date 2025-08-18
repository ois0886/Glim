package com.lovedbug.geulgwi.core.domain.admin.search;

import com.lovedbug.geulgwi.core.domain.admin.dto.response.SearchBookResponse;
import com.lovedbug.geulgwi.core.domain.admin.dto.response.SearchQuoteResponse;
import com.lovedbug.geulgwi.core.domain.admin.mapper.SearchBookMapper;
import com.lovedbug.geulgwi.core.domain.admin.mapper.SearchQuoteMapper;
import com.lovedbug.geulgwi.core.domain.book.BookRepository;
import com.lovedbug.geulgwi.core.domain.book.entity.Book;
import com.lovedbug.geulgwi.core.domain.member.Member;
import com.lovedbug.geulgwi.core.domain.member.MemberRepository;
import com.lovedbug.geulgwi.core.domain.member.dto.response.MemberResponse;
import com.lovedbug.geulgwi.core.domain.member.mapper.MemberMapper;
import com.lovedbug.geulgwi.core.domain.quote.repository.QuoteRepository;
import com.lovedbug.geulgwi.core.domain.quote.entity.Quote;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminSearchService {

    private final BookRepository bookRepository;
    private final QuoteRepository quoteRepository;
    private final MemberRepository memberRepository;

    public List<SearchBookResponse> searchBooks(String keyword, Pageable page) {
        List<Book> books = bookRepository.findByTitleContainingIgnoreCase(keyword, page).getContent();
        return SearchBookMapper.toDtoList(books);
    }

    public List<SearchQuoteResponse> searchQuotes(String keyword, Pageable page) {
        List<Quote> quotes = quoteRepository.findByContentContainingIgnoreCase(keyword, page).getContent();
        return SearchQuoteMapper.toDtoList(quotes);
    }

    public List<MemberResponse> searchMembers(String keyword, Pageable page) {
        List<Member> members = memberRepository.searchByKeyword(keyword, page).getContent();
        return members.stream()
            .map(MemberMapper::toMemberDto)
            .collect(Collectors.toList());
    }
}
