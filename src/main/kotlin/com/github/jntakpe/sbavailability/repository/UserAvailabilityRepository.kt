package com.github.jntakpe.sbavailability.repository

import com.github.jntakpe.commons.mongo.SbReactiveMongoRepository
import com.github.jntakpe.sbavailability.model.entity.UserAvailability
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface UserAvailabilityRepository : SbReactiveMongoRepository<UserAvailability> {

    fun findByUserId(userId: String): Flux<UserAvailability>

    fun create(availability: UserAvailability): Mono<UserAvailability>
}
