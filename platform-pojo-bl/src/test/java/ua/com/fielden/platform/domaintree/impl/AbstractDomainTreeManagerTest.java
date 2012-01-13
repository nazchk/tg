package ua.com.fielden.platform.domaintree.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import ua.com.fielden.platform.domaintree.ICalculatedProperty;
import ua.com.fielden.platform.domaintree.ICalculatedProperty.CalculatedPropertyCategory;
import ua.com.fielden.platform.domaintree.IDomainTreeManager;
import ua.com.fielden.platform.domaintree.IDomainTreeManager.IDomainTreeManagerAndEnhancer;
import ua.com.fielden.platform.domaintree.testing.DomainTreeManagerAndEnhancer1;
import ua.com.fielden.platform.domaintree.testing.MasterEntity;
import ua.com.fielden.platform.domaintree.testing.MasterEntityForIncludedPropertiesLogic;
import ua.com.fielden.platform.domaintree.testing.MasterEntityWithUnionForIncludedPropertiesLogic;
import ua.com.fielden.platform.types.Money;

/**
 * A test for {@link AbstractDomainTreeManager}.
 *
 * @author TG Team
 *
 */
public class AbstractDomainTreeManagerTest extends AbstractDomainTreeTest {
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////// Test initialisation ///////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Creates root types.
     *
     * @return
     */
    protected static Set<Class<?>> createRootTypes_for_AbstractDomainTreeManagerTest() {
	final Set<Class<?>> rootTypes = createRootTypes_for_AbstractDomainTreeTest();
	rootTypes.add(MasterEntityForIncludedPropertiesLogic.class);
	rootTypes.add(MasterEntityWithUnionForIncludedPropertiesLogic.class);
	return rootTypes;
    }

    /**
     * Provides a testing configuration for the manager.
     *
     * @param dtm
     */
    protected static void manageTestingDTM_for_AbstractDomainTreeManagerTest(final IDomainTreeManager dtm) {
	manageTestingDTM_for_AbstractDomainTreeTest(dtm);

	dtm.getFirstTick().checkedProperties(MasterEntity.class);
	dtm.getSecondTick().checkedProperties(MasterEntity.class);
    }

    @BeforeClass
    public static void initDomainTreeTest() {
	final IDomainTreeManagerAndEnhancer dtm = new DomainTreeManagerAndEnhancer1(serialiser(), createRootTypes_for_AbstractDomainTreeManagerTest());
	manageTestingDTM_for_AbstractDomainTreeManagerTest(dtm);
	setDtmArray(serialiser().serialise(dtm));
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////// End of Test initialisation ////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    protected void checkSomeProps(final IDomainTreeManager dtm) {
	allLevels(new IAction() {
	    public void action(final String name) {
		dtm.getSecondTick().check(MasterEntity.class, name, true);
	    }
	}, "checkedUntouchedProp");
	allLevels(new IAction() {
	    public void action(final String name) {
		dtm.getSecondTick().check(MasterEntity.class, name, true);
	    }
	}, "mutatedWithFunctionsProp");
    }

    protected void checkIllegally(final String name, final String message) {
	try {
	    dtm().getFirstTick().check(MasterEntity.class, name, true);
	    fail(message);
	} catch (final IllegalArgumentException e) {
	}
    }

    protected void isCheck_equals_to_state(final String name, final String message, final boolean state) {
	assertEquals(message, state, dtm().getFirstTick().isChecked(MasterEntity.class, name));
    }

    protected void uncheckLegally(final String name, final String message) {
	try {
	    dtm().getFirstTick().check(MasterEntity.class, name, false);
	    dtm().getFirstTick().check(MasterEntity.class, name, true);
	} catch (final Exception e) {
	    fail(message + " Cause = [" + e.getMessage() + "]");
	}
    }

    protected void checkLegally(final String name, final String message) {
	try {
	    dtm().getFirstTick().check(MasterEntity.class, name, true);
	    dtm().getFirstTick().check(MasterEntity.class, name, false);
	} catch (final Exception e) {
	    fail(message + " Cause = [" + e.getMessage() + "]");
	}
    }

    protected void isCheckIllegally(final String name, final String message) {
	try {
	    dtm().getFirstTick().isChecked(MasterEntity.class, name);
	    fail(message);
	} catch (final IllegalArgumentException e) {
	}
    }

    ////////////////////////////////////////////////////////////////
    ////////////////////// 1. Manage state legitimacy (CHECK) //////
    ////////////////////////////////////////////////////////////////
    @Test
    public void test_that_CHECK_state_managing_for_excluded_properties_is_not_permitted() {
	final String message = "Excluded property should cause illegal argument exception while changing its state.";
	allLevels(new IAction() {
	    public void action(final String name) {
		checkIllegally(name, message);
	    }
	}, "excludedManuallyProp");
    }

    @Test
    public void test_that_CHECK_state_managing_for_disabled_properties_is_not_permitted() { // (disabled == immutably checked or unchecked)
	final String message = "Immutably checked property (disabled) should cause illegal argument exception while changing its state.";
	allLevels(new IAction() {
	    public void action(final String name) {
		checkIllegally(name, message);
	    }
	}, "checkedManuallyProp");

	final String message1 = "Immutably unchecked property (disabled) should cause illegal argument exception while changing its state.";
	allLevels(new IAction() {
	    public void action(final String name) {
		checkIllegally(name, message1);
	    }
	}, "disabledManuallyProp");
    }

