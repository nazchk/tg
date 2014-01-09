package ua.com.fielden.platform.client;

import java.awt.Dimension;
import java.awt.Image;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ToolTipManager;

import org.apache.log4j.Logger;
import org.jfree.ui.RefineryUtilities;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.restlet.data.Protocol;

import ua.com.fielden.platform.application.update.ApplicationUpdateFeedback;
import ua.com.fielden.platform.basic.config.IApplicationSettings;
import ua.com.fielden.platform.basic.config.Workflows;
import ua.com.fielden.platform.branding.SplashController;
import ua.com.fielden.platform.client.config.IMainMenuBinder;
import ua.com.fielden.platform.client.session.AppSessionController;
import ua.com.fielden.platform.client.ui.DefaultApplicationMainPanel;
import ua.com.fielden.platform.client.ui.menu.TreeMenuFactory;
import ua.com.fielden.platform.entity.factory.EntityFactory;
import ua.com.fielden.platform.error.Result;
import ua.com.fielden.platform.rao.RestClientUtil;
import ua.com.fielden.platform.security.ClientAuthenticationModel;
import ua.com.fielden.platform.security.user.IUserProvider;
import ua.com.fielden.platform.security.user.User;
import ua.com.fielden.platform.swing.components.blocking.BlockingIndefiniteProgressPane;
import ua.com.fielden.platform.swing.dialogs.DialogWithDetails;
import ua.com.fielden.platform.swing.login.StyledLoginScreen;
import ua.com.fielden.platform.swing.login.StyledLoginScreenModel;
import ua.com.fielden.platform.swing.menu.TreeMenuItem;
import ua.com.fielden.platform.swing.menu.UndockableTreeMenuWithTabs;
import ua.com.fielden.platform.swing.menu.api.ITreeMenuFactory;
import ua.com.fielden.platform.swing.menu.filter.WordFilter;
import ua.com.fielden.platform.swing.review.EntityMasterManager;
import ua.com.fielden.platform.swing.utils.SwingUtilitiesEx;
import ua.com.fielden.platform.swing.view.BaseFrame;
import ua.com.fielden.platform.ui.config.IEntityCentreAnalysisConfig;
import ua.com.fielden.platform.ui.config.IMainMenu;
import ua.com.fielden.platform.ui.config.MainMenuItem;
import ua.com.fielden.platform.ui.config.api.IEntityCentreConfigController;
import ua.com.fielden.platform.ui.config.api.IMainMenuItemController;
import ua.com.fielden.platform.ui.config.api.IMainMenuItemInvisibilityController;
import ua.com.fielden.platform.ui.config.api.IMainMenuStructureBuilder;
import ua.com.fielden.platform.ui.config.controller.mixin.MainMenuItemMixin;
import ua.com.fielden.platform.ui.config.controller.mixin.PersistedMainMenuStructureBuilder;
import ua.com.fielden.platform.update.IClientApplicationRestarter;
import ua.com.fielden.platform.update.ReferenceDependancyController;
import ua.com.fielden.platform.update.Updater;
import ua.com.fielden.platform.utils.ApplicationLaunchedChecker;
import ua.com.fielden.platform.utils.ResourceLoader;

import com.google.inject.Injector;
import com.jidesoft.plaf.LookAndFeelFactory;

/**
 * A utility class containing all the necessary methods for constructing a web client in a more convenient than manual way.
 *
 * @author TG Team
 *
 */
