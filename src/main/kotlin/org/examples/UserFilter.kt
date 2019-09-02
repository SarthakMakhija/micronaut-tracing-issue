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

        //OPTION - (1)
        //This block of code does not print trace id in the logs of UserVerificationService because
        //method is not instrumented for tracing.
//        return Flowable.just(userVerificationService.isVerified())
//            .flatMap {
//                if (it)
//                    chain.proceed(request)
//                else
//                    throw RuntimeException("test")
//            }
//            .doOnSubscribe { logger.info("filtering ${request.path}") }


        //OPTION - (2)
        //This block of code does not print trace id in the logs of UserVerificationService because
        //method is not instrumented for tracing.

        //Also, traceid that gets logged in ->
        // doOnSubscribe { logger.info("isVerified") } and
        // doOnSubscribe { logger.info("doFilter") }
        // is different probably because tracing filter decorates them with different trace ids.
        val userVerificationFlowable: Flowable<Boolean> = Flowable
            .just(userVerificationService.isVerified())
            .doOnSubscribe { logger.info("isVerified") }

        val proceedFlowable: Flowable<MutableHttpResponse<*>> = Flowable
            .fromPublisher(chain.proceed(request))
            .doOnSubscribe { logger.info("doFilter") }

        return userVerificationFlowable.flatMap {
            if (it)
                proceedFlowable
            else
                throw RuntimeException("test")
        }
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