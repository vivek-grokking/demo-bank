package rabo.demobank.service

import rabo.demobank.dto.AuthenticationRequest
import rabo.demobank.dto.AuthenticationResponse

interface AuthenticationService {
    fun authenticate(authRequest: AuthenticationRequest): AuthenticationResponse
}