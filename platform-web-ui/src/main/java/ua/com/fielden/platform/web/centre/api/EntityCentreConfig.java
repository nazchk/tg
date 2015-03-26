package ua.com.fielden.platform.web.centre.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ua.com.fielden.platform.web.centre.api.actions.EntityActionConfig;

/**
 *
 * Represents a final structure of an entity centre as produced by means of using Entity Centre DSL.
 *
 * @author TG Team
 *
 */
public class EntityCentreConfig {
    // TODO the actual structure still needs to be determined

    /**
     * Contains action configurations for actions that are associated with individual properties of retrieved entities,
     * which are represented in the result set.
     * This map can be empty is there is no need to provide custom actions specific for represented in the result set properties.
     * In this case, the default actions would still get associated with all not listed in this map, but added to the result set properties.
     */
    private final Map<String, EntityActionConfig> resultSetPropActions = new HashMap<>();

    /**
     * A primary entity action configuration that is associated with every retrieved and present in the result set entity.
     * It can be <code>null</code> if no primary entity action is needed.
     */
    private EntityActionConfig primaryEntityAction;

    /**
     * A list of secondary action configurations that are associated with every retrieved and present in the result set entity.
     * It can be empty if no secondary action are necessary.
     */
    private List<EntityActionConfig> secondaryEntityActions = new ArrayList<>();
}
