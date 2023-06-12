package com.at;


import org.apache.commons.lang3.StringUtils;

import javax.tools.*;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DynamicJdkCompiler {


    private static final Pattern PACKAGE_PATTERN = Pattern.compile("package\\s+([_a-zA-Z][_a-zA-Z0-9\\.]*);");

    private static final Pattern CLASS_PATTERN = Pattern.compile("class\\s+([_a-zA-Z][_a-zA-Z0-9]*)\\s+");

    private final JavaCompiler compiler;

    private URLClassLoader parentClassLoader;
    private String classpath;

    protected String encoding = "UTF-8";

    public DynamicJdkCompiler() {
        compiler = ToolProvider.getSystemJavaCompiler();
        if (null == compiler) {
            throw new IllegalStateException("无法获取编译器！请用JDK，而不是JRE运行JAVA。");
        }
        this.parentClassLoader = (URLClassLoader) this.getClass().getClassLoader();
        this.buildClassPath();
    }

    private void buildClassPath() {
        this.classpath = null;
        StringBuilder sb = new StringBuilder();
        for (URL url : this.parentClassLoader.getURLs()) {
            String p = url.getFile();
            sb.append(p).append(File.pathSeparator);
        }
        this.classpath = sb.toString();
    }

    public Class<?> compile(String code) {
        Matcher matcher = PACKAGE_PATTERN.matcher(code);
        String packageName = null;
        if (matcher.find()) {
            packageName = matcher.group(1);
        }
        matcher = CLASS_PATTERN.matcher(code);
        String className;
        if (matcher.find()) {
            className = matcher.group(1);
        } else {
            throw new IllegalArgumentException("No such class name in " + code);
        }
        String fullClassName = null;
        if (StringUtils.isNotBlank(packageName)) {
            fullClassName = packageName + "." + className;
        } else {
            fullClassName = className;
        }
        return doCompile(fullClassName, code);
    }

    public Class<?> doCompile(String fullClassName, String javaCode) {
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager standardJavaFileManager = compiler.getStandardFileManager(diagnostics, null, null);

        ClassFileManager fileManager = new ClassFileManager(standardJavaFileManager);
        CharSequenceJavaFileObject jfile = new CharSequenceJavaFileObject(fullClassName, javaCode);

        try {
            List<JavaFileObject> jfiles = new ArrayList<JavaFileObject>();
            jfiles.add(jfile);
            List<String> options = new ArrayList<String>();
            options.add("-encoding");
            options.add(encoding);
            options.add("-classpath");
            options.add(this.classpath);
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null, jfiles);
            boolean success = task.call();

            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                if (diagnostic.getKind() == Diagnostic.Kind.ERROR) {
                    String errorCode = "compile fail: " + diagnostic.getMessage(null) + ". \r\nat " + fullClassName
                            + "(" + diagnostic.getLineNumber() + ")" + "\r\ncode:"
                            + jfile.getLineCode(diagnostic.getLineNumber());
                    throw new java.lang.Error(errorCode);
                }
            }

            if (!success) {
                String error = compilePrint(diagnostics);
                // System.err.print(error);
                throw new IllegalStateException("Compilation failed. class: " + fullClassName + ", diagnostics: \r\n"
                        + error);
            }

            DynamicClassLoader dynamicClassLoader = new DynamicClassLoader(this.parentClassLoader);

            for (JavaClassObject javaClassObject : fileManager.getInnerClassJavaClassObject()) {
                dynamicClassLoader.defineClass(javaClassObject);
            }

            JavaClassObject jco = fileManager.getJavaClassObject();
            Class<?> clazz = dynamicClassLoader.defineClass(jco);
            try {
                dynamicClassLoader.close();
            } catch (Exception e) {
                // log.error("dynamicClassLoader.close throws " + e.getClass().getSimpleName() + " !", e);
            }
            return clazz;
        } finally {
            try {
                jfile.delete();
            } catch (Exception e) {
                //log.error("jfile.delete throws " + e.getClass().getSimpleName() + " !", e);
            }
            try {
                fileManager.close();
            } catch (Exception e) {
                //log.error("fileManager.close throws " + e.getClass().getSimpleName() + " !", e);
            }
        }
    }

    protected String compilePrint(DiagnosticCollector<JavaFileObject> diagnostics) {
        StringBuilder result = new StringBuilder();
        for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
            compilePrint(diagnostic, result);
            result.append("\r\n");
        }
        return result.toString();
    }

    private void compilePrint(Diagnostic<?> diagnostic, StringBuilder stringBuilder) {
        stringBuilder.append("Code:[" + diagnostic.getCode() + "]\n");
        stringBuilder.append("Kind:[" + diagnostic.getKind() + "]\n");
        stringBuilder.append("Position:[" + diagnostic.getPosition() + "]\n");
        stringBuilder.append("Start Position:[" + diagnostic.getStartPosition() + "]\n");
        stringBuilder.append("End Position:[" + diagnostic.getEndPosition() + "]\n");
        stringBuilder.append("Source:[" + diagnostic.getSource() + "]\n");
        stringBuilder.append("Message:[" + diagnostic.getMessage(null) + "]\n");
        stringBuilder.append("LineNumber:[" + diagnostic.getLineNumber() + "]\n");
        stringBuilder.append("ColumnNumber:[" + diagnostic.getColumnNumber() + "]\n");
    }
}