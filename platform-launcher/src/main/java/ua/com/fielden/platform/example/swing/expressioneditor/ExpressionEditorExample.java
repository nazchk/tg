package ua.com.fielden.platform.example.swing.expressioneditor;

import java.awt.Dimension;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.cfg.Configuration;

import ua.com.fielden.platform.application.AbstractUiApplication;
import ua.com.fielden.platform.branding.SplashController;
import ua.com.fielden.platform.dao.MappingExtractor;
import ua.com.fielden.platform.domaintree.centre.impl.CentreDomainTreeManagerAndEnhancer;
import ua.com.fielden.platform.entity.factory.EntityFactory;
import ua.com.fielden.platform.example.entities.Vehicle;
import ua.com.fielden.platform.example.ioc.ExampleRmaHibernateModule;
import ua.com.fielden.platform.ioc.ApplicationInjectorFactory;
import ua.com.fielden.platform.persistence.HibernateUtil;
import ua.com.fielden.platform.persistence.ProxyInterceptor;
import ua.com.fielden.platform.serialisation.ClientSerialiser;
import ua.com.fielden.platform.swing.review.wizard.tree.editor.DomainTreeEditorModel;
import ua.com.fielden.platform.swing.review.wizard.tree.editor.DomainTreeEditorView;
import ua.com.fielden.platform.swing.utils.SimpleLauncher;
import ua.com.fielden.platform.swing.utils.SwingUtilitiesEx;

import com.google.inject.Injector;
import com.jidesoft.plaf.LookAndFeelFactory;

public class ExpressionEditorExample extends AbstractUiApplication {

    private EntityFactory entityFactory;

    @Override
    protected void beforeUiExposure(final String[] args, final SplashController splashController) throws Throwable {
	SwingUtilitiesEx.installNimbusLnFifPossible();
	com.jidesoft.utils.Lm.verifyLicense("Fielden Management Services", "Rollingstock Management System", "xBMpKdqs3vWTvP9gxUR4jfXKGNz9uq52");
	LookAndFeelFactory.installJideExtension();

	//initiating entity factory
	final ProxyInterceptor interceptor = new ProxyInterceptor();
	final HibernateUtil hibernateUtil = new HibernateUtil(interceptor, new Configuration().configure("hibernate4example.cfg.xml"));
	final ExampleRmaHibernateModule hibernateModule = new ExampleRmaHibernateModule(hibernateUtil.getSessionFactory(), new MappingExtractor(hibernateUtil.getConfiguration()));
	final Injector injector = new ApplicationInjectorFactory().add(hibernateModule).getInjector();
	entityFactory = injector.getInstance(EntityFactory.class);
	interceptor.setFactory(entityFactory);
    }

    @Override
    protected void exposeUi(final String[] args, final SplashController splashController) throws Throwable {
	final Set<Class<?>> rootTypes = new HashSet<Class<?>>();
	rootTypes.add(Vehicle.class);
	final CentreDomainTreeManagerAndEnhancer cdtme = new CentreDomainTreeManagerAndEnhancer(new ClientSerialiser(entityFactory), rootTypes);
	final DomainTreeEditorView<Vehicle> wizard = new DomainTreeEditorView<Vehicle>(new DomainTreeEditorModel<Vehicle>(entityFactory, cdtme, Vehicle.class));
	wizard.setPreferredSize(new Dimension(640,800));
	SimpleLauncher.show("Expression editor example", wizard);
    }

    public static void main(final String[] args) {
	new ExpressionEditorExample().launch(args);
    }



}
