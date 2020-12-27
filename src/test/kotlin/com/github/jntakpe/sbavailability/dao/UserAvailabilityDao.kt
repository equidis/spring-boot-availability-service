package com.github.jntakpe.sbusers.dao

import com.github.jntakpe.commons.mongo.test.MongoDao
import com.github.jntakpe.commons.test.TestDataProvider
import com.github.jntakpe.sbavailability.model.entity.UserAvailability
import com.github.jntakpe.sbavailability.model.entity.WorkArrangementType
import com.github.jntakpe.sbavailability.repository.UserAvailabilityRepository
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class UserAvailabilityDao(@Autowired repository: UserAvailabilityRepository) : MongoDao<UserAvailability>(repository, PersistedData) {

    object PersistedData : TestDataProvider<UserAvailability> {

        val JDOE_ID = "5fdb5cdad07bba25f645cd87"
        val JDOE_USERNAME = "jdoe"
        val ONSITE_ID = "5fe8a239b76c4a3c325ba4e5"
        val jdoeOnsite = UserAvailability(JDOE_ID, LocalDate.of(2020, 10, 15), WorkArrangementType.ONSITE, ObjectId(ONSITE_ID))
        val jdoeRemote = UserAvailability(JDOE_ID, LocalDate.of(2020, 10, 16), WorkArrangementType.REMOTE, ObjectId())
        val jdoeOff = UserAvailability(JDOE_ID, LocalDate.of(2020, 10, 19), WorkArrangementType.OFF, ObjectId())

        override fun data() = listOf(jdoeOnsite, jdoeRemote, jdoeOff)
    }

    object TransientData : TestDataProvider<UserAvailability> {

        val MMOE_ID = "5fe773204edcff0fbfbf45e4"
        val MMOE_USERNAME = "mmoe"
        val mmoeOnsite = UserAvailability(MMOE_ID, LocalDate.of(2020, 10, 15), WorkArrangementType.ONSITE, ObjectId())
        val mmoeRemote = UserAvailability(MMOE_ID, LocalDate.of(2020, 10, 16), WorkArrangementType.REMOTE, ObjectId())
        val mmoeOff = UserAvailability(MMOE_ID, LocalDate.of(2020, 10, 19), WorkArrangementType.OFF, ObjectId())

        override fun data() = listOf(mmoeOnsite, mmoeRemote, mmoeOff)
    }
}
