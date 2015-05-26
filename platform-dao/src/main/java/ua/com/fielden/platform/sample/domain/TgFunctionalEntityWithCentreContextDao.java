package ua.com.fielden.platform.sample.domain;

import ua.com.fielden.platform.dao.CommonEntityDao;
import ua.com.fielden.platform.dao.annotations.SessionRequired;
import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.fetch.IFetchProvider;
import ua.com.fielden.platform.entity.query.IFilter;
import ua.com.fielden.platform.sample.domain.mixin.TgFunctionalEntityWithCentreContextMixin;
import ua.com.fielden.platform.swing.review.annotations.EntityType;

import com.google.inject.Inject;

/**
 * DAO implementation for companion object {@link ITgFunctionalEntityWithCentreContext}.
 *
 * @author Developers
 *
 */
@EntityType(TgFunctionalEntityWithCentreContext.class)
public class TgFunctionalEntityWithCentreContextDao extends CommonEntityDao<TgFunctionalEntityWithCentreContext> implements ITgFunctionalEntityWithCentreContext {

    private final TgFunctionalEntityWithCentreContextMixin mixin;
    private final ITgPersistentEntityWithProperties dao;

    @Inject
    public TgFunctionalEntityWithCentreContextDao(final IFilter filter, final ITgPersistentEntityWithProperties dao) {
        super(filter);

        this.dao = dao;
        mixin = new TgFunctionalEntityWithCentreContextMixin(this);
    }

    @Override
    public IFetchProvider<TgFunctionalEntityWithCentreContext> createFetchProvider() {
        return super.createFetchProvider()
                .with("key") // this property is "required" (necessary during saving) -- should be declared as fetching property
                .with("desc")
                .with("valueToInsert", "withBrackets");
    }

    @Override
    @SessionRequired
    public TgFunctionalEntityWithCentreContext save(final TgFunctionalEntityWithCentreContext entity) {
        for (final AbstractEntity<?> selectedEntity : entity.getContext().getSelectedEntities()) {
            final TgPersistentEntityWithProperties selected = dao.findById(selectedEntity.getId()); // (TgPersistentEntityWithProperties) selectedEntity;
            final Object user = entity.getContext().getSelectionCrit() != null ? entity.getContext().getSelectionCrit().get("tgPersistentEntityWithProperties_userParam.key") : "UNKNOWN_USER";
            selected.set("desc", user + ": " + entity.getValueToInsert() + ": " + selected.get("desc"));
            if (entity.getWithBrackets()) {
                selected.set("desc", "[" + selected.get("desc") + "]");
            }
            dao.save(selected);
        }
        return super.save(entity);
    }
}