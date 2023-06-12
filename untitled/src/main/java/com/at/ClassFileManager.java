package com.at;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClassFileManager extends ForwardingJavaFileManager<JavaFileManager> {
    private JavaClassObject mainObject;

    private List<JavaClassObject> innerObjects = new ArrayList<JavaClassObject>();

    protected ClassFileManager(JavaFileManager fileManager) {
        super(fileManager);
    }

    public byte[] getJavaClass() {
        return mainObject.getBytes();
    }

    public JavaClassObject getJavaClassObject() {
        return mainObject;
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind,
                                               FileObject sibling) throws IOException {
        // 最后一个是mainObject
        mainObject = new JavaClassObject(className, kind);
        innerObjects.add(mainObject);
        return mainObject;
    }

    public List<JavaClassObject> getInnerClassJavaClassObject() {
        if (this.innerObjects != null && this.innerObjects.size() > 0) {
            int size = this.innerObjects.size();
            if (size == 1) {
                return Collections.emptyList();
            }
            return this.innerObjects.subList(0, size - 1);
        }
        return Collections.emptyList();
    }

    @Override
    public void close() throws IOException {
        if (null != mainObject) {
            mainObject.delete();
        }
        super.close();
    }
}