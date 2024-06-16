package rabo.demobank.payment

import org.springframework.stereotype.Service

@Service("CASH_WITHDRAWAL")
class Withdrawal: PaymentMethod {
    override fun calculateProcessingFee(amount: Double) = 0.0
}