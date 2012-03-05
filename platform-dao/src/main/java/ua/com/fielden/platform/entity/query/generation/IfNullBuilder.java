package ua.com.fielden.platform.entity.query.generation;

import java.util.Map;

import ua.com.fielden.platform.entity.query.generation.elements.IfNullModel;

public class IfNullBuilder extends TwoArgumentsFunctionBuilder {

    protected IfNullBuilder(final AbstractTokensBuilder parent, final EntQueryGenerator queryBuilder, final Map<String, Object> paramValues) {
	super(parent, queryBuilder, paramValues);
    }

    @Override
    Object getModel() {
	return new IfNullModel(getModelForSingleOperand(firstCat(), firstValue()), getModelForSingleOperand(secondCat(), secondValue()));
    }
}