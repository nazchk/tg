package ua.com.fielden.platform.eql.s1.processing;

import java.util.Map;

import ua.com.fielden.platform.entity.query.fluent.TokenCategory;
import ua.com.fielden.platform.utils.Pair;

abstract class ThreeArgumentsFunctionBuilder extends AbstractTokensBuilder {

    protected ThreeArgumentsFunctionBuilder(final AbstractTokensBuilder parent, final EntQueryGenerator queryBuilder, final Map<String, Object> paramValues) {
	super(parent, queryBuilder, paramValues);
    }

    @Override
    public boolean isClosing() {
	return getSize() == 3;
    }

    abstract Object getModel();

    @Override
    public Pair<TokenCategory, Object> getResult() {
	return new Pair<TokenCategory, Object>(TokenCategory.FUNCTION_MODEL, getModel());
    }

}
