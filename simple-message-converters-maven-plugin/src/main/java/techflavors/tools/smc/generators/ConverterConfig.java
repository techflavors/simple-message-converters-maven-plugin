package techflavors.tools.smc.generators;

import org.apache.maven.plugins.annotations.Parameter;

public class ConverterConfig {
	@Parameter(required = true)
	String packageName;
	@Parameter(required = true)
	String sourceType;
	@Parameter(required = true)
	String targetType;
	@Parameter(required = true)
	String name;
}
