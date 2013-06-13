package ua.com.fielden.platform.eql.s2.elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public abstract class AbstractCondition2 implements ICondition2 {

    protected abstract List<IElement2> getCollection();

    @Override
    public List<EntValue2> getAllValues() {
	if (ignore()) {
	    return Collections.emptyList();
	} else {
	    final List<EntValue2> result = new ArrayList<EntValue2>();

	    for (final IElement2 item : getCollection()) {
		result.addAll(item.getAllValues());
	    }

	    return result;
	}
    }
}