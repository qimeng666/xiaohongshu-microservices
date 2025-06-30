package com.example.xiaohongshu_microservices.config;

import com.example.xiaohongshu_microservices.Service.BlacklistService;
import com.example.xiaohongshu_microservices.Service.UserService;
import com.example.xiaohongshu_microservices.Utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final BlacklistService blacklistService;
    private final UserService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, BlacklistService blacklistService, UserService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.blacklistService = blacklistService;
        this.userDetailsService = userDetailsService;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                // 检查黑名单
                if (blacklistService.isBlacklisted(token)) {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    return;
                }
                Claims claims = jwtUtil.parseToken(token);
                String username = claims.getSubject();
                UserDetails user = userDetailsService.loadUserByUsername(request.getLocalName());
                var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (JwtException e) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
