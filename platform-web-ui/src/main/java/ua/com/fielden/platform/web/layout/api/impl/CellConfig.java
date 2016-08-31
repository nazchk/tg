package ua.com.fielden.platform.web.layout.api.impl;

import static org.apache.commons.lang.StringUtils.isEmpty;

import java.util.Optional;

import ua.com.fielden.platform.dom.DomElement;

/**
 * Represents layout cell.
 *
 * @author TG Team
 *
 */
public class CellConfig {

    private final Optional<ContainerConfig> container;
    private final Optional<String> layoutWidget;

    private Optional<FlexLayoutConfig> layout = Optional.empty();

    /**
     * Creates empty cell.
     */
    public CellConfig() {
        this(null, null, (String) null);
    }

    /**
     * Creates container cell.
     *
     * @param container
     */
    public CellConfig(final ContainerConfig container) {
        this(container, null, (String) null);
    }

    /**
     * Creates cell with layout container.
     *
     * @param layout
     */
    public CellConfig(final FlexLayoutConfig layout) {
        this(null, layout, (String) null);
    }

    /**
     * Creates container cell with layout configuration.
     *
     * @param container
     * @param layout
     */
    public CellConfig(final ContainerConfig container, final FlexLayoutConfig layout) {
        this(container, layout, (String) null);
    }

    /**
     * Creates the html cell.
     *
     * @param dom
     */
    public CellConfig(final DomElement dom) {
        this(null, null, "html:" + dom.toString());
    }

    /**
     * Creates html cell with layout configuration.
     *
     * @param dom
     * @param layout
     */
    public CellConfig(final DomElement dom, final FlexLayoutConfig layout) {
        this(null, layout, "html:" + dom.toString());
    }

    /**
     * Creates widget cell (e.g. subheader, select or skip cell)
     *
     * @param layoutWidget
     */
    public CellConfig(final String layoutWidget) {
        this(null, null, layoutWidget);
    }

    /**
     * Creates widget cell (e.g. subheader, select or skip cell) with specified layout.
     *
     * @param layout
     * @param layoutWidget
     */
    public CellConfig(final FlexLayoutConfig layout, final String layoutWidget) {
        this(null, layout, layoutWidget);
    }

    /**
     * Set the layout configuration for this cell if it wasn't specified yet.
     *
     * @param layout
     */
    public void setLayoutIfNotPresent(final FlexLayoutConfig layout) {
        if (!this.layout.isPresent()) {
            this.layout = Optional.ofNullable(layout);
        }
    }

    /**
     * Generates cell string for this configuration.
     *
     * @param vertical
     *            - flex-direction of the container in which this cell is placed. The direction calculation is based on layout configuration and default direction.
     * @param isVerticalDefault
     *            - the default flex-direction of the container in which this cell is placed.
     * @param gap
     * @return
     */
    public String render(final boolean vertical, final boolean isVerticalDefault, final int gap) {
        final String gapStyleString = gap == 0 ? "" : "\"" + (vertical ? "margin-bottom" : "margin-right") + ":" + gap + "px\"";
        final String layoutString = layout.map(layout -> layout.render(vertical, gap)).orElse(gapStyleString);
        final Optional<Boolean> optionalVertical = layout.flatMap(l -> l.isVerticalLayout());
        final String containerString = container.map(c -> c.render(optionalVertical.orElse(!isVerticalDefault), !isVerticalDefault)).orElse("");

        return Optional.of(layoutWidget.map(lw -> "\"" + lw + "\"").orElse(""))
        .map(l -> !isEmpty(l) && !isEmpty(layoutString) ? l + ", " : l)
        .map(l -> l + layoutString)
        .map(l -> !isEmpty(l) && !isEmpty(containerString) ? l + ", " : l)
        .map(l -> l + containerString)
        .map(l -> "[" + l + "]").get();
    }

    private CellConfig(final ContainerConfig container, final FlexLayoutConfig flexLayout, final String layoutWidget) {
        this.container = Optional.ofNullable(container);
        this.layout = Optional.ofNullable(flexLayout);
        this.layoutWidget = Optional.ofNullable(layoutWidget);
    }
}
