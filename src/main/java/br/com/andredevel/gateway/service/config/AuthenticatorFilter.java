package br.com.andredevel.gateway.service.config;

import br.com.andredevel.gateway.service.services.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RefreshScope
@Component
public class AuthenticatorFilter implements GatewayFilter {
    
    private final RouterValidator routerValidator;
    private final JwtUtils jwtUtils;      
    
    public AuthenticatorFilter(RouterValidator routerValidator, JwtUtils jwtUtils) {
        this.routerValidator = routerValidator;
        this.jwtUtils = jwtUtils;
    }
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();      
        if (routerValidator.isSecured.test(request)) {
            if (authIsMissing(request)) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }
            
            final String token = request.getHeaders().getOrEmpty("Authorization").getFirst();
            
            if (jwtUtils.isTokenExpired(token)) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            ServerHttpRequest modifiedRequest = insertUserInfosInToHeader(token, request);

            return chain.filter(exchange.mutate().request(modifiedRequest).build());    
        }
        
        return chain.filter(exchange);
    }

    private ServerHttpRequest insertUserInfosInToHeader(String token, ServerHttpRequest request) {
        Claims claims = jwtUtils.parseToken(token);
        return request.mutate()
                .header("X-User-Id", claims.get("userId", String.class))
                .header("X-User-Role", claims.get("role", String.class))
                .build();
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();       
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    private boolean authIsMissing(ServerHttpRequest request) {
            return !request.getHeaders().containsKey("Authorization");
    }
}
