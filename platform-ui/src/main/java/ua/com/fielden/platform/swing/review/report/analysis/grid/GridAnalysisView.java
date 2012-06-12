package ua.com.fielden.platform.swing.review.report.analysis.grid;

import static org.apache.commons.lang.StringUtils.isEmpty;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.border.EtchedBorder;

import net.miginfocom.swing.MigLayout;
import ua.com.fielden.platform.actionpanelmodel.ActionPanelBuilder;
import ua.com.fielden.platform.dao.IEntityDao;
import ua.com.fielden.platform.dao.IEntityProducer;
import ua.com.fielden.platform.domaintree.centre.ICentreDomainTreeManager.ICentreDomainTreeManagerAndEnhancer;
import ua.com.fielden.platform.domaintree.centre.analyses.IAbstractAnalysisDomainTreeManager.IAbstractAnalysisDomainTreeManagerAndEnhancer;
import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.pagination.IPage;
import ua.com.fielden.platform.swing.actions.BlockingLayerCommand;
import ua.com.fielden.platform.swing.actions.Command;
import ua.com.fielden.platform.swing.components.blocking.BlockingIndefiniteProgressLayer;
import ua.com.fielden.platform.swing.components.blocking.IBlockingLayerProvider;
import ua.com.fielden.platform.swing.egi.EgiPanel1;
import ua.com.fielden.platform.swing.egi.models.PropertyTableModel;
import ua.com.fielden.platform.swing.model.IUmViewOwner;
import ua.com.fielden.platform.swing.pagination.model.development.IPageChangedListener;
import ua.com.fielden.platform.swing.pagination.model.development.PageChangedEvent;
import ua.com.fielden.platform.swing.review.IEntityMasterManager;
import ua.com.fielden.platform.swing.review.OpenMasterClickAction;
import ua.com.fielden.platform.swing.review.report.analysis.grid.configuration.GridConfigurationView;
import ua.com.fielden.platform.swing.review.report.analysis.view.AbstractAnalysisReview;
import ua.com.fielden.platform.swing.review.report.centre.AbstractEntityCentre;
import ua.com.fielden.platform.swing.review.report.configuration.AbstractConfigurationView.ConfigureAction;
import ua.com.fielden.platform.swing.review.report.events.SelectionEvent;
import ua.com.fielden.platform.swing.review.report.interfaces.ISelectionEventListener;
import ua.com.fielden.platform.swing.utils.SwingUtilitiesEx;
import ua.com.fielden.platform.utils.ResourceLoader;

public class GridAnalysisView<T extends AbstractEntity<?>, CDTME extends ICentreDomainTreeManagerAndEnhancer> extends AbstractAnalysisReview<T, CDTME, IAbstractAnalysisDomainTreeManagerAndEnhancer, IPage<T>> implements IUmViewOwner, IBlockingLayerProvider{

    private static final long serialVersionUID = 8538099803371092525L;

    private final EgiPanel1<T> egiPanel;

    /**
     * Tool bar that contain master related actions.
     */
    private final JToolBar toolBar;

    public GridAnalysisView(final GridAnalysisModel<T, CDTME> model, final GridConfigurationView<T, CDTME> owner) {
	super(model, owner);
	this.egiPanel = new EgiPanel1<T>(getModel().getCriteria().getEntityClass(), getModel().getCriteria().getCentreDomainTreeMangerAndEnhancer());
	this.toolBar = createToolBar();
	if (getMasterManager() != null) {
	    OpenMasterClickAction.enhanceWithClickAction(egiPanel.getEgi().getActualModel().getPropertyColumnMappings(),//
		    model.getCriteria().getEntityClass(), //
		    getMasterManager(), //
		    this);
	}
	getModel().getPageHolder().addPageChangedListener(new IPageChangedListener() {

	    @SuppressWarnings("unchecked")
	    @Override
	    public void pageChanged(final PageChangedEvent e) {
		egiPanel.setData((IPage<T>)e.getNewPage());
	    }
	});
	getModel().getPageHolder().newPage(null);
	this.addSelectionEventListener(createGridAnalysisSelectionListener());

	//Set this analysis view for model.
	model.setAnalysisView(this);

	layoutView();
    }

    public final JToolBar getToolBar(){
	return toolBar;
    }

    public final EgiPanel1<T> getEgiPanel() {
	return egiPanel;
    }

    @Override
    public GridAnalysisModel<T, CDTME> getModel() {
	return (GridAnalysisModel<T, CDTME>) super.getModel();
    }

    @Override
    public BlockingIndefiniteProgressLayer getBlockingLayer() {
        return getOwner().getProgressLayer();
    }

