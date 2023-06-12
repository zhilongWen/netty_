package com.at;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @create 2023-06-13
 */
public class DynamicClassLoader extends URLClassLoader {

    public DynamicClassLoader(ClassLoader parent) {
        super(new URL[0], parent);
    }

    public static String getClassFullName(JavaClassObject javaClassObject) {
        String name = javaClassObject.getName();
        name = name.substring(1, name.length() - 6);
        name = name.replace("/", ".");
        return name;
    }

    public Class<?> defineClass(JavaClassObject javaClassObject) {
        String name = getClassFullName(javaClassObject);
        byte[] classData = javaClassObject.getBytes();
        return super.defineClass(name, classData, 0, classData.length);
    }

}