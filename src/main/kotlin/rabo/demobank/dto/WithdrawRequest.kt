package rabo.demobank.dto

import jakarta.validation.constraints.Min

data class WithdrawRequest(val accountId: Int,

                           @get:Min(0, message = "Cannot withdraw negative amount")
                           val amount: Double)