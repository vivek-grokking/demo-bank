package rabo.demobank.service.impl

import java.util.*
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import rabo.demobank.config.JwtProperties
import rabo.demobank.dto.AuthenticationRequest
import rabo.demobank.dto.AuthenticationResponse
import rabo.demobank.service.AuthenticationService
import rabo.demobank.service.TokenService
import rabo.demobank.service.UserService


@Service
class AuthenticationServiceImpl(
     val authManager: AuthenticationManager,
     val userService: UserService,
     val tokenService: TokenService,
     val jwtProperties: JwtProperties): AuthenticationService {

    override fun authenticate(authRequest: AuthenticationRequest): AuthenticationResponse {
        authManager.authenticate(UsernamePasswordAuthenticationToken(authRequest.username, authRequest.password))
        val user = userService.userDetailsService().loadUserByUsername(authRequest.username)
        val accessToken = tokenService.generate(user,
            Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration))
        return AuthenticationResponse(accessToken)
    }

}