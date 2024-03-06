package com.at.rpc.serial;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.at.rpc.constants.SerialType;

import java.nio.charset.StandardCharsets;

public class JsonSerializer implements ISerializer{

    @Override
    public <T> byte[] serialize(T obj) {
        return JSONObject.toJSONString(obj).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        return JSON.parseObject(new String(data,StandardCharsets.UTF_8),clazz);
    }

    @Override
    public byte getType() {
        return SerialType.JSON_SERIAL.code();
    }
}
