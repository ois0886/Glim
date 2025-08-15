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

        List<FcmTokens> activeTokens = fcmTokenRepository.findAllByMemberAndIsActive(member, true);

        if (activeTokens.isEmpty()){
            return;
        }

        activeTokens.forEach(token -> {
            try {
                FcmMessageDto fcmMessage = FcmMessageDto.createLikedNotification(token, quote, member.getNickname());
                sendPushMessage(fcmMessage);

            } catch(Exception e) {

                if (e.getMessage() != null){
                    token.updateIsActive(false);
                }
            }
        });
    }

    private void sendPushMessage (FcmMessageDto fcmMessage) {

        try{
            Message.Builder messageBuilder = Message.builder()
                .setToken(fcmMessage.getTo());

            firebaseMessaging.send(messageBuilder.putAllData(likeFcmMessageData(fcmMessage)).build());

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
        data.put("nickname", fcmMessage.getData().getNickName());

        return data;
    }
}
