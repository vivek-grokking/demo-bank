package rabo.demobank.payment

import org.springframework.stereotype.Service

@Service("CREDIT_CARD")
class CreditCardPayment: PaymentMethod {
    override fun calculateProcessingFee(amount: Double) = amount * 0.01
}