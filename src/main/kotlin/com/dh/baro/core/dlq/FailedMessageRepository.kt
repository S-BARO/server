package com.dh.baro.core.dlq

import org.springframework.data.jpa.repository.JpaRepository

interface FailedMessageRepository : JpaRepository<FailedMessage, Long>
