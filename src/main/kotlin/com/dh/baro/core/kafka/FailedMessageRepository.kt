package com.dh.baro.core.kafka

import org.springframework.data.jpa.repository.JpaRepository

interface FailedMessageRepository : JpaRepository<FailedMessage, Long>