    @Test
    public void test_that_CHECK_state_managing_for_rest_properties_is_permitted() {
	final String message = "Not disabled and not excluded property should NOT cause any exception while changing its state.";
	allLevelsWithoutCollections(new IAction() {
	    public void action(final String name) {
		uncheckLegally(name, message);
	    }
	}, "mutablyCheckedProp");
    }

    ///////////////////////////////////////////////////////////////////////
    ////////////////////// 2. Ask state checking / legitimacy (CHECK) /////
    ///////////////////////////////////////////////////////////////////////
    @Test
    public void test_that_CHECK_state_asking_for_excluded_properties_is_not_permitted() {
	final String message = "Excluded property should cause illegal argument exception while asking its state.";
	allLevels(new IAction() {
	    public void action(final String name) {
		isCheckIllegally(name, message);
	    }
	}, "excludedManuallyProp");
    }

    @Test
    public void test_that_CHECK_state_for_disabled_properties_is_correct() { // (disabled == immutably checked or unchecked)
	allLevels(new IAction() {
	    public void action(final String name) {
		final String message = "Immutably checked (disabled) property [" + name + "] should return 'true' CHECK state.";
		isCheck_equals_to_state(name, message, true);
	    }
	}, "checkedManuallyProp");

	allLevels(new IAction() {
	    public void action(final String name) {
		final String message = "Immutably unchecked (disabled) property [" + name + "] should return 'false' CHECK state.";
		isCheck_equals_to_state(name, message, false);
	    }
	}, "disabledManuallyProp");
    }

    @Test
    public void test_that_CHECK_state_for_untouched_properties_is_correct() { // correct state should be "unchecked"
	final String message = "Untouched property (also not muted in representation) should return 'false' CHECK state.";
	allLevels(new IAction() {
	    public void action(final String name) {
		isCheck_equals_to_state(name, message, false);
	    }
	}, "bigDecimalProp");
    }

    @Test
    public void test_that_CHECK_state_for_mutated_by_isChecked_method_properties_is_desired_and_after_manual_mutation_is_actually_mutated() {
	// checked properties, defined in isChecked() contract
	allLevels(new IAction() {
	    public void action(final String name) {
		final String message = "Checked property [" + name + "], defined in isChecked() contract, should return 'true' CHECK state, and after manual mutation its state should be desired.";
		isCheck_equals_to_state(name, message, true);
		dtm().getFirstTick().check(MasterEntity.class, name, false);
		isCheck_equals_to_state(name, message, false);
		dtm().getFirstTick().check(MasterEntity.class, name, true);
		isCheck_equals_to_state(name, message, true);
	    }
	}, "mutablyCheckedProp");
    }

    @Test
    public void test_that_CHECK_state_for_mutated_by_Check_method_properties_is_actually_mutated() {
	// checked properties, mutated_by_Check_method
	allLevels(new IAction() {
	    public void action(final String name) {
		final String message = "Checked property [" + name + "], defined in isChecked() contract, should return 'true' CHECK state, and after manual mutation its state should be desired.";
		dtm().getFirstTick().check(MasterEntity.class, name, false);
		isCheck_equals_to_state(name, message, false);
		dtm().getFirstTick().check(MasterEntity.class, name, true);
		isCheck_equals_to_state(name, message, true);
	    }
	}, "mutablyCheckedProp");
    }

