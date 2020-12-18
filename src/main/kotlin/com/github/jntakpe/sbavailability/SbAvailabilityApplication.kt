package com.github.jntakpe.sbavailability

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@EnableCaching
@SpringBootApplication(scanBasePackages = ["com.github.jntakpe"])
class SbAvailabilityApplication

fun main(args: Array<String>) {
    runApplication<SbAvailabilityApplication>(*args)
}
