package rabo.demobank.payment

import org.springframework.stereotype.Service

@Service("Withdrawal")
class Withdrawal: PaymentMethod {
    override fun calculateProcessingFee(amount: Double) = 0.0
}