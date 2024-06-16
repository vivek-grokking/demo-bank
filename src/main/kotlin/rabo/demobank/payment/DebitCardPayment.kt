package rabo.demobank.payment

import org.springframework.stereotype.Service

@Service("DebitCardPayment")
class DebitCardPayment: PaymentMethod {
    override fun calculateProcessingFee(amount: Double) = 0.0
}