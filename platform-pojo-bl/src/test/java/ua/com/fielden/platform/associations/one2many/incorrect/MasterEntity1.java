package ua.com.fielden.platform.associations.one2many.incorrect;

import java.util.List;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.annotation.DescTitle;
import ua.com.fielden.platform.entity.annotation.IsProperty;
import ua.com.fielden.platform.entity.annotation.KeyTitle;
import ua.com.fielden.platform.entity.annotation.KeyType;
import ua.com.fielden.platform.entity.annotation.MapTo;
import ua.com.fielden.platform.entity.annotation.Observable;

/**
 * The master type in One-to-Many association with a collectional and single (special case) properties representing assocaitons.
 *
 * @author TG Team
 *
 */
@KeyType(String.class)
@KeyTitle(value = "Key")
@DescTitle(value = "Description")
public class MasterEntity1 extends AbstractEntity<String> {
    private static final long serialVersionUID = 1L;

//    @IsProperty // linkProperty="key1" is missing on purpose
//    @MapTo
//    @Title(value = "Property 1", desc = "Desc")
//    private DetailsEntity1 one2manyAssociationSpecialCase;

    @IsProperty(linkProperty = "key1")
    @MapTo
    private List<DetailsEntity1> one2manyAssociationCollectional;

    @Observable
    public MasterEntity1 setOne2manyAssociationCollectional(final List<DetailsEntity1> one2manyAssociationCollectional) {
	this.one2manyAssociationCollectional = one2manyAssociationCollectional;
	return this;
    }

    public List<DetailsEntity1> getOne2manyAssociationCollectional() {
	return one2manyAssociationCollectional;
    }
}
