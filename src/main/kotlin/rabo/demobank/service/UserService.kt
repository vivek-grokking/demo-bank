package rabo.demobank.service

import java.util.*
import org.springframework.security.core.userdetails.UserDetailsService
import rabo.demobank.dto.UserResponse
import rabo.demobank.entity.BankUser

interface UserService {
    fun userDetailsService(): UserDetailsService
    fun getUserById(id: Int): Optional<BankUser>
    fun saveUser(user: BankUser): UserResponse
}