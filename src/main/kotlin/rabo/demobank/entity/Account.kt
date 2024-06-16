package rabo.demobank.entity

import jakarta.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity
@Table(name = "account")
@EntityListeners(AuditingEntityListener::class)
data class Account(@Id
                   @GeneratedValue(strategy = GenerationType.AUTO)
                   val id: Int?,

                   var balance: Double,
                   val cardType: CardType = CardType.DEBIT,

                   @ManyToOne
                   @JoinColumn(name="user_id", nullable = false)
                   val bankUser: BankUser
): Auditable() {
    override fun toString(): String {
        return "Account(id=$id, user=${bankUser.id}, balance=$balance)"
    }
}
