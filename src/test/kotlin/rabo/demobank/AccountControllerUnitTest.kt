package rabo.demobank

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.reactive.server.WebTestClient
import rabo.demobank.controller.AccountController
import rabo.demobank.dto.AccountDTO
import rabo.demobank.service.AccountService

@WebMvcTest(controllers = [AccountController::class])
@AutoConfigureWebTestClient
class AccountControllerUnitTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockkBean
    lateinit var accountService: AccountService

//    @Test
//    fun testGetAllAccounts() {
//        val dummyUser = userEntity("dummy_user")
//        every { accountService.getAccounts() } returns dummyAccountList(dummyUser)
//            .map { AccountDTO(it.id, it.balance, it.user.id) }
//
//        val accountDTOS = webTestClient
//            .get()
//            .uri("/v1/accounts")
//            .exchange()
//            .expectStatus().isOk
//            .expectBodyList(AccountDTO::class.java)
//            .returnResult()
//            .responseBody
//
//        assertEquals(2, accountDTOS!!.size)
//    }

    @Test
    fun testGetAccountById() {
        val accountId = 1L
//        every { accountService.getAccountById(any()) } returns AccountDTO(accountId, 1.0, 1)
//        val accountDTO = webTestClient
//            .get()
//            .uri("/v1/accounts/{account_id}", accountId)
//            .exchange()
//            .expectStatus().isOk
//            .expectBody(AccountDTO::class.java)
//            .returnResult()
//            .responseBody
//        assertEquals(accountId, accountDTO!!.accountId)
    }


}