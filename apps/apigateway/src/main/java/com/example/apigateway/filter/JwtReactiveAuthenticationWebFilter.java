package com.example.apigateway.filter;

import com.example.jwt_util.util.JWTUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

/**
 * WebFlux 환경에서 들어오는 모든 요청에 대해
 * 1) Authorization 헤더에서 Bearer 토큰 파싱
 * 2) 만료 여부, 토큰 타입(accessToken) 검사
 * 3) 토큰에서 username, roles 추출
 * 4) Authentication 객체 생성 후 ReactiveSecurityContextHolder에 세팅
 *
 * 그 결과, SecurityWebFilterChain 내의 .authenticated() 검사를 통과할 수 있게 된다.
 */
@Component
public class JwtReactiveAuthenticationWebFilter implements WebFilter {

    private final JWTUtil jwtUtil;

    public JwtReactiveAuthenticationWebFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        // Bearer 토큰이 없거나 잘못된 형태면 인증 없이 그냥 다음 필터로
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String token = authHeader.substring(7);
        try {
            // 1) 토큰 만료 여부 체크
            if (jwtUtil.isExpired(token)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            // 2) 토큰 타입(accessToken) 확인
            String type = jwtUtil.getType(token);
            if (!"accessToken".equals(type)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // 3) 토큰에서 username 추출
            String username = jwtUtil.getUsername(token);

            // 4) 토큰에서 role 추출 (getRole(token)이 String을 반환한다고 가정)
            String roleString = jwtUtil.getRole(token);
            // 만약 한 개의 역할만 들어온다면:
            List<GrantedAuthority> authorities = Collections.singletonList(
                    new SimpleGrantedAuthority(roleString)
            );

            // 4) Authentication 객체 생성
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);

            // 5) ReactiveSecurityContextHolder에 Authentication 저장
            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));

        } catch (Exception ex) {
            // JWT 파싱 중 예외 발생 시 401 Unauthorized
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}
