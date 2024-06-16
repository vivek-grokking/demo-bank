package rabo.demobank.dto

import rabo.demobank.entity.CardType

data class TransferRequest(val fromAccountId: Int,
                           val toAccountId: Int,
                           val amount: Double,
                           val cardType: CardType)