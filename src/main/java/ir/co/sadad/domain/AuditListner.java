package ir.co.sadad.domain;

import ir.co.sadad.security.SecurityHelper;

import javax.inject.Inject;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.Instant;

/**
 * Entity listener class for audit info
 */
public class AuditListner {

    @Inject
    private SecurityHelper securityHelper;

    @PrePersist
    void onCreate(AbstractAuditingEntity entity) {
        entity.setCreatedDate(Instant.now());
        entity.setCreatedBy(securityHelper.getCurrentUserLogin());
    }

    @PreUpdate
    void onUpdate(AbstractAuditingEntity entity) {
        entity.setLastModifiedDate(Instant.now());
        entity.setLastModifiedBy(securityHelper.getCurrentUserLogin());
    }
}