public class WebClientStartupUtility {
    private static final Logger logger = Logger.getLogger(WebClientStartupUtility.class);
    /**
     * Constructs and displays login prompt. Successful login results in the invocation of the passed in {@link IClientLauncher} contract.
     *
     * @param injector
     * @param autoudate
     * @param restUtil
     * @param sessionController
     * @param authenticationModel
     * @param launcher
     * @param logger
     */
    public static void showLoginPrompt(//
	    final Injector injector, //
	    final boolean autoudate, //
	    final RestClientUtil restUtil, //
	    final AppSessionController sessionController, //
	    final ClientAuthenticationModel authenticationModel, //
	    final IClientLauncher launcher,//
	    final Logger logger) {
	SwingUtilitiesEx.invokeLater(new Runnable() {
	    @Override
	    public void run() {
		try {
		    JFrame.setDefaultLookAndFeelDecorated(true);
		    JDialog.setDefaultLookAndFeelDecorated(true);

		    // installing Nimbus LnF once again in order to correctly draw check box as table renderer
		    // this installation should be performed after main frame was created
		    SwingUtilitiesEx.installNimbusLnFifPossible();

		    // prepare the login prompt
		    final StyledLoginScreenModel loginScreenModel = new StyledLoginScreenModel(authenticationModel, "Login Screen", ResourceLoader.getIcon("images/styled-login-scr-background.png")) {
			@Override
			protected void authenticationPassed(final StyledLoginScreen loginScreen, final Result result) {
			    final User user = (User) result.getInstance();
			    final IApplicationSettings settings = injector.getInstance(IApplicationSettings.class);
			    if (Workflows.development.equals(Workflows.valueOf(settings.workflow())) && !user.isBase()) {
				throw new IllegalArgumentException("Development mode of the client application startup is not permitted for non-base user. Please start the client application in deployment mode. Current user [" + user + "] is not base user.");
			    }
			    try {
				sessionController.persist(user.getKey(), restUtil.getPrivateKey());
				launcher.launch(null, loginScreen, restUtil, injector, autoudate, sessionController, logger);
			    } catch (final Exception ex) {
				ex.printStackTrace();
				new DialogWithDetails(null, "Could not persist user settings.", ex).setVisible(true);
				System.exit(1);
			    }
			}
		    };
		    final StyledLoginScreen loginScreen = new StyledLoginScreen(loginScreenModel);
		    loginScreen.setVisible(true);

		} catch (final Exception ex) {
		    throw new IllegalStateException("Could not start application.", ex);
		} // try
	    }// run
	});
    }

    /**
     * Handles update of the client application. Exists the JVM in case an exceptional situaton.
     *
     * @param splash
     * @param loginScreen
     * @param restUtil
     * @param autoudate
     * @param restarter
     * @param logger
     */
    public static void handleAutoupdate(//
	    final SplashController splash,//
	    final StyledLoginScreen loginScreen,//
	    final RestClientUtil restUtil,//
	    final boolean autoudate,//
	    final IClientApplicationRestarter restarter,//
	    final Logger logger) {
	if (autoudate) {
	    try {
		logger.info("Running autoupdate...");
		new Updater(System.getProperty("user.dir"), new ReferenceDependancyController(restUtil), new ApplicationUpdateFeedback(splash, loginScreen, restarter)).run();
	    } catch (final Exception ex) {
		try {
		    SwingUtilitiesEx.invokeAndWaitIfPossible(new Runnable() {
			@Override
			public void run() {
			    final DialogWithDetails dialog = new DialogWithDetails(null, "Exception during update", ex);
			    dialog.setVisible(true);
			}
		    });
		} catch (final Exception e) {
		    e.printStackTrace();
		}
		logger.info("Exiting the system...");
		System.exit(1);
	    }
	}
    }

    /**
     * Constructs and displays the main application window with prior validation of user credentials.
     *
     * @param splash
     * @param loginScreen
     * @param restUtil
     * @param injector
     * @param emm -- entity master manager, which provides instantiation and caching of entity master review/edit windows.
     * @param mmBinder -- main menu binder, which is used for instantiation and configuration of the application main menu.
     * @param caption -- default application caption displayed in the title of the main window.
     * @param dim -- default main window dimension
     * @param icon -- application icon, which appears in the title of every application window.
     * @param logger
     */
    public static void showMainWindow(//
	    final SplashController splash, //
	    final StyledLoginScreen loginScreen, //
	    final RestClientUtil restUtil, //
	    final Injector injector, //
	    final EntityMasterManager emm, //
	    final IMainMenuBinder mmBinder, //
	    final IMainMenuStructureBuilder developmentMainMenuStructureBuilder, //
	    final String caption, //
	    final Dimension dim, //
	    final Image icon, //
	    final Logger logger) {
	logger.info("Showing main window...");
	SwingUtilitiesEx.invokeLater(new Runnable() {
	    @Override
	    public void run() {
		message("Loading application modules...", splash, loginScreen);
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		SwingUtilitiesEx.installNimbusLnFifPossible();

		if (restUtil.isUserConfigured()) {
		    createAndRunMainApplicationFrame(restUtil, splash, loginScreen, injector, emm, mmBinder, developmentMainMenuStructureBuilder, caption, dim, icon);
		}
	    }// run
	});
    }

