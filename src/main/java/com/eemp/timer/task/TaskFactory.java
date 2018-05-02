package com.eemp.timer.task;

/**
 * Created by A03742 on 2017-07-21.
 */


import org.apache.log4j.Logger;
import com.eemp.timer.worker.Worker;
import java.util.ArrayList;
import java.util.List;

public class TaskFactory {
    public static Logger log = Logger.getLogger(TaskFactory.class.getName());
    ////记录工厂内所有的员工
    public static List<Worker> workers = new ArrayList<Worker>();

    public static List<Boss> bosses = new ArrayList<Boss>();

    public static List<Worker> getWorkers(){
        return workers;
    }

    public static void work(Worker worker , long frequency){
        boolean ipOpen = true;
        work(worker , frequency , ipOpen);
    }

    /***
     * 工人开工
     * @param worker   具体的工人
     * @param frequency  工作频率，多久工作一次
     */
    public static void work(Worker worker , long frequency , boolean ipOpen){
        String className = worker.getClass().getSimpleName();
        //未开启
        if(!ipOpen){
            log.info("定时器：" + worker.getName() + "未开启。" + className);
            return;
        }else{
            log.info("定时器：" + worker.getName() + "已开启。" + className);
        }

        Boss boss = new Boss();
        worker.workFrequency = frequency;//取一次价格
        boss.setWorker(worker);
        boss.start(worker.name);
        workers.add(worker);
        bosses.add(boss);
    }


    public static void shutdown(){
        try {
            for(Boss w : bosses){

                try {
                    w.getWorker().stop = true;
                    if(w.getWorker().isAutoReplace() && w.service != null){
                        //w.service.shutdownNow();
                        w.scheduledFuture.cancel(true);
                        w.scheduledFuture = null;

                        if (!w.service.isShutdown()) {
                            try {
                                if(!w.service.isShutdown()) {
                                    w.service.shutdownNow();//关闭线程池 (强行关闭)
                                }
                            }finally{
                                if (!w.service.isShutdown()) {
                                    w.service.shutdownNow();
                                }
                            }
                        }
                        log.info("所有线程关闭"+w.service.isShutdown());

                        log.info("service---销毁：" + w.getWorker().getName());
                        w.service = null;
                    }else{
                        if(w.getWorker().timer != null){
                            w.getWorker().timer.cancel();
                            w.getWorker().timer = null;
                        }
                        log.info("timer---销毁：" + w.getWorker().getName());
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
