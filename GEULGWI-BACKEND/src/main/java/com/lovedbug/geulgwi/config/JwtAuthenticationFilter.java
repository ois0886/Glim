package com.lovedbug.geulgwi.config;

import com.lovedbug.geulgwi.service.CustomUserDetailsService;
import com.lovedbug.geulgwi.service.MemberService;
import com.lovedbug.geulgwi.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final BearerTokenResolver tokenResolver = new DefaultBearerTokenResolver();

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        String token = tokenResolver.resolve(request);

        if (token == null){
            filterChain.doFilter(request, response);
            return ;
        }

        try {
            authenticateWithJwt(token, request);
        }catch (Exception e){
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateWithJwt(String token, HttpServletRequest request){

        if (!jwtUtil.validateAccessToken(token)) {
            return ;
        }

        if (jwtUtil.extractEmail(token) == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            return ;
        }

        setAuthenticationInSecurityContext(jwtUtil.extractEmail(token), request);
    }

    private void setAuthenticationInSecurityContext(String email, HttpServletRequest request) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
            );

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
