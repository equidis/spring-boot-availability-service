package com.github.jntakpe.sbavailability.mapping

import com.github.jntakpe.sbavailability.model.dto.api.UserAvailabilityApiDto
import com.github.jntakpe.sbavailability.model.entity.UserAvailability
import com.github.jntakpe.sbavailability.model.entity.WorkArrangementType
import com.github.jntakpe.sbusers.dao.UserAvailabilityDao.PersistedData.JDOE_ID
import com.github.jntakpe.sbusers.dao.UserAvailabilityDao.PersistedData.jdoeOnsite
import com.github.jntakpe.sbusers.dao.UserAvailabilityDao.TransientData.MMOE_ID
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class UserAvailabilityMappingsKtTest {

    @Test
    fun `to entity should map request`() {
        val date = LocalDate.of(2020, 10, 31)
        val request = UserAvailabilityApiDto(MMOE_ID, date, WorkArrangementType.ONSITE)
        val entity = request.toEntity()
        val expected = UserAvailability(MMOE_ID, date, WorkArrangementType.ONSITE)
        assertThat(entity).usingRecursiveComparison()
            .ignoringFields(UserAvailability::id.name)
            .isEqualTo(expected)
        assertThat(entity.id).isNotNull
    }

    @Test
    fun `to response should map entity`() {
        val response = jdoeOnsite.toResponse()
        val expected = UserAvailabilityApiDto(JDOE_ID, LocalDate.of(2020, 10, 15), WorkArrangementType.ONSITE, jdoeOnsite.id.toString())
        assertThat(response).usingRecursiveComparison().isEqualTo(expected)
    }
}