    public static boolean isApplicationAlreadyRunning(final IApplicationSettings settings) {
	final ApplicationLaunchedChecker checker = new ApplicationLaunchedChecker(settings);
	return checker.isAnotherRunning();
    }

    /**
     * Updates UI by drawing the specified message either on the splash or a blocking pane of the login screen.
     */
    private static void message(final String msg, final SplashController splash, final StyledLoginScreen loginScreen) {
	SwingUtilitiesEx.invokeLater(new Runnable() {
	    @Override
	    public void run() {
		if (loginScreen != null) {
		    loginScreen.getBlockingPane().setText(msg);
		} else {
		    splash.drawSplashProgress(msg);
		}
	    }
	});
    }

    private static void createAndRunMainApplicationFrame(//
	    final RestClientUtil restUtil, //
	    final SplashController splash,//
	    final StyledLoginScreen loginScreen,//
	    final Injector injector,//
	    final EntityMasterManager emm,//
	    final IMainMenuBinder mmBuilder,//
	    final IMainMenuStructureBuilder developmentMainMenuStructureBuilder, //
	    final String caption,//
	    final Dimension dim,//
	    final Image icon) {
	message("Loading user configurations...", splash, loginScreen);
	// Define all menu items for the main application frame menu
	// compose all menu items into one vector
	@SuppressWarnings("rawtypes")
	final TreeMenuItem<?> rootMenuItem = new TreeMenuItem("root", "root panel");
	final BaseFrame mainApplicationFrame = new BaseFrame(caption, emm.getEntityMasterCache());
	@SuppressWarnings({ "rawtypes", "unchecked" })
	final UndockableTreeMenuWithTabs<?> menu = new UndockableTreeMenuWithTabs(rootMenuItem, new WordFilter(), injector.getInstance(IUserProvider.class), new BlockingIndefiniteProgressPane(mainApplicationFrame));

	final Thread thread = new Thread(new Runnable() {
	    @Override
	    public void run() {
		try {
		    buildMainMenu(restUtil, splash, loginScreen, injector, mmBuilder, developmentMainMenuStructureBuilder, rootMenuItem, menu);

		    SwingUtilitiesEx.invokeLater(new Runnable() {
			@Override
			public void run() {
			    // create main application frame and menu menu based on the above menu items
			    message("Instantiating application main window...", splash, loginScreen);

			    mainApplicationFrame.setIconImage(icon); // setIconImages(icons) should be used to pass icons of different sizes
			    mainApplicationFrame.setPreferredSize(dim);

			    mainApplicationFrame.add(new DefaultApplicationMainPanel(menu));
			    mainApplicationFrame.pack();

			    RefineryUtilities.centerFrameOnScreen(mainApplicationFrame);
			    mainApplicationFrame.setVisible(true);

			    if (loginScreen != null) {
				loginScreen.getBlockingPane().unlock();
				loginScreen.dispose();
			    }

			}
		    });
		} catch (final Exception ex) {
		    ex.printStackTrace();
		    new DialogWithDetails(null, "Could not launch application", ex).setVisible(true);
		    System.exit(1);
		}
	    }
	});
	thread.setDaemon(false);
	thread.start();
    }

