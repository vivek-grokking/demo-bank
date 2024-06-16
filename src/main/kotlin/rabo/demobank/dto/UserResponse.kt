package rabo.demobank.dto

import rabo.demobank.entity.Role

data class UserResponse(val id: Int, val name:String, val role: Role)
