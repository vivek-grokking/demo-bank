package rabo.demobank.service.impl

import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.stereotype.Service
import rabo.demobank.exceptions.UnauthorizedTransactionException
import rabo.demobank.repository.AccountRepository
import rabo.demobank.service.AuthorizationService
import rabo.demobank.service.UserService

@Service
class AuthorizationServiceImpl(val userService: UserService, val accountRepository: AccountRepository,
                               val securityContext: SecurityContext
): AuthorizationService {
    override fun checkIsUserAuthorized(accountId: Int): Boolean {
        val auth: Authentication = securityContext.authentication
        val user = userService.userDetailsService().loadUserByUsername(auth.name)
        val account = accountRepository.findByIdOrNull(accountId)
        return if (account?.bankUser?.name.equals(user.username)) {
            true
        } else {
            throw UnauthorizedTransactionException("Logged-in user is unauthorized to perform this operation")
        }
    }
}