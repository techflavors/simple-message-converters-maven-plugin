package techflavors.tools.smc.generators.converters.test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import techflavors.tools.smc.converters.MessageConverters;
import techflavors.tools.smc.generators.source.data.Address;
import techflavors.tools.smc.generators.source.data.Person;

public class TestMessageConversion {

	public void testMessageConversion(MessageConverters messageConverters) {
		Person person = new Person();
		person.firstname = "Biju";
		person.lastname = "Nair";
		person.address = new Address();
		person.address.street = "My Street";
		person.address.city = "San Jose";

		techflavors.tools.smc.generators.target.data.Person personToPersist = messageConverters.toTargetType(person);

		assertEquals(personToPersist.firstname, "Biju");
		assertEquals(personToPersist.lastname, "Nair");
		assertNotNull(personToPersist.address);
		assertEquals(personToPersist.address.street, "My Street");
		assertEquals(personToPersist.address.city, "San Jose");

		person = messageConverters.toSourceType(personToPersist);

		assertEquals(person.firstname, "Biju");
		assertEquals(person.lastname, "Nair");
		assertNotNull(person.address);
		assertEquals(person.address.street, "My Street");
		assertEquals(person.address.city, "San Jose");
	}

}
