package rabo.demobank.service

interface AuthorizationService {
    fun checkIsUserAuthorized(accountId: Int): Boolean
}