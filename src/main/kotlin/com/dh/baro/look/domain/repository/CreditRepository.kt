package com.dh.baro.look.domain.repository

interface CreditRepository {
    fun checkCreditAvailability(userId: Long): Boolean
    fun deductCredit(userId: Long): Boolean
}
