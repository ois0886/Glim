package com.lovedbug.geulgwi.core.common.log;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GeulgwiLogFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final List<String> IGNORE_START_URI = Stream.of("/images", "/webjars/s", "/actuator", "/favicon.ico").collect(Collectors.toList());
    private static final List<String> IGNORE_CONTAIN_URI = Stream.of("health", "favicon.ico").collect(Collectors.toList());
    private static final List<String> IGNORE_URI = Stream.of("/").collect(Collectors.toList());

    @Override
    public void init(FilterConfig filterConfig) {
        logger.info(this.getClass().getCanonicalName() + "is initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (isIgnoreFilterCase(request, response)) {
            chain.doFilter(request, response);
        } else {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            putHttpRequestLogField(httpRequest);

            logger.info("Incoming request: {} {}", httpRequest.getMethod(), httpRequest.getRequestURI());

            chain.doFilter(request, response);

            putHttpResponseLogField(httpResponse);

            logger.info("Response status: {} for {} {}", httpResponse.getStatus(), httpRequest.getMethod(), httpRequest.getRequestURI());

            clearLogField();
        }
    }

    private void putHttpRequestLogField(HttpServletRequest httpRequest) {
        String requestURI = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();
        String queryString = httpRequest.getQueryString();
        String remoteAddr = httpRequest.getRemoteAddr();

        MDC.put("requestURI", requestURI);
        MDC.put("method", method);
        MDC.put("queryString", queryString == null ? "" : queryString);
        MDC.put("remoteAddress", remoteAddr);
    }

    private void putHttpResponseLogField(HttpServletResponse httpResponse) {
        MDC.put("status", Integer.toString(httpResponse.getStatus()));
    }

    private void clearLogField() {
        MDC.clear();
    }

    private boolean isIgnoreFilterCase(ServletRequest request, ServletResponse response) {
        if (!this.isHttpServlet(request, response)) {
            return true;
        } else {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String uri = httpRequest.getRequestURI().trim();

            return isIgnoreUri(uri);
        }
    }


    private boolean isHttpServlet(ServletRequest request, ServletResponse response) {
        return request instanceof HttpServletRequest && response instanceof HttpServletResponse;
    }

    private boolean isIgnoreUri(String uri) {
        return startWithIgnoreUri(uri) || entireIgnoreUri(uri) || containIgnoreUri(uri);
    }

    private boolean startWithIgnoreUri(String uri) {
        Objects.requireNonNull(uri);

        return IGNORE_START_URI.stream().anyMatch(i -> uri.startsWith(i));
    }

    private boolean containIgnoreUri(String uri) {
        Objects.requireNonNull(uri);

        return IGNORE_CONTAIN_URI.stream().anyMatch(i -> uri.contains(i));
    }

    private boolean entireIgnoreUri(String uri) {
        Objects.requireNonNull(uri);

        return IGNORE_URI.stream().anyMatch(i -> uri.equals(i));
    }

    @Override
    public void destroy() {
        logger.info(this.getClass().getCanonicalName() + "is destroyed");
    }
}
