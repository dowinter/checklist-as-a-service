package de.dowinter.checklist.core;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@RequestScoped
public class CheckablesRepository {

    @Inject
    EntityManager em;

    public Optional<Checkable> withId(long id) {
        return Optional.ofNullable(em.find(Checkable.class, id));
    }

    public Long save(Checkable checkable) {
        em.persist(checkable);
        return checkable.getId();
    }

    public List<Checkable> all() {
        return em.createQuery("SELECT c FROM Checkable c ORDER BY c.id", Checkable.class).getResultList();
    }
}
