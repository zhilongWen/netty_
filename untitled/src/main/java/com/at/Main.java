package com.at;

import java.lang.reflect.Method;

/**
 * @create 2023-06-13
 */
public class Main {

    public static void main(String[] args) throws Exception {


        DynamicJdkCompiler dynamicJdkCompiler = new DynamicJdkCompiler();
//        String java = "public class TestJava {\n" + "\n" + "\tpublic String test(String param){\n" + "\t\tSystem.out" +
//                ".println(param);\n" + "\t\treturn param;\n" + "\t}\n" + "\n" + "}";


        String java = "package com.at;\n"
                + "\n"
                + "/**\n"
                + " * @create 2023-06-13\n"
                + " */\n"
                + "public class TestJava {\n"
                + "\n"
                + "    public String test(String param){\n"
                + "        System.out.println(param);\n"
                + "        return param;\n"
                + "    }\n"
                + "\n"
                + "}\n";

        Class<?> compile = dynamicJdkCompiler.compile(java);
        Object obj = compile.newInstance();
        Method m = compile.getMethod("test", String.class);
        Object invoke = m.invoke(obj, "123456");
        System.out.println(invoke.toString());
    }

}
