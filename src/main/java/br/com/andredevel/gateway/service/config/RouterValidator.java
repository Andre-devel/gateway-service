package br.com.andredevel.gateway.service.config;

import org.springframework.http.server.reactive.ServerHttpRequest;      
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;

@Service
public class RouterValidator {
    public static final List<String> openApiEndpoints = List.of(
            "/auth/login"
    );  
    
    public Predicate<ServerHttpRequest> isSecured = request -> 
            openApiEndpoints.stream()
                .noneMatch(uri -> request.getURI().getPath().contains(uri));
}
