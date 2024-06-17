package rabo.demobank.payment

import org.springframework.stereotype.Component
import rabo.demobank.entity.CardType
import rabo.demobank.exceptions.InvalidPaymentTypeException

@Component
class PaymentGateway(val paymentMethodMap: Map<String, PaymentMethod>) {

    fun transactionFee(amount: Double, cardType: CardType?): Double {
        val paymentType = when (cardType) {
            CardType.DEBIT -> PaymentType.DEBIT_CARD
            CardType.CREDIT -> PaymentType.CREDIT_CARD
            else -> PaymentType.CASH_WITHDRAWAL
        }
        return paymentMethodMap[paymentType.name]?.calculateProcessingFee(amount)
            ?: throw InvalidPaymentTypeException("Invalid payment type ${paymentType.name}")
    }

}