package com.github.jntakpe.sbavailability.endpoint

import com.github.jntakpe.commons.context.CommonException
import com.github.jntakpe.commons.context.logger
import com.github.jntakpe.sbavailability.mapping.toEntity
import com.github.jntakpe.sbavailability.mapping.toResponse
import com.github.jntakpe.sbavailability.model.dto.api.UserAvailabilityApiDto
import com.github.jntakpe.sbavailability.service.UserAvailabilityService
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.validation.Valid

@RestController
@RequestMapping("/availabilities")
class UserAvailabilityEndpoint(private val service: UserAvailabilityService) {

    private val log = logger()

    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): Mono<UserAvailabilityApiDto> {
        return service.findById(ObjectId(id))
            .map { it.toResponse() }
    }

    @GetMapping
    fun findByUser(@RequestParam userId: String?, @RequestParam username: String?): Flux<UserAvailabilityApiDto> {
        return when {
            userId != null -> service.findByUserId(userId)
            username != null -> service.findByUsername(username)
            else -> throw CommonException("Both parameters userId and username are null", log::info, HttpStatus.BAD_REQUEST)
        }.map { it.toResponse() }
    }

    @PostMapping
    fun declareAvailability(@Valid @RequestBody dto: UserAvailabilityApiDto): Mono<UserAvailabilityApiDto> {
        return service.declareAvailability(dto.toEntity())
            .map { it.toResponse() }
    }
}
