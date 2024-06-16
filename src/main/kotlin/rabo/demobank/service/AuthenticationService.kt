package rabo.demobank.service

import java.util.*
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import rabo.demobank.config.JwtProperties
import rabo.demobank.dto.AuthenticationRequest
import rabo.demobank.dto.AuthenticationResponse

@Service
class AuthenticationService(
    private val authManager: AuthenticationManager,
    private val userService: UserService,
    private val tokenService: TokenService,
    private val jwtProperties: JwtProperties) {

    fun authenticate(authRequest: AuthenticationRequest): AuthenticationResponse {
        authManager.authenticate(UsernamePasswordAuthenticationToken(authRequest.username, authRequest.password))
        val user = userService.loadUserByUsername(authRequest.username)
        val accessToken = tokenService.generate(user,
            Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration))
        return AuthenticationResponse(accessToken)
    }

}