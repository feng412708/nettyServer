/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.eemp.websocket.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import java.io.File;

/**
 * A HTTP server which serves Web Socket requests at:
 *
 * http://localhost:8080/websocket
 *
 * Open your browser at http://localhost:8080/, then the demo page will be loaded and a Web Socket connection will be
 * made automatically.
 *
 * This server illustrates support for the different web socket specification versions and will work with:
 *
 * <ul>
 * <li>Safari 5+ (draft-ietf-hybi-thewebsocketprotocol-00)
 * <li>Chrome 6-13 (draft-ietf-hybi-thewebsocketprotocol-00)
 * <li>Chrome 14+ (draft-ietf-hybi-thewebsocketprotocol-10)
 * <li>Chrome 16+ (RFC 6455 aka draft-ietf-hybi-thewebsocketprotocol-17)
 * <li>Firefox 7+ (draft-ietf-hybi-thewebsocketprotocol-10)
 * <li>Firefox 11+ (RFC 6455 aka draft-ietf-hybi-thewebsocketprotocol-17)
 * </ul>
 */
public final class WebSocketServer {

	public static final boolean SSL = false;
    public static int PORT ;
    public static EventLoopGroup bossGroup = null;
    public static EventLoopGroup workerGroup = null;

    
    public static void startServer() throws Exception {
		String ssl_port = "8443";
		String port = "8580";
		PORT = Integer.parseInt( SSL? ssl_port : port );
    	
        // Configure SSL.
        final SslContext sslCtx;
        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            
            File keyCertChainFile = new File("a.pem");
            File keyFile = new File("a.key");
            File rootFile = new File("root.cer");
            
            sslCtx = SslContextBuilder.forServer(keyCertChainFile, keyFile)
            		.trustManager(rootFile)
            		.clientAuth(ClientAuth.REQUIRE).build();
            
            //sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }

        bossGroup = new NioEventLoopGroup(); //用于处理服务器端接收客户端连接
        workerGroup = new NioEventLoopGroup();//进行网络通信（读写）
        try {
            ServerBootstrap b = new ServerBootstrap();//辅助工具类，用于服务器通道的一系列配置
            b.group(bossGroup, workerGroup)//绑定两个线程组
             .channel(NioServerSocketChannel.class)//指定NIO的模式
           //  .handler(new LoggingHandler(LogLevel.INFO))
             .childHandler(new WebSocketServerInitializer(sslCtx));//配置具体的数据处理方式


            
            b.option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 256 * 1024);
            b.option(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 128 * 1024);
            b.option(ChannelOption.SO_BACKLOG, 128) //设置TCP缓冲区
                    .option(ChannelOption.SO_SNDBUF, 32 * 1024) //设置发送数据缓冲大小
                    .option(ChannelOption.SO_RCVBUF, 32 * 1024)  //设置接受数据缓冲大小
                    .childOption(ChannelOption.SO_KEEPALIVE, true); //保持连接
            /**
             * 对于ChannelOption.SO_BACKLOG的解释：
             * 服务器端TCP内核维护有两个队列，我们称之为A、B队列。客户端向服务器端connect时，会发送带有SYN标志的包（第一次握手），服务器端
             * 接收到客户端发送的SYN时，向客户端发送SYN ACK确认（第二次握手），此时TCP内核模块把客户端连接加入到A队列中，然后服务器接收到
             * 客户端发送的ACK时（第三次握手），TCP内核模块把客户端连接从A队列移动到B队列，连接完成，应用程序的accept会返回。也就是说accept
             * 从B队列中取出完成了三次握手的连接。
             * A队列和B队列的长度之和就是backlog。当A、B队列的长度之和大于ChannelOption.SO_BACKLOG时，新的连接将会被TCP内核拒绝。
             * 所以，如果backlog过小，可能会出现accept速度跟不上，A、B队列满了，导致新的客户端无法连接。要注意的是，backlog对程序支持的
             * 连接数并无影响，backlog影响的只是还没有被accept取出的连接
             */

            ChannelFuture future = b.bind(PORT).sync();
            future.channel().closeFuture().sync();

          //  ch = b.bind(PORT).sync().channel();

            System.out.println("Open your web browser and navigate to " +
                    (SSL? "https" : "http") + "://127.0.0.1:" + PORT + '/');
           // ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
        
    }
}
