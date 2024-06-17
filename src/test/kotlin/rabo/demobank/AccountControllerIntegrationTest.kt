package rabo.demobank

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import rabo.demobank.config.AuditConfig
import rabo.demobank.config.AuthConfig
import rabo.demobank.config.SecurityConfig
import rabo.demobank.dto.AccountDTO
import rabo.demobank.dto.WithdrawRequest
import rabo.demobank.dto.TransactionResponse
import rabo.demobank.entity.Role
import rabo.demobank.repository.AccountRepository
import rabo.demobank.repository.UserRepository
import rabo.demobank.service.AccountService

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [SecurityConfig::class, AuthConfig::class])
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@EnableJpaRepositories("rabo.demobank.repository")
@ExtendWith(SpringExtension::class)
class AccountControllerIntegrationTest {

    @Qualifier("userDetailsServiceImpl")
    @Autowired
    private lateinit var userService: UserDetailsService

    @Autowired
    private lateinit var accountService: AccountService

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var auditorAwareImpl: AuditorAwareImpl

    @BeforeEach
    internal fun setUp() {
        accountRepository.deleteAll()
        userRepository.deleteAll()

        var user1 = userEntity("user1", Role.USER, "pass1")
        var user2 = userEntity("user2", Role.ADMIN, "pass2")

        user1 = userRepository.save(user1)
        user2 = userRepository.save(user2)
        accountRepository.saveAll(dummyAccountList(user1))
        accountRepository.saveAll(dummyAccountList(user2))
    }

    @Test
    @WithMockUser(value = "user2", username = "user2", password = "pass2", roles = ["ADMIN"])
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
    @WithMockUser(username = "user1", password = "pass1", roles = ["USER"])
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

        val accountId2 = 2
        val account2 = webTestClient
            .get()
            .uri("/v1/account/{accountId}", accountId2)
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody(AccountDTO::class.java)
            .returnResult()
            .responseBody
        assertEquals(1, account2!!.userId)
    }

    @Test
    @WithMockUser(value = "user1", username = "user1", password = "pass1", roles = ["USER"])
    fun testWithdraw() {
        val withdrawRequest = WithdrawRequest(1, 50.0)
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
        val withdrawRequest = WithdrawRequest(1, 500.0)
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
        val withdrawRequest = WithdrawRequest(11, 500.0)
        val response1 = webTestClient
            .put()
            .uri("v1/account/withdraw")
            .bodyValue(withdrawRequest)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(String::class.java)
            .returnResult()
            .responseBody
        val response = webTestClient
            .put()
            .uri("v1/account/withdraw")
            .bodyValue(withdrawRequest)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(String::class.java)
            .returnResult()
            .responseBody
        println(response)
        assertEquals("Account not found for ID 11", response)
    }

    @Test
    fun testWithdrawInvalidRequest() {
        val withdrawRequest = WithdrawRequest(1, -500.0)
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