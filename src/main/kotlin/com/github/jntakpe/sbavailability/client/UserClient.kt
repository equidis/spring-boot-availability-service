package com.github.jntakpe.sbavailability.client

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.jntakpe.commons.context.CommonException
import com.github.jntakpe.commons.context.CommonExceptionDto
import com.github.jntakpe.commons.context.logger
import com.github.jntakpe.commons.context.toException
import com.github.jntakpe.sbavailability.model.dto.client.UserClientDto
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono

@Component
class UserClient(discoveryClient: DiscoveryClient) {

    private val log = logger()
    private val objectMapper = jacksonObjectMapper()
    private val client = WebClient.builder().baseUrl("${discoveryClient.getInstances("sb-users").first().uri}/users").build()

    fun findById(id: String): Mono<UserClientDto> {
        return client.get()
            .uri("/{id}", id)
            .retrieve()
            .bodyToMono(UserClientDto::class.java)
            .onErrorMap { it.toCommonException() }
    }

    fun findByUsername(username: String): Mono<UserClientDto> {
        return client.get()
            .uri { it.queryParam("username", username).build() }
            .retrieve()
            .bodyToMono(UserClientDto::class.java)
            .onErrorMap { it.toCommonException() }
    }

    private fun Throwable.toCommonException(): CommonException {
        return (this as? WebClientResponseException)?.toCommonException()
            ?: CommonException("Unexpected client error", log::warn, HttpStatus.INTERNAL_SERVER_ERROR, this)
    }

    private fun WebClientResponseException.toCommonException(): CommonException {
        return objectMapper.readValue<CommonExceptionDto>(responseBodyAsByteArray)
            .toException(log::info)
    }
}
