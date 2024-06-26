package rabo.demobank.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import rabo.demobank.entity.BankUser

@Repository
interface UserRepository: JpaRepository<BankUser, Int> {

    @Query("SELECT * FROM bank_users WHERE name = ?1", nativeQuery = true)
    fun findUserByName(username: String): BankUser?


}