    /**
     * Builds menu items.
     *
     * @param splash
     * @param loginScreen
     * @param injector
     * @param mmBinder
     * @param rootMenuItem
     * @param menu
     */
    private static void buildMainMenu(//
	    final RestClientUtil restUtil,//
	    final SplashController splash,//
	    final StyledLoginScreen loginScreen,//
	    final Injector injector, //
	    final IMainMenuBinder mmBinder,//
	    final IMainMenuStructureBuilder developmentMainMenuStructureBuilder, //
	    final TreeMenuItem<?> rootMenuItem, //
	    final UndockableTreeMenuWithTabs<?> menu) {

	final IApplicationSettings settings = injector.getInstance(IApplicationSettings.class);
	final IMainMenu mmController = injector.getInstance(IMainMenu.class);
	final IMainMenuItemController mmiController = injector.getInstance(IMainMenuItemController.class);
	final IEntityCentreConfigController eccController = injector.getInstance(IEntityCentreConfigController.class);
	final IEntityCentreAnalysisConfig ecacController = injector.getInstance(IEntityCentreAnalysisConfig.class);
	final IMainMenuItemInvisibilityController mmiiController = injector.getInstance(IMainMenuItemInvisibilityController.class);
	final EntityFactory factory = injector.getInstance(EntityFactory.class);

	if (Workflows.development.equals(Workflows.valueOf(settings.workflow()))) {
	    message("Updating user configurations (development mode)...", splash, loginScreen);
	    // persist NEW / UPDATED menu items (delete OBSOLETE items) into database
	    final MainMenuItemMixin mixin = new MainMenuItemMixin(mmController, mmiController, eccController, ecacController, mmiiController, factory);
	    mixin.setUser(restUtil.getUser());
	    mixin.updateMenuItemsWithDevelopmentOnes(developmentMainMenuStructureBuilder);
	}

	Period pd = null;
	DateTime st = null;
	message("Building user configurations...", splash, loginScreen);
	// get menu items using "remote" IMainMenuStructureBuilder (from database)
	st = new DateTime();

	final IMainMenuStructureBuilder persistedMainMenuStructureBuilder = new PersistedMainMenuStructureBuilder(mmController, mmiController, eccController, ecacController, mmiiController, factory, restUtil.getUser());
	final List<MainMenuItem> itemsFromCloud = persistedMainMenuStructureBuilder.build();

	pd = new Period(st, new DateTime());
	logger.info("Build using PersistedMainMenuStructureBuilder...done in " + pd.getSeconds() + " s " + pd.getMillis() + " ms");

	final ITreeMenuFactory menuFactory = new TreeMenuFactory(rootMenuItem, menu, injector);
	mmBinder.bindMainMenuItemFactories(menuFactory);

	st = new DateTime();
	menuFactory.build(itemsFromCloud);
	menu.getModel().getOriginModel().reload();

	pd = new Period(st, new DateTime());
	logger.info("Building menu items from cloud using menu factory...done in " + pd.getSeconds() + " s " + pd.getMillis() + " ms");

	menu.selectFirtItem();
    }

    /**
     * Make a HTTP request to the application server in order to check its availability.
     *
     * @param splash
     * @param props
     * @param VERSION
     * @return
     */
    public static RestClientUtil checkApplicationServerAvailability(final SplashController splash, final Properties props, final String VERSION) {
	splash.drawSplashProgress("Checking application server availability...");
	final RestClientUtil restUtil = new RestClientUtil(Protocol.HTTP, props.getProperty("host"), Integer.parseInt(props.getProperty("port")), VERSION, props.getProperty("username"));
	restUtil.warmUp();
	return restUtil;
    }

    /**
     * Reads application setting from the specified as args property file. The args should have exactly one element. Otherwise, file "aplication.properties" is used. The array args
     * is used as a convenience for calling method from main without additional processing such as checking args lengs etc.
     * <p>
     * The splash instance is used for providing user feedback.
     * <p>
     * An illegal argument exception is thrown of the application property file could not be found or read.
     *
     * @param args
     * @param splash
     * @return -- application settings
     */
    public static Properties readProperties(final String[] args, final SplashController splash) {
	splash.drawSplashProgress("Reading configuration settings...");

	final Properties props = new Properties();
	final String fileName = args.length == 1 ? args[0] : "application.properties";
	InputStream st = null;
	try {
	    st = new FileInputStream(fileName);
	    props.load(st);
	    return props;
	} catch (final Exception ex) {
	    ex.printStackTrace();
	    JOptionPane.showMessageDialog(null, String.format("Application property file %s could not be located or its values are not recognised.", fileName), "Application properties", JOptionPane.ERROR_MESSAGE);
	    throw new IllegalArgumentException(ex);
	} finally {
	    try {
		st.close();
	    } catch (final Exception e) {
		e.printStackTrace(); // can be ignored
	    }
	}
    }

    /**
     * Sets up UI preferences such as LaF tool tip manager dismiss delay etc.
     */
    public static void setupUiPreferences() {
	SwingUtilitiesEx.invokeLater(new Runnable() {
	    @Override
	    public void run() {
		SwingUtilitiesEx.installNimbusLnFifPossible();
		com.jidesoft.utils.Lm.verifyLicense("Fielden Management Services", "Rollingstock Management System", "xBMpKdqs3vWTvP9gxUR4jfXKGNz9uq52");
		LookAndFeelFactory.installJideExtension();

		ToolTipManager.sharedInstance().setDismissDelay(1000 * 300);
	    }
	});
    }

}