    @Test
    public void test_that_CHECKed_properties_order_is_correct() throws Exception {
	checkSomeProps(dtm());

	// at first the manager will be initialised the first time and its "included" and then "checked" props will be initialised (heavy operation)
	assertEquals("The checked properties are incorrect.", Arrays.asList("mutablyCheckedProp", "entityProp.mutablyCheckedProp", "entityProp.entityProp.mutablyCheckedProp", "entityProp.entityProp.checkedManuallyProp", "entityProp.collection.mutablyCheckedProp", "entityProp.collection.checkedManuallyProp", "entityProp.checkedManuallyProp", "entityProp.resultOnlyProp.mutablyCheckedProp", "collection.mutablyCheckedProp", "collection.entityProp.mutablyCheckedProp", "collection.collection.mutablyCheckedProp", "collection.checkedManuallyProp", "collection.resultOnlyProp.mutablyCheckedProp", "checkedManuallyProp", "resultOnlyProp.mutablyCheckedProp", "resultOnlyProp.entityProp.mutablyCheckedProp", "resultOnlyProp.collection.mutablyCheckedProp", "resultOnlyProp.excludedManuallyProp.mutablyCheckedProp", "resultOnlyProp.resultOnlyProp.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.entityProp.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.collection.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.excludedManuallyProp.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.resultOnlyProp.mutablyCheckedProp", "entityProp.collection.slaveEntityProp.mutablyCheckedProp", "entityProp.collection.slaveEntityProp.checkedManuallyProp"), dtm().getFirstTick().checkedProperties(MasterEntity.class));

	// next -- lightweight operation -- no loading will be performed
	assertEquals("The checked properties are incorrect.", Arrays.asList("mutablyCheckedProp", "entityProp.mutablyCheckedProp", "entityProp.entityProp.mutablyCheckedProp", "entityProp.entityProp.checkedManuallyProp", "entityProp.collection.mutablyCheckedProp", "entityProp.collection.checkedManuallyProp", "entityProp.checkedManuallyProp", "entityProp.resultOnlyProp.mutablyCheckedProp", "collection.mutablyCheckedProp", "collection.entityProp.mutablyCheckedProp", "collection.collection.mutablyCheckedProp", "collection.checkedManuallyProp", "collection.resultOnlyProp.mutablyCheckedProp", "checkedManuallyProp", "resultOnlyProp.mutablyCheckedProp", "resultOnlyProp.entityProp.mutablyCheckedProp", "resultOnlyProp.collection.mutablyCheckedProp", "resultOnlyProp.excludedManuallyProp.mutablyCheckedProp", "resultOnlyProp.resultOnlyProp.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.entityProp.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.collection.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.excludedManuallyProp.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.resultOnlyProp.mutablyCheckedProp", "entityProp.collection.slaveEntityProp.mutablyCheckedProp", "entityProp.collection.slaveEntityProp.checkedManuallyProp"), dtm().getFirstTick().checkedProperties(MasterEntity.class));
	// serialise and deserialise and then check the order of "checked properties"
	final byte[] array = getSerialiser().serialise(dtm());
	final IDomainTreeManagerAndEnhancer copy = getSerialiser().deserialise(array, IDomainTreeManagerAndEnhancer.class);
	assertEquals("The checked properties are incorrect.", Arrays.asList("mutablyCheckedProp", "entityProp.mutablyCheckedProp", "entityProp.entityProp.mutablyCheckedProp", "entityProp.entityProp.checkedManuallyProp", "entityProp.collection.mutablyCheckedProp", "entityProp.collection.checkedManuallyProp", "entityProp.checkedManuallyProp", "entityProp.resultOnlyProp.mutablyCheckedProp", "collection.mutablyCheckedProp", "collection.entityProp.mutablyCheckedProp", "collection.collection.mutablyCheckedProp", "collection.checkedManuallyProp", "collection.resultOnlyProp.mutablyCheckedProp", "checkedManuallyProp", "resultOnlyProp.mutablyCheckedProp", "resultOnlyProp.entityProp.mutablyCheckedProp", "resultOnlyProp.collection.mutablyCheckedProp", "resultOnlyProp.excludedManuallyProp.mutablyCheckedProp", "resultOnlyProp.resultOnlyProp.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.entityProp.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.collection.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.excludedManuallyProp.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.resultOnlyProp.mutablyCheckedProp", "entityProp.collection.slaveEntityProp.mutablyCheckedProp", "entityProp.collection.slaveEntityProp.checkedManuallyProp"), copy.getFirstTick().checkedProperties(MasterEntity.class));
	// simple lightweight example
	assertEquals("The checked properties are incorrect.", Collections.emptyList(), dtm().getFirstTick().checkedProperties(MasterEntityForIncludedPropertiesLogic.class));
    }

    @Test
    public void test_that_CHECKed_properties_order_is_correct_and_can_be_altered() throws Exception {
	checkSomeProps(dtm());

	// at first the manager will be initialised the first time and its "included" and then "checked" props will be initialised (heavy operation)
	assertEquals("The checked properties are incorrect.", Arrays.asList("mutablyCheckedProp", "entityProp.mutablyCheckedProp", "entityProp.entityProp.mutablyCheckedProp", "entityProp.entityProp.checkedManuallyProp", "entityProp.collection.mutablyCheckedProp", "entityProp.collection.checkedManuallyProp", "entityProp.checkedManuallyProp", "entityProp.resultOnlyProp.mutablyCheckedProp", "collection.mutablyCheckedProp", "collection.entityProp.mutablyCheckedProp", "collection.collection.mutablyCheckedProp", "collection.checkedManuallyProp", "collection.resultOnlyProp.mutablyCheckedProp", "checkedManuallyProp", "resultOnlyProp.mutablyCheckedProp", "resultOnlyProp.entityProp.mutablyCheckedProp", "resultOnlyProp.collection.mutablyCheckedProp", "resultOnlyProp.excludedManuallyProp.mutablyCheckedProp", "resultOnlyProp.resultOnlyProp.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.entityProp.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.collection.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.excludedManuallyProp.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.resultOnlyProp.mutablyCheckedProp", "entityProp.collection.slaveEntityProp.mutablyCheckedProp", "entityProp.collection.slaveEntityProp.checkedManuallyProp"), dtm().getFirstTick().checkedProperties(MasterEntity.class));

	// alter checked properties by checking / un-checking some property
	dtm().getFirstTick().check(MasterEntity.class, "integerProp", true);
	dtm().getFirstTick().check(MasterEntity.class, "entityProp.mutablyCheckedProp", false);

	// next -- lightweight operation -- no loading will be performed
	assertEquals("The checked properties are incorrect.", Arrays.asList("mutablyCheckedProp", "entityProp.entityProp.mutablyCheckedProp", "entityProp.entityProp.checkedManuallyProp", "entityProp.collection.mutablyCheckedProp", "entityProp.collection.checkedManuallyProp", "entityProp.checkedManuallyProp", "entityProp.resultOnlyProp.mutablyCheckedProp", "collection.mutablyCheckedProp", "collection.entityProp.mutablyCheckedProp", "collection.collection.mutablyCheckedProp", "collection.checkedManuallyProp", "collection.resultOnlyProp.mutablyCheckedProp", "checkedManuallyProp", "resultOnlyProp.mutablyCheckedProp", "resultOnlyProp.entityProp.mutablyCheckedProp", "resultOnlyProp.collection.mutablyCheckedProp", "resultOnlyProp.excludedManuallyProp.mutablyCheckedProp", "resultOnlyProp.resultOnlyProp.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.entityProp.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.collection.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.excludedManuallyProp.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.resultOnlyProp.mutablyCheckedProp", "entityProp.collection.slaveEntityProp.mutablyCheckedProp", "entityProp.collection.slaveEntityProp.checkedManuallyProp", "integerProp"), dtm().getFirstTick().checkedProperties(MasterEntity.class));
	// serialise and deserialise and then check the order of "checked properties"
	final byte[] array = getSerialiser().serialise(dtm());
	final IDomainTreeManagerAndEnhancer copy = getSerialiser().deserialise(array, IDomainTreeManagerAndEnhancer.class);
	assertEquals("The checked properties are incorrect.", Arrays.asList("mutablyCheckedProp", "entityProp.entityProp.mutablyCheckedProp", "entityProp.entityProp.checkedManuallyProp", "entityProp.collection.mutablyCheckedProp", "entityProp.collection.checkedManuallyProp", "entityProp.checkedManuallyProp", "entityProp.resultOnlyProp.mutablyCheckedProp", "collection.mutablyCheckedProp", "collection.entityProp.mutablyCheckedProp", "collection.collection.mutablyCheckedProp", "collection.checkedManuallyProp", "collection.resultOnlyProp.mutablyCheckedProp", "checkedManuallyProp", "resultOnlyProp.mutablyCheckedProp", "resultOnlyProp.entityProp.mutablyCheckedProp", "resultOnlyProp.collection.mutablyCheckedProp", "resultOnlyProp.excludedManuallyProp.mutablyCheckedProp", "resultOnlyProp.resultOnlyProp.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.entityProp.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.collection.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.excludedManuallyProp.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.resultOnlyProp.mutablyCheckedProp", "entityProp.collection.slaveEntityProp.mutablyCheckedProp", "entityProp.collection.slaveEntityProp.checkedManuallyProp", "integerProp"), copy.getFirstTick().checkedProperties(MasterEntity.class));
    }

