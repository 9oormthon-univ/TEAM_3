package com.allergenie.server.config.jwt;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public
class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //String token = resolveToken(request.getHeader("Authorization"));// HTTP header에서 token 받아오기
        String token = jwtTokenProvider.resolveToken(request);


        if (token != null) {
            boolean authentication = jwtTokenProvider.validateToken(token);
            System.out.println(authentication);
            //SecurityContextHolder.getContext().setAuthentication(authentication);

            System.out.println("token received");
            System.out.println(SecurityContextHolder.getContext().getAuthentication());
        }
        filterChain.doFilter(request, response); // 필터 작동
    }

    private String resolveToken(String authorization) {
        return authorization != null ? authorization.substring(7) : null;
    }
}

