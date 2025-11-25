package com.example.api_gateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtForwardingHeadersFilter implements GlobalFilter, Ordered {

    @Autowired
    private ReactiveJwtDecoder decoder;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        String auth = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (auth == null || !auth.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String token = auth.substring(7);

        return decoder.decode(token)
                .map(jwt -> {
                    var req = exchange.getRequest().mutate()
                            .header("X-User-Id", String.valueOf(jwt.getClaims().get("userId")))
                            .header("X-User", jwt.getSubject())
                            .header("X-Roles", jwt.getClaims().get("roles").toString())
                            .build();

                    return exchange.mutate().request(req).build();
                })
                .flatMap(chain::filter)
                .onErrorResume(e -> chain.filter(exchange));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
