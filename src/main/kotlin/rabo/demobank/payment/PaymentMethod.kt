package rabo.demobank.payment

interface PaymentMethod {
    fun calculateProcessingFee(amount: Double): Double
}