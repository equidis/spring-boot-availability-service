package com.github.jntakpe.sbavailability.client

import com.github.jntakpe.sbavailability.model.dto.client.UserClientDto
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class UserClient(discoveryClient: DiscoveryClient) {

    private val client = WebClient.builder().baseUrl("${discoveryClient.getInstances("sb-users").first().uri}/users").build()

    fun findById(id: String): Mono<UserClientDto> = client.get().uri("/{id}", id).retrieve().bodyToMono(UserClientDto::class.java)

    fun findByUsername(username: String): Mono<UserClientDto> {
        return client.get()
            .uri { it.queryParam("username", username).build() }
            .retrieve()
            .bodyToMono(UserClientDto::class.java)
    }
}
