package com.dh.baro.core.config

import com.dh.baro.core.auth.CurrentUserArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
internal class WebConfig(
    private val currentUserArgumentResolver: CurrentUserArgumentResolver
) : WebMvcConfigurer {

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(currentUserArgumentResolver)
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("*")
            .allowedHeaders("**")
            .allowedMethods(*ALLOWED_METHOD_NAMES)
            .maxAge(3600)
    }

    companion object {
        private val ALLOWED_METHOD_NAMES = arrayOf(
            "GET", "HEAD", "POST", "PUT", "DELETE", "TRACE",
            "OPTIONS", "PATCH"
        )
    }
}
