package rabo.demobank.dto

import rabo.demobank.entity.CardType

data class AccountDTO(
    val accountId: Int?,
    val balance: Double = 0.0,
    val cardType: CardType = CardType.DEBIT,
    val userId: Int? = null
)
