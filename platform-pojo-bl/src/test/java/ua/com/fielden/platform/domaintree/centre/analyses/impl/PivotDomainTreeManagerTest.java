package ua.com.fielden.platform.domaintree.centre.analyses.impl;

import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import ua.com.fielden.platform.domaintree.centre.ICentreDomainTreeManager.ICentreDomainTreeManagerAndEnhancer;
import ua.com.fielden.platform.domaintree.centre.analyses.IPivotDomainTreeManager;
import ua.com.fielden.platform.domaintree.centre.impl.CentreDomainTreeManagerAndEnhancer;
import ua.com.fielden.platform.domaintree.testing.MasterEntity;


/**
 * A test for {@link AbstractAnalysisDomainTreeManager}.
 *
 * @author TG Team
 *
 */
public class PivotDomainTreeManagerTest extends AbstractAnalysisDomainTreeManagerTest {
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////// Test initialisation ///////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected IPivotDomainTreeManager dtm() {
	return (IPivotDomainTreeManager) just_a_dtm();
    }

    @BeforeClass
    public static void initDomainTreeTest() throws Exception {
	initialiseDomainTreeTest(PivotDomainTreeManagerTest.class);
    }

    public static Object createDtm_for_PivotDomainTreeManagerTest() {
	return new PivotDomainTreeManager(serialiser(), createRootTypes_for_PivotDomainTreeManagerTest());
    }

    public static Object createIrrelevantDtm_for_PivotDomainTreeManagerTest() {
	final ICentreDomainTreeManagerAndEnhancer dtm = new CentreDomainTreeManagerAndEnhancer(serialiser(), createRootTypes_for_PivotDomainTreeManagerTest());
	enhanceManagerWithBasicCalculatedProperties(dtm);
	return dtm;
    }

    protected static Set<Class<?>> createRootTypes_for_PivotDomainTreeManagerTest() {
	final Set<Class<?>> rootTypes = new HashSet<Class<?>>(createRootTypes_for_AbstractAnalysisDomainTreeManagerTest());
	return rootTypes;
    }

    public static void manageTestingDTM_for_PivotDomainTreeManagerTest(final Object obj) {
	manageTestingDTM_for_AbstractAnalysisDomainTreeManagerTest(obj);
    }

    public static void performAfterDeserialisationProcess_for_PivotDomainTreeManagerTest(final Object obj) {
	performAfterDeserialisationProcess_for_AbstractAnalysisDomainTreeManagerTest(obj);
    }

    public static void assertInnerCrossReferences_for_PivotDomainTreeManagerTest(final Object dtm) {
	assertInnerCrossReferences_for_AbstractAnalysisDomainTreeManagerTest(dtm);
    }

    public static String [] fieldWhichReferenceShouldNotBeDistictButShouldBeEqual_for_PivotDomainTreeManagerTest() {
	return fieldWhichReferenceShouldNotBeDistictButShouldBeEqual_for_AbstractAnalysisDomainTreeManagerTest();
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////// End of Test initialisation ////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void test_that_unused_properties_actions_for_both_ticks_cause_exceptions_for_all_specific_logic() {
	final String message = "Unused property should cause IllegalArgument exception.";

	allLevels(new IAction() {
	    public void action(final String name) {
		// get/set width
		try {
		    dtm().getFirstTick().getWidth(MasterEntity.class, name);
		    fail(message);
		} catch (final IllegalArgumentException e) {
		}
		try {
		    dtm().getFirstTick().setWidth(MasterEntity.class, name, 85);
		    fail(message);
		} catch (final IllegalArgumentException e) {
		}
	    }
	}, "excludedManuallyProp", "dateProp", "integerProp", "booleanProp", "intAggExprProp");

	allLevels(new IAction() {
	    public void action(final String name) {
		// get/set width
		try {
		    dtm().getSecondTick().getWidth(MasterEntity.class, name);
		    fail(message);
		} catch (final IllegalArgumentException e) {
		}
		try {
		    dtm().getSecondTick().setWidth(MasterEntity.class, name, 85);
		    fail(message);
		} catch (final IllegalArgumentException e) {
		}
	    }
	}, "excludedManuallyProp", "dateProp", "integerProp", "booleanProp", "intAggExprProp");
    }

    @Test
    public void test_that_Widths_for_first_tick_are_default_for_the_first_time_and_can_be_altered_and_are_treated_as_single_column() {
	final String property = "booleanProp";
	final String property2 = "entityProp.booleanProp";
	final String property3 = "entityProp.entityProp.booleanProp";

	allLevelsWithoutCollections(new IAction() {
	    public void action(final String name) {
		dtm().getFirstTick().check(MasterEntity.class, name, true);
		dtm().getFirstTick().use(MasterEntity.class, name, true);
	    }
	}, property);

	// There are three "used" properties "booleanProp", "entityProp.booleanProp", "entityProp.entityProp.booleanProp".
	// They should be used as a couple for setting / determining a width.

	// THE FIRST TIME -- returns DEFAULT VALUES //
	// default value should be 80
	checkOrSetMethodValuesForOneLevel(80, property, dtm().getFirstTick(), "getWidth");
	checkOrSetMethodValuesForOneLevel(80, property2, dtm().getFirstTick(), "getWidth");
	checkOrSetMethodValuesForOneLevel(80, property3, dtm().getFirstTick(), "getWidth");

	checkOrSetMethodValuesForOneLevel(85, property, dtm().getRepresentation().getFirstTick(), "setWidthByDefault", int.class);
	checkOrSetMethodValuesForOneLevel(85, property, dtm().getFirstTick(), "getWidth");
	checkOrSetMethodValuesForOneLevel(85, property2, dtm().getFirstTick(), "getWidth");
	checkOrSetMethodValuesForOneLevel(85, property3, dtm().getFirstTick(), "getWidth");

	checkOrSetMethodValuesForOneLevel(87, property2, dtm().getRepresentation().getFirstTick(), "setWidthByDefault", int.class);
	checkOrSetMethodValuesForOneLevel(87, property, dtm().getFirstTick(), "getWidth");
	checkOrSetMethodValuesForOneLevel(87, property2, dtm().getFirstTick(), "getWidth");
	checkOrSetMethodValuesForOneLevel(87, property3, dtm().getFirstTick(), "getWidth");

	// Alter and check //
	checkOrSetMethodValuesForOneLevel(95, property, dtm().getFirstTick(), "setWidth", int.class);

	checkOrSetMethodValuesForOneLevel(95, property, dtm().getFirstTick(), "getWidth");
	checkOrSetMethodValuesForOneLevel(95, property2, dtm().getFirstTick(), "getWidth");
	checkOrSetMethodValuesForOneLevel(95, property3, dtm().getFirstTick(), "getWidth");

	checkOrSetMethodValuesForOneLevel(97, property3, dtm().getFirstTick(), "setWidth", int.class);

	checkOrSetMethodValuesForOneLevel(97, property, dtm().getFirstTick(), "getWidth");
	checkOrSetMethodValuesForOneLevel(97, property2, dtm().getFirstTick(), "getWidth");
	checkOrSetMethodValuesForOneLevel(97, property3, dtm().getFirstTick(), "getWidth");
    }

    @Test
    public void test_that_Widths_for_second_tick_are_default_for_the_first_time_and_can_be_altered() {
	final String property = "intAggExprProp";

	oneLevel(new IAction() {
	    public void action(final String name) {
		dtm().getSecondTick().check(MasterEntity.class, name, true);
		dtm().getSecondTick().use(MasterEntity.class, name, true);
	    }
	}, property);

	// THE FIRST TIME -- returns DEFAULT VALUES //
	// default value should be 80
	checkOrSetMethodValuesForOneLevel(80, property, dtm().getSecondTick(), "getWidth");
	checkOrSetMethodValuesForOneLevel(85, property, dtm().getRepresentation().getSecondTick(), "setWidthByDefault", int.class);
	checkOrSetMethodValuesForOneLevel(85, property, dtm().getSecondTick(), "getWidth");

	// Alter and check //
	checkOrSetMethodValuesForOneLevel(95, property, dtm().getSecondTick(), "setWidth", int.class);

	checkOrSetMethodValuesForOneLevel(95, property, dtm().getSecondTick(), "getWidth");
    }

    @Override
    public void test_that_PropertyUsageListeners_work() {
    }

    @Override
    public void test_that_PropertyOrderingListeners_work() {
    }
}
