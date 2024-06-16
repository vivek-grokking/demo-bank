package rabo.demobank

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import rabo.demobank.dto.AccountDTO
import rabo.demobank.dto.WithdrawRequest
import rabo.demobank.dto.TransactionResponse
import rabo.demobank.entity.Role
import rabo.demobank.repository.AccountRepository
import rabo.demobank.repository.UserRepository
import rabo.demobank.service.AccountService
import rabo.demobank.service.UserService

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class AccountControllerIntegrationTest {

    @Qualifier("userService")
    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var accountService: AccountService

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    lateinit var accountRepository: AccountRepository

    @BeforeEach
    internal fun setUp() {
        userRepository.deleteAll()
        accountRepository.deleteAll()

        val user1 = userEntity("user1", Role.USER, "pass1")
        val user2 = userEntity("user2", Role.ADMIN, "pass2")

        userService.saveUser(user1)
        userService.saveUser(user2)
        accountRepository.saveAll(dummyAccountList(user1))
        accountRepository.saveAll(dummyAccountList(user2))
    }

    @Test
    fun testGetAllAccounts() {
        val accountDTOS = webTestClient
            .get()
            .uri("/v1/account/all")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(AccountDTO::class.java)
            .returnResult()
            .responseBody

        assertEquals(4, accountDTOS!!.size)
    }

    @Test
    fun testGetAccountById() {
        val accountId = 1
        val account1 = webTestClient
            .get()
            .uri("/v1/account/{accountId}", accountId)
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody(AccountDTO::class.java)
            .returnResult()
            .responseBody
        assertEquals(1, account1!!.userId)

        val accountId3 = 3
        val account3 = webTestClient
            .get()
            .uri("/v1/account/{accountId}", accountId3)
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody(AccountDTO::class.java)
            .returnResult()
            .responseBody
        assertEquals(2, account3!!.userId)
    }

    @Test
    fun testWithdraw() {
        val accountDto = AccountDTO(1)
        val withdrawRequest = WithdrawRequest(accountDto, 50.0)
        val response = webTestClient
            .put()
            .uri("v1/account/withdraw")
            .bodyValue(withdrawRequest)
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody(TransactionResponse::class.java)
            .returnResult()
            .responseBody
        assertEquals(50.0, response!!.remainingBalance)
    }

    @Test
    fun testWithdrawExcessAmount() {
        val accountDto = AccountDTO(1)
        val withdrawRequest = WithdrawRequest(accountDto, 500.0)
        val response = webTestClient
            .put()
            .uri("v1/account/withdraw")
            .bodyValue(withdrawRequest)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(String::class.java)
            .returnResult()
            .responseBody
        assertEquals("Less than 500.0 is available in account", response)
    }

    @Test
    fun testWithdrawFromInvalidAccount() {
        val accountDto = AccountDTO(11)
        val withdrawRequest = WithdrawRequest(accountDto, 500.0)
        val response = webTestClient
            .put()
            .uri("v1/account/withdraw")
            .bodyValue(withdrawRequest)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(String::class.java)
            .returnResult()
            .responseBody
        assertEquals("Account not found for ID 11", response)
    }

    @Test
    fun testWithdrawInvalidRequest() {
        val accountDto = AccountDTO(11)
        val withdrawRequest = WithdrawRequest(accountDto, -500.0)
        val response = webTestClient
            .put()
            .uri("v1/account/withdraw")
            .bodyValue(withdrawRequest)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(String::class.java)
            .returnResult()
            .responseBody
        assertEquals("Cannot withdraw negative amount", response)
    }
}