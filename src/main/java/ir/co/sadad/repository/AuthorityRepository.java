package ir.co.sadad.repository;

import ir.co.sadad.domain.Authority;
import javax.inject.Inject;
import javax.persistence.EntityManager;

public class AuthorityRepository extends AbstractRepository<Authority, String> {

    @Inject
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AuthorityRepository() {
        super(Authority.class);
    }
}
