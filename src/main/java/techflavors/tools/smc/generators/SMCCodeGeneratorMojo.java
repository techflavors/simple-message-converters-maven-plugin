package techflavors.tools.smc.generators;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;

/**
 * @goal generate-sources
 * @phase generate-sources
 */
@Mojo(name = "smc-generator", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresOnline = false, requiresProject = true, threadSafe = false)
public class SMCCodeGeneratorMojo extends AbstractMojo {

	@Parameter(required = true, readonly = true, defaultValue = "${project}")
	MavenProject project;

	@Parameter(defaultValue = "false")
	protected Boolean overwrite;

	@Parameter
	protected File sourceDirectory;

	@Parameter(required = true, readonly = true)
	protected ConverterConfig[] converters;

	@Parameter(defaultValue = "target/generated-sources/smc", required = true)
	protected File outputDirectory;

	StringTemplateGroup templates;
	JavaDocBuilder docBuilder;

	@Override
	public void execute() {
		try {
			// TODO: validate convertor params
			this.docBuilder = new JavaDocBuilder();

			for (ConverterConfig converter : converters) {
				docBuilder.addSourceTree(new File(sourceDirectory, getAsFilePath(converter.sourceType) + ".java"));
				docBuilder.addSourceTree(new File(sourceDirectory, getAsFilePath(converter.targetType) + ".java"));
			}

			generate();

			project.addCompileSourceRoot(outputDirectory.getAbsolutePath());

		} catch (Exception e) {
			getLog().error("General error", e);
		}
	}

	private String getAsFilePath(String type) {
		String filename = type.replaceAll("\\.", "/");
		return filename;
	}

	private void generate() throws Exception {
		for (ConverterConfig converter : converters) {
			generateConverters(converter);
		}
	}

	private String getClassName(String fullName) {
		return fullName.substring(fullName.lastIndexOf(".") + 1);
	}

	public void generateConverters(ConverterConfig converter) throws IOException {

		JavaClass sourceClass = this.docBuilder.getClassByName(converter.sourceType);
		JavaClass targetClass = this.docBuilder.getClassByName(converter.targetType);

		File pd = new File(outputDirectory, getAsFilePath(converter.packageName));
		pd.mkdirs();

		String classFromName = getClassName(targetClass.asType().getValue());
		String classToName = getClassName(sourceClass.asType().getValue());

		String className = (classToName.equals(classFromName) ? classToName : classToName + classFromName)
				+ "Converter";

		applyTemplate(pd,converter, className);

		// for (JavaMethod m : jc.getMethods()) {
		// if (m.isConstructor()) {
		// DocletTag dc = m.getTagByName("builder");
		// if (dc != null) {
		// boolean builderAbstract = Boolean.valueOf(dc.getNamedParameter("abstract"));
		//
		// String builderName = dc.getNamedParameter("name");
		// if (builderName == null) {
		// if (builderAbstract)
		// builderName = "Abstract" + jc.getName() + "Builder";
		// else
		// builderName = jc.getName() + "Builder";
		// }
		//
		// String createMethod = dc.getNamedParameter("createMethod");
		// if (createMethod == null)
		// createMethod = "create";
		//
		// String packageName = dc.getNamedParameter("package");
		// if (packageName == null)
		// packageName = jc.getPackageName();
		//
		// String extendsClass = dc.getNamedParameter("extends");
		//
		// StringTemplate st = templates.getInstanceOf(builderAbstract ?
		// "abstractBuilder" : "builder");
		//
		// st.setAttribute("packageName", packageName);
		// st.setAttribute("builderName", builderName);
		// st.setAttribute("resultClass", jc.asType().toString());
		// st.setAttribute("createMethod", createMethod);
		// st.setAttribute("extendsClass", extendsClass);
		//
		// List<Param> ps = new LinkedList<Param>();
		// List<Param> cs = new LinkedList<Param>();
		// for (JavaParameter p : m.getParameters()) {
		// Param param = new Param(p.getType().toGenericString(), p.getName());
		// if (!p.getName().startsWith("_"))
		// ps.add(param);
		// cs.add(param);
		// }
		// st.setAttribute("parameters", ps);
		// st.setAttribute("arguments", cs);
		//
		// File pd = new File(outputDirectory, packageName.replaceAll("\\.", "/"));
		// pd.mkdirs();
		//
		// FileWriter out = new FileWriter(new File(pd, builderName + ".java"));
		// try {
		// out.append(st.toString());
		// } finally {
//		out.flush();
//		out.close();
		// }
		//
		// if (getLog().isDebugEnabled()) {
		// getLog().debug(builderName + ".java :");
		// getLog().debug(st.toString());
		// }
		// }
		// }
		// }
	}

	private void applyTemplate(File pd,ConverterConfig converter, String className) throws IOException {
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
		
		FileWriter writer = new FileWriter(new File(pd, className + ".java"));
		t.merge( context, writer );
		writer.flush();
		writer.close();
	}

	private void generateConverterMethod(FileWriter out, JavaClass classFrom, JavaClass classTo, String dir)
			throws IOException {

		String classFromName = classFrom.asType().getValue();
		String classToName = classTo.asType().getValue();

		out.write("\n@Override\n");
		out.write(String.format("protected %s %sType(%s data) {\n", classToName, dir, classFromName));

		out.write(String.format("%s result = new %s();", classToName, classFromName));

		// for (JavaField srcField : sourceClass.getFields()) {
		// JavaField targetField = targetClass.getFieldByName(srcField.getName());
		//
		// if (targetField == null) {
		//
		// } else {
		//
		// }
		//
		// }

		out.write("\nreturn result;\n}\n");

	}
}
