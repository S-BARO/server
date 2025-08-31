package com.dh.baro.core.config

import org.slf4j.LoggerFactory
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@EnableAsync
@Configuration
@EnableConfigurationProperties(AsyncProperties::class)
class AsyncConfig(
    private val asyncProperties: AsyncProperties
) : AsyncConfigurer {

    private val log = LoggerFactory.getLogger(javaClass)

    @Bean(EVENT_ASYNC_TASK_EXECUTOR)
    fun eventAsyncExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        val props = asyncProperties.executor
        
        executor.corePoolSize = props.corePoolSize
        executor.maxPoolSize = props.maxPoolSize
        executor.queueCapacity = props.queueCapacity
        executor.keepAliveSeconds = props.keepAliveSeconds
        executor.threadNamePrefix = props.threadNamePrefix
        executor.setRejectedExecutionHandler { r, _ ->
            log.warn("Async executor queue is full, executing task in current thread")
            r.run()
        }
        executor.initialize()
        return executor
    }

    override fun getAsyncExecutor(): Executor {
        return eventAsyncExecutor()
    }

    override fun getAsyncUncaughtExceptionHandler(): AsyncUncaughtExceptionHandler {
        return AsyncUncaughtExceptionHandler { ex, method, params ->
            log.error("Async execution failed in {}.{} with params: {}",
                    method.declaringClass.simpleName,
                    method.name,
                    params.contentToString(), ex)
        }
    }

    companion object {
        const val EVENT_ASYNC_TASK_EXECUTOR = "eventAsyncExecutor"
    }
}
