package com.socarras.gatewayserver.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class ResponseTraceFilter {

    private final FilterUtility filterUtility;

    public ResponseTraceFilter(FilterUtility filterUtility) {
        this.filterUtility = filterUtility;
    }

    @Bean
    public GlobalFilter postGlobalFilter() {
        return (exchange, chain) ->
                chain.filter(exchange).then(Mono.fromRunnable(() -> {
                    HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
                    String correlationId = filterUtility.getCorrelationId(requestHeaders);

                    exchange.getResponse()
                            .getHeaders()
                            .add(FilterUtility.CORRELATION_ID, correlationId);

                    log.info("Updated the correlation id to the outbound headers: {}", correlationId);
                }));
    }
}
