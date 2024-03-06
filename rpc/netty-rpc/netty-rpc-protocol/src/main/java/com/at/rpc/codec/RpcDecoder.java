package com.at.rpc.codec;


import com.at.rpc.constants.ReqType;
import com.at.rpc.constants.RpcConstant;
import com.at.rpc.core.Header;
import com.at.rpc.core.RpcProtocol;
import com.at.rpc.core.RpcRequest;
import com.at.rpc.core.RpcResponse;
import com.at.rpc.serial.ISerializer;
import com.at.rpc.serial.SerializerManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class RpcDecoder extends ByteToMessageDecoder {

    /*
    +----------------------------------------------+
    | 魔数 2byte | 序列化算法 1byte | 请求类型 1byte  |
    +----------------------------------------------+
    | 消息 ID 8byte     |      数据长度 4byte       |
    +----------------------------------------------+
    */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        log.info("==========begin RpcDecoder ==============");

        if (in.readableBytes() < RpcConstant.HEAD_TOTAL_LEN) {
            // 消息长度不够，不需要解析
            return;
        }

        // 标记一个读取数据的索引，后续用来重置
        in.markReaderIndex();

        short magic = in.readShort();
        if (magic != RpcConstant.MAGIC) {
            return;
        }

        byte serialType = in.readByte();
        byte reqType = in.readByte();
        long requestId = in.readLong();
        int dataLen = in.readInt();

        // 可读区域的字节数小于实际数据长度
        if (in.readableBytes() < dataLen) {
            in.resetReaderIndex();
            return;
        }

        // 读取消息内容
        byte[] content = new byte[dataLen];
        in.readBytes(content);


        Header header = new Header(magic, serialType, reqType, requestId, dataLen);
        ISerializer serializer = SerializerManager.getSerializer(serialType);
        ReqType code = ReqType.findByCode(reqType);

        switch (code) {
            case REQUEST:
                RpcRequest request = serializer.deserialize(content, RpcRequest.class);
                RpcProtocol<RpcRequest> reqRpcProtocol = new RpcProtocol<>();
                reqRpcProtocol.setHeader(header);
                reqRpcProtocol.setContent(request);
                out.add(reqRpcProtocol);
                break;
            case RESPONSE:
                RpcResponse response = serializer.deserialize(content, RpcResponse.class);
                RpcProtocol<RpcResponse> resRpcProtocol = new RpcProtocol<>();
                resRpcProtocol.setHeader(header);
                resRpcProtocol.setContent(response);
                out.add(resRpcProtocol);
                break;
            case HEARTBEAT:
                break;
            default:
                break;
        }


    }
}
