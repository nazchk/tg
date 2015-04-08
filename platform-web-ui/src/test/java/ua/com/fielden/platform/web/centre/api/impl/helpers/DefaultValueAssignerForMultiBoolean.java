package ua.com.fielden.platform.web.centre.api.impl.helpers;

import java.util.List;
import java.util.Optional;

import ua.com.fielden.platform.sample.domain.TgWorkOrder;
import ua.com.fielden.platform.web.centre.CentreContext;
import ua.com.fielden.platform.web.centre.api.crit.defaults.assigners.IMultiValueAssigner;
import ua.com.fielden.platform.web.centre.api.crit.defaults.mnemonics.MultiCritBooleanValueMnemonic;


/**
 * A stub implementation for a default value assigner for multi-valued criteria of type boolean.
 *
 * @author TG Team
 *
 */
public class DefaultValueAssignerForMultiBoolean implements IMultiValueAssigner<MultiCritBooleanValueMnemonic, TgWorkOrder> {

    @Override
    public Optional<List<MultiCritBooleanValueMnemonic>> getValues(final CentreContext<TgWorkOrder, ?> entity, final String name) {
        return null;
    }


}
