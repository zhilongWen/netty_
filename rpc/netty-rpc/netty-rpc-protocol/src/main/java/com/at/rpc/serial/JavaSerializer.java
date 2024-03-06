package com.at.rpc.serial;


import com.at.rpc.constants.SerialType;

import java.io.*;

public class JavaSerializer implements ISerializer{
    @Override
    public <T> byte[] serialize(T obj) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {

            oos.writeObject(obj);
            byte[] res = bos.toByteArray();

            oos.close();
            bos.close();

            return res;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {

        ByteArrayInputStream bis = new ByteArrayInputStream(data);

        try {
            ObjectInputStream ois = new ObjectInputStream(bis);

            Object o = ois.readObject();

            ois.close();
            bis.close();

            return (T)o;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public byte getType() {
        return SerialType.JAVA_SERIAL.code();
    }
}
