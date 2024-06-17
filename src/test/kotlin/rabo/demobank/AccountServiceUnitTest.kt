package rabo.demobank

import io.mockk.every
import io.mockk.mockk
import java.util.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull
import rabo.demobank.entity.Account
import rabo.demobank.entity.CardType
import rabo.demobank.entity.Role
import rabo.demobank.exceptions.AccountNotFoundException
import rabo.demobank.exceptions.InsufficientFundsException
import rabo.demobank.exceptions.UnauthorizedTransactionException
import rabo.demobank.payment.PaymentGateway
import rabo.demobank.payment.PaymentType
import rabo.demobank.repository.AccountRepository
import rabo.demobank.service.AccountService
import rabo.demobank.service.AuthorizationService
import rabo.demobank.service.UserService
import rabo.demobank.service.impl.AccountServiceImpl

class AccountServiceUnitTest {

    var accountRepository = mockk<AccountRepository>()
    var userService = mockk<UserService>()
    var paymentGateway= mockk<PaymentGateway>()
    var authorizationService = mockk<AuthorizationService>()
    var accountService: AccountService = AccountServiceImpl(accountRepository, userService, paymentGateway, authorizationService)

    @Test
    fun testGetAccounts() {
        every { accountRepository.findAll() } returns mutableListOf(Account(id = 1, balance = 1.0, cardType = CardType.CREDIT, bankUser = userEntity(name = "user1", role = Role.USER, password = "pass1")),
            Account(id = 2, balance = 11.0, cardType = CardType.DEBIT, bankUser = userEntity(name = "user1", role = Role.USER, password = "pass1")))
        val accounts = accountService.getAccounts()
        assertEquals(2, accounts.size)
        assertEquals(1, accounts[0].accountId)
        assertEquals(1.0, accounts[0].balance)
        assertEquals(CardType.CREDIT, accounts[0].cardType)
        assertEquals(2, accounts[1].accountId)
        assertEquals(11.0, accounts[1].balance)
        assertEquals(CardType.DEBIT, accounts[1].cardType)
    }

    @Test
    fun testGetAccountByIdNotFound() {
        every { accountRepository.findById(any()) } returns Optional.empty()
        every { authorizationService.checkIsUserAuthorized(1) } returns true
        val account = accountService.getAccountById(1)
        assertTrue(account == null)
    }

    @Test
    fun testGetAccountById() {
        every { accountRepository.findById(any()) } returns Optional.of(Account(id = 2, balance = 11.0, cardType = CardType.DEBIT, bankUser = userEntity(name = "user1", role = Role.USER, password = "pass1")))
        every { authorizationService.checkIsUserAuthorized(any()) } returns true
        val account = accountService.getAccountById(1)
        assertEquals(2, account!!.accountId)
        assertEquals(11.0, account.balance)
        assertEquals(CardType.DEBIT, account.cardType)
    }

    @Test
    fun testWithdraw() {
        val user = userEntity(name = "user1", role = Role.USER, password = "pass1")
        every { authorizationService.checkIsUserAuthorized(any()) } returns true
        every { accountRepository.findByIdOrNull(any()) } returns Account(id = 2, balance = 11.0, cardType = CardType.DEBIT, bankUser = user)
        every { paymentGateway.transactionFee(any(), any()) } returns 0.0
        every { accountRepository.save(Account(id=2, bankUser = user, balance=6.0)) } returns Account(id=2, bankUser = user, balance=6.0)
        val account = accountService.withdrawFromAccount(2, 5.0)
        assertEquals(6.0, account.balance)
    }

    @Test
    fun testWithdrawExtraAmount() {
        val user = userEntity(name = "user1", role = Role.USER, password = "pass1")
        every { authorizationService.checkIsUserAuthorized(any()) } returns true
        every { accountRepository.findByIdOrNull(any()) } returns Account(id = 2, balance = 11.0, cardType = CardType.DEBIT, bankUser = user)
        every { paymentGateway.transactionFee(any(), any()) } returns 0.0
        every { accountRepository.save(Account(id=2, bankUser = user, balance=6.0)) } returns Account(id=2, bankUser = user, balance=6.0)
        val exception = assertThrows<InsufficientFundsException> {
            accountService.withdrawFromAccount(2, 50.0)
        }
        assertEquals("Less than 50.0 is available in account", exception.message)
    }

