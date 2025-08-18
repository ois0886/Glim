package com.lovedbug.geulgwi.external.email;

import com.lovedbug.geulgwi.external.email.constant.EmailErrorCode;
import com.lovedbug.geulgwi.external.email.exception.EmailException;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSender {

    private final JavaMailSender mailSender;
    private final ResourceLoader resourceLoader;
    private final Mustache.Compiler mustacheCompiler;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.name:글귀}")
    private String appName;

    public void sendVerificationEmail(String toEmail, String verificationCode){

        try{
            Map<String, Object> model = new HashMap<>();
            model.put("appName", appName);
            model.put("email", toEmail);
            model.put("verificationCode", verificationCode);
            model.put("expirationMinutes", "10");

            String htmlContent = renderTemplate("email/verification-email", model);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, appName);
            helper.setTo(toEmail);
            helper.setSubject(appName + "- 이메일 인증번호");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("이메일 인증 메일 발송 완료: {}", toEmail);

        }catch (Exception e){
            log.error("이메일 발송 실패: {}", toEmail, e);
            throw new EmailException(EmailErrorCode.EMAIL_SEND_FAILED, "to=" + toEmail);
        }
    }

    public void sendWelcomeEmail(String toEmail, String nickname){

        try {
            Map<String, Object> model = new HashMap<>();
            model.put("appName", appName);
            model.put("nickname", nickname);
            model.put("email", toEmail);

            String htmlContent = renderTemplate("email/welcome-email", model);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, appName);
            helper.setTo(toEmail);
            helper.setSubject(appName +"에 오신것을 환영합니다!");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("환영 이메일 발송 완료: {}", toEmail);

        }catch (Exception e){
            log.error("환영 이메일 발송 실패: {}", toEmail, e);
        }
    }

    private String renderTemplate(String templateName, Map<String, Object> model){

        try{
            Resource resource = resourceLoader.getResource("classpath:templates/" + templateName + ".mustache");

            try(Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {

                Template template = mustacheCompiler.compile(reader);
                return template.execute(model);
            }
        }catch (IOException e){
            log.error("템플릿 렌더링 실패: {}", templateName, e);
            throw new EmailException(EmailErrorCode.EMAIL_TEMPLATE_ERROR, "template=" + templateName);
        }
    }
}
