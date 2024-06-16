package rabo.demobank.payment

import org.springframework.stereotype.Component
import rabo.demobank.exceptions.InvalidPaymentTypeException

@Component
class PaymentGateway(val paymentMethodMap: Map<String, PaymentMethod>) {

    fun transactionFee(amount: Double, paymentType: PaymentType): Double {
        return paymentMethodMap[paymentType.name]?.calculateProcessingFee(amount)
            ?: throw InvalidPaymentTypeException("Invalid payment type ${paymentType.name}")
    }

}