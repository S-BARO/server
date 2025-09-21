package com.dh.baro.core.dlt

import org.springframework.data.jpa.repository.JpaRepository

interface FailedMessageRepository : JpaRepository<FailedMessage, Long>
