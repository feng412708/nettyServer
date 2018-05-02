package com.eemp.timer.task;


import com.alibaba.fastjson.JSONObject;
import com.eemp.utils.RedisUtil;
import com.eemp.websocket.server.ChannelBussinessHandler;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by A03742 on 2017-07-17.
 */
@Component
public class PushSnapshotTask  {



    @Autowired
    private RedisUtil redisUtil;
    //首次延迟启动时间 10秒
    private final long DELAY_TIME = 1000;
    //任务执行频率时间 30秒
    private final long EXECUTE_RATE = 1000;

    public static ExecutorService executorService = Executors.newFixedThreadPool(5);

    //日志对象
    private Logger log = Logger.getLogger(this.getClass());



    @Scheduled(initialDelay = DELAY_TIME, fixedRate = EXECUTE_RATE)
    public void run() {
        try {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    String channelName = "test";
                    CopyOnWriteArraySet<Channel> set = ChannelBussinessHandler.channelMap.get(channelName);
                    //if(channelName.equals("dish_depth_001_btcdefault")){
                    //	System.err.println(channelName+">>"+set);
                    //}
                    long t2 = System.currentTimeMillis();
                    if (set != null && set.size() > 0) {
                        JSONObject msg = new JSONObject();

                        msg.put("channel",channelName);
                        msg.put("msg", redisUtil.getDoubleValue("template"));
                        ChannelBussinessHandler.sendMsg(set, msg.toJSONString());
                        long t3 = System.currentTimeMillis();
                        System.out.println("推送耗时：" + (t3 - t2) + "ms");
                    }

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        } finally {

        }
    }


}
