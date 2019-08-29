package org.examples

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.client.annotation.Client
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Controller
class UserController(private val userService: UserService) {

    private val logger: Logger = LoggerFactory.getLogger(UserController::class.java)

    @Get("/users/{userId}")
    fun userBy(userId: String): User {
        logger.info("Returning user by $userId")
        val user = userService.userBy(userId)
        logger.info("Found user with email ${user.email}")
        return user
    }
}

@Singleton
class UserService(private val userClient: UserClient) {

    private val logger: Logger = LoggerFactory.getLogger(UserService::class.java)

    fun userBy(userId: String): User {
        val response = userClient.userBy(userId)
        val user = response.body()!!
        logger.info("Found user with email ${user.email}")
        return user
    }
}

@Client("https://reqres.in/api")
interface UserClient {

    @Get("/users/{userId}")
    fun userBy(userId: String): HttpResponse<User>
}

class User {

    var id: Int = -1
    lateinit var email: String

    @JsonProperty("data")
    private fun attributes(attributes: Map<String, Any>) {
        this.id    = attributes["id"] as Int
        this.email = attributes["email"] as String
    }
}