package com.lovedbug.geulgwi.security;

import com.lovedbug.geulgwi.annotation.JwtRequired;
import com.lovedbug.geulgwi.dto.resposne.AuthenticatedUser;
import com.lovedbug.geulgwi.service.AuthenticatedUserService;
import com.lovedbug.geulgwi.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AuthenticatedUserService authenticatedUserService;
    private final RequestMappingHandlerMapping handlerMapping;
    public static final String AUTHENTICATED_USER_KEY = "authenticatedUser";

    public JwtAuthenticationFilter(
        JwtUtil jwtUtil,
        AuthenticatedUserService authenticatedUserService,
        @Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping handlerMapping
    ) {
        this.jwtUtil = jwtUtil;
        this.authenticatedUserService = authenticatedUserService;
        this.handlerMapping = handlerMapping;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain)
        throws IOException {
            
        try {
            if (request.getRequestURI().equals("/api/v1/auth/refresh") || !requiresJwtAuthentication(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = extractTokenFromRequest(request);
            if (token == null || !jwtUtil.validateAccessToken(token)) {
                sendUnauthorizedResponse(response, "유효하지 않은 토큰입니다.");
                return;
            }

            AuthenticatedUser user = extractUserFromToken(token);
            request.setAttribute(AUTHENTICATED_USER_KEY, user);

            filterChain.doFilter(request, response);

        } catch (JwtValidationException e) {
            sendUnauthorizedResponse(response, "토큰 검증 실패: " + e.getMessage());
        } catch (Exception e) {
            sendUnauthorizedResponse(response, "인증 처리 중 오류가 발생했습니다.");
        }
    }

    private boolean requiresJwtAuthentication(HttpServletRequest request) throws Exception {

        HandlerExecutionChain executionChain = handlerMapping.getHandler(request);
        if (executionChain == null || !(executionChain.getHandler() instanceof HandlerMethod)) {
            return false;
        }

        JwtRequired jwtRequired = getJwtRequiredAnnotation((HandlerMethod) executionChain.getHandler());
        return jwtRequired != null;
    }

    private JwtRequired getJwtRequiredAnnotation(HandlerMethod handlerMethod) {

        JwtRequired methodAnnotation = handlerMethod.getMethodAnnotation(JwtRequired.class);
        if (methodAnnotation != null) {
            return methodAnnotation;
        }

        return handlerMethod.getBeanType().getAnnotation(JwtRequired.class);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {

        String authHeader = request.getHeader(JwtUtil.HEADER_AUTH);

        if (authHeader != null && authHeader.startsWith(JwtUtil.TOKEN_PREFIX)) {
            String token = authHeader.substring(JwtUtil.TOKEN_PREFIX.length()).trim();
            return token.isEmpty() ? null : token;
        }

        return null;
    }

    private AuthenticatedUser extractUserFromToken(String token) {

        String email = jwtUtil.extractEmail(token);
        AuthenticatedUser user = authenticatedUserService.getAuthenticatedUser(email);

        return AuthenticatedUser.builder()
            .memberId(user.getMemberId())
            .email(user.getEmail())
            .build();
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(String.format("{\"error\":\"%s\"}", message));
    }
}
