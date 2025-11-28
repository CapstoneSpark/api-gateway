package com.example.api_gateway.config;

import javax.crypto.spec.SecretKeySpec;  // ✅ correct import

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;

import java.util.Base64;

@Configuration
public class GatewaySecurityConfig {

    @Autowired
    private JwtConfigProperties jwtConfig;

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {

        http.csrf(ServerHttpSecurity.CsrfSpec::disable);

//        http.authorizeExchange(exchange -> exchange
//                .pathMatchers("/actuator/**").permitAll()
//                .pathMatchers("/api/v1/users/login", "/api/v1/users/register", "/auth/**").permitAll()
//                .anyExchange().authenticated()
//        );
        http.authorizeExchange(exchange -> exchange
                .pathMatchers("/actuator/**").permitAll()
                .pathMatchers("/api/v1/payments/**").permitAll()   // ← FIXED
                .pathMatchers("/api/v1/users/login", "/api/v1/users/register", "/auth/**").permitAll()
                .anyExchange().authenticated()
        );


        http.oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtDecoder(jwtDecoder()))
        );

        return http.build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {

        byte[] decodedKey = Base64.getDecoder().decode(jwtConfig.getSecret());
        SecretKeySpec keySpec = new SecretKeySpec(decodedKey, "HmacSHA512");

        return NimbusReactiveJwtDecoder
                .withSecretKey(keySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }
}
