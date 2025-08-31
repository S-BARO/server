package com.dh.baro.core.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.async")
data class AsyncProperties(
    val executor: ExecutorProperties = ExecutorProperties()
) {
    
    data class ExecutorProperties(
        val corePoolSize: Int = 5,
        val maxPoolSize: Int = 10,
        val queueCapacity: Int = 25,
        val keepAliveSeconds: Int = 60,
        val threadNamePrefix: String = "event-async-"
    )
}
