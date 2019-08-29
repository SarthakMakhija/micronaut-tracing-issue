package org.examples

import io.micronaut.http.HttpRequest
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Filter
import io.micronaut.http.filter.OncePerRequestHttpServerFilter
import io.micronaut.http.filter.ServerFilterChain
import io.micronaut.tracing.annotation.NewSpan
import io.opentracing.Tracer
import org.reactivestreams.Publisher
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Filter("/**")
open class UserFilter(private val tracer: Tracer) : OncePerRequestHttpServerFilter() {

    @NewSpan("doFilter")
    override fun doFilterOnce(request: HttpRequest<*>, chain: ServerFilterChain): Publisher<MutableHttpResponse<*>> {
        logger.info("doFilter $tracer")
        return chain.proceed(request)
    }

    private val logger: Logger = LoggerFactory.getLogger(UserFilter::class.java)
}