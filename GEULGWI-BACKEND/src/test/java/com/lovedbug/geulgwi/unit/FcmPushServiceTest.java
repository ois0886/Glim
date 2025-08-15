package com.lovedbug.geulgwi.unit;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.lovedbug.geulgwi.core.domain.book.entity.Book;
import com.lovedbug.geulgwi.core.domain.member.Member;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberGender;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberRole;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberStatus;
import com.lovedbug.geulgwi.core.domain.quote.entity.Quote;
import com.lovedbug.geulgwi.external.fcm.constant.NotificationType;
import com.lovedbug.geulgwi.external.fcm.dto.response.FcmMessageDto;
import com.lovedbug.geulgwi.external.fcm.entity.FcmTokens;
import com.lovedbug.geulgwi.external.fcm.repository.FcmTokenRepository;
import com.lovedbug.geulgwi.external.fcm.service.FcmPushService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class FcmPushServiceTest {

    @Mock
    private FirebaseMessaging firebaseMessaging;

    @Mock
    private FcmTokenRepository fcmTokenRepository;

    @InjectMocks
    private FcmPushService fcmPushService;

    @Test
    @DisplayName("좋아요 알림 메시지 발송 한다")
    void should_send_create_like_notification_message() {

        Member quoteOwner = createTestMember();
        Book book = createTestBook();
        Quote quote = createTestQuote(book, quoteOwner.getMemberId());
        String deviceToken = "test_device_token_123";
        String deviceType = "ANDROID";

        FcmTokens fcmToken = createFcmToken(quoteOwner, deviceToken, deviceType);

        FcmMessageDto message = FcmMessageDto.createLikedNotification(fcmToken, quote, quoteOwner.getNickname());

        assertThat(message.getTo()).isEqualTo(deviceToken);
        assertThat(message.getData().getScreen().toString()).isEqualTo(NotificationType.LIKE.toString());
        assertThat(message.getData().getBookId()).isEqualTo(String.valueOf(book.getBookId()));
    }

    @DisplayName("단일 디바이스로 좋아요 알림 메시지를 발송 한다.")
    @Test
    void should_send_like_notification_to_single_device_successfully() throws Exception {

        Member quoteOwner = createTestMember();
        Book book = createTestBook();
        Quote quote = createTestQuote(book, quoteOwner.getMemberId());
        String deviceToken = "test_device_token_123";
        String deviceType = "ANDROID";

        FcmTokens activeToken = createFcmToken(quoteOwner, deviceToken, deviceType);
        List<FcmTokens> activeTokens = Arrays.asList(activeToken);

        when(fcmTokenRepository.findAllByMemberAndIsActive(quoteOwner, true))
            .thenReturn(activeTokens);
        when(firebaseMessaging.send(any(Message.class)))
            .thenReturn("projects/test-project/messages/msg_123456789");

        fcmPushService.sendLikeNotification(quoteOwner, quote);

        verify(fcmTokenRepository, times(1))
            .findAllByMemberAndIsActive(quoteOwner, true);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(firebaseMessaging, times(1)).send(messageCaptor.capture());

        Message sentMessage = messageCaptor.getValue();

        assertThat(sentMessage).isNotNull();
    }

    @DisplayName("여러 디바이스로 동시 알림을 전송 한다")
    @Test
    void should_send_like_notification_to_multiple_devices() throws Exception {

        Member member = createTestMember();
        Book book = createTestBook();
        Quote quote = createTestQuote(book, member.getMemberId());
        String deviceType = "ANDROID";

        FcmTokens androidToken1 = createFcmToken(member, "android_token_1", deviceType);
        FcmTokens androidToken2 = createFcmToken(member, "android_token_2", deviceType);
        FcmTokens androidToken3 = createFcmToken(member, "android_token_3", deviceType);

        List<FcmTokens> activeTokens = Arrays.asList(androidToken1, androidToken2, androidToken3);

        when(fcmTokenRepository.findAllByMemberAndIsActive(member, true))
            .thenReturn(activeTokens);
        when(firebaseMessaging.send(any(Message.class)))
            .thenReturn("projects/test-project/messages/msg_123456789");

        fcmPushService.sendLikeNotification(member, quote);

        verify(firebaseMessaging, times(3)).send(any(Message.class));
    }

    private Member createTestMember() {
        return Member.builder()
            .memberId(1L)
            .email("test@example.com")
            .password("encoded_password")
            .nickname("테스트사용자")
            .status(MemberStatus.ACTIVE)
            .role(MemberRole.USER)
            .gender(MemberGender.MALE)
            .birthDate(LocalDateTime.of(1999, 1, 7, 0, 0, 0))
            .build();
    }

    private Book createTestBook() {
        return Book.builder()
            .bookId(1L)
            .title("회람어 시간")
            .author("김금희")
            .publisher("문학동네")
            .coverUrl("https://example.com/cover.jpg")
            .isbn("9788954689206")
            .build();
    }

    private Quote createTestQuote(Book book, Long memberId) {
        return Quote.builder()
            .quoteId(1L)
            .bookTitle("회람어 시간")
            .content("그 때 우린 그때의 시간 안에서 최선을 다한 거야. 지난 시간은 그대로 두자. 자연스럽게.")
            .page(51)
            .views(0)
            .book(book)
            .memberId(memberId)
            .build();
    }

    private FcmTokens createFcmToken(Member member, String deviceToken, String deviceType) {
        return FcmTokens.builder()
            .member(member)
            .deviceToken(deviceToken)
            .deviceType(deviceType)
            .deviceId("device_" + deviceToken)
            .isActive(true)
            .build();
    }
}