    @Override
    public <E extends AbstractEntity<?>> void notifyEntityChange(final E entity) {
        if (entity.isPersisted()) {
            SwingUtilitiesEx.invokeLater(new Runnable() {
        	@SuppressWarnings("unchecked")
        	@Override
        	public void run() {
        	    getEgiPanel().getEgi().getActualModel().refresh((T) entity);
        	    //getProgressLayer().setLocked(false);
        	}
            });
        }

    }

    @Override
    protected void enableRelatedActions(final boolean enable, final boolean navigate) {
	if(getModel().getCriteria().isDefaultEnabled()){
	    getCentre().getDefaultAction().setEnabled(enable);
	}
	if(!navigate){
	    getCentre().getPaginator().setEnableActions(enable, !enable);
	}
	getCentre().getExportAction().setEnabled(enable);
	getCentre().getRunAction().setEnabled(enable);
    }

    @Override
    protected ConfigureAction createConfigureAction() {
	return null;
    }

    protected JToolBar createToolBar() {
	if(getMasterManager() != null){
	    final JToolBar toolbar = new ActionPanelBuilder()//
	    .addButton(createOpenMasterWithNewCommand())//
	    .addButton(createOpenMasterCommand())//
	    .buildActionPanel();
	    toolbar.setFloatable(false);
	    toolbar.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
	    return toolbar;
	}
	return null;
    }

