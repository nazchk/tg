package ua.com.fielden.platform.entity.query.model.transformation;


public class YieldedProp implements IYieldedItem {

    private final IQuerySourceItem source;
    private final String name;
    private final SqlEntQuery query;


    public YieldedProp(final SqlEntQuery query, final IQuerySourceItem source, final String name) {
	this.source = source;
	this.name = name;
	this.query = query;
    }

    @Override
    public String name() {
	return name;
    }

    private int getYieldPosition() {
	int position = 0;
	for (final String itemName : query.getYields().keySet()) {
	    position  = position + 1;
	    if (name.equals(itemName)) {
		return position;
	    }
	}
	throw new RuntimeException("Unable to determine yield position within query yields");
    }


    @Override
    public String sql() {
	return query.alias() + "." + source.name();
	//return source.sql() + " AS C" + getYieldPosition();
    }
}