    @Test
    public void test_that_CHECKed_properties_Move_Swap_operations_work() throws Exception {
	checkSomeProps(dtm());

	// at first the manager will be initialised the first time and its "included" and then "checked" props will be initialised (heavy operation)
	// alter the order of properties
	dtm().getFirstTick().swap(MasterEntity.class, "mutablyCheckedProp", "entityProp.mutablyCheckedProp");
	assertEquals("The checked properties are incorrect.", Arrays.asList("entityProp.mutablyCheckedProp", "mutablyCheckedProp", "entityProp.entityProp.mutablyCheckedProp", "entityProp.entityProp.checkedManuallyProp", "entityProp.collection.mutablyCheckedProp", "entityProp.collection.checkedManuallyProp", "entityProp.checkedManuallyProp", "entityProp.resultOnlyProp.mutablyCheckedProp", "collection.mutablyCheckedProp", "collection.entityProp.mutablyCheckedProp", "collection.collection.mutablyCheckedProp", "collection.checkedManuallyProp", "collection.resultOnlyProp.mutablyCheckedProp", "checkedManuallyProp", "resultOnlyProp.mutablyCheckedProp", "resultOnlyProp.entityProp.mutablyCheckedProp", "resultOnlyProp.collection.mutablyCheckedProp", "resultOnlyProp.excludedManuallyProp.mutablyCheckedProp", "resultOnlyProp.resultOnlyProp.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.entityProp.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.collection.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.excludedManuallyProp.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.resultOnlyProp.mutablyCheckedProp", "entityProp.collection.slaveEntityProp.mutablyCheckedProp", "entityProp.collection.slaveEntityProp.checkedManuallyProp"), dtm().getFirstTick().checkedProperties(MasterEntity.class));
	dtm().getFirstTick().swap(MasterEntity.class, "mutablyCheckedProp", "entityProp.entityProp.mutablyCheckedProp");
	assertEquals("The checked properties are incorrect.", Arrays.asList("entityProp.mutablyCheckedProp", "entityProp.entityProp.mutablyCheckedProp", "mutablyCheckedProp", "entityProp.entityProp.checkedManuallyProp", "entityProp.collection.mutablyCheckedProp", "entityProp.collection.checkedManuallyProp", "entityProp.checkedManuallyProp", "entityProp.resultOnlyProp.mutablyCheckedProp", "collection.mutablyCheckedProp", "collection.entityProp.mutablyCheckedProp", "collection.collection.mutablyCheckedProp", "collection.checkedManuallyProp", "collection.resultOnlyProp.mutablyCheckedProp", "checkedManuallyProp", "resultOnlyProp.mutablyCheckedProp", "resultOnlyProp.entityProp.mutablyCheckedProp", "resultOnlyProp.collection.mutablyCheckedProp", "resultOnlyProp.excludedManuallyProp.mutablyCheckedProp", "resultOnlyProp.resultOnlyProp.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.entityProp.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.collection.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.excludedManuallyProp.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.resultOnlyProp.mutablyCheckedProp", "entityProp.collection.slaveEntityProp.mutablyCheckedProp", "entityProp.collection.slaveEntityProp.checkedManuallyProp"), dtm().getFirstTick().checkedProperties(MasterEntity.class));
	dtm().getFirstTick().moveToTheEnd(MasterEntity.class, "mutablyCheckedProp");
	assertEquals("The checked properties are incorrect.", Arrays.asList("entityProp.mutablyCheckedProp", "entityProp.entityProp.mutablyCheckedProp", "entityProp.entityProp.checkedManuallyProp", "entityProp.collection.mutablyCheckedProp", "entityProp.collection.checkedManuallyProp", "entityProp.checkedManuallyProp", "entityProp.resultOnlyProp.mutablyCheckedProp", "collection.mutablyCheckedProp", "collection.entityProp.mutablyCheckedProp", "collection.collection.mutablyCheckedProp", "collection.checkedManuallyProp", "collection.resultOnlyProp.mutablyCheckedProp", "checkedManuallyProp", "resultOnlyProp.mutablyCheckedProp", "resultOnlyProp.entityProp.mutablyCheckedProp", "resultOnlyProp.collection.mutablyCheckedProp", "resultOnlyProp.excludedManuallyProp.mutablyCheckedProp", "resultOnlyProp.resultOnlyProp.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.entityProp.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.collection.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.excludedManuallyProp.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.resultOnlyProp.mutablyCheckedProp", "entityProp.collection.slaveEntityProp.mutablyCheckedProp", "entityProp.collection.slaveEntityProp.checkedManuallyProp", "mutablyCheckedProp"), dtm().getFirstTick().checkedProperties(MasterEntity.class));
	dtm().getFirstTick().move(MasterEntity.class, "mutablyCheckedProp", "entityProp.entityProp.mutablyCheckedProp");
	assertEquals("The checked properties are incorrect.", Arrays.asList("entityProp.mutablyCheckedProp", "mutablyCheckedProp", "entityProp.entityProp.mutablyCheckedProp", "entityProp.entityProp.checkedManuallyProp", "entityProp.collection.mutablyCheckedProp", "entityProp.collection.checkedManuallyProp", "entityProp.checkedManuallyProp", "entityProp.resultOnlyProp.mutablyCheckedProp", "collection.mutablyCheckedProp", "collection.entityProp.mutablyCheckedProp", "collection.collection.mutablyCheckedProp", "collection.checkedManuallyProp", "collection.resultOnlyProp.mutablyCheckedProp", "checkedManuallyProp", "resultOnlyProp.mutablyCheckedProp", "resultOnlyProp.entityProp.mutablyCheckedProp", "resultOnlyProp.collection.mutablyCheckedProp", "resultOnlyProp.excludedManuallyProp.mutablyCheckedProp", "resultOnlyProp.resultOnlyProp.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.entityProp.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.collection.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.excludedManuallyProp.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.resultOnlyProp.mutablyCheckedProp", "entityProp.collection.slaveEntityProp.mutablyCheckedProp", "entityProp.collection.slaveEntityProp.checkedManuallyProp"), dtm().getFirstTick().checkedProperties(MasterEntity.class));
	dtm().getFirstTick().move(MasterEntity.class, "mutablyCheckedProp", "entityProp.mutablyCheckedProp");
	assertEquals("The checked properties are incorrect.", Arrays.asList("mutablyCheckedProp", "entityProp.mutablyCheckedProp", "entityProp.entityProp.mutablyCheckedProp", "entityProp.entityProp.checkedManuallyProp", "entityProp.collection.mutablyCheckedProp", "entityProp.collection.checkedManuallyProp", "entityProp.checkedManuallyProp", "entityProp.resultOnlyProp.mutablyCheckedProp", "collection.mutablyCheckedProp", "collection.entityProp.mutablyCheckedProp", "collection.collection.mutablyCheckedProp", "collection.checkedManuallyProp", "collection.resultOnlyProp.mutablyCheckedProp", "checkedManuallyProp", "resultOnlyProp.mutablyCheckedProp", "resultOnlyProp.entityProp.mutablyCheckedProp", "resultOnlyProp.collection.mutablyCheckedProp", "resultOnlyProp.excludedManuallyProp.mutablyCheckedProp", "resultOnlyProp.resultOnlyProp.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.entityProp.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.collection.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.excludedManuallyProp.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.resultOnlyProp.mutablyCheckedProp", "entityProp.collection.slaveEntityProp.mutablyCheckedProp", "entityProp.collection.slaveEntityProp.checkedManuallyProp"), dtm().getFirstTick().checkedProperties(MasterEntity.class));
	dtm().getFirstTick().swap(MasterEntity.class, "mutablyCheckedProp", "entityProp.mutablyCheckedProp");
	assertEquals("The checked properties are incorrect.", Arrays.asList("entityProp.mutablyCheckedProp", "mutablyCheckedProp", "entityProp.entityProp.mutablyCheckedProp", "entityProp.entityProp.checkedManuallyProp", "entityProp.collection.mutablyCheckedProp", "entityProp.collection.checkedManuallyProp", "entityProp.checkedManuallyProp", "entityProp.resultOnlyProp.mutablyCheckedProp", "collection.mutablyCheckedProp", "collection.entityProp.mutablyCheckedProp", "collection.collection.mutablyCheckedProp", "collection.checkedManuallyProp", "collection.resultOnlyProp.mutablyCheckedProp", "checkedManuallyProp", "resultOnlyProp.mutablyCheckedProp", "resultOnlyProp.entityProp.mutablyCheckedProp", "resultOnlyProp.collection.mutablyCheckedProp", "resultOnlyProp.excludedManuallyProp.mutablyCheckedProp", "resultOnlyProp.resultOnlyProp.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.entityProp.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.collection.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.excludedManuallyProp.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.resultOnlyProp.mutablyCheckedProp", "entityProp.collection.slaveEntityProp.mutablyCheckedProp", "entityProp.collection.slaveEntityProp.checkedManuallyProp"), dtm().getFirstTick().checkedProperties(MasterEntity.class));
	// serialise and deserialise and then check the order of "checked properties"
	final byte[] array = getSerialiser().serialise(dtm());
	final IDomainTreeManagerAndEnhancer copy = getSerialiser().deserialise(array, IDomainTreeManagerAndEnhancer.class);
	assertEquals("The checked properties are incorrect.", Arrays.asList("entityProp.mutablyCheckedProp", "mutablyCheckedProp", "entityProp.entityProp.mutablyCheckedProp", "entityProp.entityProp.checkedManuallyProp", "entityProp.collection.mutablyCheckedProp", "entityProp.collection.checkedManuallyProp", "entityProp.checkedManuallyProp", "entityProp.resultOnlyProp.mutablyCheckedProp", "collection.mutablyCheckedProp", "collection.entityProp.mutablyCheckedProp", "collection.collection.mutablyCheckedProp", "collection.checkedManuallyProp", "collection.resultOnlyProp.mutablyCheckedProp", "checkedManuallyProp", "resultOnlyProp.mutablyCheckedProp", "resultOnlyProp.entityProp.mutablyCheckedProp", "resultOnlyProp.collection.mutablyCheckedProp", "resultOnlyProp.excludedManuallyProp.mutablyCheckedProp", "resultOnlyProp.resultOnlyProp.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.entityProp.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.collection.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.excludedManuallyProp.mutablyCheckedProp", "entityWithCompositeKeyProp.keyPartPropFromSlave.resultOnlyProp.mutablyCheckedProp", "entityProp.collection.slaveEntityProp.mutablyCheckedProp", "entityProp.collection.slaveEntityProp.checkedManuallyProp"), copy.getFirstTick().checkedProperties(MasterEntity.class));
    }

