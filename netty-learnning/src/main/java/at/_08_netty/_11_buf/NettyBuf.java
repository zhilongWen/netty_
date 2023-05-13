package at._08_netty._11_buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * @create 2022-04-25
 */
public class NettyBuf {

    public static void main(String[] args) {

        //创建一个buf
        ByteBuf buffer = Unpooled.copiedBuffer("hello,world!未来", CharsetUtil.UTF_8);


        if(!buffer.hasArray()) return;

        byte[] content = buffer.array();

        //将 content 转为 string
        System.out.println(new String(content, Charset.forName("utf-8")));


        System.out.println("buffer：" + buffer);

        System.out.println(buffer.arrayOffset()); //0
        System.out.println(buffer.readerIndex()); //0
        System.out.println(buffer.writerIndex()); //18
        System.out.println(buffer.capacity()); //64

        System.out.println(buffer.readableBytes()); // 18

//        System.out.println(buffer.readByte()); // 104
//        System.out.println(buffer.readableBytes()); //17

        System.out.println(buffer.getByte(0)); //104 -> h
        System.out.println(buffer.readableBytes()); // 18


        for (int i = 0; i < buffer.readableBytes(); i++) {
            System.out.println((char) buffer.getByte(i));
        }


        //按照某个范围读取
        System.out.println(buffer.getCharSequence(0, 4, Charset.forName("utf-8")));
        System.out.println(buffer.getCharSequence(4, 6, Charset.forName("utf-8")));




    }

}
