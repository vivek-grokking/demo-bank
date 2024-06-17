package rabo.demobank.service.impl

import java.util.*
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import rabo.demobank.dto.UserResponse
import rabo.demobank.entity.BankUser
import rabo.demobank.repository.UserRepository
import rabo.demobank.service.UserService

@Service
class UserServiceImpl(val userRepository: UserRepository, val encoder: PasswordEncoder): UserService {

    override fun getUserById(id: Int): Optional<BankUser> = userRepository.findById(id)

    override fun userDetailsService(): UserDetailsService {
        return UserDetailsService { username ->
            userRepository.findUserByName(username)
                ?.mapToUserDetails()?: throw UsernameNotFoundException("Invalid credentials !")
        }
    }

    override fun saveUser(user: BankUser): UserResponse {
        val currentUser = userRepository.findUserByName(user.name)
        if (currentUser != null) {
            return UserResponse(currentUser.id!!, currentUser.name, currentUser.role)
        }
        val updated = user.copy(password = encoder.encode(user.password))
        userRepository.save(updated)
        return UserResponse(updated.id!!, updated.name, updated.role)
    }

    private fun BankUser.mapToUserDetails(): UserDetails {
        return User.builder()
            .username(this.name)
            .password(this.password)
            .roles(this.role.name)
            .build()
    }
}