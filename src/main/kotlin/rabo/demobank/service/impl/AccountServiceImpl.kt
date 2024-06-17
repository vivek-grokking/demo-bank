package rabo.demobank.service.impl

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import rabo.demobank.dto.AccountDTO
import rabo.demobank.entity.Account
import rabo.demobank.exceptions.AccountNotFoundException
import rabo.demobank.exceptions.InsufficientFundsException
import rabo.demobank.exceptions.UserNotFoundException
import rabo.demobank.payment.PaymentGateway
import rabo.demobank.repository.AccountRepository
import rabo.demobank.service.AccountService
import rabo.demobank.service.AuthorizationService

@Service
class AccountServiceImpl(val accountRepository: AccountRepository,
                         val userService: UserDetailsServiceImpl,
                         val paymentGateway: PaymentGateway,
                         val authorizationService: AuthorizationService
): AccountService {

    override fun createAccount(accountDTO: AccountDTO): AccountDTO {

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

    override fun getAccounts(): List<AccountDTO> = accountRepository.findAll()
        .map { AccountDTO(it.id, it.balance, it.cardType, it.bankUser.id) }

    override fun getAccountById(accountId: Int): AccountDTO? {
        authorizationService.checkIsUserAuthorized(accountId)
        val account = accountRepository.findByIdOrNull(accountId)
        return account?.let { AccountDTO(it.id, it.balance, it.cardType, it.bankUser.id) }
    }

    override fun withdrawFromAccount(accountId: Int, amount: Double): AccountDTO {
        authorizationService.checkIsUserAuthorized(accountId)
        val account = accountRepository.findByIdOrNull(accountId)
        return account?.let {
            val processingFee = paymentGateway.transactionFee(amount, null)
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
    override fun transferMoney(fromAccountId: Int, toAccountId: Int, amount: Double): AccountDTO {
        authorizationService.checkIsUserAuthorized(fromAccountId)
        val fromAccount = accountRepository.findByIdOrNull(fromAccountId)
        val toAccount = accountRepository.findByIdOrNull(toAccountId)
        return if (fromAccount != null && toAccount != null) {
            val processingFee = paymentGateway.transactionFee(amount, fromAccount.cardType)
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


}