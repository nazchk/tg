package ua.com.fielden.platform.entity.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.type.Type;

import ua.com.fielden.platform.dao.MappingsGenerator;
import ua.com.fielden.platform.dao.annotations.SessionRequired;
import ua.com.fielden.platform.dao2.PropertyPersistenceInfo;
import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.factory.EntityFactory;
import ua.com.fielden.platform.entity.query.generation.DbVersion;


/**
 * This class contains the Hibernate driven implementation of getting results from the provided QueryModel.
 *
 * @author TG Team
 *
 * @param <E>
 */
public class AbstractFetcher<E extends AbstractEntity<?>> {
    private Session session;
    private EntityFactory entityFactory;
    private MappingsGenerator mappingsGenerator;
    private DbVersion dbVersion;
    private final IFilter filter;
    private final String username;
    private final EntityEnhancer<E> entityEnhancer;

    public AbstractFetcher(final Session session, final EntityFactory entityFactory, final MappingsGenerator mappingsGenerator, final DbVersion dbVersion, final IFilter filter, final String username) {
	this.session = session;
	this.entityFactory = entityFactory;
	this.mappingsGenerator = mappingsGenerator;
	this.dbVersion = dbVersion;
	this.filter = filter;
	this.username = username;
	this.entityEnhancer = new EntityEnhancer<E>(session, entityFactory, mappingsGenerator, dbVersion, filter, username);
    }

    private static class HibernateScalar {
	private String columnName;
	private Type hibType;

	public HibernateScalar(final String columnName, final Type hibType) {
	    this.columnName = columnName;
	    this.hibType = hibType;
	}

	public boolean hasHibType() {
	    return hibType != null;
	}
    }

    protected Query produceHibernateQuery(final String sql, final List<HibernateScalar> retrievedColumns, final Map<String, Object> queryParams) {
	final SQLQuery q = session.createSQLQuery(sql);

	for (final HibernateScalar aliasEntry : retrievedColumns) {
	    if (aliasEntry.hasHibType()) {
		q.addScalar(aliasEntry.columnName, aliasEntry.hibType);
	    } else {
		q.addScalar(aliasEntry.columnName);
	    }
	}

	for (final Map.Entry<String, Object> paramEntry : queryParams.entrySet()) {
	    if (paramEntry.getValue() instanceof Collection) {
		q.setParameterList(paramEntry.getKey(), (Collection<?>) paramEntry.getValue());
	    } else {
		q.setParameter(paramEntry.getKey(), paramEntry.getValue());
	    }
	}

	return q;
    }

    protected List<HibernateScalar> getScalarFromValueTree(final ValueTree tree) {
	final List<HibernateScalar> result = new ArrayList<HibernateScalar>();

	for (final Map.Entry<PropertyPersistenceInfo, Integer> single : tree.getSingles().entrySet()) {
	    result.add(new HibernateScalar(single.getKey().getColumn(), single.getKey().getHibTypeAsType()));
	}

	return result;
    }

    protected List<HibernateScalar> getScalarFromEntityTree(final EntityTree<? extends AbstractEntity<?>> tree) {
	final List<HibernateScalar> result = new ArrayList<HibernateScalar>();

	for (final Map.Entry<PropertyPersistenceInfo, Integer> single : tree.getSingles().entrySet()) {
	    result.add(new HibernateScalar(single.getKey().getColumn(), single.getKey().getHibTypeAsType()));
	}

	for (final Map.Entry<String, ValueTree> composite : tree.getCompositeValues().entrySet()) {
	    result.addAll(getScalarFromValueTree(composite.getValue()));
	}

	for (final Map.Entry<String, EntityTree<? extends AbstractEntity<?>>> composite : tree.getComposites().entrySet()) {
	    result.addAll(getScalarFromEntityTree(composite.getValue()));
	}

	return result;
    }

    protected List<E> instantiateFromContainers(final List<EntityContainer<E>> containers, final boolean userViewOnly) {
	final List<E> result = new ArrayList<E>();
	for (final EntityContainer<E> entityContainer : containers) {
	    result.add(entityContainer.instantiate(getEntityFactory(), userViewOnly));
	}
	return result;
    }

    @SessionRequired
    protected List<EntityContainer<E>> listContainersAsIs(final QueryModelResult<E> modelResult, final Integer pageNumber, final Integer pageCapacity) throws Exception {
	final EntityTree<E> resultTree = new EntityResultTreeBuilder(getMappingsGenerator()).buildEntityTree(modelResult.getResultType(), modelResult.getYieldedPropsInfo());

	final Query query = produceHibernateQuery(modelResult.getSql(), getScalarFromEntityTree(resultTree), modelResult.getParamValues());
	if (pageNumber != null && pageCapacity != null) {
	    query.//
	    setFirstResult(pageNumber * pageCapacity).//
	    setFetchSize(pageCapacity).//
	    setMaxResults(pageCapacity);
	}

	return new EntityRawResultConverter<E>(getEntityFactory()).transformFromNativeResult(resultTree, query.list());
    }

    public Session getSession() {
        return session;
    }

    public EntityFactory getEntityFactory() {
        return entityFactory;
    }

    public MappingsGenerator getMappingsGenerator() {
        return mappingsGenerator;
    }

    public DbVersion getDbVersion() {
        return dbVersion;
    }

    public IFilter getFilter() {
        return filter;
    }

    public String getUsername() {
        return username;
    }

    public EntityEnhancer<E> getEntityEnhancer() {
        return entityEnhancer;
    }
}