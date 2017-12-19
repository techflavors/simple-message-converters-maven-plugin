package techflavors.tools.smc.generators.converters.test;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import techflavors.tools.smc.converters.MessageConverters;

/**
 * Before running this test case - Generate files using injection type as SPRING
 * 
 * @author Biju Nair
 *
 */
@ContextConfiguration(locations = { "classpath:spring-test-config.xml" })
public class TestMessageConversionDependencyInjection extends AbstractTestNGSpringContextTests {

//	@Inject
//	@Named("techflavors.tools.smc.generators.converters")
//	MessageConverters messageConverters;
//
//	@Test
//	public void testMessageConversion() {
//		TestMessageConversion messageConversion = new TestMessageConversion();
//		messageConversion.testMessageConversion(messageConverters);
//	}

}
