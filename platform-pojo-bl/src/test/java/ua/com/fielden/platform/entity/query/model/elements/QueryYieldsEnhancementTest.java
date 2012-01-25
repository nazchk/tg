package ua.com.fielden.platform.entity.query.model.elements;

import org.junit.Test;

import ua.com.fielden.platform.entity.query.BaseEntQueryTCase;
import ua.com.fielden.platform.entity.query.model.EntityResultQueryModel;
import ua.com.fielden.platform.sample.domain.TgVehicle;
import ua.com.fielden.platform.sample.domain.TgVehicleMake;
import static org.junit.Assert.assertEquals;
import static ua.com.fielden.platform.entity.query.fluent.query.select;

public class QueryYieldsEnhancementTest extends BaseEntQueryTCase {

    @Test
    public void test_yield_of_property_as_result_entity() {
	final EntityResultQueryModel<TgVehicleMake> shortcutQry = select(VEHICLE).yield().prop("model.make").modelAsEntity(MAKE);

	final EntityResultQueryModel<TgVehicleMake> explicitQry = select(VEHICLE). //
	join(MODEL).as("model").on().prop("model").eq().prop("model.id"). //
	yield().prop("model.make").as("id").modelAsEntity(MAKE);

	assertEquals("Incorrect yields enhancement", entValidQuery(explicitQry), entValidQuery(shortcutQry));
    }

    @Test
    public void test_model_with_no_explicit_yields() {
	final EntityResultQueryModel<TgVehicle> shortcutQry = select(VEHICLE).model();

	final EntityResultQueryModel<TgVehicle> explicitQry = select(VEHICLE). //
		yield().prop("id").as("id"). //
		yield().prop("version").as("version"). //
		yield().prop("key").as("key"). //
		yield().prop("desc").as("desc"). //
		yield().prop("replacedBy").as("replacedBy"). //
		yield().prop("initDate").as("initDate"). //
		yield().prop("model").as("model"). //
		yield().prop("station").as("station"). //
		yield().prop("price").as("price"). //
		yield().prop("purchasePrice").as("purchasePrice"). //
		modelAsEntity(VEHICLE);

	assertEquals("Incorrect yields enhancement", entValidQuery(explicitQry), entValidQuery(shortcutQry));
    }
}