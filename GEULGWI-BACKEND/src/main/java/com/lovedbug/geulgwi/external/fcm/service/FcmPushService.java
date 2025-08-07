package com.lovedbug.geulgwi.external.fcm.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.lovedbug.geulgwi.external.fcm.dto.response.FcmMessageDto;
import com.lovedbug.geulgwi.external.fcm.entity.FcmTokens;
import com.lovedbug.geulgwi.external.fcm.repository.FcmTokenRepository;
import com.lovedbug.geulgwi.core.domain.member.Member;
import com.lovedbug.geulgwi.core.domain.quote.entity.Quote;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmPushService {

    private final FirebaseMessaging firebaseMessaging;
    private final FcmTokenRepository fcmTokenRepository;

    @Transactional(readOnly = true)
    public void sendLikeNotification(Member member, Quote quote) {

        log.info("FCM 좋아요 알림 전송 시작 - Member: {}, Quote: {}", member.getMemberId(), quote.getQuoteId());

        List<FcmTokens> activeTokens = fcmTokenRepository.findAllByMemberAndIsActive(member, true);

        if (activeTokens.isEmpty()){
            log.info("FCM 토큰이 없어 알림 전송 건너뜀 - Member: {}", member.getMemberId());
            return;
        }

        log.info("FCM 알림 전송 대상 토큰 수: {} - Member: {}", activeTokens.size(), member.getMemberId());

        activeTokens.forEach(token -> {
            try {
                FcmMessageDto fcmMessage = FcmMessageDto.createLikedNotification(token, quote);
                sendPushMessage(fcmMessage);

            } catch(Exception e) {

                log.error("FCM 토큰 비활성화 처리 - Token: {}, Error: {}", token.getDeviceToken(), e.getMessage());

                if (e.getMessage() != null){
                    token.updateIsActive(false);
                }
            }
        });
    }

    private void sendPushMessage (FcmMessageDto fcmMessage) {

        try{
            Message.Builder messageBuilder = Message.builder()
                .setToken(fcmMessage.getTo())
                .setNotification(Notification.builder()
                    .setTitle(fcmMessage.getNotification().getTitle())
                    .setBody(fcmMessage.getNotification().getBody())
                    .setImage(fcmMessage.getNotification().getImage())
                    .build());

//            firebaseMessaging.send(messageBuilder.putAllData(likeFcmMessageData(fcmMessage)).build());
            String messageId = firebaseMessaging.send(messageBuilder.putAllData(likeFcmMessageData(fcmMessage)).build());

            // 성공 로그 추가 - INFO 레벨
            log.info("FCM 알림 전송 성공 - MessageId: {}, Token: {}, Title: {}",
                messageId, fcmMessage.getTo(), fcmMessage.getNotification().getTitle());

            // 상세 로그 - DEBUG 레벨
            log.debug("FCM 알림 상세 정보 - Body: {}, Data: {}",
                fcmMessage.getNotification().getBody(),
                likeFcmMessageData(fcmMessage));

        } catch (Exception e) {

            throw new RuntimeException("FCM 알림 전송 실패", e);
        }
    }

    private Map<String, String> likeFcmMessageData(FcmMessageDto fcmMessage) {

        Map<String, String> data = new HashMap<>();
        data.put("bookId", fcmMessage.getData().getBookId());
        data.put("quoteId", fcmMessage.getData().getQuoteId());
        data.put("bookTitle", fcmMessage.getData().getBookTitle());
        data.put("page", fcmMessage.getData().getPage().toString());
        data.put("quoteText", fcmMessage.getData().getQuoteText());
        data.put("screen", fcmMessage.getData().getScreen().toString());

        return data;
    }
}
