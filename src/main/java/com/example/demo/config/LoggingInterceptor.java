package com.example.demo.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LogManager.getLogger(LoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        // You can later replace "Guest" with a real username from session or form
        String username = "Guest";

        // Store contextual data in ThreadContext (MDC)
        ThreadContext.put("user", username);
        ThreadContext.put("uri", request.getRequestURI());
        ThreadContext.put("method", request.getMethod());

        logger.info("Incoming request: [{}] {} by {}", request.getMethod(), request.getRequestURI(), username);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {

        logger.info("Completed request: [{}] {} with status: {}", request.getMethod(),
                request.getRequestURI(), response.getStatus());

        // Clear MDC to prevent data leaks between threads
        ThreadContext.clearAll();
    }
}