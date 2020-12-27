package com.github.jntakpe.sbavailability.endpoint

import com.github.jntakpe.commons.context.CommonExceptionDto
import com.github.jntakpe.sbavailability.mapping.toResponse
import com.github.jntakpe.sbavailability.model.dto.api.UserAvailabilityApiDto
import com.github.jntakpe.sbavailability.model.entity.UserAvailability
import com.github.jntakpe.sbavailability.model.entity.WorkArrangementType
import com.github.jntakpe.sbusers.dao.UserAvailabilityDao
import com.github.jntakpe.sbusers.dao.UserAvailabilityDao.PersistedData.JDOE_ID
import com.github.jntakpe.sbusers.dao.UserAvailabilityDao.PersistedData.JDOE_USERNAME
import com.github.jntakpe.sbusers.dao.UserAvailabilityDao.TransientData.MDOE_ID
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import java.time.LocalDate

@SpringBootTest
@AutoConfigureWebTestClient
internal class UserAvailabilityEndpointTest(
    @Autowired private val dao: UserAvailabilityDao,
    @Autowired private val client: WebTestClient,
    @Autowired private val cacheManager: RedisCacheManager,
) {

    private val rawCache = cacheManager.getCache("users-availability")!!
    private val availabilitiesPath = "/availabilities"

    @BeforeEach
    fun setup() {
        dao.init()
        rawCache.clear()
    }

    @ParameterizedTest
    @ArgumentsSource(UserAvailabilityDao.PersistedData::class)
    fun `find by id should return ok response`(userAvailability: UserAvailability) {
        client.get()
            .uri("$availabilitiesPath/{id}", userAvailability.id)
            .exchange()
            .expectBody<UserAvailabilityApiDto>()
            .consumeWith { assertThat(it.responseBody?.id).isNotNull.isEqualTo(userAvailability.id.toString()) }
    }

    @ParameterizedTest
    @ArgumentsSource(UserAvailabilityDao.TransientData::class)
    fun `find by id should fail when user does not exist`(userAvailability: UserAvailability) {
        client.get()
            .uri("$availabilitiesPath/{id}", userAvailability.id)
            .exchange()
            .expectBody<CommonExceptionDto>()
            .consumeWith { assertThat(it.responseBody?.code).isNotNull.isEqualTo(HttpStatus.NOT_FOUND.value()) }
    }

    @Test
    fun `find by user should return ok response using user id`() {
        client.get()
            .uri { it.path(availabilitiesPath).queryParam("userId", JDOE_ID).build() }
            .exchange()
            .expectBody<List<UserAvailabilityApiDto>>()
            .consumeWith { r -> assertThat(r.responseBody!!.map { it.userId }).isNotEmpty.containsOnly(JDOE_ID) }
    }

    @Test
    fun `find by user should return empty when user id does not exist`() {
        client.get()
            .uri { it.path(availabilitiesPath).queryParam("userId", MDOE_ID).build() }
            .exchange()
            .expectBody<List<UserAvailabilityApiDto>>()
            .consumeWith { assertThat(it.responseBody!!).isEmpty() }
    }

    @Test
    fun `find by user should return ok response using username`() {
        client.get()
            .uri { it.path(availabilitiesPath).queryParam("username", JDOE_USERNAME).build() }
            .exchange()
            .expectBody<List<UserAvailabilityApiDto>>()
            .consumeWith { r -> assertThat(r.responseBody!!.map { it.userId }).isNotEmpty.containsOnly(JDOE_ID) }
    }

    @Test
    fun `find by user should return empty when username does not exist`() {
        client.get()
            .uri { it.path(availabilitiesPath).queryParam("username", "unknown").build() }
            .exchange()
            .expectBody<List<UserAvailabilityApiDto>>()
            .consumeWith { assertThat(it.responseBody!!).isEmpty() }
    }

    @ParameterizedTest
    @ArgumentsSource(UserAvailabilityDao.TransientData::class)
    fun `declare availability should return ok response`(userAvailability: UserAvailability) {
        val initSize = dao.count()
        client.post()
            .uri(availabilitiesPath)
            .bodyValue(userAvailability.toResponse())
            .exchange()
            .expectBody<UserAvailabilityApiDto>()
            .consumeWith {
                assertThat(it.responseBody!!.id).isNotEmpty
                assertThat(dao.count()).isEqualTo(initSize + 1)
            }
    }

    @ParameterizedTest
    @ArgumentsSource(UserAvailabilityDao.PersistedData::class)
    fun `declare availability should fail when user availability already exists`(userAvailability: UserAvailability) {
        val initSize = dao.count()
        client.post()
            .uri(availabilitiesPath)
            .bodyValue(userAvailability.toResponse())
            .exchange()
            .expectBody<CommonExceptionDto>()
            .consumeWith {
                assertThat(it.responseBody!!.code).isEqualTo(HttpStatus.CONFLICT.value())
                assertThat(dao.count()).isEqualTo(initSize)
            }
    }

    @Test
    fun `declare availability should fail when missing user id`() {
        client.post()
            .uri(availabilitiesPath)
            .bodyValue(UserAvailabilityApiDto("", LocalDate.of(2020, 12, 28), WorkArrangementType.OFF))
            .exchange()
            .expectBody<CommonExceptionDto>()
            .consumeWith { assertThat(it.responseBody!!.code).isEqualTo(HttpStatus.BAD_REQUEST.value()) }
    }
}
