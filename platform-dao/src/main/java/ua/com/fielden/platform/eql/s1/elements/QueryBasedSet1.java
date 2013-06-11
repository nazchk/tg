package ua.com.fielden.platform.eql.s1.elements;

import java.util.Arrays;
import java.util.List;

import ua.com.fielden.platform.eql.meta.TransformatorToS2;
import ua.com.fielden.platform.eql.s2.elements.QueryBasedSet2;


public class QueryBasedSet1 implements ISetOperand1<QueryBasedSet2> {
    private final EntQuery1 model;

    public QueryBasedSet1(final EntQuery1 model) {
	super();
	this.model = model;
    }

    @Override
    public QueryBasedSet2 transform(final TransformatorToS2 resolver) {
	return new QueryBasedSet2(model.transform(resolver));
    }

    @Override
    public List<EntProp1> getLocalProps() {
	return model.getLocalProps();
    }

    @Override
    public List<EntQuery1> getLocalSubQueries() {
	return Arrays.asList(new EntQuery1[]{model});
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((model == null) ? 0 : model.hashCode());
	return result;
    }

    @Override
    public boolean equals(final Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (!(obj instanceof QueryBasedSet1)) {
	    return false;
	}
	final QueryBasedSet1 other = (QueryBasedSet1) obj;
	if (model == null) {
	    if (other.model != null) {
		return false;
	    }
	} else if (!model.equals(other.model)) {
	    return false;
	}
	return true;
    }

    @Override
    public boolean ignore() {
	// TODO Auto-generated method stub
	return false;
    }

}