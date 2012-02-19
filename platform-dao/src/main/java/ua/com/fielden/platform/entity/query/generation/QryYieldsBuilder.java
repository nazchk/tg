package ua.com.fielden.platform.entity.query.generation;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import ua.com.fielden.platform.entity.query.fluent.TokenCategory;
import ua.com.fielden.platform.entity.query.generation.elements.YieldModel;
import ua.com.fielden.platform.entity.query.generation.elements.YieldsModel;
import ua.com.fielden.platform.utils.Pair;

public class QryYieldsBuilder extends AbstractTokensBuilder {

    protected QryYieldsBuilder(final AbstractTokensBuilder parent, final EntQueryGenerator queryBuilder, final Map<String, Object> paramValues) {
	super(parent, queryBuilder, paramValues);
    }

    @Override
    public boolean isClosing() {
	return false;
    }

    // TODO handle yield().entity(String joinAlias) properly

    public YieldsModel getModel() {
	if (getChild() != null && getSize() == 0) {
	    finaliseChild();
	    //throw new RuntimeException("Unable to produce result - unfinished model state!");
	}
	final SortedMap<String, YieldModel> yields = new TreeMap<String, YieldModel>();
	for (final Pair<TokenCategory, Object> pair : getTokens()) {
	    yields.put(((YieldModel) pair.getValue()).getAlias(), (YieldModel) pair.getValue());
	}

	return new YieldsModel(yields);
    }


    @Override
    public Pair<TokenCategory, Object> getResult() {
	throw new RuntimeException("Not applicable!");
    }
}
