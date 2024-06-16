package rabo.demobank.entity

import jakarta.persistence.*

@Entity
@Table(name = "bank_users")
data class BankUser(@Id
                    @GeneratedValue(strategy = GenerationType.AUTO)
                    val id: Int?,

                    @Column(unique=true) val name: String,
                    val role: Role,
                    val password: String,

                    @OneToMany(
                        fetch = FetchType.LAZY,
                        cascade = [CascadeType.ALL],
                        orphanRemoval = true)
                    val accounts: List<Account> = mutableListOf()
): Auditable()