    @Test
    public void test_that_CHECKed_properties_Move_Swap_operations_doesnot_work_for_non_checked_properties() {
	// at first the manager will be initialised the first time and its "included" and then "checked" props will be initialised (heavy operation)
	// alter the order of properties
	try {
	    dtm().getFirstTick().swap(MasterEntity.class, "unknown-prop", "entityProp.mutablyCheckedProp");
	    fail("Non-existent or non-checked properties operation should fail.");
	} catch (final IllegalArgumentException e) {
	}
	try {
	    dtm().getFirstTick().swap(MasterEntity.class, "mutablyCheckedProp", "entityProp.doubleProp"); // second is unchecked
	    fail("Non-existent or non-checked properties operation should fail.");
	} catch (final IllegalArgumentException e) {
	}
	try {
	    dtm().getFirstTick().move(MasterEntity.class, "unknown-prop", "entityProp.mutablyCheckedProp");
	    fail("Non-existent or non-checked properties operation should fail.");
	} catch (final IllegalArgumentException e) {
	}
	try {
	    dtm().getFirstTick().move(MasterEntity.class, "mutablyCheckedProp", "entityProp.doubleProp"); // second is unchecked
	    fail("Non-existent or non-checked properties operation should fail.");
	} catch (final IllegalArgumentException e) {
	}
	try {
	    dtm().getFirstTick().moveToTheEnd(MasterEntity.class, "doubleProp"); // unchecked
	    fail("Non-existent or non-checked properties operation should fail.");
	} catch (final IllegalArgumentException e) {
	}
    }

