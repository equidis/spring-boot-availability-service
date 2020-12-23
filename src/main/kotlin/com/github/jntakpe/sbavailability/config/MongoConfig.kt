package com.github.jntakpe.sbavailability.config

import com.github.jntakpe.sbavailability.model.entity.UserAvailability
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.Sort.Direction
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.index.Index
import org.springframework.data.mongodb.core.indexOps
import javax.annotation.PostConstruct

@Configuration
class MongoConfig(private val mongoTemplate: ReactiveMongoTemplate) {

    @PostConstruct
    fun createIndexes() {
        val uniqueUserIdDayIndex = Index()
            .on(UserAvailability::userId.name, Direction.ASC)
            .on(UserAvailability::day.name, Direction.ASC)
            .unique()
        mongoTemplate.indexOps<UserAvailability>().ensureIndex(uniqueUserIdDayIndex).subscribe()
        mongoTemplate.indexOps<UserAvailability>().ensureIndex(Index(UserAvailability::userId.name, Direction.ASC)).subscribe()
    }
}
