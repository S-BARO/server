package com.dh.baro.look.domain.repository

interface CreditRepository {
    fun reserveCredit(userId: Long): Boolean
    fun refundCredit(userId: Long): Boolean
}