    @Test
    fun testTransfer() {
        val transferAmount = 5.0
        val user1 = userEntity(name = "user1", role = Role.USER, password = "pass1")
        val account1 = Account(id = 1, balance = 10.0, cardType = CardType.DEBIT, bankUser = user1)

        val user2 = userEntity(name = "user2", role = Role.USER, password = "pass1")
        val account2 = Account(id = 2, balance = 10.0, cardType = CardType.DEBIT, bankUser = user2)

        val expectedAccount1 = Account(id = 1, balance = 5.0, cardType = CardType.DEBIT, bankUser = user1)
        val expectedAccount2 = Account(id = 2, balance = 15.0, cardType = CardType.DEBIT, bankUser = user2)

        every { authorizationService.checkIsUserAuthorized(any()) } returns true
        every { accountRepository.findByIdOrNull(1) } returns account1
        every { accountRepository.findByIdOrNull(2) } returns account2
        every { paymentGateway.transactionFee(transferAmount, PaymentType.DEBIT_CARD) } returns 0.0
        every { accountRepository.saveAll(listOf(expectedAccount1, expectedAccount2)) } returns listOf(expectedAccount1, expectedAccount2)
        assertEquals(expectedAccount1.balance, accountService.transferMoney(1, 2, transferAmount).balance)
    }

    @Test
    fun testTransferExtraAmount() {
        val transferAmount = 500.0
        val user1 = userEntity(name = "user1", role = Role.USER, password = "pass1")
        val account1 = Account(id = 1, balance = 10.0, cardType = CardType.DEBIT, bankUser = user1)

        val user2 = userEntity(name = "user2", role = Role.USER, password = "pass1")
        val account2 = Account(id = 2, balance = 10.0, cardType = CardType.DEBIT, bankUser = user2)

        val expectedAccount1 = Account(id = 1, balance = 5.0, cardType = CardType.DEBIT, bankUser = user1)
        val expectedAccount2 = Account(id = 2, balance = 15.0, cardType = CardType.DEBIT, bankUser = user2)

        every { authorizationService.checkIsUserAuthorized(any()) } returns true
        every { accountRepository.findByIdOrNull(1) } returns account1
        every { accountRepository.findByIdOrNull(2) } returns account2
        every { paymentGateway.transactionFee(transferAmount, PaymentType.DEBIT_CARD) } returns 0.0
        every { accountRepository.saveAll(listOf(expectedAccount1, expectedAccount2)) } returns listOf(expectedAccount1, expectedAccount2)
        val exception = assertThrows<InsufficientFundsException> {
            accountService.transferMoney(1, 2, transferAmount)
        }
        assertEquals("Less than 500.0 is available in account", exception.message)
    }

    @Test
    fun testCreditCardTransfer() {
        val transferAmount = 5.0
        val user1 = userEntity(name = "user1", role = Role.USER, password = "pass1")
        val account1 = Account(id = 1, balance = 10.0, cardType = CardType.CREDIT, bankUser = user1)

        val user2 = userEntity(name = "user2", role = Role.USER, password = "pass1")
        val account2 = Account(id = 2, balance = 10.0, cardType = CardType.DEBIT, bankUser = user2)

        val expectedAccount1 = Account(id = 1, balance = 4.95, cardType = CardType.CREDIT, bankUser = user1)
        val expectedAccount2 = Account(id = 2, balance = 15.0, cardType = CardType.DEBIT, bankUser = user2)

        every { authorizationService.checkIsUserAuthorized(any()) } returns true
        every { accountRepository.findByIdOrNull(1) } returns account1
        every { accountRepository.findByIdOrNull(2) } returns account2
        every { paymentGateway.transactionFee(transferAmount, PaymentType.CREDIT_CARD) } returns transferAmount * 0.01
        every { accountRepository.saveAll(listOf(expectedAccount1, expectedAccount2)) } returns listOf(expectedAccount1, expectedAccount2)
        assertEquals(expectedAccount1.balance, accountService.transferMoney(1, 2, transferAmount).balance)
    }

    @Test
    fun testTransferInvalidAccount() {
        val transferAmount = 500.0
        val user1 = userEntity(name = "user1", role = Role.USER, password = "pass1")
        val account1 = Account(id = 1, balance = 10.0, cardType = CardType.DEBIT, bankUser = user1)

        val user2 = userEntity(name = "user2", role = Role.USER, password = "pass1")

        every { authorizationService.checkIsUserAuthorized(any()) } returns true
        every { accountRepository.findByIdOrNull(1) } returns account1
        every { accountRepository.findByIdOrNull(2) } returns null
        val exception = assertThrows<AccountNotFoundException> {
            accountService.transferMoney(1, 2, transferAmount)
        }
        assertTrue(exception.message!!.contains("Invalid accounts for transfer"))
    }

    @Test
    fun testTransferUnauthorizedAccount() {
        every { authorizationService.checkIsUserAuthorized(any()) } throws UnauthorizedTransactionException("Logged-in user is unauthorized to perform this operation")
        val exception = assertThrows<UnauthorizedTransactionException> {
            accountService.transferMoney(1, 2, 500.0)
        }
        assertEquals("Logged-in user is unauthorized to perform this operation", exception.message!!)
    }
}