package com.github.jntakpe.sbavailability.mapping

import com.github.jntakpe.sbavailability.model.dto.api.UserAvailabilityApiDto
import com.github.jntakpe.sbavailability.model.entity.UserAvailability

fun UserAvailabilityApiDto.toEntity() = UserAvailability(userId, day, arrangement)

fun UserAvailability.toResponse() = UserAvailabilityApiDto(userId, day, arrangement, id.toString())
