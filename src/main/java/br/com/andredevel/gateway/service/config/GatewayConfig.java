package br.com.andredevel.gateway.service.config;

import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.gateway.route.RouteLocator;    

@Configuration
@EnableHystrix
public class GatewayConfig {
    
    private final AuthenticatorFilter authenticatorFilter;
    
    public GatewayConfig(AuthenticatorFilter authenticatorFilter) {
        this.authenticatorFilter = authenticatorFilter;
    }
    
    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service", r -> r.path("/users/**")
                        .filters(f -> f.filter(authenticatorFilter))
                        .uri("lb://user-service"))
                .route("auth-service", r -> r.path("/auth/**")
                        .filters(f -> f.filter(authenticatorFilter))
                        .uri("lb://auth-service"))
                .route("transaction-category-service", r -> r.path("/transaction-categories/**")
                        .filters(f -> f.filter(authenticatorFilter))
                        .uri("lb://transaction-category-service"))
                .build();
    }       
}
