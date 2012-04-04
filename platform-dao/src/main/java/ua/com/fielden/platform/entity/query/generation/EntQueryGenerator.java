package ua.com.fielden.platform.entity.query.generation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ua.com.fielden.platform.dao.DomainPersistenceMetadata;
import ua.com.fielden.platform.entity.query.IFilter;
import ua.com.fielden.platform.entity.query.fluent.QueryTokens;
import ua.com.fielden.platform.entity.query.fluent.TokenCategory;
import ua.com.fielden.platform.entity.query.generation.elements.EntQuery;
import ua.com.fielden.platform.entity.query.generation.elements.QueryCategory;
import ua.com.fielden.platform.entity.query.model.OrderingModel;
import ua.com.fielden.platform.entity.query.model.QueryModel;
import ua.com.fielden.platform.utils.Pair;

public class EntQueryGenerator {
    private final DbVersion dbVersion;
    private final DomainPersistenceMetadata domainPersistenceMetadata;
    private final IFilter filter;
    private final String username;


    public EntQueryGenerator(final DbVersion dbVersion, final DomainPersistenceMetadata domainPersistenceMetadata, final IFilter filter, final String username) {
	this.dbVersion = dbVersion;
	this.domainPersistenceMetadata = domainPersistenceMetadata;
	this.filter = filter;
	this.username = username;
    }

    public EntQuery generateEntQueryAsResultQuery(final QueryModel<?> qryModel, final OrderingModel orderModel, final Map<String, Object> paramValues) {
	return generateEntQuery(qryModel, orderModel, paramValues, QueryCategory.RESULT_QUERY, filter, username);
    }

    public EntQuery generateEntQueryAsResultQuery(final QueryModel<?> qryModel, final Map<String, Object> paramValues) {
	return generateEntQuery(qryModel, null, paramValues, QueryCategory.RESULT_QUERY, filter, username);
    }

    public EntQuery generateEntQueryAsResultQuery(final QueryModel<?> qryModel) {
	return generateEntQueryAsResultQuery(qryModel, new HashMap<String, Object>());
    }

    public EntQuery generateEntQueryAsSourceQuery(final QueryModel<?> qryModel, final Map<String, Object> paramValues, final IFilter filter, final String username) {
	return generateEntQuery(qryModel, null, paramValues, QueryCategory.SOURCE_QUERY, filter, username);
    }

    public EntQuery generateEntQueryAsSourceQuery(final QueryModel<?> qryModel, final Map<String, Object> paramValues) {
	return generateEntQuery(qryModel, null, paramValues, QueryCategory.SOURCE_QUERY, filter, username);
    }

    public EntQuery generateEntQueryAsSourceQuery(final QueryModel<?> qryModel) {
	return generateEntQueryAsSourceQuery(qryModel, new HashMap<String, Object>(), null, null);
    }

    public EntQuery generateEntQueryAsSubquery(final QueryModel<?> qryModel, final Map<String, Object> paramValues) {
	return generateEntQuery(qryModel, null, paramValues, QueryCategory.SUB_QUERY, filter, username);
    }

    public EntQuery generateEntQueryAsSubquery(final QueryModel<?> qryModel) {
	return generateEntQueryAsSubquery(qryModel, new HashMap<String, Object>());
    }

    private EntQuery generateEntQuery(final QueryModel<?> qryModel, final OrderingModel orderModel, final Map<String, Object> paramValues, final QueryCategory category, final IFilter filter, final String username) {
	ConditionsBuilder where = null;
	final QrySourcesBuilder from = new QrySourcesBuilder(null, this, paramValues);
	final QryYieldsBuilder select = new QryYieldsBuilder(null, this, paramValues);
	final QryGroupsBuilder groupBy = new QryGroupsBuilder(null, this, paramValues);
	final QryOrderingsBuilder orderBy = new QryOrderingsBuilder(null, this, paramValues);

	ITokensBuilder active = null;

	for (final Pair<TokenCategory, Object> pair : qryModel.getTokens()) {
	    if (!TokenCategory.QUERY_TOKEN.equals(pair.getKey())) {
		if (active != null) {
		    active.add(pair.getKey(), pair.getValue());
		}
	    } else {
		switch ((QueryTokens) pair.getValue()) {
		case WHERE: //eats token
		    where = new ConditionsBuilder(null, this, paramValues);
		    active = where;
		    break;
		case FROM: //eats token
		    active = from;
		    break;
		case YIELD: //eats token
		    active = select;
		    select.setChild(new YieldBuilder(select, this, paramValues));
		    break;
		case GROUP_BY: //eats token
		    active = groupBy;
		    groupBy.setChild(new GroupBuilder(groupBy, this, paramValues));
		    break;
		default:
		    break;
		}
	    }
	}

	if (orderModel != null) {
	    for (final Iterator<Pair<TokenCategory, Object>> iterator = orderModel.getTokens().iterator(); iterator.hasNext();) {
		final Pair<TokenCategory, Object> pair = iterator.next();
		if (TokenCategory.SORT_ORDER.equals(pair.getKey())) {
		    orderBy.add(pair.getKey(), pair.getValue());
		    if (iterator.hasNext()) {
			orderBy.setChild(new OrderByBuilder(orderBy, this, paramValues));
		    }
		} else {
		    if (orderBy.getChild() == null) {
			orderBy.setChild(new OrderByBuilder(orderBy, this, paramValues));
		    }
		    orderBy.add(pair.getKey(), pair.getValue());
		}
	    }

	}

	return new EntQuery(from.getModel(), where != null ? where.getModel() : null, select.getModel(), groupBy.getModel(), orderBy.getModel(), qryModel.getResultType(), category, //
		domainPersistenceMetadata, filter, username, this);
    }

    public DbVersion getDbVersion() {
        return dbVersion;
    }

    public DomainPersistenceMetadata getDomainPersistenceMetadata() {
        return domainPersistenceMetadata;
    }
}