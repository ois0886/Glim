package com.lovedbug.geulgwi.core.security;

import com.lovedbug.geulgwi.core.security.dto.AuthenticatedUser;
import com.lovedbug.geulgwi.external.email.AuthenticatedUserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AuthenticatedUserService authenticatedUserService;
    public static final String AUTHENTICATED_USER_KEY = "authenticatedUser";

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain)
        throws IOException {

        try {
            String token = extractTokenFromRequest(request);

            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }

            if (request.getRequestURI().equals("/api/v1/auth/refresh")) {
                if (!jwtUtil.validateRefreshToken(token)) {
                    sendUnauthorizedResponse(response, "유효하지 않은 리프레시 토큰입니다.");
                    return;
                }
            } else {

                if (!jwtUtil.validateAccessToken(token)) {
                    sendUnauthorizedResponse(response, "유효하지 않은 액세스 토큰입니다.");
                    return;
                }
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
