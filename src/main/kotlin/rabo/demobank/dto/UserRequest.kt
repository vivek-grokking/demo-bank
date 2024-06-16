package rabo.demobank.dto

import rabo.demobank.entity.Role

data class UserRequest(val username: String, val password: String, val role: Role)
