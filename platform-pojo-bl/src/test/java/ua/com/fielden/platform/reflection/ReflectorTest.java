package ua.com.fielden.platform.reflection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.annotation.IsProperty;
import ua.com.fielden.platform.entity.annotation.KeyType;
import ua.com.fielden.platform.entity.annotation.Observable;
import ua.com.fielden.platform.entity.annotation.factory.HandlerAnnotation;
import ua.com.fielden.platform.entity.annotation.factory.ParamAnnotation;
import ua.com.fielden.platform.entity.annotation.mutator.DateParam;
import ua.com.fielden.platform.entity.annotation.mutator.Handler;
import ua.com.fielden.platform.entity.before_change_event_handling.BeforeChangeEventHandler;
import ua.com.fielden.platform.entity.factory.EntityFactory;
import ua.com.fielden.platform.entity.validation.annotation.GreaterOrEqual;
import ua.com.fielden.platform.entity.validation.annotation.Max;
import ua.com.fielden.platform.ioc.ApplicationInjectorFactory;
import ua.com.fielden.platform.reflection.test_entities.ComplexKeyEntity;
import ua.com.fielden.platform.reflection.test_entities.SecondLevelEntity;
import ua.com.fielden.platform.reflection.test_entities.SimplePartEntity;
import ua.com.fielden.platform.reflection.test_entities.UnionEntityForReflector;
import ua.com.fielden.platform.reflection.test_entities.UnionEntityHolder;
import ua.com.fielden.platform.test.CommonTestEntityModuleWithPropertyFactory;
import ua.com.fielden.platform.utils.Pair;

import com.google.inject.Injector;

/**
 * Test case for {@link Reflector}.
 *
 * @author TG Team
 *
 */
public class ReflectorTest {
    final Injector injector = new ApplicationInjectorFactory().add(new CommonTestEntityModuleWithPropertyFactory()).getInjector();
    final EntityFactory factory = injector.getInstance(EntityFactory.class);

    @Test
    public void test_that_obtain_getter_works() throws Exception {
	Method method = Reflector.obtainPropertyAccessor(SecondLevelEntity.class, "propertyOfSelfType");
	assertNotNull("Failed to located a getter method.", method);
	assertEquals("Incorrect getter.", "getPropertyOfSelfType", method.getName());
	method = Reflector.obtainPropertyAccessor(UnionEntityForReflector.class, "commonProperty");
	assertNotNull("Failed to locate a getter method for commonProperty in the UnionEntity", method);
	assertEquals("Incorect commonProperty getter", "getCommonProperty", method.getName());
	method = Reflector.obtainPropertyAccessor(UnionEntityForReflector.class, "simplePartEntity");
	assertNotNull("Failed to locate a getter method for simplePartEntity in the UnionEntity", method);
	assertEquals("Incorect simplePartEntity getter", "getSimplePartEntity", method.getName());
	try {
	    Reflector.obtainPropertyAccessor(UnionEntityForReflector.class, "uncommonProperty");
	    fail("there shouldn't be any getter for uncommonProperty");
	} catch (final Exception e) {
	    System.out.println(e.getMessage());
	}
    }

    @Test
    public void test_whether_obtainPropertySetter_works() {
	try {
	    Method method = Reflector.obtainPropertySetter(ComplexKeyEntity.class, "key.key");
	    assertNotNull("Couldn't find setter for key.key property of the ComplexKeyEntity class", method);
	    method = Reflector.obtainPropertySetter(ComplexKeyEntity.class, "key.simpleEntity");
	    assertNotNull("Couldn't find setter for key.simpleEntity property of the ComplexKeyEntity class", method);
	    method = Reflector.obtainPropertySetter(ComplexKeyEntity.class, "key.simpleEntity.key");
	    assertNotNull("Couldn't find setter for key.simpleEntity.key property of the ComplexKeyEntity class", method);
	    method = Reflector.obtainPropertySetter(ComplexKeyEntity.class, "simpleEntity.key");
	    assertNotNull("Couldn't find setter for simpleEntity.key property of the ComplexKeyEntity class", method);
	    method = Reflector.obtainPropertySetter(ComplexKeyEntity.class, "key.simpleEntity.desc");
	    assertNotNull("Couldn't find setter for key.simpleEntity.desc property of the ComplexKeyEntity class", method);
	    method = Reflector.obtainPropertySetter(UnionEntityForReflector.class, "commonProperty");
	    assertNotNull("Failed to locate a setter method for commonProperty in the UnionEntity", method);
	    method = Reflector.obtainPropertyAccessor(UnionEntityForReflector.class, "simplePartEntity");
	    assertNotNull("Failed to locate a setter method for simplePartEntity in the UnionEntity", method);
	    method = Reflector.obtainPropertySetter(UnionEntityHolder.class, "unionEntity.commonProperty");
	    assertNotNull("Couldn't find setter for unionEntity.commonProperty in the UnionEntityHolder", method);
	    method = Reflector.obtainPropertySetter(UnionEntityHolder.class, "unionEntity.levelEntity.propertyOfSelfType.key");
	    assertNotNull("Couldn't find setter for unionEntity.levelEntity.propertyOfSelfType.key in the UnionEntityHolder", method);
	    try {
		Reflector.obtainPropertySetter(UnionEntityForReflector.class, "uncommonProperty");
		fail("there shouldn't be any setter for uncommonProperty");
	    } catch (final Exception e) {
		System.out.println(e.getMessage());
	    }
	} catch (final Exception ex) {
	    fail(ex.getMessage());
	    ex.printStackTrace();
	}
    }

