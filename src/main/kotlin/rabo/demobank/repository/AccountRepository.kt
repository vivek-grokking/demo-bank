package rabo.demobank.repository

import org.springframework.data.jpa.repository.JpaRepository
import rabo.demobank.entity.Account

interface AccountRepository: JpaRepository<Account, Int>