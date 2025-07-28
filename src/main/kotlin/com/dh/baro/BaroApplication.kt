package com.dh.baro

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.retry.annotation.EnableRetry
import org.springframework.scheduling.annotation.EnableScheduling

@EnableRetry
@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication
class BaroApplication

fun main(args: Array<String>) {
	runApplication<BaroApplication>(*args)
}
