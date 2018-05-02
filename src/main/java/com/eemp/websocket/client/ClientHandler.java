package com.eemp.websocket.client;



import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;


import java.nio.charset.Charset;

/**
 * Created by A03742 on 2018-04-28.
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {


    private String clientMessage;


    public ClientHandler() {

          clientMessage = "{\"event\":\"addChannel\",\"channel\":\"test\",\"key\":\"123456\"}";

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接已经建立："+ctx);
        ctx.writeAndFlush(clientMessage);
        System.out.println("发送请求消息："+clientMessage);

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        /* ByteBuf buf = (ByteBuf)msg;
        byte [] req = new byte[buf.readableBytes()];

        buf.readBytes(req);

        String message = new String(req,"UTF-8");

        System.out.println("Netty-Client:Receive Message,"+ message);*/
        System.out.println("Netty-Client:Receive Message,"+ (String)msg);
        ctx.fireChannelRead(msg);
    }

    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        ctx.close();
    }
}