package com.eemp.timer;

/**
 * Created by A03742 on 2017-07-21.
 */

import com.eemp.timer.task.TaskFactory;
import com.eemp.timer.worker.PushDataWorker;
import com.eemp.timer.worker.StartServerWorker;
import io.netty.util.concurrent.Future;
import com.eemp.websocket.server.WebSocketServer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class Listener implements ServletContextListener{
    //销毁
    public void contextDestroyed(ServletContextEvent event) {

        Future fb = WebSocketServer.bossGroup.shutdownGracefully();
        Future fw = WebSocketServer.workerGroup.shutdownGracefully();
        try {
            fb.await();
            fw.await();
            TaskFactory.shutdown();
        } catch (InterruptedException ignore) {}
        System.out.println("定时器销毁-----");
    }

    //初始化监听器
    public void contextInitialized(ServletContextEvent event) {
        startWork();
        event.getServletContext().log("定时器容器已启动");
        event.getServletContext().log("已经添加任务");
        System.out.println("启动定时器");
    }

    private void startWork(){
        //启动netty服务
         TaskFactory.work(new StartServerWorker("START_NETTY_SERVER" , "nettyserver"), 1000);
      /*  try {
            WebSocketServer.startServer();
        }catch (Exception e){
            e.printStackTrace();
        }*/
        //启动推送数据服务
        TaskFactory.work(new PushDataWorker("PushDataWorker","PushDataWorker"),2000);

    };
}
