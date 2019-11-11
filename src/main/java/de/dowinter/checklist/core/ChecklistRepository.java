package de.dowinter.checklist.core;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@RequestScoped
public class ChecklistRepository {

    @Inject
    EntityManager em;

    public Optional<Checklist> withId(String id) {
        return Optional.ofNullable(em.find(Checklist.class, id));
    }

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public boolean save(Checklist checklist) {
        try {
            em.persist(checklist);
            em.flush();

            return true;
        } catch (PersistenceException exception) {
            return false;
        }
    }

    public List<Checklist> all() {
        return em.createQuery("SELECT c FROM Checklist c ORDER BY c.id", Checklist.class).getResultList();
    }
}
