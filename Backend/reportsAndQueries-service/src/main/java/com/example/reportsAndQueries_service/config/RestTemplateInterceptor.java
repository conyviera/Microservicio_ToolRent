package com.example.reportsAndQueries_service.config;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.io.IOException;

public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        // Tomamos el token de la sesión actual
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Si es un token JWT válido, lo pegamos en la petición saliente
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) authentication;
            request.getHeaders().add("Authorization", "Bearer " + jwtToken.getToken().getTokenValue());
        }
        return execution.execute(request, body);
    }
}