    @Test
    public void test_that_domain_changes_are_correctly_reflected_in_CHECKed_properties() {
	assertEquals("Incorrect checked properties.", Collections.emptyList(), dtm().getFirstTick().checkedProperties(MasterEntityForIncludedPropertiesLogic.class));
	dtm().getEnhancer().addCalculatedProperty(new CalculatedProperty(MasterEntityForIncludedPropertiesLogic.class, "entityProp.prop1_mutablyCheckedProp", CalculatedPropertyCategory.EXPRESSION, "integerProp", Integer.class, "1 * 2", "Property 1", "desc"));
	dtm().getEnhancer().apply();
	assertEquals("Incorrect checked properties.", Arrays.asList("entityProp.prop1_mutablyCheckedProp"), dtm().getFirstTick().checkedProperties(MasterEntityForIncludedPropertiesLogic.class));
	dtm().getEnhancer().addCalculatedProperty(new CalculatedProperty(MasterEntityForIncludedPropertiesLogic.class, "entityProp.prop2_mutablyCheckedProp", CalculatedPropertyCategory.EXPRESSION, "integerProp", Integer.class, "1 * 2", "Property 1", "desc"));
	dtm().getEnhancer().apply();
	assertEquals("Incorrect checked properties.", Arrays.asList("entityProp.prop1_mutablyCheckedProp", "entityProp.prop2_mutablyCheckedProp"), dtm().getFirstTick().checkedProperties(MasterEntityForIncludedPropertiesLogic.class));
	dtm().getFirstTick().swap(MasterEntityForIncludedPropertiesLogic.class, "entityProp.prop1_mutablyCheckedProp", "entityProp.prop2_mutablyCheckedProp");
	assertEquals("Incorrect checked properties.", Arrays.asList("entityProp.prop2_mutablyCheckedProp", "entityProp.prop1_mutablyCheckedProp"), dtm().getFirstTick().checkedProperties(MasterEntityForIncludedPropertiesLogic.class));
	dtm().getEnhancer().removeCalculatedProperty(MasterEntityForIncludedPropertiesLogic.class, "entityProp.prop1_mutablyCheckedProp");
	dtm().getEnhancer().apply();
	assertEquals("Incorrect checked properties.", Arrays.asList("entityProp.prop2_mutablyCheckedProp"), dtm().getFirstTick().checkedProperties(MasterEntityForIncludedPropertiesLogic.class));
	dtm().getEnhancer().removeCalculatedProperty(MasterEntityForIncludedPropertiesLogic.class, "entityProp.prop2_mutablyCheckedProp");
	dtm().getEnhancer().apply();
	assertEquals("Incorrect checked properties.", Collections.emptyList(), dtm().getFirstTick().checkedProperties(MasterEntityForIncludedPropertiesLogic.class));
    }

