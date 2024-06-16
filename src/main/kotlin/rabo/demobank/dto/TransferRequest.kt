package rabo.demobank.dto

data class TransferRequest(val fromAccountId: Int,
                           val toAccountId: Int,
                           val amount: Double)