package rabo.demobank

import rabo.demobank.entity.Account
import rabo.demobank.entity.CardType
import rabo.demobank.entity.Role
import rabo.demobank.entity.BankUser

fun userEntity(name: String, role: Role, password: String) = BankUser(null, name, role, password)

fun dummyAccountList(bankUser: BankUser) = listOf(
    Account(null, 100.0, CardType.DEBIT, bankUser),
    Account(null, 200.0, CardType.CREDIT, bankUser),
)
