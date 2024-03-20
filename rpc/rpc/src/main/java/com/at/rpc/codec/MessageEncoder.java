package com.at.rpc.codec;

import com.at.rpc.MessageProtocol;
import com.at.rpc.RpcConstant;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;


/**
 * 传输编码协议
 *
 * <pre>
 *   0     1     2     3     4      5      6     7     8     9      10     11     12    13    14    15   16
 *   +-----+-----+-----+-----+------+------+-----+-----+-----+------+------+------+-----+-----+-----+-----+
 *   |   magic   code        | type | flag |    message length      |              content                |
 *   +-----------------------+------+------+-----+------+----+------+------+------+-----+-----+-----+-----+
 *   |                                                                                                    |
 *   |                                             body                                                   |
 *   |                                                                                                    |
 *   |                                            ... ...                                                 |
 *   +----------------------------------------------------------------------------------------------------+
 * 4B  magic code（魔数）       1B type（消息类型）        4B full length（消息长度）
 * body（object类型数据）
 * </pre>
 *
 * @Author: ppphuang
 * @Create: 2022/4/4
 */
public class MessageEncoder extends MessageToByteEncoder<MessageProtocol> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MessageProtocol msg, ByteBuf out) throws Exception {

        if (msg == null) {
            throw new Exception("msg is null");
        }

        out.writeBytes(RpcConstant.MAGIC); // (byte) 'g', (byte) 'r', (byte) 'p', (byte) 'c'} 4B
        out.writeByte(msg.getType()); // 1B
        out.writeByte(msg.getFlag()); // 1B
        out.writeInt(msg.getLength() + 8); // 4B

        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(msg.getRequestId());
        byte[] requestIdBytes = buffer.array();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(requestIdBytes);
        outputStream.write(msg.getContent());
        byte[] content = outputStream.toByteArray();


        out.writeBytes(content);


    }
}
