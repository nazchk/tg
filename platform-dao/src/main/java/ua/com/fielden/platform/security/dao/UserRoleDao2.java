package ua.com.fielden.platform.security.dao;

import java.util.List;

import ua.com.fielden.platform.dao.annotations.SessionRequired;
import ua.com.fielden.platform.dao2.CommonEntityDao2;
import ua.com.fielden.platform.dao2.IUserRoleDao2;
import ua.com.fielden.platform.entity.query.IFilter;
import ua.com.fielden.platform.entity.query.model.EntityResultQueryModel;
import ua.com.fielden.platform.entity.query.model.OrderingModel;
import ua.com.fielden.platform.security.user.UserRole;
import ua.com.fielden.platform.swing.review.annotations.EntityType;

import com.google.inject.Inject;

import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.from;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.orderBy;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.select;

/**
 * Db driven implementation of the {@link IUserRoleDao2}.
 *
 * @author TG Team
 *
 */
@EntityType(UserRole.class)
public class UserRoleDao2 extends CommonEntityDao2<UserRole> implements IUserRoleDao2 {

    @Inject
    protected UserRoleDao2(final IFilter filter) {
	super(filter);
    }

    @Override
    @SessionRequired
    public List<UserRole> findAll() {
	final EntityResultQueryModel<UserRole> model = select(UserRole.class).model();
	final OrderingModel orderBy = orderBy().prop("key").asc().model();
	return getAllEntities(from(model).with(orderBy).build());
    }
}