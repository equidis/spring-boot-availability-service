package com.github.jntakpe.sbavailability.model.entity

import com.github.jntakpe.commons.mongo.Identifiable
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document
import java.io.Serializable
import java.time.LocalDate

@Document
data class UserAvailability(
    val userId: String,
    val day: LocalDate,
    val arrangement: WorkArrangementType,
    override val id: ObjectId = ObjectId(),
) : Identifiable, Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserAvailability

        if (userId != other.userId) return false
        if (day != other.day) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + day.hashCode()
        return result
    }

    override fun toString(): String {
        return "UserAvailability(userId='$userId', day=$day, arrangement=$arrangement)"
    }
}
