package com.example.apigateway.filter;

import com.example.jwt_util.util.JWTUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Gateway 전용 JWT 검증 필터.
 * <p>
 * 1. Authorization 헤더가 "Bearer {token}" 형식인지 확인합니다.
 * 2. JWTUtil 로 만료 여부와 토큰 type(accessToken) 을 검사합니다.
 * 3. 유효하지 않을 경우 401/400 오류를 반환하고, 유효하면 다음 필터 체인으로 전달합니다.
 */
@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    private final JWTUtil jwtUtil;

    public AuthorizationHeaderFilter(JWTUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    /** 외부 YAML 설정이 필요한 경우를 대비한 빈 Config */
    public static class Config {}

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // 1) Authorization 헤더 존재 여부
            if (!request.getHeaders().containsKey("Authorization")) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            // 2) Bearer 토큰 파싱
            String authorization = request.getHeaders().getFirst("Authorization");
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                return onError(exchange, HttpStatus.BAD_REQUEST);
            }

            String token = authorization.substring(7);

            // 토큰에서 username과 roles 추출
            String username;
            String rolesCsv;
            try {
                username = jwtUtil.getUsername(token);
                rolesCsv = String.join(",", jwtUtil.getRole(token));
            } catch (Exception e) {
                // 토큰 파싱 중 예외 발생 시
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            try {
                // 3-a) 토큰 만료 체크
                if (jwtUtil.isExpired(token)) {
                    return onError(exchange, HttpStatus.UNAUTHORIZED);
                }

                // 3-b) accessToken 인지 확인
                String type = jwtUtil.getType(token);
                if (!"accessToken".equals(type)) {
                    return onError(exchange, HttpStatus.UNAUTHORIZED);
                }
            } catch (Exception e) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            // 4) 토큰이 유효하면, downstream 요청에 사용자 정보 헤더 추가
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Name", username)
                    .header("X-User-Roles", rolesCsv)
                    .build();

            ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(modifiedRequest)
                    .build();

            // 5) 필터 체인으로 전달
            return chain.filter(modifiedExchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }
}

