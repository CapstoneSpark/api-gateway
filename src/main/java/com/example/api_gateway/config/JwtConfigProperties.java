package com.example.api_gateway.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Component;

@Component
public class JwtConfigProperties {

    private final Dotenv dotenv = Dotenv.load();

    public String getSecret() {
        String s = dotenv.get("JWT_SECRET");
        if (s == null || s.isEmpty()) {
            throw new IllegalStateException("JWT_SECRET missing in .env");
        }
        return s;
    }

    public long getExpirationMs() {
        String v = dotenv.get("JWT_EXPIRATION_MS");
        return v == null ? 3600000 : Long.parseLong(v);
    }
}
