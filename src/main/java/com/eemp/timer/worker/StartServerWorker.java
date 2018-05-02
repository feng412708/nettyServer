package com.eemp.timer.worker;

import com.eemp.websocket.server.WebSocketServer;


public class StartServerWorker extends Worker {

    private static final long serialVersionUID = 7868569197081557700L;

    private static boolean isServerStart = false; //服务器是否已经启动

    public StartServerWorker(String name, String des) {
        super(name, des);
    }

    @Override
    public void run() {
        super.run();
        if (!StartServerWorker.isServerStart) {
            try {
                WebSocketServer.startServer();
                StartServerWorker.isServerStart = true;
                log.info("netty服务器已启动成功");
            } catch (Exception e) {
                e.printStackTrace();
                log.error("netty服务器已启动失败");
            }
        }
    }

}
