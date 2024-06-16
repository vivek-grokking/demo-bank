package rabo.demobank.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import rabo.demobank.dto.AccountDTO
import rabo.demobank.dto.TransferRequest
import rabo.demobank.dto.WithdrawRequest
import rabo.demobank.dto.TransactionResponse
import rabo.demobank.service.AccountService

@RestController
@RequestMapping("v1/account")
class AccountController(val accountService: AccountService) {

    @GetMapping("/all")
    fun getAccounts() : List<AccountDTO> = accountService.getAccounts()

    @GetMapping("/{accountId}")
    fun getAccount(@PathVariable accountId: Int): ResponseEntity<AccountDTO> {
        val accountDTO = accountService.getAccountById(accountId)
        return if (accountDTO != null) {
            ResponseEntity(accountDTO, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @PutMapping("/withdraw")
    fun withdraw(@Valid @RequestBody withdrawRequest: WithdrawRequest): ResponseEntity<TransactionResponse> {
        val accountDto = accountService.withdrawFromAccount(withdrawRequest.accountId,
            withdrawRequest.amount)
        return ResponseEntity(
            TransactionResponse("${withdrawRequest.amount} successfully withdrawn !",
            accountDto.balance), HttpStatus.OK
        )
    }

    @PutMapping("/transfer")
    fun transfer(@Valid @RequestBody transferRequest: TransferRequest): ResponseEntity<TransactionResponse> {
        val accountDTO = accountService.transferMoney(transferRequest.fromAccountId,
            transferRequest.toAccountId, transferRequest.amount)
        return ResponseEntity(TransactionResponse("${transferRequest.amount} successfully transferred to ${transferRequest.toAccountId}",
            accountDTO.balance), HttpStatus.OK)
    }

    @PostMapping("/create")
    fun createAccount(@Valid @RequestBody accountDTO: AccountDTO): ResponseEntity<AccountDTO> {
        return ResponseEntity(accountService.createAccount(accountDTO), HttpStatus.CREATED)
    }


}