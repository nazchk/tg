package ua.com.fielden.platform.sample.domain;

import ua.com.fielden.platform.data.generator.WithCreatedByUser;
import ua.com.fielden.platform.entity.AbstractPersistentEntity;
import ua.com.fielden.platform.entity.annotation.CompanionObject;
import ua.com.fielden.platform.entity.annotation.CritOnly;
import ua.com.fielden.platform.entity.annotation.CritOnly.Type;
import ua.com.fielden.platform.entity.annotation.IsProperty;
import ua.com.fielden.platform.entity.annotation.KeyTitle;
import ua.com.fielden.platform.entity.annotation.KeyType;
import ua.com.fielden.platform.entity.annotation.MapEntityTo;
import ua.com.fielden.platform.entity.annotation.Observable;
import ua.com.fielden.platform.entity.annotation.Title;
import ua.com.fielden.platform.security.user.User;

/**
 * Represents example entity to be generated by centre's generator TgGeneratedEntityGenerator.
 * <p>
 * Please, note that there is no restriction to whether implement this entity as {@link AbstractPersistentEntity} descendant or not.
 * <p>
 * If it does extend {@link AbstractPersistentEntity}, then basic logic will take care of assigning 'createdBy' property automatically
 * and there will be no need to provide extra implementation for {@link WithCreatedByUser#getCreatedBy()} method.
 * <p>
 * If it doesn't extend {@link AbstractPersistentEntity}, then similar implementation of 'AbstractPersistentEntity.createdBy' property 
 * should be provided and automatic assignment of it should be implemented (most likely it should be done in generator by explicit setting before save).
 * 
 * @author TG Team
 *
 */
@KeyType(String.class)
@KeyTitle(value = "Key", desc = "Some key description")
@CompanionObject(ITgGeneratedEntity.class)
@MapEntityTo
public class TgGeneratedEntity extends AbstractPersistentEntity<String> implements WithCreatedByUser<TgGeneratedEntity> {
    private static final long serialVersionUID = 1L;

    @IsProperty
    @CritOnly(Type.MULTI)
    @Title(value = "CritOnly Multi", desc = "CritOnly multi property")
    private User critOnlyMultiProp;
    
    @IsProperty
    @CritOnly(Type.SINGLE)
    @Title(value = "CritOnly Single", desc = "CritOnly single property")
    private User critOnlySingleProp;

    @Observable
    public TgGeneratedEntity setCritOnlySingleProp(final User critOnlySingleProp) {
        this.critOnlySingleProp = critOnlySingleProp;
        return this;
    }

    public User getCritOnlySingleProp() {
        return critOnlyMultiProp;
    }

    @Observable
    public TgGeneratedEntity setCritOnlyMultiProp(final User critOnlyMultiProp) {
        this.critOnlyMultiProp = critOnlyMultiProp;
        return this;
    }

    public User getCritOnlyMultiProp() {
        return critOnlyMultiProp;
    }
}