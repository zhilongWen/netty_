package at._08_netty._10_http_demo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.spdy.SpdyHeaders;
import io.netty.util.CharsetUtil;

/**
 *
 * @create 2022-04-23
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    /*
        HttpObject 客户端和服务器端相互通讯的数据被封装成 HttpObject
        SimpleChannelInboundHandler 是 ChannelInboundHandlerAdapter
     */

    /**
     * 读取client端数据
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {


        //每个浏览器都是独立的 channel pipline

        System.out.println("ChannelHandlerContext: " + ctx );
        System.out.println("channel: " + ctx.channel().hashCode() );
        System.out.println("channel 的 pipeline : " + ctx.channel().pipeline().hashCode() );
        System.out.println("pipeline: " + ctx.pipeline().hashCode() );
        System.out.println("pipeline 的 channel: " + ctx.pipeline().channel().hashCode() );

        System.out.println("server端的handler：" + ctx.handler());


        //断 msg 是不是 httpRequest请求
        if(msg instanceof HttpRequest){

            System.out.println("msg 类型：" + msg.getClass());
            System.out.println("客户端地址：" + ctx.channel().remoteAddress());

            ByteBuf buffer = Unpooled.copiedBuffer("Hello! 我是服务器 ..", CharsetUtil.UTF_8);


            //过滤掉请求图标请求
            HttpRequest httpRequest = (HttpRequest) msg;
            String uri = httpRequest.uri();
            System.out.println("http uri：" + uri);

            if("/favicon.ico".equals(uri)) return;

            System.out.println("............................");



            //构造一个http响应
            HttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buffer);

            httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain");
            httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH,buffer.readableBytes());

            ctx.writeAndFlush(httpResponse);

        }






    }
}
