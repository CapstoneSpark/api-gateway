package com.example.api_gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

import org.springframework.web.server.ServerWebExchange;

@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        String id = UUID.randomUUID().toString();

        log.info("[{}] {} {}", id,
                exchange.getRequest().getMethod(),
                exchange.getRequest().getURI());

        exchange.getResponse().getHeaders().add("X-Correlation-Id", id);

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
