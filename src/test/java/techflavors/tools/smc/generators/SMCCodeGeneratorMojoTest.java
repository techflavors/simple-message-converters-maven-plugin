package techflavors.tools.smc.generators;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Test;

public class SMCCodeGeneratorMojoTest extends AbstractMojoTestCase {

	@Test
	public void test() throws Exception {
		File pom = getTestFile("src/test/resources/unit/smc/pom.xml");
		assertNotNull(pom);
		assertTrue(pom.exists());
		
		SMCCodeGeneratorMojo myMojo = (SMCCodeGeneratorMojo) lookupMojo("smc-generator", pom);
		assertNotNull(myMojo);
		myMojo.execute();
	}

}