    /**
     * A command that creates and opens an entity master frame for the new entity.
     *
     * @return
     */
    protected Command<T> createOpenMasterWithNewCommand() {
	final Command<T> action = new Command<T>("New") {
	    private static final long serialVersionUID = 1L;

	    private IEntityProducer<T> entityProducer;
	    private IEntityMasterManager masterManager;

	    @Override
	    protected boolean preAction() {
		if (super.preAction()) {
		    masterManager = getMasterManager();
		    final Class<T> entityType = getModel().getCriteria().getEntityClass();
		    entityProducer = masterManager != null ? masterManager.getEntityProducer(entityType) : null;
		    return entityProducer != null;
		}
		return false;
	    }

	    @Override
	    protected T action(final ActionEvent event) throws Exception {
		return entityProducer.newEntity();
	    }

	    @Override
	    protected void postAction(final T entity) {
		masterManager.<T, IEntityDao<T>> showMaster(entity, GridAnalysisView.this);
		super.postAction(entity);
	    }
	};
	action.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_1);
	action.putValue(Action.LARGE_ICON_KEY, ResourceLoader.getIcon("images/document-new.png"));
	action.putValue(Action.SMALL_ICON, ResourceLoader.getIcon("images/document-new.png"));
	action.putValue(Action.SHORT_DESCRIPTION, "New");
	action.setEnabled(true);
	return action;
    }

    /**
     * A command that creates and opens an entity master frame for the selected in the EGI entity.
     *
     * @return
     */
    protected Command<T> createOpenMasterCommand() {
	final Command<T> action = new BlockingLayerCommand<T>("Edit", getBlockingLayer()) {
	    private static final long serialVersionUID = 1L;

	    private IEntityMasterManager masterManager;

	    @Override
	    protected boolean preAction() {
		setMessage("Opening...");
		if (super.preAction()) {
		    masterManager = getMasterManager();
		    return masterManager != null && getSelectedEntities().size() == 1;
		}
		return false;
	    }

	    @Override
	    protected T action(final ActionEvent event) throws Exception {
		return getSelectedEntities().get(0);
	    }

	    @Override
	    protected void postAction(final T entity) {
		super.postAction(entity);
		if (entity != null) {
		    masterManager.<T, IEntityDao<T>> showMaster(entity, GridAnalysisView.this);
		}
	    }
	};
	action.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_2);
	action.putValue(Action.LARGE_ICON_KEY, ResourceLoader.getIcon("images/document-edit.png"));
	action.putValue(Action.SMALL_ICON, ResourceLoader.getIcon("images/document-edit.png"));
	action.putValue(Action.SHORT_DESCRIPTION, "Edit");
	action.setEnabled(true);
	return action;
    }

    /**
     * A command that removes the selected in the EGI entity.
     *
     * @return
     */
    protected Command<T> createDeleteCommand() {
	final Command<T> action = new BlockingLayerCommand<T>("Delete", getBlockingLayer()) {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected boolean preAction() {
		final boolean superRes = super.preAction();
		if(!superRes){
		    return false;
		}
		final List<T> selectedEntities = getSelectedEntities();
		if(selectedEntities.size() != 1){//There are no selected entities or there are more then one are selected.
		    return false;
		}
		final T entity = selectedEntities.get(0);
		if (JOptionPane.showConfirmDialog(GridAnalysisView.this, "Entity " + entity + " will be deleted. Proceed?", "Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
		    return false;
		}
		setMessage("Deleting...");
		return true;
	    }

	    @Override
	    protected T action(final ActionEvent event) throws Exception {
		final List<T> selectedEntities = getSelectedEntities();
		final T entity = selectedEntities.get(0);
		getModel().getCriteria().delete(entity);
		return entity;
	    }

	    @SuppressWarnings("unchecked")
	    @Override
	    protected void postAction(final T entity) {
		final PropertyTableModel<T> tableModel = egiPanel.getEgi().getActualModel();
		tableModel.removeInstances(entity);
		tableModel.fireTableDataChanged();
		super.postAction(entity);
	    }

	};
	action.setEnabled(true);
	action.putValue(Action.SHORT_DESCRIPTION, "Delete");
	action.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_3);
	action.putValue(Action.LARGE_ICON_KEY, ResourceLoader.getIcon("images/document-delete.png"));
	action.putValue(Action.SMALL_ICON, ResourceLoader.getIcon("images/document-delete.png"));
	return action;
    }

    /**
     * Layouts the components of this analysis.
     */
    private void layoutView() {
	final List<JComponent> components = new ArrayList<JComponent>();
	final StringBuffer rowConstraints = new StringBuffer("");

	//Creates entity centre's tool bar.
	rowConstraints.append(AbstractEntityCentre.addToComponents(components, "[fill]", getToolBar()));
	rowConstraints.append(AbstractEntityCentre.addToComponents(components, "[fill, grow]", getEgiPanel()));

	setLayout(new MigLayout("fill, insets 0","[fill, grow]",  isEmpty(rowConstraints.toString()) ? "[fill, grow]" : rowConstraints.toString()));
	for(int componentIndex = 0; componentIndex < components.size() - 1; componentIndex++){
	    add(components.get(componentIndex), "wrap");
	}
	add(components.get(components.size()-1));
    }

    /**
     * Determines the number of rows in the table those must be shown on the page using the size of the content panel as the basis.
     * If the calculated size is zero then value of 25 is returned.
     * This is done to handle cases where calculation happens prior to panel resizing takes place.
     *
     * @return
     */
    final int getPageSize() {
	double pageSize = egiPanel.getSize().getHeight() / EgiPanel1.ROW_HEIGHT;
	if (getOwner().getOwner().getCriteriaPanel() != null) {
	    pageSize += getOwner().getOwner().getCriteriaPanel().getSize().getHeight() / EgiPanel1.ROW_HEIGHT;
	}
	final int pageCapacity = (int) Math.floor(pageSize);
	return pageCapacity > 1 ? pageCapacity : 1;
    }

    //    /**
    //     * Enables or disables the paginator's actions without enabling or disabling blocking layer.
    //     *
    //     * @param enable
    //     */
    //    private void enablePaginatorActionsWithoutBlockingLayer(final boolean enable){
    //	getOwner().getPaginator().getFirst().setEnabled(enable, false);
    //	getOwner().getPaginator().getPrev().setEnabled(enable, false);
    //	getOwner().getPaginator().getNext().setEnabled(enable, false);
    //	getOwner().getPaginator().getLast().setEnabled(enable, false);
    //	if(getOwner().getPaginator().getFeedback() != null){
    //	    getOwner().getPaginator().getFeedback().enableFeedback(false);
    //	}
    //    }

    /**
     * Returns the list of selected entities.
     *
     * @return
     */
    public List<T> getSelectedEntities() {
	final PropertyTableModel<T> tableModel = egiPanel.getEgi().getActualModel();
	return tableModel.getSelectedEntities();
    }

    protected IEntityMasterManager getMasterManager() {
        return getOwner().getOwner().getModel().getMasterManager();
    }

    /**
     * Returns the {@link ISelectionEventListener} that enables or disable appropriate actions when this analysis was selected.
     *
     * @return
     */
    private ISelectionEventListener createGridAnalysisSelectionListener() {
	return new ISelectionEventListener() {

	    @Override
	    public void viewWasSelected(final SelectionEvent event) {
		//Managing the default, design and custom action changer button enablements.
		getCentre().getDefaultAction().setEnabled(getModel().getCriteria().isDefaultEnabled());
		if (getCentre().getCriteriaPanel() != null && getCentre().getCriteriaPanel().canConfigure()) {
		    getCentre().getCriteriaPanel().getSwitchAction().setEnabled(true);
		}
		if(getCentre().getCustomActionChanger() != null){
		    getCentre().getCustomActionChanger().setEnabled(true);
		}
		//Managing the paginator's enablements.
		getCentre().getPaginator().setEnableActions(true, false);
		//Managing load and export enablements.
		getCentre().getExportAction().setEnabled(true);
		getCentre().getRunAction().setEnabled(true);
	    }
	};
    }

    private AbstractEntityCentre<T, CDTME> getCentre(){
	return getOwner().getOwner();
    }
}