    ///////////////////////////////////////////////////////////////////////
    ////////////////////// 3. Calculated properties ///////////////////////
    ///////////////////////////////////////////////////////////////////////

    @Test
    public void test_that_calculated_properties_work() throws Exception {
	/////////////// ADDING & MANAGING ///////////////
	// enhance domain with new calculated property
	dtm().getEnhancer().addCalculatedProperty(new CalculatedProperty(MasterEntity.class, "calcProp1", CalculatedPropertyCategory.EXPRESSION, "integerProp", Integer.class, "1 * 2", "title", "desc"));
	dtm().getEnhancer().apply();

	dtm().getRepresentation().excludeImmutably(MasterEntity.class, "calcProp1");
	assertTrue("The brand new calculated property should be excluded.", dtm().getRepresentation().isExcludedImmutably(MasterEntity.class, "calcProp1"));

	// enhance domain with new calculated property
	final String calcProp2 = "entityProp.calcProp2";
	dtm().getEnhancer().addCalculatedProperty(new CalculatedProperty(MasterEntity.class, calcProp2, CalculatedPropertyCategory.EXPRESSION, "moneyProp", Money.class, "1 * 2.5", "title", "desc"));
	dtm().getEnhancer().apply();

	dtm().getRepresentation().getFirstTick().disableImmutably(MasterEntity.class, calcProp2);
	assertTrue("The brand new calculated property should be excluded.", dtm().getRepresentation().isExcludedImmutably(MasterEntity.class, "calcProp1"));
	assertTrue("The brand new calculated property should be disabled.", dtm().getRepresentation().getFirstTick().isDisabledImmutably(MasterEntity.class, calcProp2));

	// enhance domain with new calculated property
	dtm().getEnhancer().addCalculatedProperty(new CalculatedProperty(MasterEntity.class, "calcProp3", CalculatedPropertyCategory.EXPRESSION, "moneyProp", Money.class, "1 * 2.5", "title", "desc"));
	dtm().getEnhancer().apply();

	dtm().getRepresentation().getSecondTick().checkImmutably(MasterEntity.class, "calcProp3");
	assertTrue("The brand new calculated property should be excluded.", dtm().getRepresentation().isExcludedImmutably(MasterEntity.class, "calcProp1"));
	assertTrue("The brand new calculated property should be disabled.", dtm().getRepresentation().getFirstTick().isDisabledImmutably(MasterEntity.class, calcProp2));
	assertTrue("The brand new calculated property should be immutable checked.", dtm().getRepresentation().getSecondTick().isCheckedImmutably(MasterEntity.class, "calcProp3"));

	// enhance domain with new calculated property
	dtm().getEnhancer().addCalculatedProperty(new CalculatedProperty(MasterEntity.class, "calcProp4", CalculatedPropertyCategory.EXPRESSION, "moneyProp", Money.class, "1 * 2.5", "title", "desc"));
	dtm().getEnhancer().apply();

	// enhance domain with new calculated property
	dtm().getEnhancer().addCalculatedProperty(new CalculatedProperty(MasterEntity.class, "calcProp5", CalculatedPropertyCategory.AGGREGATED_EXPRESSION, "bigDecimalProp", BigDecimal.class, "1 * 2.5", "title", "desc"));
	dtm().getEnhancer().apply();

	dtm().getSecondTick().check(MasterEntity.class, "calcProp5", true);
	assertTrue("The brand new calculated property should be excluded.", dtm().getRepresentation().isExcludedImmutably(MasterEntity.class, "calcProp1"));
	assertTrue("The brand new calculated property should be disabled.", dtm().getRepresentation().getFirstTick().isDisabledImmutably(MasterEntity.class, calcProp2));
	assertTrue("The brand new calculated property should be immutable checked.", dtm().getRepresentation().getSecondTick().isCheckedImmutably(MasterEntity.class, "calcProp3"));
	assertTrue("The brand new calculated property should be checked.", dtm().getSecondTick().isChecked(MasterEntity.class, "calcProp5"));

	/////////////// MODIFYING & MANAGING ///////////////
	dtm().getEnhancer().getCalculatedProperty(MasterEntity.class, "calcProp1").setTitle("new title");
	dtm().getEnhancer().getCalculatedProperty(MasterEntity.class, "calcProp1").setDesc("new title");
	dtm().getEnhancer().getCalculatedProperty(MasterEntity.class, "calcProp1").setExpression("56 * 78 / [integerProp]");

	dtm().getEnhancer().getCalculatedProperty(MasterEntity.class, "calcProp1").setResultType(BigInteger.class);
	dtm().getEnhancer().getCalculatedProperty(MasterEntity.class, calcProp2).setResultType(BigDecimal.class);
	dtm().getEnhancer().getCalculatedProperty(MasterEntity.class, "calcProp3").setResultType(BigDecimal.class);
	dtm().getEnhancer().getCalculatedProperty(MasterEntity.class, "calcProp4").setResultType(BigDecimal.class);
	dtm().getEnhancer().getCalculatedProperty(MasterEntity.class, "calcProp5").setResultType(Money.class);
	dtm().getEnhancer().apply();

	assertTrue("The brand new calculated property should be excluded.", dtm().getRepresentation().isExcludedImmutably(MasterEntity.class, "calcProp1"));
	assertTrue("The brand new calculated property should be disabled.", dtm().getRepresentation().getFirstTick().isDisabledImmutably(MasterEntity.class, calcProp2));
	assertTrue("The brand new calculated property should be immutable checked.", dtm().getRepresentation().getSecondTick().isCheckedImmutably(MasterEntity.class, "calcProp3"));
	assertTrue("The brand new calculated property should be checked.", dtm().getSecondTick().isChecked(MasterEntity.class, "calcProp5"));

	/////////////// REMOVING & MANAGING ///////////////
	final ICalculatedProperty calc1 = dtm().getEnhancer().getCalculatedProperty(MasterEntity.class, "calcProp1");
	final ICalculatedProperty calc2 = dtm().getEnhancer().getCalculatedProperty(MasterEntity.class, calcProp2);
	final ICalculatedProperty calc3 = dtm().getEnhancer().getCalculatedProperty(MasterEntity.class, "calcProp3");
	final ICalculatedProperty calc4 = dtm().getEnhancer().getCalculatedProperty(MasterEntity.class, "calcProp4");
	final ICalculatedProperty calc5 = dtm().getEnhancer().getCalculatedProperty(MasterEntity.class, "calcProp5");
	dtm().getEnhancer().removeCalculatedProperty(calc1);
	dtm().getEnhancer().removeCalculatedProperty(calc2);
	dtm().getEnhancer().removeCalculatedProperty(calc3);
	dtm().getEnhancer().removeCalculatedProperty(calc4);
	dtm().getEnhancer().removeCalculatedProperty(calc5);
	dtm().getEnhancer().apply();

	try {
	    dtm().getRepresentation().isExcludedImmutably(MasterEntity.class, "calcProp1");
	    fail("At this moment property 'calcProp1' should not exist and should cause exception.");
	} catch (final IllegalArgumentException e) {
	}
	try {
	    dtm().getRepresentation().getFirstTick().isDisabledImmutably(MasterEntity.class, calcProp2);
	    fail("At this moment property 'calcProp2' should not exist and should cause exception.");
	} catch (final IllegalArgumentException e) {
	}
	try {
	    dtm().getRepresentation().getSecondTick().isCheckedImmutably(MasterEntity.class, "calcProp3");
	    fail("At this moment property 'calcProp3' should not exist and should cause exception.");
	} catch (final IllegalArgumentException e) {
	}
	try {
	    dtm().getSecondTick().isChecked(MasterEntity.class, "calcProp5");
	    fail("At this moment property 'calcProp5' should not exist and should cause exception.");
	} catch (final IllegalArgumentException e) {
	}

	dtm().getEnhancer().addCalculatedProperty(calc1);
	dtm().getEnhancer().addCalculatedProperty(calc2);
	dtm().getEnhancer().addCalculatedProperty(calc3);
	dtm().getEnhancer().addCalculatedProperty(calc4);
	dtm().getEnhancer().addCalculatedProperty(calc5);
	dtm().getEnhancer().apply();

	assertTrue("The brand new calculated property should be excluded.", dtm().getRepresentation().isExcludedImmutably(MasterEntity.class, "calcProp1"));
	assertTrue("The brand new calculated property should be disabled.", dtm().getRepresentation().getFirstTick().isDisabledImmutably(MasterEntity.class, calcProp2));
	assertTrue("The brand new calculated property should be immutable checked.", dtm().getRepresentation().getSecondTick().isCheckedImmutably(MasterEntity.class, "calcProp3"));
	assertFalse("The brand new calculated property should NOT be checked.", dtm().getSecondTick().isChecked(MasterEntity.class, "calcProp5"));

	///////////////////////////////////////////////////////////////////////////////////////////
	// serialise and deserialise and then check the order of "checked properties"
	final byte[] array = getSerialiser().serialise(dtm());
	final IDomainTreeManagerAndEnhancer copy = getSerialiser().deserialise(array, IDomainTreeManagerAndEnhancer.class);
	assertNotNull("", copy.getEnhancer().getCalculatedProperty(MasterEntity.class, "calcProp1"));
	assertNotNull("", copy.getEnhancer().getCalculatedProperty(MasterEntity.class, calcProp2));
	assertNotNull("", copy.getEnhancer().getCalculatedProperty(MasterEntity.class, "calcProp3"));
	assertNotNull("", copy.getEnhancer().getCalculatedProperty(MasterEntity.class, "calcProp5"));
	assertTrue("The brand new calculated property should be excluded.", copy.getRepresentation().isExcludedImmutably(MasterEntity.class, "calcProp1"));
	assertTrue("The brand new calculated property should be disabled.", copy.getRepresentation().getFirstTick().isDisabledImmutably(MasterEntity.class, calcProp2));
	assertTrue("The brand new calculated property should be immutable checked.", copy.getRepresentation().getSecondTick().isCheckedImmutably(MasterEntity.class, "calcProp3"));
	assertFalse("The brand new calculated property should NOT be checked.", copy.getSecondTick().isChecked(MasterEntity.class, "calcProp5"));
    }
}
