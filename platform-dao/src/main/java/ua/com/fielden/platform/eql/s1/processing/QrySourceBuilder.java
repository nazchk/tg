package ua.com.fielden.platform.eql.s1.processing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ua.com.fielden.platform.dao.EntityMetadata;
import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.query.fluent.TokenCategory;
import ua.com.fielden.platform.entity.query.model.QueryModel;
import ua.com.fielden.platform.eql.s1.elements.EntQuery;
import ua.com.fielden.platform.eql.s1.elements.QueryBasedSource;
import ua.com.fielden.platform.eql.s1.elements.TypeBasedSource;
import ua.com.fielden.platform.utils.Pair;

public class QrySourceBuilder extends AbstractTokensBuilder {

    protected QrySourceBuilder(final AbstractTokensBuilder parent, final EntQueryGenerator queryBuilder, final Map<String, Object> paramValues) {
	super(parent, queryBuilder, paramValues);
    }

    private boolean isEntityTypeAsSourceTest() {
	return getSize() == 2 && TokenCategory.ENTITY_TYPE_AS_QRY_SOURCE.equals(firstCat()) && TokenCategory.QRY_SOURCE_ALIAS.equals(secondCat());
    }

    private boolean isEntityTypeAsSourceWithoutAliasTest() {
	return getSize() == 1 && TokenCategory.ENTITY_TYPE_AS_QRY_SOURCE.equals(firstCat());
    }

    private boolean isEntityModelAsSourceTest() {
	return getSize() == 2 && TokenCategory.QRY_MODELS_AS_QRY_SOURCE.equals(firstCat()) && TokenCategory.QRY_SOURCE_ALIAS.equals(secondCat());
    }

    private boolean isEntityModelAsSourceWithoutAliasTest() {
	return getSize() == 1 && TokenCategory.QRY_MODELS_AS_QRY_SOURCE.equals(firstCat());
    }

    @Override
    public boolean isClosing() {
	return false;
    }

    @Override
    public boolean canBeClosed() {
	return isEntityTypeAsSourceTest() || isEntityModelAsSourceTest() || isEntityModelAsSourceWithoutAliasTest() || isEntityTypeAsSourceWithoutAliasTest();
    }

    private Pair<TokenCategory, Object> getResultForEntityTypeAsSource() {
	final Class<AbstractEntity<?>> resultType = (Class) firstValue();
	final EntityMetadata entityMetadata = getQueryBuilder().getDomainMetadataAnalyser().getEntityMetadata(resultType);
	if (entityMetadata.isPersisted()) {
	    return new Pair<TokenCategory, Object>(TokenCategory.QRY_SOURCE, new TypeBasedSource(entityMetadata, (String) secondValue(), getQueryBuilder().getDomainMetadataAnalyser()));
	} else {
	    final List<QueryModel> readyModels = new ArrayList<QueryModel>();
	    readyModels.addAll(entityMetadata.getModels());
	    return getResultForEntityModelAsSource(readyModels, (String) secondValue(), resultType);
	}
    }

    private Pair<TokenCategory, Object> getResultForEntityModelAsSource(final List<QueryModel> readyModels, final String readyAlias, final Class readyResultType) {
	final List<QueryModel> models = readyModels != null ? readyModels : (List<QueryModel>) firstValue();
	final String alias = readyAlias != null ? readyAlias : (String) secondValue();
	final Class resultType = readyResultType != null ? readyResultType : null;
	final List<EntQuery> queries = new ArrayList<EntQuery>();
	for (final QueryModel qryModel : models) {
	    queries.add(getQueryBuilder().generateEntQueryAsSourceQuery(qryModel, getParamValues(), resultType));
	}

	return new Pair<TokenCategory, Object>(TokenCategory.QRY_SOURCE, new QueryBasedSource(alias, getQueryBuilder().getDomainMetadataAnalyser(), queries.toArray(new EntQuery[]{})));
    }

    @Override
    public Pair<TokenCategory, Object> getResult() {
	if (isEntityTypeAsSourceTest() || isEntityTypeAsSourceWithoutAliasTest()) {
	    return getResultForEntityTypeAsSource();
	} else if (isEntityModelAsSourceTest() || isEntityModelAsSourceWithoutAliasTest()) {
	    return getResultForEntityModelAsSource(null, null, null);
	} else {
	    throw new RuntimeException("Unable to get result - unrecognised state.");
	}
    }
}