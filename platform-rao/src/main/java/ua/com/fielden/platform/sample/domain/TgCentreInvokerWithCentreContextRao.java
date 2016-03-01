package ua.com.fielden.platform.sample.domain;

import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.fetchAll;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.from;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.select;
import ua.com.fielden.platform.entity.query.fluent.fetch;
import ua.com.fielden.platform.entity.query.model.EntityResultQueryModel;
import ua.com.fielden.platform.pagination.IPage;
import ua.com.fielden.platform.rao.CommonEntityRao;
import ua.com.fielden.platform.rao.RestClientUtil;
import ua.com.fielden.platform.swing.review.annotations.EntityType;
import ua.com.fielden.platform.sample.domain.mixin.TgCentreInvokerWithCentreContextMixin;
import com.google.inject.Inject;

/** 
 * RAO implementation for master object {@link ITgCentreInvokerWithCentreContext} based on a common with DAO mixin.
 * 
 * @author Developers
 *
 */
@EntityType(TgCentreInvokerWithCentreContext.class)
public class TgCentreInvokerWithCentreContextRao extends CommonEntityRao<TgCentreInvokerWithCentreContext> implements ITgCentreInvokerWithCentreContext {

    
    private final TgCentreInvokerWithCentreContextMixin mixin;
    
    @Inject
    public TgCentreInvokerWithCentreContextRao(final RestClientUtil restUtil) {
        super(restUtil);
        
        mixin = new TgCentreInvokerWithCentreContextMixin(this);
    }
    
}