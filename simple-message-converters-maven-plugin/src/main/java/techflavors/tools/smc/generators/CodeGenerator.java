package techflavors.tools.smc.generators;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.logging.Log;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.model.impl.DefaultJavaParameterizedType;

public class CodeGenerator {
	private JavaProjectBuilder javaProjectBuilder;

	private HashSet<String> generatedConverters = new HashSet<>();

	GeneratorConfig generatorConfig;

	public void generate(Log log, GeneratorConfig generatorConfig) throws Exception {
		this.generatorConfig = generatorConfig;

		this.javaProjectBuilder = new JavaProjectBuilder();

		for (ConverterConfig converterConfig : generatorConfig.converters) {
			File pd = new File(generatorConfig.outputDirectory, getAsFilePath(converterConfig.packageName));
			pd.mkdirs();
			generateConverters(log, pd, converterConfig);
			generateFactoryClass(log, pd, converterConfig);
		}

	}

	private void generateFactoryClass(Log log, File pd, ConverterConfig converterConfig) throws IOException {
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.init();

		Template t = velocityEngine.getTemplate("/templates/" + generatorConfig.injectionType.name() + ".tpl");

		File factoryFile = new File(pd, converterConfig.name + "Factory.java");
		VelocityContext context = new VelocityContext();
		context.put("converters", generatedConverters);
		context.put("package", converterConfig.packageName);
		context.put("mcname", converterConfig.packageName);
		context.put("cname", converterConfig.name + "Factory");

		log.info("Generating " + factoryFile.getAbsolutePath());
		FileWriter writer = new FileWriter(factoryFile);
		t.merge(context, writer);
		writer.flush();
		writer.close();

	}

	private String getAsFilePath(String type) {
		String filename = type.replaceAll("\\.", "/");
		return filename;
	}

	private String getClassName(String fullName) {
		return fullName.substring(fullName.lastIndexOf(".") + 1);
	}

	public void generateConverters(Log log, File pd, ConverterConfig converterConfig) throws IOException {

		String className = generateClassName(converterConfig.sourceType);

		if (generatedConverters.contains(className))
			return;

		File sourceFile = new File(generatorConfig.sourceDirectory,
				getAsFilePath(converterConfig.sourceType) + ".java");
		File targetFile = new File(generatorConfig.sourceDirectory,
				getAsFilePath(converterConfig.targetType) + ".java");

		if (!(targetFile.exists() && sourceFile.exists()))
			return;

		generatedConverters.add(className);

		javaProjectBuilder.addSource(sourceFile);
		javaProjectBuilder.addSource(targetFile);

		List<MethodParam> srcSetters = getSetterMethods(log, pd, converterConfig.packageName,
				converterConfig.sourceType, converterConfig.targetType);

		List<MethodParam> targetSetters = getSetterMethods(log, pd, converterConfig.packageName,
				converterConfig.targetType, converterConfig.sourceType);

		applyTemplate(log, pd, converterConfig, className, srcSetters, targetSetters);

	}

	private String generateClassName(String sourceType) {
		// String classFromName = getClassName(targetType);
		String classToName = getClassName(sourceType);

		// String className = (classToName.equals(classFromName) ? classToName :
		// classToName + classFromName)
		// + "Converter";
		return classToName + "Converter";
	}

	private List<MethodParam> getSetterMethods(Log log, File pd, String packageName, String sourceType,
			String targetType) throws IOException {
		JavaClass sourceClass = this.javaProjectBuilder.getClassByName(sourceType);
		JavaClass targetClass = this.javaProjectBuilder.getClassByName(targetType);

		List<MethodParam> methods = new ArrayList<MethodParam>();

		Map<String, JavaMethod> targetSetters = getSettersMethods(targetClass);

		for (JavaMethod method : sourceClass.getMethods()) {
			if (isSetterMethod(method)) {
				String mname = method.getName();
				JavaParameter javaParameter = method.getParameters().get(0);
				DefaultJavaParameterizedType type = (DefaultJavaParameterizedType) javaParameter.getType();

				MethodParam methodParam = new MethodParam(mname.substring(3), getType(method), getGType(method));

				if (type.isArray())
					methodParam.paramType = "A";
				else if (type.isA(java.util.Collection.class.getCanonicalName()))
					methodParam.paramType = "C";
				else if (type.isPrimitive() || allowDirectSetters(javaParameter.getType()))
					methodParam.paramType = "P";
				else
					methodParam.paramType = "O";

				methods.add(methodParam);

				if (targetSetters.containsKey(mname) && !methodParam.type.equals("?")) {
					ConverterConfig objConverter = new ConverterConfig();
					objConverter.packageName = packageName;
					objConverter.sourceType = methodParam.type;
					objConverter.targetType = getType(targetSetters.get(method.getName()));

					generateConverters(log, pd, objConverter);
				}

			}
		}
		return methods;
	}

	private String getType(JavaMethod javaMethod) {
		DefaultJavaParameterizedType type = (DefaultJavaParameterizedType) javaMethod.getParameters().get(0).getType();
		String typeValue = type.getFullyQualifiedName();
		if (type.getActualTypeArguments().size() > 0) {
			typeValue = type.getActualTypeArguments().get(0).getFullyQualifiedName();
		} /*
			 * else if (type.getActualTypeArguments().size() == 0) { typeValue =
			 * getGType(javaMethod); }
			 */
		if (typeValue.endsWith("]")) // remove array
			typeValue = typeValue.substring(0, typeValue.length() - 2);

		return typeValue;
	}

	private String getGType(JavaMethod javaMethod) {
		DefaultJavaParameterizedType type = (DefaultJavaParameterizedType) javaMethod.getParameters().get(0).getType();
		String typeValue = type.getFullyQualifiedName();
		return typeValue;
	}

	private Map<String, JavaMethod> getSettersMethods(JavaClass clz) {
		Map<String, JavaMethod> setterMethods = new HashMap<>();
		for (JavaMethod method : clz.getMethods()) {
			if (isSetterMethod(method)) {
				setterMethods.put(method.getName(), method);
			}
		}
		return setterMethods;
	}

	private boolean isSetterMethod(JavaMethod method) {
		return (method.getName().startsWith("set") && method.getParameters().size() == 1);
	}

	private boolean allowDirectSetters(JavaType ptype) {
		String type = ptype.getGenericFullyQualifiedName();
		if (type.startsWith("java."))
			return true;
		return false;
	}

	private void applyTemplate(Log log, File pd, ConverterConfig converter, String className,
			List<MethodParam> srcSetters, List<MethodParam> targetSetters) throws IOException {
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.init();

		Template t = velocityEngine.getTemplate("/templates/converter.tpl");

		VelocityContext context = new VelocityContext();
		context.put("package", converter.packageName);
		context.put("class", className);
		context.put("source", converter.sourceType);
		context.put("target", converter.targetType);
		context.put("smethods", srcSetters);
		context.put("tmethods", targetSetters);

		File converterFile = new File(pd, className + ".java");
		log.info("Generating " + converterFile.getAbsolutePath());

		FileWriter writer = new FileWriter(converterFile);
		t.merge(context, writer);
		writer.flush();
		writer.close();
	}

	public static class MethodParam {
		public String name;
		public String type;
		public String gtype;
		public String paramType;

		public MethodParam(String name, String type, String gtype) {
			super();
			this.name = name;
			this.type = type;
			this.gtype = gtype;
		}

		public String getName() {
			return name;
		}

		public String getType() {
			return type;
		}

		public String getGtype() {
			return gtype;
		}

		public String getParamType() {
			return paramType;
		}
	}
}
