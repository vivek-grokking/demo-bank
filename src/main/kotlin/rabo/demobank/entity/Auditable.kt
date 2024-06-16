package rabo.demobank.entity

import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
open class Auditable(@CreatedBy var createdBy: String? = null,
                     @CreatedDate var createdDate: LocalDateTime = LocalDateTime.now(),
                     @LastModifiedBy var lastModifiedBy: String? = null,
                     @LastModifiedDate var lastModifiedDate: LocalDateTime =  LocalDateTime.now())