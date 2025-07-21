package com.benruehl.transaction_normalizer.api.configuration

import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Signal
import reactor.util.context.ContextView

@Component
class CustomerIdWebFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val path = exchange.request.path.toString()
        val customerId = extractCustomerId(path) // fallback if needed

        return chain.filter(exchange)
            .contextWrite { ctx -> ctx.put("customerId", customerId) }
            .withMdcContext()
    }

    private fun extractCustomerId(path: String): String {
        val match = Regex("/customers/([^/]+)").find(path)
        return match?.groupValues?.get(1) ?: "unknown"
    }
}

fun <T> Mono<T>.withMdcContext(): Mono<T> {
    return this
        .contextWrite { ctx -> ctx } // enforces context propagation in some scheduling cases
        .doOnEach { signal -> applyContextToMdc(signal) }
        .doFinally { MDC.clear() }
}

fun <T> Flux<T>.withMdcContext(): Flux<T> {
    return this
        .doOnEach { signal -> applyContextToMdc(signal) }
        .doFinally { MDC.clear() }
}

private fun applyContextToMdc(signal: Signal<*>) {
    if (signal.isOnNext || signal.isOnError || signal.isOnComplete) {
        copyContextToMdc(signal.contextView)
    }
}

private fun copyContextToMdc(contextView: ContextView) {
    contextView.getOrEmpty<String>("customerId")
        .ifPresent { MDC.put("customerId", it) }
}