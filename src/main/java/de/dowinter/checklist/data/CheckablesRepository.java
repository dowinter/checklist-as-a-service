package de.dowinter.checklist.data;

import com.google.common.collect.Lists;
import de.dowinter.checklist.core.Checkable;
import de.dowinter.checklist.core.Checklist;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@RequestScoped
public class CheckablesRepository {

    @Inject
    EntityManager em;

    @Inject
    ChecklistRepository checklists;

    @Transactional
    public boolean addCheckable(String checklistId, Checkable c) {
        Optional<Checklist> checklist = checklists.withId(checklistId);

        if (checklist.isPresent()) {
            em.persist(c);
            checklist.get().getCheckables().add(c);
            return true;
        } else {
            return false;
        }
    }

    public Optional<Checkable> getCheckableFromChecklist(String checklistId, Long checkableId) {
        Optional<Checklist> checklist = checklists.withId(checklistId);
        return checklist.flatMap(
            checklist1 -> checklist1.getCheckables().stream()
                    .filter(checkable -> checkable.getId().equals(checkableId))
                    .findFirst()
        );
    }

    public List<Checkable> getAllCheckablesFromChecklist(String checklistId) {
        Optional<Checklist> checklist = checklists.withId(checklistId);
        return checklist.map(Checklist::getCheckables).orElse(Lists.newArrayList());
    }
}
