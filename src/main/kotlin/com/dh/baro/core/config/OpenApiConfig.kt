package com.dh.baro.core.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

@Configuration
class OpenApiConfig(private val environment: Environment) {

    @Bean
    fun customOpenAPI(): OpenAPI {
        val activeProfile = environment.getProperty("spring.profiles.active", "local")

        val serverUrl = System.getenv("PROD_SERVER_URL").takeUnless { it.isNullOrBlank() }
            ?: "http://localhost:8080"

        return OpenAPI()
            .components(Components())
            .servers(
                listOf(
                    Server()
                        .url(serverUrl)
                        .description("$activeProfile server")
                )
            )
            .info(
                Info()
                    .title("Baro(바로) API Documentation")
                    .description("Team DH's Baro service API specification.")
                    .version("1.0")
            )
    }
}
