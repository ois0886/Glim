package com.lovedbug.geulgwi.unit;

import com.lovedbug.geulgwi.core.domain.like.MemberLikeQuoteService;
import com.lovedbug.geulgwi.core.domain.like.entity.MemberLikeQuote;
import com.lovedbug.geulgwi.core.domain.like.repository.MemberLikeQuoteRepository;
import com.lovedbug.geulgwi.core.domain.member.Member;
import com.lovedbug.geulgwi.core.domain.member.MemberRepository;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberGender;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberRole;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberStatus;
import com.lovedbug.geulgwi.core.domain.quote.entity.Quote;
import com.lovedbug.geulgwi.core.domain.quote.repository.QuoteRepository;
import com.lovedbug.geulgwi.external.fcm.service.FcmPushService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class FcmPushTest {

    @Mock
    private MemberLikeQuoteRepository memberLikeQuoteRepository;

    @Mock
    private QuoteRepository quoteRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private FcmPushService fcmPushService;

    @InjectMocks
    private MemberLikeQuoteService memberLikeQuoteService;

    private final Long likerId = 1L;
    private final Long quoteId = 2L;
    private final Long memberId = 3L;
    private final Long userId = 1L;

    @DisplayName("좋아요 등록 시 FCM 알림을 전송 한다.")
    @Test
    void should_send_fcm_notification_when_user_likes_others_quote() {

        Quote quote = createQuote(quoteId, memberId, "테스트 글귀");
        Member member = createMember(memberId, "test1@test.com", "글귀 작성자");

        when(memberLikeQuoteRepository.existsByMemberIdAndQuote_QuoteId(likerId, quoteId))
            .thenReturn(false);
        when(quoteRepository.findById(quoteId))
            .thenReturn(Optional.of(quote));
        when(memberRepository.findById(memberId))
            .thenReturn(Optional.of(member));

        doNothing().when(fcmPushService).sendLikeNotification(any(Member.class), any(Quote.class));

        memberLikeQuoteService.likeQuote(likerId, quoteId);

        verify(memberLikeQuoteRepository, times(1)).save(any(MemberLikeQuote.class));
        verify(fcmPushService, times(1)).sendLikeNotification(member, quote);

    }

    @DisplayName("자신의 글귀에 좋아요 시 FCM 알림을 발송하지 않는다.")
    @Test
    void should_not_send_fcm_notification_when_user_likes_own_quote() {

        Quote ownQuote = createQuote(quoteId, userId, "제가 작성한 글귀입니다.");

        when(memberLikeQuoteRepository.existsByMemberIdAndQuote_QuoteId(userId, quoteId))
            .thenReturn(false);
        when(quoteRepository.findById(quoteId))
            .thenReturn(Optional.of(ownQuote));

        memberLikeQuoteService.likeQuote(userId, quoteId);

        verify(memberLikeQuoteRepository, times(1)).save(any(MemberLikeQuote.class));
        verify(fcmPushService, never()).sendLikeNotification(any(), any());
        verify(memberRepository, never()).findById(any());
    }

    @DisplayName("FCM 서비스 예외 발생 시에도 좋아요는 정상 처리한다.")
    @Test
    void should_save_like_event_if_fcm_notification_failed() {

        Quote quote = createQuote(quoteId, memberId, "테스트 글귀");
        Member quoteOwner = createMember(memberId, "owner@test.com", "글귀 작성자");

        when(memberLikeQuoteRepository.existsByMemberIdAndQuote_QuoteId(likerId, quoteId))
            .thenReturn(false);
        when(quoteRepository.findById(quoteId))
            .thenReturn(Optional.of(quote));
        when(memberRepository.findById(memberId))
            .thenReturn(Optional.of(quoteOwner));

        doThrow(new RuntimeException("FCM 전송 실패")).when(fcmPushService)
            .sendLikeNotification(any(), any());

        assertDoesNotThrow(() -> {
            memberLikeQuoteService.likeQuote(likerId, quoteId);
        });

        verify(memberLikeQuoteRepository, times(1)).save(any(MemberLikeQuote.class));
        verify(fcmPushService, times(1)).sendLikeNotification(quoteOwner, quote);
    }

    private Quote createQuote(Long quoteId, Long memberId, String content) {
        return Quote.builder()
            .quoteId(quoteId)
            .content(content)
            .page(100)
            .bookTitle("테스트 책")
            .memberId(memberId)
            .build();
    }

    private Member createMember(Long memberId, String email, String nickname) {
        return Member.builder()
            .memberId(memberId)
            .email(email)
            .password("encoded_password")
            .nickname(nickname)
            .status(MemberStatus.ACTIVE)
            .role(MemberRole.USER)
            .gender(MemberGender.MALE)
            .birthDate(LocalDateTime.of(1999, 1, 7, 0, 0, 0))
            .build();
    }
}
