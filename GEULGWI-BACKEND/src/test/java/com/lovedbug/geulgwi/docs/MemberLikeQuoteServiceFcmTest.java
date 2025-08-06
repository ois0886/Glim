package com.lovedbug.geulgwi.docs;

import com.lovedbug.geulgwi.core.domain.like.MemberLikeQuoteService;
import com.lovedbug.geulgwi.core.domain.like.repository.MemberLikeQuoteRepository;
import com.lovedbug.geulgwi.core.domain.member.MemberRepository;
import com.lovedbug.geulgwi.core.domain.quote.repository.QuoteRepository;
import com.lovedbug.geulgwi.external.fcm.service.FcmPushService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class MemberLikeQuoteServiceFcmTest {

    @MockitoBean
    private MemberLikeQuoteRepository memberLikeQuoteRepository;

    @MockitoBean
    private QuoteRepository quoteRepository;

    @MockitoBean
    private MemberRepository memberRepository;

    @MockitoBean
    private FcmPushService fcmPushService;

    @InjectMocks
    private MemberLikeQuoteService memberLikeQuoteService;


}
