package com.example.apigateway.config;

import com.example.apigateway.filter.JwtReactiveAuthenticationWebFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final JwtReactiveAuthenticationWebFilter jwtWebFilter;

    public SecurityConfig(JwtReactiveAuthenticationWebFilter jwtWebFilter) {
        this.jwtWebFilter = jwtWebFilter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(csrf -> csrf.disable())
//                .cors(cors -> {}) // CORS 활성화
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers("/user-service/users/me").authenticated()
                        .pathMatchers(HttpMethod.POST,
                                "/article-service/comments/**").authenticated()
                        .pathMatchers(HttpMethod.PUT,
                                "/article-service/comments/**").authenticated()
                        .pathMatchers(HttpMethod.DELETE,
                                "/article-service/comments/**").authenticated()
                        .pathMatchers(HttpMethod.POST,
                                "/article-service/articles/like/**").authenticated()
                        .anyExchange().permitAll()
                );

        http.addFilterAt(jwtWebFilter, SecurityWebFiltersOrder.AUTHENTICATION);

        return http.build();
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("https://swtodaynews.netlify.app");
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);

        config.setExposedHeaders(List.of("accessToken", "refreshToken"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}
