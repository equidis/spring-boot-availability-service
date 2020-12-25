package com.github.jntakpe.sbavailability.service

import com.github.jntakpe.commons.cache.RedisReactiveCache
import com.github.jntakpe.commons.context.CommonException
import com.github.jntakpe.commons.context.logger
import com.github.jntakpe.sbavailability.client.UserClient
import com.github.jntakpe.sbavailability.model.entity.UserAvailability
import com.github.jntakpe.sbavailability.repository.UserAvailabilityRepository
import org.bson.types.ObjectId
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono

@Service
class UserAvailabilityService(
    private val repository: UserAvailabilityRepository,
    private val client: UserClient,
    cacheManager: RedisCacheManager,
) {

    private val log = logger()
    private val usersAvailabilityCache = RedisReactiveCache("users-availability", cacheManager)

    fun findById(id: ObjectId): Mono<UserAvailability> {
        return usersAvailabilityCache.orPutOnCacheMiss(id) { repository.findById(id) }
            .doOnSubscribe { log.debug("Searching user availability by id {}", id) }
            .doOnNext { log.debug("{} retrieved using it's id", it) }
            .switchIfEmpty(missingIdError(id).toMono())
    }

    fun findByUserId(userId: String): Flux<UserAvailability> {
        return usersAvailabilityCache.orPutOnCacheMiss(userId) { repository.findByUserId(userId).collectList() }
            .doOnSubscribe { log.debug("Searching user availability by user id {}", userId) }
            .doOnNext { log.debug("{} availabilities retrieved using user id {}", it.size, userId) }
            .flatMapMany { it.toFlux() }
    }

    fun findByUsername(username: String): Flux<UserAvailability> {
        return client.findByUsername(username)
            .onErrorResume({ it.isNotFoundError() }) { Mono.empty() }
            .doOnSubscribe { log.debug("Searching user availability identifier using it's username {}", username) }
            .doOnNext { log.debug("User {} retrieved using it's username", it.username) }
            .flatMapMany { findByUserId(it.id) }
    }

    private fun Throwable.isNotFoundError() = (this as? CommonException)?.status == HttpStatus.NOT_FOUND

    private fun missingIdError(id: ObjectId) = CommonException("No user found for id $id", log::debug, HttpStatus.NOT_FOUND)
}

