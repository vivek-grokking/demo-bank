package rabo.demobank.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import rabo.demobank.repository.UserRepository
import rabo.demobank.service.UserService

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
class AuthConfig {

    @Bean
    fun encoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun userDetailsService(userRepository: UserRepository, encoder: PasswordEncoder) = UserService(userRepository, encoder)

    @Bean
    fun authenticationProvider(userService: UserService, encoder: PasswordEncoder): AuthenticationProvider =
        DaoAuthenticationProvider()
            .also {
                it.setUserDetailsService(userService)
                it.setPasswordEncoder(encoder)
            }

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration
    ): AuthenticationManager =
         config.authenticationManager
}