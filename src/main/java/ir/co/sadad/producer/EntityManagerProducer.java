package ir.co.sadad.producer;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Producer for injectable EntityManager
 *
 */
@RequestScoped
public class EntityManagerProducer {

    @PersistenceContext(unitName = "DEFAULT_PU")
    private EntityManager em;

    @Produces
    public EntityManager getEntityManager() {
        return em;
    }

}
