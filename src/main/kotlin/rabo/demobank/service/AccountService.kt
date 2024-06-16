package rabo.demobank.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import rabo.demobank.dto.AccountDTO
import rabo.demobank.entity.Account
import rabo.demobank.entity.CardType
import rabo.demobank.exceptions.AccountNotFoundException
import rabo.demobank.exceptions.InsufficientFundsException
import rabo.demobank.exceptions.UnauthorizedTransactionException
import rabo.demobank.exceptions.UserNotFoundException
import rabo.demobank.payment.PaymentGateway
import rabo.demobank.payment.PaymentType
import rabo.demobank.repository.AccountRepository

@Service
class AccountService(val accountRepository: AccountRepository,
                     val userService: UserService,
                     val paymentGateway: PaymentGateway) {

    fun createAccount(accountDTO: AccountDTO): AccountDTO {
        val user = userService.getUserById(accountDTO.userId!!)
        if (!user.isPresent) {
            throw UserNotFoundException("User with id: ${accountDTO.userId} not found")
        }

        val account = accountDTO.let {
            Account(null, it.balance, it.cardType, user.get())
        }
        accountRepository.save(account)
        return account.let {
            AccountDTO(it.id, it.balance, it.cardType, it.bankUser.id)
        }
    }

    fun getAccounts(): List<AccountDTO> = accountRepository.findAll()
        .map { AccountDTO(it.id, it.balance, it.cardType, it.bankUser.id) }

    fun getAccountById(id: Int): AccountDTO? {
        val account = accountRepository.findByIdOrNull(id)
        return account?.let { AccountDTO(it.id, it.balance, it.cardType, it.bankUser.id) }
    }

    fun withdrawFromAccount(accountId: Int, amount: Double): AccountDTO {
        checkIsUserAuthorized(accountId)
        val account = accountRepository.findByIdOrNull(accountId)
        return account?.let {
            val processingFee = paymentGateway.transactionFee(amount, PaymentType.CASH_WITHDRAWAL)
            if (it.balance < (amount + processingFee)) {
                throw InsufficientFundsException("Less than $amount is available in account");
            } else {
                it.balance -= (amount + processingFee)
                accountRepository.save(it)
                AccountDTO(it.id, it.balance, it.cardType, it.bankUser.id)
            }
        } ?: throw AccountNotFoundException("Account not found for ID ${accountId}")
    }

    @Transactional
    fun transferMoney(fromAccountId: Int, toAccountId: Int, amount: Double): AccountDTO {
        checkIsUserAuthorized(fromAccountId)
        val fromAccount = accountRepository.findByIdOrNull(fromAccountId)
        val toAccount = accountRepository.findByIdOrNull(toAccountId)
        return if (fromAccount != null && toAccount != null) {
            val processingFee = if (fromAccount.cardType == CardType.DEBIT) {
                paymentGateway.transactionFee(amount, PaymentType.DEBIT_CARD)
            } else {
                paymentGateway.transactionFee(amount, PaymentType.CREDIT_CARD)
            }
            if (fromAccount.balance < (amount + processingFee)) {
                throw InsufficientFundsException("Less than $amount is available in account");
            } else {
                fromAccount.balance -= (amount + processingFee)
                toAccount.balance += amount
                accountRepository.saveAll(listOf(fromAccount, toAccount))
                AccountDTO(fromAccount.id, fromAccount.balance, fromAccount.cardType, fromAccount.bankUser.id)
            }
        } else {
            throw AccountNotFoundException("Invalid accounts for transfer, $fromAccount and $toAccount")
        }
    }

    fun checkIsUserAuthorized(accountId: Int): Boolean {
        val auth: Authentication = SecurityContextHolder.getContext().authentication
        val user = userService.loadUserByUsername(auth.name)
        val account = accountRepository.findByIdOrNull(accountId)
        return if (account?.bankUser?.name.equals(user.username)) {
             true
        } else {
            throw UnauthorizedTransactionException("Logged-in user is unauthorized to perform this operation")
        }
    }
}