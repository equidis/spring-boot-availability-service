package com.github.jntakpe.sbavailability.model.dto.api

import com.github.jntakpe.sbavailability.model.entity.WorkArrangementType
import java.time.LocalDate

data class UserAvailabilityApiDto(val userId: String, val day: LocalDate, val arrangement: WorkArrangementType, val id: String? = null)
