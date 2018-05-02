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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.util.CharsetUtil;

import java.net.SocketAddress;

/**
 * Generates the demo HTML page which is served at http://localhost:8080/
 */
public final class WebSocketServerCountPage {

    private static final String NEWLINE = "\r\n";

    public static ByteBuf getContent(String webSocketLocation) {
        StringBuffer ipStr = new StringBuffer(); //显示客户端ip列表的内容

        for (Channel channel : WebSocketServerHandler.channels) {
            SocketAddress scoketAddress = channel.remoteAddress();
            String clientIP = scoketAddress.toString();
            ipStr.append("<br/>连接客户端ip: " + clientIP);
        }
        return Unpooled.copiedBuffer(
                "<html><head><meta http-equiv=\"refresh\" content=\"5\">"
                        + "<META http-equiv=\"Content-type\" content=\"text/html; charset=utf-8\">"
                        + "<title>Web Socket Test</title></head>" + NEWLINE +
                        "<body>" + NEWLINE +
                        "<script type=\"text/javascript\">" + NEWLINE +
                        "</script>" + NEWLINE +
                        "netty_req_headers:" + NEWLINE + ipStr +
                        "</body>" + NEWLINE +
                        "</html>" + NEWLINE, CharsetUtil.UTF_8);
    }

    private WebSocketServerCountPage() {
        // Unused
    }
}
