package ua.com.fielden.platform.web.centre.api.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ua.com.fielden.platform.dao.IEntityDao;
import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.swing.review.development.EnhancedCentreEntityQueryCriteria;

/**
 * A structure that represents an execution context for an entity centre. Not all of its properties should or need to be populated. Depending on specific needs actions may choose
 * what parts of the context do they require. This allows for optimising the amount of data marshaled between between the client and server.
 *
 * @author TG Team
 *
 * @param <T>
 * @param <M>
 */
public class CentreContext<T extends AbstractEntity<?>, M extends AbstractEntity<?>> {

    /**
     * An action may be applicable to zero, one or more entities that are selected on an entity centre. If an action is applicable only to one entity it is associated with (i.e.
     * button in a row against an entity) then only this one entity should be present in the list of selected entities. The action configuration should drive the client side logic
     * what should be serialised and provided as its context at the server side.
     */
    private List<T> selectedEntities = new ArrayList<>();

    /**
     * Represents selection criteria of an entity centre. Provides access to their values and meta-values.
     */
    private EnhancedCentreEntityQueryCriteria<T, ? extends IEntityDao<T>> selectionCrit;

    /**
     * If an entity centre is a part of some compound master then a corresponding master entity could be provided as a context member.
     */
    private M masterEntity;

    public T getCurrEntity() {
        if (selectedEntities.size() == 1) {
            return selectedEntities.get(0);
        }
        throw new IllegalStateException(String.format("The number of selected entities is %s, which is not appliacable for determining a current entity.", selectedEntities.size()));
    }

    public List<T> getSelectedEntities() {
        return Collections.unmodifiableList(selectedEntities);
    }

    public void setSelectedEntities(final List<T> selectedEntities) {
        this.selectedEntities.clear();
        if (selectedEntities != null) {
            this.selectedEntities.addAll(selectedEntities);
        }
    }

    public EnhancedCentreEntityQueryCriteria<T, ? extends IEntityDao<T>> getSelectionCrit() {
        return selectionCrit;
    }

    public void setSelectionCrit(final EnhancedCentreEntityQueryCriteria<T, ? extends IEntityDao<T>> selectionCrit) {
        this.selectionCrit = selectionCrit;
    }

    public M getMasterEntity() {
        return masterEntity;
    }

    public void setMasterEntity(final M masterEntity) {
        this.masterEntity = masterEntity;
    }

}
