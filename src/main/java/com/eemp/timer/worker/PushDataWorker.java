package com.eemp.timer.worker;

import com.alibaba.fastjson.JSONObject;
import com.eemp.dataprocess.RedisData;

import com.eemp.utils.RedisUtil;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;

import com.eemp.websocket.server.ChannelBussinessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class PushDataWorker extends Worker {

    private static final long serialVersionUID = 7868569197081557700L;
    private static Logger log = Logger.getLogger(PushDataWorker.class.getName());


    public static ExecutorService executorService = Executors.newFixedThreadPool(5);

    public static int i = 0;


    RedisData redisData = new RedisData();

    public PushDataWorker(String name, String des) {
        super(name, des);
    }

    @Override
    public void run() {
        super.run();
        long s1 = System.currentTimeMillis();
        //推送数据
        webSoketPushData();
        long s2 = System.currentTimeMillis();
        // log.info("推送盘口数据总耗时：" + (s2 - s1));
    }

    // websocket数据推送
    private void webSoketPushData() {

        try {
            i++;

            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    String channelName = "test";
                    CopyOnWriteArraySet<Channel> set = ChannelBussinessHandler.channelMap.get(channelName);
                    //if(channelName.equals("dish_depth_001_btcdefault")){
                    //	System.err.println(channelName+">>"+set);
                    //}

                    if (set != null && set.size() > 0) {
                        JSONObject msg = new JSONObject();
                        msg.put("i",i);
                        msg.put("channel",channelName);
                         msg.put("msg",        redisData.readData() );
                        ChannelBussinessHandler.sendMsg(set, msg.toJSONString());

                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
