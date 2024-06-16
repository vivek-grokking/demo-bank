package rabo.demobank.payment

import org.springframework.stereotype.Service

@Service("DEBIT_CARD")
class DebitCardPayment: PaymentMethod {
    override fun calculateProcessingFee(amount: Double) = 0.0
}