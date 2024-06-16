package rabo.demobank.controller

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import rabo.demobank.dto.UserRequest
import rabo.demobank.dto.UserResponse
import rabo.demobank.entity.BankUser
import rabo.demobank.service.UserService

@RestController
@RequestMapping("/v1/user")
class UserController(private val userService: UserService) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody userRequest: UserRequest): UserResponse =
        userService.saveUser(
            user = userRequest.toModel()
        )

    private fun UserRequest.toModel(): BankUser =
        BankUser(id = null, name = this.username, password = this.password, role = this.role)

    private fun BankUser.toModel(): UserResponse =
        UserResponse(id = this.id!!, name = this.name, role = this.role)
}