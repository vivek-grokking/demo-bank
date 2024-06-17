package rabo.demobank.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import rabo.demobank.dto.AuthenticationRequest
import rabo.demobank.dto.AuthenticationResponse
import rabo.demobank.service.impl.AuthenticationServiceImpl

@RestController
@RequestMapping("/v1/auth")
class AuthController(private val authenticationServiceImpl: AuthenticationServiceImpl) {

    @PostMapping
    fun authenticate(@RequestBody authRequest: AuthenticationRequest): AuthenticationResponse =
        authenticationServiceImpl.authenticate(authRequest)
}