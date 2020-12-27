package com.github.jntakpe.sbavailability.model.dto.api

import com.github.jntakpe.sbavailability.model.entity.WorkArrangementType
import org.hibernate.validator.constraints.Length
import java.time.LocalDate

data class UserAvailabilityApiDto(
    @field:Length(min = 24, max = 24) val userId: String,
    val day: LocalDate,
    val arrangement: WorkArrangementType,
    @field:Length(min = 24, max = 24) val id: String? = null,
)
