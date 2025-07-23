package com.lovedbug.geulgwi.service;

import com.lovedbug.geulgwi.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchHistoryService {

    private BookRepository bookRepository;


}
