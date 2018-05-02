package com.eemp.timer.worker;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;



/****
 * 按时执行任务者父类
 * @author apple
 *
 */
public class Worker extends TimerTask implements java.io.Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public Worker(){}
	
	public Worker(String name , String des){
		this.name = name;
		this.des = des;
	}
	
	public Worker(String name , String des , boolean autoReplace){
		this.name = name;
		this.des = des;
		this.autoReplace = autoReplace;
	}
	
	public static Logger log = Logger.getLogger(Worker.class.getName());
	public transient Timer timer;//工作时钟
	public long workFrequency;//工作频率
	public String name;
	public String des;//对工人的描述

/*	public Timestamp startDate = java.util.Calendar.getInstance().getTime();//启动时间
	public Timestamp lastActivityDate = TimeUtil.getNow();//最后活跃时间*/
	public boolean autoReplace = false;//自动重启线程
	public boolean stop = false;

	public boolean isAutoReplace() {
		return autoReplace;
	}
	public void setAutoReplace(boolean autoReplace) {
		this.autoReplace = autoReplace;
	}
	@Override
	public void run() {
//		log.info("任务："+name + ",说明：" + des +",当前线程：" + Thread.currentThread().getName());
		//lastActivityDate = TimeUtil.getNow();
		if(stop){
			this.cancel();
		}
	}

	public long getWorkFrequency() {
		return workFrequency;
	}
	public String getName() {
		return name;
	}
	public String getDes() {
		return des;
	}

}
