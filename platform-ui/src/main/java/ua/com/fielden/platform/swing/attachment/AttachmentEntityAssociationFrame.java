package ua.com.fielden.platform.swing.attachment;

import org.jfree.ui.RefineryUtilities;

import ua.com.fielden.platform.attachment.IEntityAttachmentAssociationController;
import ua.com.fielden.platform.dao.IDaoFactory;
import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.matcher.IValueMatcherFactory;
import ua.com.fielden.platform.swing.components.NotificationLayer.MessageType;
import ua.com.fielden.platform.swing.locator.ILocatorConfigurationRetriever;
import ua.com.fielden.platform.swing.model.FrameTitleUpdater;
import ua.com.fielden.platform.swing.model.ICloseGuard;
import ua.com.fielden.platform.swing.review.IEntityMasterManager;
import ua.com.fielden.platform.swing.view.BaseFrame;
import ua.com.fielden.platform.ui.config.api.interaction.ILocatorConfigurationController;
import ua.com.fielden.platform.utils.ResourceLoader;

/**
 * A convenience frame for holding attachment/entity association master view.
 * 
 * @author TG Team
 * 
 */
public class AttachmentEntityAssociationFrame extends BaseFrame {
    private static final long serialVersionUID = 1L;

    private final AttachmentEntityAssociationModel model;
    private final AttachmentEntityAssociationView view;

    public AttachmentEntityAssociationFrame(final AbstractEntity<?> entity, //
    final IEntityAttachmentAssociationController controller, //
    final IValueMatcherFactory valueMatcherFactory,//
    final IEntityMasterManager entityMasterFactory,//
    final IDaoFactory daoFactory,//
    final ILocatorConfigurationController locatorController, final ILocatorConfigurationRetriever locatorRetriever) {
	setIconImage(ResourceLoader.getImage("images/tg-icon.png"));

	model = new AttachmentEntityAssociationModel(entity, controller, valueMatcherFactory, entityMasterFactory, daoFactory, new FrameTitleUpdater(this), locatorController, locatorRetriever);
	add(view = new AttachmentEntityAssociationView(model));

	setTitle(model.toString());

	pack();
	RefineryUtilities.centerFrameOnScreen(this);
    }

    public AttachmentEntityAssociationModel getModel() {
	return model;
    }

    @Override
    protected void notify(final ICloseGuard guard) {
	view.notify(guard.whyCannotClose(), MessageType.WARNING);
    }

    public void refresh() {
	model.getRefreshAction().actionPerformed(null);
    }
}
