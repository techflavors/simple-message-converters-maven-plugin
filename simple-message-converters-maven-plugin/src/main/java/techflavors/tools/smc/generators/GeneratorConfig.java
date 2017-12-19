package techflavors.tools.smc.generators;

import java.io.File;
import java.util.Set;

public final class GeneratorConfig {
	protected Boolean overwrite;

	protected File sourceDirectory;

	protected Set<ConverterConfig> converters;

	protected File outputDirectory;
	
	protected InjectionType injectionType;

	public static GeneratorConfig create() {
		return new GeneratorConfig();
	}

	private GeneratorConfig() {
	}

	public Boolean Overwrite() {
		return overwrite;
	}

	public GeneratorConfig Overwrite(Boolean overwrite) {
		this.overwrite = overwrite;
		return this;
	}

	public File SourceDirectory() {
		return sourceDirectory;
	}

	public GeneratorConfig SourceDirectory(File sourceDirectory) {
		this.sourceDirectory = sourceDirectory;
		return this;
	}

	public Set<ConverterConfig> Converters() {
		return converters;
	}

	public GeneratorConfig Converters(Set<ConverterConfig> converters) {
		this.converters = converters;
		return this;
	}

	public File OutputDirectory() {
		return outputDirectory;
	}

	public GeneratorConfig OutputDirectory(File outputDirectory) {
		this.outputDirectory = outputDirectory;
		return this;
	}
	
	public GeneratorConfig InjectionType(InjectionType injectionType) {
		this.injectionType = injectionType;
		return this;
	}
	
	public InjectionType InjectionType() {
		return injectionType;
	}

}
