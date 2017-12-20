# Simple Message or Object Converter Generator

Maven plugin to generate convertors for Simple Message or Object Converter. This plugin generates convertor and loaders.
For spring based (injectionType = SPRING) project it generate Configuration File which creates Message Convertor and other converters as beans.
For non-spring based (injectionType = PLAIN) project it generate Factory File which creates Message Convertor and other converters as factory.

## Getting Started

Add following line in your pom.xml

```xml
<plugin>
<groupId>com.github.techflavors</groupId>
<artifactId>smc-maven-plugin</artifactId>
<version>1.0.0</version>
<executions>
    <execution>
        <id>smc</id>
        <goals>
            <goal>smc-generator</goal>
        </goals>
    </execution>
</executions>
<configuration>
    <sourceDirectory>${basedir}/src/main/java</sourceDirectory>
    <converters>
        <converter>
            <name>TestConverter</name> <!-- Name used for -->
            <packageName>techflavors.tools.smc.generators.converters</packageName>
            <sourceType>techflavors.tools.smc.generators.source.data.Person</sourceType>
            <targetType>techflavors.tools.smc.generators.target.data.Person</targetType>
        </converter>
    </converters>
    <injectionType>PLAIN</injectionType> <!-- use SPRING for spring based loaders -->
    <outputDirectory>target/generated-sources/smc</outputDirectory>
</configuration>
</plugin>
```
*The plugin generate conversion for direct convertable fields, for customization you have to modify the generated code*

You can refer to [sample test case](https://github.com/techflavors/simple-message-converters-maven-plugin/tree/master/simple-message-converters-maven-plugin-test) on how to use maven plugin. 

## Sample Generator Loaders 

### For Spring based Java Project 
```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import techflavors.tools.smc.converters.MessageConverter;
import techflavors.tools.smc.converters.MessageConverters;

@Configuration
@ComponentScan(basePackages = { "techflavors.tools.smc.generators.converters" })
public class TestConverterFactory {

	@Bean(name = "techflavors.tools.smc.generators.converters")
	public MessageConverters converters() {
		return new MessageConverters("techflavors.tools.smc.generators.converters");
	}
	
		
	@Bean(name = "AddressConvertor")
	public MessageConverter<?,?> AddressConvertor() {
		return new AddressConvertor();
	}
		
	@Bean(name = "PersonConvertor")
	public MessageConverter<?,?> PersonConvertor() {
		return new PersonConvertor();
	}
}
```
Usage
```java
public class TestMessageConversionDependencyInjection extends AbstractTestNGSpringContextTests {

	@Inject
	@Named("techflavors.tools.smc.generators.converters")
	MessageConverters messageConverters;

	@Test
	public void testMessageConversion() {
		TestMessageConversion messageConversion = new TestMessageConversion();
		messageConversion.testMessageConversion(messageConverters);
	}

}
```
### For Normal Java Project (non-Spring based)
```java
public final class TestConverterFactory {

	private static final MessageConverters messageConverters = new MessageConverters(
			"techflavors.tools.smc.generators.converters");

	static {
		List<MessageConverter<?, ?>> messageConverterList = new ArrayList<>();
		messageConverterList.add(new PersonConverter());
		messageConverterList.add(new AddressConverter());

		messageConverters.setMessageConverters(messageConverterList);
	}

	public static final MessageConverters messageConvertors() {
		return messageConverters;
	}

	private TestConverterFactory() {
	}

}
```
Usage
```java
public class TestMessageConversionWithoutDependencyInjection {

	@Test
	public void testMessageConversion() throws Exception {
		TestMessageConversion messageConversion = new TestMessageConversion();
		messageConversion.testMessageConversion(TestConverterFactory.messageConvertors());
	}

}
```
#### TestMessageConversion.java
```java
public class TestMessageConversion {

	public void testMessageConversion(MessageConverters messageConverters) {
		Person person = new Person();
		person.firstname = "Biju";
		person.lastname = "Nair";
		person.address = new Address();
		person.address.street = "My Street";
		person.address.city = "My City";

		techflavors.tools.smc.generators.target.data.Person personToPersist = messageConverters.toTargetType(person);

		assertEquals(personToPersist.firstname, "Biju");
		assertEquals(personToPersist.lastname, "Nair");
		assertNotNull(personToPersist.address);
		assertEquals(personToPersist.address.street, "My Street");
		assertEquals(personToPersist.address.city, "My City");

		person = messageConverters.toSourceType(personToPersist);

		assertEquals(person.firstname, "Biju");
		assertEquals(person.lastname, "Nair");
		assertNotNull(person.address);
		assertEquals(person.address.street, "My Street");
		assertEquals(person.address.city, "My City");
	}

}
```

## Contributing

Please feel free to contribute.

## Authors

* **Biju Nair** 

See also the list of [contributors](https://github.com/techflavors/simple-message-converters-maven-plugin/contributors) who participated in this project.

