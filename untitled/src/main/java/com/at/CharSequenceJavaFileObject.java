package com.at;

import org.apache.commons.io.IOUtils;

import javax.tools.SimpleJavaFileObject;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.net.URI;

public class CharSequenceJavaFileObject extends SimpleJavaFileObject {

    /**
     * CharSequence representing the source code to be compiled
     */
    private String content;

    /**
     * This constructor will store the source code in the internal "content"
     * variable and register it as a source code, using a URI containing the
     * class full name
     *
     * @param className
     *            name of the public class in the source code
     * @param code
     *            source code to compile
     */
    public CharSequenceJavaFileObject(String className, String code) {
        super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.content = code;
    }

    /**
     * Answers the CharSequence to be compiled. It will give the source code
     * stored in variable "content"
     */
    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return content;
    }

    /**
     * 获取某个位置的代码
     */
    public String getLineCode(long line) {
        LineNumberReader reader = new LineNumberReader(new StringReader(content));
        int num = 0;
        String codeLine = null;
        try {
            while ((codeLine = reader.readLine()) != null) {
                num++;
                if (num == line) {
                    break;
                }
            }
        } catch (Throwable ignored) {

        } finally {
            IOUtils.closeQuietly(reader);
        }
        return codeLine;
    }
}