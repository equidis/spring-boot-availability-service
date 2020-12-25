package com.github.jntakpe.sbavailability.service

import com.github.jntakpe.commons.test.expectCommonException
import com.github.jntakpe.sbavailability.client.UserClient
import com.github.jntakpe.sbavailability.model.dto.client.UserClientDto
import com.github.jntakpe.sbavailability.model.entity.UserAvailability
import com.github.jntakpe.sbavailability.repository.UserAvailabilityRepository
import com.github.jntakpe.sbusers.dao.UserAvailabilityDao
import com.github.jntakpe.sbusers.dao.UserAvailabilityDao.PersistedData.JDOE_ID
import com.github.jntakpe.sbusers.dao.UserAvailabilityDao.PersistedData.JDOE_USERNAME
import com.github.jntakpe.sbusers.dao.UserAvailabilityDao.TransientData.MDOE_ID
import com.github.jntakpe.sbusers.dao.UserAvailabilityDao.TransientData.MDOE_USERNAME
import io.mockk.Called
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.http.HttpStatus
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test

@SpringBootTest
internal class UserAvailabilityServiceTest(
    @Autowired private val service: UserAvailabilityService,
    @Autowired private val dao: UserAvailabilityDao,
    @Autowired private val repository: UserAvailabilityRepository,
    @Autowired private val client: UserClient,
    @Autowired private val cacheManager: RedisCacheManager,
) {

    private val rawCache = cacheManager.getCache("users-availability")!!

    @BeforeEach
    fun setup() {
        dao.init()
        rawCache.clear()
    }

    @ParameterizedTest
    @ArgumentsSource(UserAvailabilityDao.PersistedData::class)
    fun `find by id should return user availability`(userAvailability: UserAvailability) {
        service.findById(userAvailability.id).test()
            .expectNext(userAvailability)
            .verifyComplete()
    }

    @ParameterizedTest
    @ArgumentsSource(UserAvailabilityDao.PersistedData::class)
    fun `find by id should call repository since cache miss`(userAvailability: UserAvailability) {
        val repoSpy = spyk(repository)
        UserAvailabilityService(repoSpy, client, cacheManager).findById(userAvailability.id).test()
            .expectNext(userAvailability)
            .then {
                verify { repoSpy.findById(userAvailability.id) }
                confirmVerified(repoSpy)
                assertThat(rawCache.get(userAvailability.id, UserAvailability::class.java)).isNotNull.isEqualTo(userAvailability)
            }
            .verifyComplete()
    }

    @ParameterizedTest
    @ArgumentsSource(UserAvailabilityDao.PersistedData::class)
    fun `find by id should not call repository since retrieved from cache`(userAvailability: UserAvailability) {
        rawCache.put(userAvailability.id, userAvailability)
        val repoSpy = spyk(repository)
        UserAvailabilityService(repoSpy, client, cacheManager).findById(userAvailability.id).test()
            .expectNext(userAvailability)
            .then {
                verify { repoSpy.findById(userAvailability.id) wasNot Called }
                confirmVerified(repoSpy)
            }
            .verifyComplete()
    }

    @ParameterizedTest
    @ArgumentsSource(UserAvailabilityDao.TransientData::class)
    fun `find by id fail when user availability does not exists`(userAvailability: UserAvailability) {
        service.findById(userAvailability.id).test()
            .expectCommonException(HttpStatus.NOT_FOUND)
            .verify()
    }

    @Test
    fun `find by user id should return multiple availabilities`() {
        service.findByUserId(JDOE_ID).test()
            .recordWith { ArrayList() }
            .expectNextCount(UserAvailabilityDao.PersistedData.data().size.toLong())
            .consumeRecordedWith { l -> assertThat(l.map { it.userId }).containsOnly(JDOE_ID) }
            .verifyComplete()
    }

    @Test
    fun `find by user id should call repository since cache miss`() {
        val repoSpy = spyk(repository)
        UserAvailabilityService(repoSpy, client, cacheManager).findByUserId(JDOE_ID).test()
            .recordWith { ArrayList() }
            .expectNextCount(UserAvailabilityDao.PersistedData.data().size.toLong())
            .consumeRecordedWith { l -> assertThat(l.map { it.userId }).containsOnly(JDOE_ID) }
            .then {
                verify { repoSpy.findByUserId(JDOE_ID) }
                confirmVerified(repoSpy)
                assertThat(rawCache.get(JDOE_ID, List::class.java)).isNotNull.isEqualTo(UserAvailabilityDao.PersistedData.data())
            }
            .verifyComplete()
    }

    @Test
    fun `find by user id should not call repository since retrieved from cache`() {
        rawCache.put(JDOE_ID, UserAvailabilityDao.PersistedData.data())
        val repoSpy = spyk(repository)
        UserAvailabilityService(repoSpy, client, cacheManager).findByUserId(JDOE_ID).test()
            .recordWith { ArrayList() }
            .expectNextCount(UserAvailabilityDao.PersistedData.data().size.toLong())
            .then {
                verify { repoSpy.findByUserId(JDOE_ID) wasNot Called }
                confirmVerified(repoSpy)
            }
            .verifyComplete()
    }

    @Test
    fun `find by user id return empty when user id does not exists`() {
        service.findByUserId(MDOE_ID).test()
            .expectNextCount(0)
            .verifyComplete()
    }

    @Test
    fun `find by username should return multiple availabilities`() {
        service.findByUsername(JDOE_USERNAME).test()
            .recordWith { ArrayList() }
            .expectNextCount(UserAvailabilityDao.PersistedData.data().size.toLong())
            .consumeRecordedWith { l -> assertThat(l.map { it.userId }).containsOnly(JDOE_ID) }
            .verifyComplete()
    }

    @Test
    fun `find by username return empty when username does not exists in database`() {
        val client = mockk<UserClient>()
        every { client.findByUsername(MDOE_USERNAME) } returns UserClientDto(MDOE_USERNAME, MDOE_ID).toMono()
        UserAvailabilityService(repository, client, cacheManager).findByUsername(MDOE_USERNAME).test()
            .expectNextCount(0)
            .verifyComplete()
    }

    @Test
    fun `find by username return empty when username does not exists in client service`() {
        service.findByUsername("unknown").test()
            .expectNextCount(0)
            .verifyComplete()
    }
}
