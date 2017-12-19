package techflavors.tools.smc.generators;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Test;

public class SMCCodeGeneratorMojoTest extends AbstractMojoTestCase {

	@Test // Uncomment if you need to run as Junit Test using IDE
	public void test() throws Exception {
		File pom = getTestFile("src/test/resources/unit/smc/pom.xml");
		assertNotNull(pom);
		assertTrue(pom.exists());

		SMCCodeGeneratorMojo myMojo = (SMCCodeGeneratorMojo) lookupMojo("com.github.techflavors", "smc-maven-plugin",
				"0.0.1-SNAPSHOT", "smc-generator", extractPluginConfiguration("smc-maven-plugin", pom));
		assertNotNull(myMojo);
		myMojo.execute();
	}

}
