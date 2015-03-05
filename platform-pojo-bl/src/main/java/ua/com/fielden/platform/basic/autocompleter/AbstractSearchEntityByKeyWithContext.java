/**
 *
 */
package ua.com.fielden.platform.basic.autocompleter;

import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.fetchKeyAndDescOnly;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.from;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.orderBy;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.select;

import java.util.List;

import ua.com.fielden.platform.basic.IValueMatcherWithContext;
import ua.com.fielden.platform.basic.IValueMatcherWithFetch;
import ua.com.fielden.platform.dao.IEntityDao;
import ua.com.fielden.platform.entity.AbstractEntity;
import static ua.com.fielden.platform.entity.AbstractEntity.KEY;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.ICompoundCondition0;
import ua.com.fielden.platform.entity.query.fluent.fetch;
import ua.com.fielden.platform.entity.query.model.EntityResultQueryModel;
import ua.com.fielden.platform.entity.query.model.OrderingModel;

/**
 * Key based value matcher, which supports context assignment.
 *
 * @author TG Team
 */
public abstract class AbstractSearchEntityByKeyWithContext<CONTEXT extends AbstractEntity<?>, T extends AbstractEntity<?>>
implements IValueMatcherWithContext<CONTEXT, T>, IValueMatcherWithFetch<T> {

    private final IEntityDao<T> dao;
    private final fetch<T> defaultFetchModel;
    private fetch<T> fetchModel;

    private int pageSize = 10;


    public AbstractSearchEntityByKeyWithContext(final IEntityDao<T> dao) {
        this.dao = dao;
        this.defaultFetchModel = fetchKeyAndDescOnly(dao.getEntityType());
    }

    /**
     * This method needs to be implemented to enhance the resulting query based on the provided context.
     *
     * @param incompleteEql
     * @return
     */
    protected abstract EntityResultQueryModel<T> completeEqlBasedOnContext(final CONTEXT context, final ICompoundCondition0<T> incompleteEql);


    @Override
    public List<T> findMatches(final String searchString) {
        final ICompoundCondition0<T> incompleteEql =select(dao.getEntityType()).where().prop(KEY).iLike().val(searchString);
        final EntityResultQueryModel<T> queryModel = completeEqlBasedOnContext(getContext(), incompleteEql);
        final OrderingModel ordering = orderBy().yield(KEY).asc().model();
        return dao.getFirstEntities(from(queryModel).with(ordering).with(defaultFetchModel).model(), pageSize);
    }

    @Override
    public List<T> findMatchesWithModel(final String value) {

        return null;
    }


    @Override
    public fetch<T> getFetchModel() {
        return fetchModel;
    }

    @Override
    public void setFetchModel(final fetch<T> fetchModel) {
        this.fetchModel = fetchModel;
    }


}