package rabo.demobank

import java.util.*
import org.springframework.data.domain.AuditorAware
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class AuditorAwareImpl : AuditorAware<String> {
    override fun getCurrentAuditor(): Optional<String> {
        //val auth: Authentication = SecurityContextHolder.getContext().authentication
        return Optional.of("dummy_auditor");
    }
}