package ua.com.fielden.platform.swing.categorychart;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import ua.com.fielden.platform.selectioncheckbox.SelectionCheckBoxPanel.IAction;

/**
 *
 *
 * @author oleh
 *
 */
public class SwitchChartsModel<M, T> {

    private final MultipleChartPanel<M, T> chartPanel;

    private T currentType;

    public SwitchChartsModel(final MultipleChartPanel<M, T> chartPanel) {
	this.chartPanel = chartPanel;
    }

    public T getCurrentType() {
	return currentType;
    }

    protected void setCurrentType(final T currentType) {
	this.currentType = currentType;
    }

    public ItemListener createListenerForChartType(final T type) {
	return new ChartTypeChangeListener(type);
    }

    /**
     * Listener for chart type change events.
     *
     * @author Jhou
     *
     */
    public class ChartTypeChangeListener implements ItemListener {
	private final T type;

	public ChartTypeChangeListener(final T type) {
	    this.type = type;
	}

	@Override
	public void itemStateChanged(final ItemEvent e) {
	    if (e.getStateChange() == ItemEvent.SELECTED) {
		currentType = type;
		if (chartPanel.getChartPanelsCount() > 0) {
		    chartPanel.getChartPanel(0).setPostAction(new IAction() {

			@Override
			public void action() {
			    for (int index = 1; index < chartPanel.getChartPanelsCount(); index++) {
				final ActionChartPanel<M, T> panel = chartPanel.getChartPanel(index);
				panel.setPostAction(null);
				panel.setChart(type);
			    }
			}

		    });
		    chartPanel.getChartPanel(0).setChart(type);
		}
	    }
	}
    }
}
