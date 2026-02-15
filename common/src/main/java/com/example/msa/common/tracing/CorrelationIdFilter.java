package com.example.msa.common.tracing;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class CorrelationIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String incoming = request.getHeader(CorrelationContext.HEADER_NAME);
        if (StringUtils.hasText(incoming)) {
            CorrelationContext.set(incoming);
        } else {
            CorrelationContext.getOrGenerate();
        }
        response.setHeader(CorrelationContext.HEADER_NAME, CorrelationContext.getOrGenerate());
        try {
            filterChain.doFilter(request, response);
        } finally {
            CorrelationContext.clear();
        }
    }
}
