package com.github.jntakpe.sbavailability.client

import com.github.jntakpe.commons.context.CommonException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
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

    @Test
    fun `find by id should fail when id does not exists`() {
        val id = "123456789012345678901234"
        client.findById(id).test()
            .consumeErrorWith {
                assertThat(it).isInstanceOf(CommonException::class.java)
                it as CommonException
                assertThat(it.status).isEqualTo(HttpStatus.NOT_FOUND)
            }
            .verify()
    }

    @Test
    fun `find by username should return user`() {
        val username = "jdoe"
        client.findByUsername(username).test()
            .consumeNextWith { assertThat(it.username).isEqualTo(username) }
            .verifyComplete()
    }

    @Test
    fun `find by username should fail when username does not exists`() {
        val username = "unknown"
        client.findByUsername(username).test()
            .consumeErrorWith {
                assertThat(it).isInstanceOf(CommonException::class.java)
                it as CommonException
                assertThat(it.status).isEqualTo(HttpStatus.NOT_FOUND)
            }
            .verify()
    }
}
