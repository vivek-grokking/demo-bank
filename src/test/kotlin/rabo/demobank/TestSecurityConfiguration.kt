package rabo.demobank

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.WebSecurityConfigurer
import org.springframework.security.config.annotation.web.builders.WebSecurity


@TestConfiguration
@Order(1)
class TestSecurityConfiguration : WebSecurityConfigurer<WebSecurity?> {

    @Throws(Exception::class)
    override fun init(builder: WebSecurity?) {
        builder?.ignoring()?.anyRequest()
    }

    @Throws(Exception::class)
    override fun configure(builder: WebSecurity?) {
    }
}