package com.at.rpc.codec;

import com.at.rpc.MessageProtocol;
import com.at.rpc.RpcConstant;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class MessageDecoder extends LengthFieldBasedFrameDecoder {

    private static final int HEADER_SIZE = 10;

/*    public MessageDecoder() {
        // lengthFieldOffset: magic code is 4B, and type 1B, and flag 1B, and then message length, so value is 6
        // lengthFieldLength: full length is 4B. so value is 4
        // lengthAdjustment: full length include all data and read 10 bytes before, so the left length is (fullLength-10). so values is -10
        // initialBytesToStrip: we will check magic code and version manually, so do not strip any bytes. so values is 0
        this(RpcConstant.MAX_FRAME_LENGTH,6,4,0,0,false);
    }*/

    public MessageDecoder() {
        this(RpcConstant.MAX_FRAME_LENGTH, 6, 4, 0, 0, false);
    }

    /**
     * @param maxFrameLength      帧的最大长度
     * @param lengthFieldOffset   length字段偏移的地址
     * @param lengthFieldLength   length字段所占的字节长
     * @param lengthAdjustment    修改帧数据长度字段中定义的值，可以为负数 因为有时候我们习惯把头部记入长度,若为负数,则说明要推后多少个字段
     * @param initialBytesToStrip 解析时候跳过多少个长度
     * @param failFast            为true，当frame长度超过maxFrameLength时立即报TooLongFrameException异常，为false，读取完整个帧再报异
     */

    public MessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip, boolean failFast) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip, failFast);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {

        in = (ByteBuf) super.decode(ctx, in);

        if (in == null) {
            System.out.println("receive msg is null");
            return null;
        }

        if (in.readableBytes() < HEADER_SIZE) {
            throw new Exception("字节数不足");
        }

        byte[] bytes = new byte[4];
        in.readBytes(bytes);

        if (!Arrays.equals(bytes, RpcConstant.MAGIC)) {
            System.out.println("magic 异常");
            return null;
        }

        byte type = in.readByte();
        byte flag = in.readByte();
        int len = in.readInt();

        if (in.readableBytes() != len) {
            throw new Exception("标记的长度不符合实际长度");
        }

        bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);

        byte[] requestIdBytes = Arrays.copyOfRange(bytes, 0, 8);
        bytes = Arrays.copyOfRange(bytes, 8, len);

        long requestId = ByteBuffer.wrap(requestIdBytes).getLong();

        MessageProtocol messageProtocol = new MessageProtocol(type, flag, len, bytes, requestId);
        messageProtocol.setInputBuf(in);

        return messageProtocol;
    }
}
