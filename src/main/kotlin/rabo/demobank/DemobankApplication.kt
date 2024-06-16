package rabo.demobank

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableTransactionManagement
class DemobankApplication

fun main(args: Array<String>) {

    runApplication<DemobankApplication>(*args)

}

