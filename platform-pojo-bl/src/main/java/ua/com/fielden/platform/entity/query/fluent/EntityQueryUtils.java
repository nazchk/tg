package ua.com.fielden.platform.entity.query.fluent;

import ua.com.fielden.platform.dao2.AggregatesQueryExecutionModel;
import ua.com.fielden.platform.dao2.QueryExecutionModel;
import ua.com.fielden.platform.dao2.QueryExecutionModel.Builder;
import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IFromAlias;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IOrderingItem;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IStandAloneExprOperand;
import ua.com.fielden.platform.entity.query.model.AggregatedResultQueryModel;
import ua.com.fielden.platform.entity.query.model.EntityResultQueryModel;

public class EntityQueryUtils {
    public static <T extends AbstractEntity<?>> IFromAlias select(final Class<T> entityType) {
	return new FromAlias((new Tokens()).from(entityType));
    }

    public static <T extends AbstractEntity<?>> IFromAlias select(final EntityResultQueryModel<T> sourceQueryModel) {
	return new FromAlias((new Tokens()).from(sourceQueryModel));
    }

    public static <T extends AbstractEntity<?>> IFromAlias select(final EntityResultQueryModel<T>... sourceQueryModels) {
	return new FromAlias((new Tokens()).from(sourceQueryModels));
    }

    public static <T extends AbstractEntity<?>> IFromAlias select(final AggregatedResultQueryModel... sourceQueryModels) {
	return new FromAlias((new Tokens()).from(sourceQueryModels));
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static IStandAloneExprOperand expr() {
	return new StandAloneExpOperand(new Tokens());
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static IOrderingItem orderBy() {
	return new OrderingItem(new Tokens());
    }

    public static <T extends AbstractEntity<?>> Builder<T> from(final EntityResultQueryModel<T> queryModel) {
	return QueryExecutionModel.from(queryModel);
    }

    public static ua.com.fielden.platform.dao2.AggregatesQueryExecutionModel.Builder from(final AggregatedResultQueryModel queryModel) {
	return AggregatesQueryExecutionModel.from(queryModel);
    }
}