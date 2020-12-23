package com.github.jntakpe.sbavailability.client

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.kotlin.test.test

@SpringBootTest
internal class UserClientTest(@Autowired private val client: UserClient) {

    @Test
    fun `find by id should return user`() {
        val id = "5fdb5cdad07bba25f645cd87"
        client.findById(id).test()
            .consumeNextWith { assertThat(it.id).isEqualTo(id) }
            .verifyComplete()
    }
}
