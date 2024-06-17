package rabo.demobank.service

import rabo.demobank.dto.AccountDTO

interface AccountService {
    fun createAccount(accountDTO: AccountDTO): AccountDTO
    fun getAccounts(): List<AccountDTO>
    fun getAccountById(accountId: Int): AccountDTO?
    fun withdrawFromAccount(accountId: Int, amount: Double): AccountDTO
    fun transferMoney(fromAccountId: Int, toAccountId: Int, amount: Double): AccountDTO
}