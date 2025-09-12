package com.dh.baro.core.config

import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.EnableMongoAuditing
import java.util.concurrent.TimeUnit

@Configuration
@EnableMongoAuditing
class MongoConfig {

    @Bean
    fun mongoClient(): MongoClient {
        val settings = MongoClientSettings.builder()
            .applyToConnectionPoolSettings { builder ->
                builder.maxSize(30)
                    .minSize(10)
                    .maxWaitTime(30, TimeUnit.SECONDS)
            }
            .applyToSocketSettings { builder ->
                builder.connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
            }
            .applyToClusterSettings { builder ->
                builder.serverSelectionTimeout(15, TimeUnit.SECONDS)
            }
            .build()

        return MongoClients.create(settings)
    }
}
