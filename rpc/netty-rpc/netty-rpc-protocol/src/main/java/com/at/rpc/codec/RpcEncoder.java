package com.at.rpc.codec;


import com.at.rpc.core.Header;
import com.at.rpc.core.RpcProtocol;
import com.at.rpc.serial.ISerializer;
import com.at.rpc.serial.SerializerManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcEncoder extends MessageToByteEncoder<RpcProtocol<Object>> {

        /*
    +----------------------------------------------+
    | 魔数 2byte | 序列化算法 1byte | 请求类型 1byte  |
    +----------------------------------------------+
    | 消息 ID 8byte     |      数据长度 4byte       |
    +----------------------------------------------+
    */

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcProtocol<Object> msg, ByteBuf out) throws Exception {

        log.info("=============begin RpcEncoder============");

        Header header = msg.getHeader();

        // 写入魔数
        out.writeShort(header.getMagic());
        // 写入序列化类型
        out.writeByte(header.getSerialType());
        // 写入请求类型
        out.writeByte(header.getReqType());
        // 写入请求id
        out.writeLong(header.getRequestId());

        ISerializer serializer = SerializerManager.getSerializer(header.getSerialType());
        byte[] data = serializer.serialize(msg.getContent());

        header.setLength(data.length);

        // 写入消息长度
        out.writeInt(data.length);
        // 写入消息
        out.writeBytes(data);


    }
}
