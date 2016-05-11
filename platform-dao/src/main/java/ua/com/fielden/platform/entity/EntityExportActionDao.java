package ua.com.fielden.platform.entity;

import java.io.IOException;

import ua.com.fielden.platform.dao.CommonEntityDao;
import ua.com.fielden.platform.dao.annotations.SessionRequired;
import ua.com.fielden.platform.entity.query.IFilter;
import ua.com.fielden.platform.swing.review.annotations.EntityType;
import ua.com.fielden.platform.swing.review.development.EnhancedCentreEntityQueryCriteria;

import com.google.inject.Inject;

/**
 * DAO implementation for companion object {@link IEntityExportAction}.
 *
 * @author Developers
 *
 */
@EntityType(EntityExportAction.class)
public class EntityExportActionDao extends CommonEntityDao<EntityExportAction> implements IEntityExportAction {
    @Inject
    public EntityExportActionDao(final IFilter filter) {
        super(filter);
    }

    @Override
    @SessionRequired
    public EntityExportAction save(final EntityExportAction entity) {
        final EnhancedCentreEntityQueryCriteria<?, ?> selectionCrit = entity.getContext().getSelectionCrit();

        entity.setFileName(String.format("export-of-%s.xls", selectionCrit.getEntityClass().getSimpleName()));
        entity.setMime("application/vnd.ms-excel");
        try {
            if (entity.getAll()) {
                entity.setData(selectionCrit.exportAll());
            } else if (entity.getPageRange()) {
                entity.setData(selectionCrit.exportPages(entity.getFromPage(), entity.getToPage(), entity.getPageCapacity(), entity.getPageCount()));
            } else if (entity.getSelected()) {
                entity.setData(selectionCrit.exportEntities(entity.getContext().getSelectedEntities()));
            }
        } catch (final IOException e) {

        }

        return entity;
    }
}