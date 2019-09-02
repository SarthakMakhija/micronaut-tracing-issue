package org.examples

import io.micronaut.http.HttpRequest
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Filter
import io.micronaut.http.filter.OncePerRequestHttpServerFilter
import io.micronaut.http.filter.ServerFilterChain
import io.micronaut.tracing.annotation.NewSpan
import io.reactivex.Flowable
import org.reactivestreams.Publisher
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Filter("/**")
open class UserFilter(
    private val userVerificationService: UserVerificationService
) : OncePerRequestHttpServerFilter() {

    private val logger: Logger = LoggerFactory.getLogger(UserFilter::class.java)

    @NewSpan("doFilter")
    override fun doFilterOnce(request: HttpRequest<*>, chain: ServerFilterChain): Publisher<MutableHttpResponse<*>> {

        return Flowable.fromCallable { userVerificationService.isVerified() }
            .flatMap {
                if (it)
                    chain.proceed(request)
                else
                    throw RuntimeException("test")
            }
            .doOnSubscribe { logger.info("filtering ${request.path}") }
    }
}

@Singleton
open class UserVerificationService {

    private val logger: Logger = LoggerFactory.getLogger(UserVerificationService::class.java)

    open fun isVerified(): Boolean {
        //some network call and logging the response ..
        logger.info("Returning if user is verified ..")
        return true
    }
}