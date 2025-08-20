package com.lovedbug.geulgwi.core.domain.quote;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.lovedbug.geulgwi.core.common.exception.GeulgwiException;
import com.lovedbug.geulgwi.core.domain.book.BookRepository;
import com.lovedbug.geulgwi.core.domain.book.dto.BookCreateRequest;
import com.lovedbug.geulgwi.core.domain.book.entity.Book;
import com.lovedbug.geulgwi.core.domain.like.MemberLikeQuoteService;
import com.lovedbug.geulgwi.core.domain.quote.constant.Visibility;
import com.lovedbug.geulgwi.core.domain.quote.dto.request.QuoteCreateRequest;
import com.lovedbug.geulgwi.core.domain.quote.dto.response.QuoteResponse;
import com.lovedbug.geulgwi.core.domain.quote.dto.response.QuoteSearchResponse;
import com.lovedbug.geulgwi.core.domain.quote.dto.response.QuoteWithBookResponse;
import com.lovedbug.geulgwi.core.domain.quote.entity.MemberQuote;
import com.lovedbug.geulgwi.core.domain.quote.entity.Quote;
import com.lovedbug.geulgwi.core.domain.quote.mapper.QuoteMapper;
import com.lovedbug.geulgwi.core.domain.quote.repository.MemberQuoteRepository;
import com.lovedbug.geulgwi.core.domain.quote.repository.QuoteRepository;
import com.lovedbug.geulgwi.core.domain.search.SearchKeywordService;
import com.lovedbug.geulgwi.external.image.ImageMetaData;
import com.lovedbug.geulgwi.external.image.handler.ImageHandler;


@Service
@RequiredArgsConstructor
public class QuoteService {

    private final QuoteRankingService quoteRankingService;
    private final MemberLikeQuoteService memberLikeQuoteService;
    private final SearchKeywordService searchKeywordService;

    private final MemberQuoteRepository memberQuoteRepository;
    private final QuoteRepository quoteRepository;
    private final BookRepository bookRepository;

    private final ImageHandler imageHandler;

    public List<QuoteWithBookResponse> getQuotesByRandom(Pageable pageable, Long memberId) {
        List<Quote> quotes = quoteRepository.findPublicQuotesByRandom(pageable);

        return quotes.stream()
            .map(quote -> {
                boolean isLiked = (memberId != null) && memberLikeQuoteService.isLikedBy(memberId, quote.getQuoteId());
                long likeCount = memberLikeQuoteService.countLikes(quote.getQuoteId());

                return QuoteWithBookResponse.builder()
                    .quoteId(quote.getQuoteId())
                    .quoteImageName(quote.getImageName())
                    .page(quote.getPage())
                    .bookId(quote.getBook().getBookId())
                    .quoteViews(quote.getViews())
                    .bookTitle(quote.getBook().getTitle())
                    .content(quote.getContent())
                    .author(quote.getBook().getAuthor())
                    .publisher(quote.getBook().getPublisher())
                    .bookCoverUrl(quote.getBook().getCoverUrl())
                    .isLiked(isLiked)
                    .likeCount(likeCount)
                    .build();
            })
            .collect(Collectors.toList());
    }

    public List<QuoteResponse> getPublicQuotesByIsbn(String isbn, Long memberId) {

        List<Quote> quotes = quoteRepository.findAllByBookIsbnAndVisibility(isbn, "PUBLIC");

        return quotes.stream()
            .map(quote -> {
                boolean isLiked = (memberId != null) && memberLikeQuoteService.isLikedBy(memberId, quote.getQuoteId());
                long likeCount = memberLikeQuoteService.countLikes(quote.getQuoteId());

                return QuoteResponse.toResponseDto(quote, isLiked, likeCount);
            })
            .collect(Collectors.toList());
    }

    public QuoteWithBookResponse getPublicQuoteById(Long quoteId, Long memberId) {
        Quote quote = quoteRepository.findByQuoteIdAndVisibility(quoteId, Visibility.PUBLIC.name())
            .orElseThrow(() -> new GeulgwiException("없는 글귀 입니다. id = " + quoteId));

        return QuoteMapper.toQuoteWithBookResponse(quote, memberId);
    }

    @Transactional
    public void increaseViewCount(Long quoteId) {
        Quote quote = quoteRepository.findById(quoteId)
            .orElseThrow(() -> new EntityNotFoundException("없는 글귀 id 입니다. 글귀 id = " + quoteId));

        quote.increaseViewCount();
        quoteRankingService.updateQuoteRanking(quote);
    }

    @Transactional
    public void createQuote(QuoteCreateRequest quoteData, MultipartFile quoteImage) {
        Book book = bookRepository.findBookByIsbn(quoteData.getIsbn())
            .orElseGet(() -> bookRepository.save(BookCreateRequest.toEntity(quoteData.getBookCreateData())));

        ImageMetaData imageMetaData = imageHandler.saveImage(quoteImage);

        Quote savedQuote = quoteRepository.save(QuoteCreateRequest.toEntity(quoteData, book, imageMetaData));

        MemberQuote memberQuote = MemberQuote.builder()
            .memberId(quoteData.getMemberId())
            .quote(savedQuote)
            .build();

        memberQuoteRepository.save(memberQuote);
    }

    public QuoteSearchResponse searchQuotesByContent(Long memberId, String content, Pageable pageable) {
        Page<Quote> quotes = quoteRepository.findByContentContainingAndVisibility(
            content, Visibility.PUBLIC.name(), pageable);

        searchKeywordService.increaseKeywordScore(content);

        return QuoteMapper.toQuoteSearchResponse(quotes, memberId);
    }

    public List<QuoteWithBookResponse> getPopularQuotesWithBook() {
        List<Quote> quotes = quoteRepository.findPublicQuotes(
            PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "views")));

        return quotes.stream()
            .map(QuoteMapper::toQuoteWithBookResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<QuoteResponse> getUploadedQuotesByMemberId(Long memberId) {

        List<MemberQuote> memberQuotes = memberQuoteRepository.findAllByMemberIdOrderByCreatedAtDesc(memberId);

        return memberQuotes.stream()
            .map(memberQuote -> {
                Quote quote = memberQuote.getQuote();
                boolean isLiked = (memberId != null) && memberLikeQuoteService.isLikedBy(memberId, quote.getQuoteId());
                long likeCount = memberLikeQuoteService.countLikes(quote.getQuoteId());

                return QuoteResponse.toResponseDto(quote, isLiked, likeCount, memberQuote.getCreatedAt());
            })
            .toList();
    }


}
