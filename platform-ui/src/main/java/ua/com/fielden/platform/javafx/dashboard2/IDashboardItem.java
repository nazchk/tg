package ua.com.fielden.platform.javafx.dashboard2;

import java.util.List;

import ua.com.fielden.platform.dashboard.IDashboardItemResult;
import ua.com.fielden.platform.security.user.User;
import ua.com.fielden.platform.swing.review.DynamicQueryBuilder.QueryProperty;


/** A general interface for dashboard item. */
public interface IDashboardItem <RESULT extends IDashboardItemResult, UI extends IDashboardItemUi> {

    /** Runs a computation behind the dashboard item and displays result. */
    void runAndDisplay(final List<QueryProperty> customParameters);

    /**
     * Acknowledges the potentially changed alert information by the <code>user</code>.
     *
     * @param user
     */
    void acknowledge(final User user);

    void configure();

    void invokeErrorDetails();
    void invokeWarningDetails();
    void invokeRegularDetails();

    UI getUi();
}