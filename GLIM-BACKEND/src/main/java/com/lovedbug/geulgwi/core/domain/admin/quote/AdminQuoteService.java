package com.lovedbug.geulgwi.core.domain.admin.quote;


import com.lovedbug.geulgwi.core.domain.admin.dto.response.AdminQuoteResponse;
import com.lovedbug.geulgwi.core.domain.admin.exception.QuoteNotFoundException;
import com.lovedbug.geulgwi.core.domain.quote.repository.QuoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminQuoteService {

    private final QuoteRepository quoteRepository;

    public List<AdminQuoteResponse> getAllQuotes() {
        return quoteRepository.findAll().stream()
            .map(AdminQuoteResponse::toResponseDto)
            .collect(Collectors.toList());
    }

    public void deleteQuote(Long quoteId) {
        if (!quoteRepository.existsById(quoteId)) {
            throw new QuoteNotFoundException(quoteId);
        }
        quoteRepository.deleteById(quoteId);
    }
}

