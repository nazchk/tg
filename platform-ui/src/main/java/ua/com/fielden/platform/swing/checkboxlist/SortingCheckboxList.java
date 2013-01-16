package ua.com.fielden.platform.swing.checkboxlist;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.ListModel;

import ua.com.fielden.platform.domaintree.centre.IOrderingRepresentation.Ordering;
import ua.com.fielden.platform.utils.Pair;

public class SortingCheckboxList<T> extends CheckboxList<T> {

    private static final long serialVersionUID = -1396817497547523857L;

    private ListSortingModel<T> sortingModel;

    private SorterEventListener<T> defaultSortingListener;

    public SortingCheckboxList(final DefaultListModel<T> model) {
	super(model);
	this.defaultSortingListener = new SorterEventListener<T>() {

	    @Override
	    public void valueChanged(final SorterChangedEvent<T> e) {
		repaint();
	    }
	};
	sortingModel = new DefaultSortingModel<T>();
	sortingModel.addSorterEventListener(defaultSortingListener);
	setCellRenderer(new SortingCheckboxListCellRenderer<T>(this, new JCheckBox()));
    }

    public ListSortingModel<T> getSortingModel() {
	return sortingModel;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setModel(final ListModel<T> newModel) {
	super.setModel(newModel);
	if (getCellRenderer() instanceof SortingCheckboxListCellRenderer) {
	    ((SortingCheckboxListCellRenderer<T>) getCellRenderer()).updateCellWidth(this);
	}
	final Vector<T> listData = getVectorListData();
	for (final Pair<T, Ordering> sortObject : sortingModel.getSortObjects()) {
	    if (!listData.contains(sortObject.getKey())) {
		resetSortOrder(sortObject.getKey(), sortObject.getValue());
	    }
	}
	repaint();
    }

    /**
     * Set the sorting model for this {@link SortingCheckboxList}.
     *
     * @param sortingModel
     */
    public void setSortingModel(final ListSortingModel<T> newSortingModel) {
	if(sortingModel == newSortingModel){
	    return;
	}
	if (newSortingModel != null) {
	    final DefaultListModel<T> listModel = getModel();
	    for (final Pair<T, Ordering> orderItem : newSortingModel.getSortObjects()) {
		if (!listModel.contains(orderItem.getKey())) {
		    resetSortOrder(orderItem.getKey(), orderItem.getValue());
		}
	    }
	    newSortingModel.addSorterEventListener(defaultSortingListener);
	}
	if(sortingModel != null){
	    sortingModel.removeSorterEventListener(defaultSortingListener);
	}
	this.sortingModel = newSortingModel;
	repaint();
    }

    /**
     * Rests the sort order for the specified pair of sorting value and it's ordering.
     *
     * @param sortObject
     */
    private void resetSortOrder(final T item, final Ordering ordering) {
	getSortingModel().toggleSorter(item);
	if(ordering == Ordering.ASCENDING){
	    getSortingModel().toggleSorter(item);
	}
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void processMouseEvent(final MouseEvent e) {
	if (e.getID() == MouseEvent.MOUSE_PRESSED) {
	    final int x = e.getX();
	    final int y = e.getY();
	    final int row = locationToIndex(e.getPoint());
	    final Rectangle rect = row < 0 ? null : getCellBounds(row, row);
	    final int actualX = rect != null ? x - rect.x : 0;
	    final int actualY = rect != null ? y - rect.y : 0;
	    if (getCellRenderer() instanceof ISortableListCellRenderer) {
		if (row >= 0 && isValueChecked(getModel().getElementAt(row)) && //
			((ISortableListCellRenderer<T>) getCellRenderer()).isOnOrderingArrow(actualX, actualY)) {
		    if (!e.isControlDown()) {
			sortingModel.toggleSorterSingle(getModel().getElementAt(row));
		    } else {
			sortingModel.toggleSorter(getModel().getElementAt(row));
		    }
		    if (!isSelectsByChecking()) {
			return;
		    }
		}
	    }
	}
	super.processMouseEvent(e);
    }
}
