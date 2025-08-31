package com.dh.baro.core.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.redisson.config.EqualJitterDelay
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class RedissonConfig {

    @Value("\${spring.data.redis.host}")
    private lateinit var redisHost: String

    @Value("\${spring.data.redis.port}")
    private var redisPort: Int = 6379

    @Value("\${spring.data.redis.password:}")
    private var redisPassword: String = ""

    @Bean
    fun redissonClient(): RedissonClient {
        val config = Config()
        val address = "redis://$redisHost:$redisPort"
        
        config.useSingleServer()
            .setAddress(address)
            .also { server ->
                if (redisPassword.isNotBlank()) {
                    server.password = redisPassword
                }
            }
            .setConnectionMinimumIdleSize(8)
            .setConnectionPoolSize(32)
            .setIdleConnectionTimeout(10000)
            .setConnectTimeout(10000)
            .setTimeout(3000)
            .setRetryAttempts(3)
            .setRetryDelay(EqualJitterDelay(Duration.ofMillis(1000), Duration.ofMillis(2000)))

        return Redisson.create(config)
    }
}
