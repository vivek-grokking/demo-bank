package rabo.demobank.service.impl

import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import rabo.demobank.exceptions.UnauthorizedTransactionException
import rabo.demobank.repository.AccountRepository
import rabo.demobank.service.AuthorizationService

@Service
class AuthorizationServiceImpl(val userService: UserDetailsService, val accountRepository: AccountRepository,
    ): AuthorizationService {
    override fun checkIsUserAuthorized(accountId: Int): Boolean {
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