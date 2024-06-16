package rabo.demobank

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import rabo.demobank.dto.AuthenticationRequest
import rabo.demobank.entity.Account
import rabo.demobank.entity.BankUser
import rabo.demobank.entity.CardType
import rabo.demobank.entity.Role
import rabo.demobank.repository.AccountRepository
import rabo.demobank.repository.UserRepository
import rabo.demobank.service.AuthenticationService

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
class DataAuditTest {

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var userRepository: UserRepository

    lateinit var account: Account
    lateinit var user: BankUser

    @Test
    @WithMockUser(username = "user1", roles = ["ADMIN"])
    fun testAuditColumnsArePopulated() {
        user = userRepository.save(BankUser(id = 1, name = "testUser", role = Role.USER, password = "testPassword"))
        account = accountRepository.save(
            Account(1, 1.0, CardType.DEBIT, user)
        )

        assertNotNull(user.createdDate)
        assertEquals("user1", user.createdBy)
        assertNotNull(user.lastModifiedDate)
        assertEquals("user1", user.lastModifiedBy)

        assertNotNull(account.createdDate)
        assertEquals("user1", account.createdBy)
        assertNotNull(account.lastModifiedDate)
        assertEquals("user1", account.lastModifiedBy)
    }
}