    @Test
    public void test_whether_getMethod_works() {
	final SecondLevelEntity inst = new SecondLevelEntity();
	inst.setPropertyOfSelfType(inst);
	inst.setProperty("value");

	final SimplePartEntity simpleProperty = factory.newEntity(SimplePartEntity.class, 1L, "KEY");
	simpleProperty.setDesc("DESC");
	simpleProperty.setCommonProperty("common value");
	simpleProperty.setLevelEntity(inst);
	simpleProperty.setUncommonProperty("uncommon value");

	final UnionEntityForReflector unionEntity = factory.newEntity(UnionEntityForReflector.class);
	unionEntity.setSimplePartEntity(simpleProperty);

	try {
	    assertNotNull("The getProperty() method must be present in the SecondLevelEntity", Reflector.getMethod(SecondLevelEntity.class, "getProperty"));
	} catch (final NoSuchMethodException e) {
	    fail("There shouldn't be any exception");
	    e.printStackTrace();
	}

	try {
	    assertNotNull("The getCommonProperty() must be present in UnionEntity class", Reflector.getMethod(UnionEntityForReflector.class, "getCommonProperty"));
	} catch (final NoSuchMethodException e) {
	    fail("There shouldn't be any exception");
	    e.printStackTrace();
	}

	try {
	    assertNotNull("The getCommonProperty() must be present in UnionEntity class", Reflector.getMethod(unionEntity, "getCommonProperty"));
	} catch (final NoSuchMethodException e) {
	    fail("There shouldn't be any exception");
	    e.printStackTrace();
	}

	try {
	    assertNull("The getUncommonProerty mustn't be presnet in UnionEntity class", Reflector.getMethod(UnionEntityForReflector.class, "getUncommonProperty"));
	    fail("The getUncommonProerty mustn't be presnet in UnionEntity class");
	} catch (final Exception e) {
	    System.out.println(e.getMessage());
	}

	try {
	    assertNull("The getUncommonProerty mustn't be presnet in UnionEntity class", Reflector.getMethod(unionEntity, "getUncommonProperty"));
	    fail("The getUncommonProerty mustn't be presnet in UnionEntity class");
	} catch (final Exception e) {
	    System.out.println(e.getMessage());
	}
    }

    private static class A {
	public A(){
	}
	public A(final Integer x){
	}
	public A(final Integer x, final Double y){
	}
    }

    private static class B extends A {
	public B(){
	}
	public B(final Integer x, final Double y){
	}
    }

    @Test
    public void test_constructor_retrieval(){
	try {
	    final Constructor def, sing, doub, def1, sing1, doub1;

	    assertNotNull("Constructor should not be null.", def = Reflector.getConstructorForClass(A.class));
	    assertNotNull("Constructor should not be null.", sing = Reflector.getConstructorForClass(A.class, Integer.class));
	    assertNotNull("Constructor should not be null.", doub = Reflector.getConstructorForClass(A.class, Integer.class, Double.class));

	    assertNotNull("Constructor should not be null.", def1 = Reflector.getConstructorForClass(B.class));
	    assertNotSame("Should not be equal.", def1, def);
	    assertNotNull("Constructor should not be null.", sing1 = Reflector.getConstructorForClass(B.class, Integer.class));
	    assertEquals("Should be equal.", sing1, sing);
	    assertNotNull("Constructor should not be null.", doub1 = Reflector.getConstructorForClass(B.class, Integer.class, Double.class));
	    assertNotSame("Should not be equal.", doub1, doub);
	} catch (final Exception e) {
	    fail("Constructor retrieval failed. " + e.getMessage());
	}
    }

    @Test
    public void test_validation_limits_extraction(){
	final C c = factory.newEntity(C.class, 1L, "KEY");
	assertEquals("Should be equal.", new Pair<Comparable, Comparable>(1, 12), Reflector.extractValidationLimits(c, "month"));
	assertEquals("Should be equal.", new Pair<Comparable, Comparable>(1950, Integer.MAX_VALUE), Reflector.extractValidationLimits(c, "year"));
    }

    @Test
    public void test_annotataion_params() {
	final List<String> params = Reflector.annotataionParams(Handler.class);
	assertEquals("Unexpected number of annotation parameters.", 9, params.size());
	assertTrue(params.contains("value"));
	assertTrue(params.contains("non_ordinary"));
	assertTrue(params.contains("clazz"));
	assertTrue(params.contains("integer"));
	assertTrue(params.contains("str"));
	assertTrue(params.contains("dbl"));
	assertTrue(params.contains("date"));
	assertTrue(params.contains("date_time"));
	assertTrue(params.contains("money"));
    }

    @Test
    public void test_that_annotation_param_value_can_be_obtained() {
	final Handler handler = new HandlerAnnotation(BeforeChangeEventHandler.class).date(new DateParam[]{ParamAnnotation.dateParam("dateParam", "2011-12-01 00:00:00")}).newInstance();
	final Pair<Class<?>, Object> pair = Reflector.getAnnotationParamValue(handler, "date");
	final DateParam[] dateParams = (DateParam[])pair.getValue();
	assertEquals("Incorrect number of parameter values.", 1, dateParams.length);
	final DateParam param = dateParams[0];
	assertEquals("Incorrect parameter value.", "dateParam", param.name());
	assertEquals("Incorrect parameter value.", "2011-12-01 00:00:00", param.value());
    }

    @KeyType(String.class)
    private static class C extends AbstractEntity<String> {
	public C() {
	}

	@IsProperty
	private Integer month;
	@IsProperty
	private Integer year;

	public Integer getMonth() {
	    return month;
	}

	@Observable
	@GreaterOrEqual(1)
	@Max(12)
	public void setMonth(final Integer month) {
	    this.month = month;
	}

	public Integer getYear() {
	    return year;
	}

	@Observable
	@GreaterOrEqual(1950)
	public void setYear(final Integer year) {
	    this.year = year;
	}
    }

}
