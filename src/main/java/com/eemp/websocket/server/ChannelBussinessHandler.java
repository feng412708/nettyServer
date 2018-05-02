package com.eemp.websocket.server;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

public class ChannelBussinessHandler {
    public static Map<String, CopyOnWriteArraySet<Channel>> channelMap = new HashMap<String, CopyOnWriteArraySet<Channel>>();


    /**
     *  业务处理方法
     * @param cl 通道
     * @param requestData 请求数据
     */
    public static void execute(Channel cl, String requestData) {
        try {
            JSONObject requestObj = null;
            try {
                requestObj = JSONObject.parseObject(requestData);
            } catch (Exception e) {
                //cl.writeAndFlush("无效数据");
                sendChannelMsg(cl,"无效数据");
                return;
            }
            //命令
            String event = requestObj.getString("event");
            ChannelProperty.Event eObj = ChannelProperty.Event.getEvent(event);
            if (eObj == null) {
                //cl.writeAndFlush("无效命令");
                sendChannelMsg(cl,"无效命令");
                return;
            }
            //接口类型
            String channel = requestObj.getString("channel");
            ChannelProperty.ChannelInfo channelInfo = ChannelProperty.ChannelInfo.getChannelInfo(channel);
            if (channelInfo == null) {
                sendChannelMsg(cl,"无效通道");
                //cl.writeAndFlush("无效通道");
                return;
            }


            //保持长链接通信
            if (channelInfo.isKeepAlive()) {
                CopyOnWriteArraySet<Channel> set = getChannelSet(channel);
                switch (eObj) {
                    case addChannel: {
                        setChannelAttr(requestObj, cl);
                        set.add(cl);
                        break;
                    }
                    case removeChannel: {
                        if (set.contains(cl)) {
                            set.remove(cl);
                        }
                        break;
                    }
                }
                //log.info("链接IP："+cl.remoteAddress().toString()+"  event: "+event+"  channel: "+channel+" "+set.size()+"  isopen:"+cl.isOpen()+" isactive :"+cl.isActive() );
                return;
            }



            //cl.writeAndFlush(getResultNormal(data, channelInfo.getKey() ));
            return;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            cl.writeAndFlush(getResult(false, "", null, null));
            return;
        }
    }

    /**
     * 返回信息
     *
     * @param success
     * @param errorCode
     * @param data
     * @return
     */
    private static TextWebSocketFrame getResult(boolean success, String errorCode, String data, String type) {
        JSONObject obj = new JSONObject();
        obj.put("success", success);
        if (errorCode != null) {
            obj.put("ecode", errorCode);
        }
        if (data != null) {
            obj.put("data", data);
        }
        if (type != null) {
            obj.put("channel", type);
        }
        return new TextWebSocketFrame(obj.toString());
    }


    /**
     * 获取频道集合
     *
     * @param channel
     * @return
     */
    public synchronized static CopyOnWriteArraySet<Channel> getChannelSet(String channel) {
        CopyOnWriteArraySet<Channel> set = channelMap.get(channel);
        if (set == null) {
            set = new CopyOnWriteArraySet<Channel>();
            channelMap.put(channel, set);
        }
        return set;
    }


    /**
     * 发送消息
     *
     * @param set 频道通道集合
     * @param msg 发送消息内容
     */
    public static void sendMsg(CopyOnWriteArraySet<Channel> set, String msg) {
        String channelName = "";
        try {
            //日志打印频道名
            JSONObject msgJSON = JSONObject.parseObject(msg);
            channelName = msgJSON.getString("channel");
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if (set == null) return;
        try {
            if (set.size() == 0) return;

            Iterator<Channel> it = set.iterator();
            int pushTimes = 0;
            String pushIps = "";
            while (it.hasNext()) {
                Channel channel = it.next();
                if (channel.isActive()) {
                    channel.writeAndFlush(new TextWebSocketFrame(msg));
                    pushIps += "," + channel.remoteAddress().toString();
                    pushTimes++;
                } else {
                    set.remove(channel);
                }
            }
            //log.info("推送消息的频道名=" + channelName + "，本次推送了" + pushTimes + "条，推送到=" + pushIps);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送消息
     *
     * @param set 频道通道集合
     * @param msg 发送消息内容
     */
    public static void sendBinaryMsg(CopyOnWriteArraySet<Channel> set, String msg, String channelName) {

        if (set == null) return;
        try {
            if (set.size() == 0) return;
            String frameType = "";
            String zipMsg = msg;//ZipUtil.gzip(msg);//压缩数据
            AttributeKey<String> key = AttributeKey.valueOf(channelName + "_binary");
            AttributeKey<String> zipKey = AttributeKey.valueOf(channelName + "_isZip");//判断是否要发送压缩后数据


            Iterator<Channel> it = set.iterator();
            while (it.hasNext()) {
                Channel channel = it.next();
                if (channel.isActive()) {
                    String sendMsg = msg;
                    if (channel.attr(key) != null) {
                        frameType = channel.attr(key).get();//发送二进制数据
                    }

                    if (channel.attr(zipKey) != null) {
                        String isZip = channel.attr(zipKey).get();
                        if (isZip != null && isZip.equals("true")) {
                            sendMsg = zipMsg;//压缩数据
                        }
                    }
                    if (frameType != null && frameType.equals("false")) {
                        channel.writeAndFlush(new TextWebSocketFrame(sendMsg));
                    } else {
                        channel.writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(sendMsg.getBytes())));
                    }
                } else {
                    set.remove(channel);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean sendChannelMsg(Channel channel, String msg) {
        if (channel != null) {
            channel.writeAndFlush(new TextWebSocketFrame(msg));
            System.err.println(msg);
            return true;
        } else {
            return false;
        }
    }


    /**
     * 设置频道属性
     *
     * @param requestObj
     * @param cl
     */
    public static void setChannelAttr(JSONObject requestObj, Channel cl) {

        //将RequestObj里面的内容都保存到channel属性里面去
        if (requestObj != null) {
            String channelName = requestObj.getString("channel");
            Iterator<String> it = requestObj.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next().toString();
                String value = requestObj.getString(key);
                if (key != null && !key.equals("event") && !key.equals("channel")) {
                    AttributeKey<String> attkey = AttributeKey.valueOf(channelName + "_" + key);
                    if (value != null && !value.equals("")) {
                        cl.attr(attkey).set(value);
                    }
                }

            }
        }
    }

    /**
     * 从请求参数里面获取no这个参数，如果没有的话返回0
     *
     * @param requestObj
     * @return
     */
    public static String getReqNo(JSONObject requestObj) {
        if (requestObj == null) {
            return "0";
        } else {
            if (requestObj.get("no") == null) {
                return "0";
            } else {
                return requestObj.getString("no");
            }
        }
    }
}
