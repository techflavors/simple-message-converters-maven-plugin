package techflavors.tools.smc.generators;

import java.io.File;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * @goal generate-sources
 * @phase generate-sources
 */
@Mojo(name = "smc-generator", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresOnline = false, requiresProject = true, threadSafe = true)
public class SMCCodeGeneratorMojo extends AbstractMojo {

	@Parameter(required = true, readonly = true, defaultValue = "${project}")
	MavenProject project;

	@Parameter(required = true, readonly = true, defaultValue = "${basedir}")
	String basedir;

	@Parameter(defaultValue = "true")
	protected Boolean overwrite;

	@Parameter(defaultValue = "PLAIN")
	protected InjectionType injectionType;

	@Parameter
	protected File sourceDirectory;

	@Parameter(required = true, readonly = true)
	protected Set<ConverterConfig> converters;

	@Parameter(defaultValue = "target/generated-sources/smc", required = true)
	protected File outputDirectory;

	private CodeGenerator codeGenerator;

	@Override
	public void execute() {
		try {
			// TODO: validate convertor params
			codeGenerator = new CodeGenerator();

			setDefault();

			GeneratorConfig generatorConfig = GeneratorConfig.create().Overwrite(overwrite).Converters(converters)
					.SourceDirectory(sourceDirectory).OutputDirectory(outputDirectory).InjectionType(injectionType);

			codeGenerator.generate(getLog(), generatorConfig);

			project.addCompileSourceRoot(outputDirectory.getAbsolutePath());

		} catch (Exception e) {
			getLog().error("General error", e);
		}
	}

	private void setDefault() {
		if (injectionType == null) {
			injectionType = InjectionType.PLAIN;
		}

		if (outputDirectory == null) {
			outputDirectory = new File(basedir, "target/generated-sources/smc");
		}
	}

}
