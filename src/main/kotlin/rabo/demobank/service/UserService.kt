package rabo.demobank.service

import java.util.Optional
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import rabo.demobank.repository.UserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import rabo.demobank.dto.UserResponse
import rabo.demobank.entity.BankUser

typealias ApplicationUser = rabo.demobank.entity.BankUser

@Service
class UserService(val userRepository: UserRepository, val encoder: PasswordEncoder): UserDetailsService {

    fun saveUser(user: BankUser): UserResponse {
        val currentUser = userRepository.findUserByName(user.name)
        if (currentUser != null) {
            return UserResponse(currentUser.id!!, currentUser.name, currentUser.role)
        }
        val updated = user.copy(password = encoder.encode(user.password))
        userRepository.save(updated)
        return UserResponse(updated.id!!, updated.name, updated.role)
    }

    fun getUserById(id: Int): Optional<ApplicationUser> = userRepository.findById(id)

    override fun loadUserByUsername(username: String): UserDetails {
        return userRepository.findUserByName(username)
            ?.mapToUserDetails()?: throw UsernameNotFoundException("Invalid credentials !")
    }

    private fun BankUser.mapToUserDetails(): UserDetails {
        return User.builder()
            .username(this.name)
            .password(this.password)
            .roles(this.role.name)
            .build()
    }
}