package ua.com.fielden.platform.test.domain.entities.daos;

import ua.com.fielden.platform.dao2.CommonEntityDao2;
import ua.com.fielden.platform.entity.query.IFilter;
import ua.com.fielden.platform.swing.review.annotations.EntityType;
import ua.com.fielden.platform.test.domain.entities.Wagon;

import com.google.inject.Inject;

/**
 * DAO for retrieving wagon related data: wagon itself, wagon with its rotables.
 *
 * @author TG Team
 *
 */
@EntityType(Wagon.class)
public class WagonDao2 extends CommonEntityDao2<Wagon> implements IWagonDao2 {

    @Inject
    protected WagonDao2(final IFilter filter) {
	super(filter);
    }

}
