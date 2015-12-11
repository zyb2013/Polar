package com.game.server.thread;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.game.db.dao.GoldRaffleDao;
import com.game.goldraffle.structs.GoldRaffleData;

/**
 * 保存钻石抽奖线程
 */
public class SaveGoldRaffleThread extends Thread {

	//日志
	private Logger log = LogManager.getLogger(SaveGoldRaffleThread.class);
	
	//日志
	private Logger failedlog = LogManager.getLogger("SAVEGOLDRAFFLEFAILED");
		
	//命令执行队列
	private LinkedBlockingQueue<GoldRaffleData> gold_raffle_queue = new LinkedBlockingQueue<GoldRaffleData>();
	
	//运行标志
	private boolean stop;
	
	//线程名称
	protected String threadName;
	
	boolean insertDB = true;
	
	private static int MAX_SIZE = 10000;
	
	private GoldRaffleDao dao = new GoldRaffleDao();
		
	public SaveGoldRaffleThread(String threadName){
		super(threadName);
		this.threadName = threadName;
	}
	
	public void run(){
		stop = false;
		while(!stop || gold_raffle_queue.size() > 0){
			GoldRaffleData goldRaffleData = gold_raffle_queue.poll();
			if(goldRaffleData == null){
				try{
					synchronized (this) {
						wait();
					}
				}catch (InterruptedException e) {
					log.error("Save Gold Raffle Thread " + threadName + " Wait Exception:" + e.getMessage());
				}
			}else{
				try{
					if(gold_raffle_queue.size() > MAX_SIZE){
						insertDB = false;
					}
					if(insertDB){
						dao.save(goldRaffleData);
						log.debug(goldRaffleData);
					}else{
						failedlog.debug(goldRaffleData);
					}
				}catch (Exception e) {
					failedlog.error("Gold Raffle Exception:" + goldRaffleData, e);
					gold_raffle_queue.add(goldRaffleData);
				}
			}
		}
	}
	
	public void stop(boolean flag){
		stop = flag;
		try{
			synchronized (this) {
				notify();
			}
		}catch (Exception e) {
			log.error("Gold Raffle Thread " + threadName + " Notify Exception:" + e.getMessage());
		}
	}
	
	/**
	 * 添加钻石抽奖数据
	 * @param goldRaffleData 钻石抽奖数据
	 */
	public void addGoldRaffleData(GoldRaffleData goldRaffleData){
		try{
			this.gold_raffle_queue.add(goldRaffleData);
			synchronized (this) {
				notify();
			}
		}catch (Exception e) {
			log.error("Gold Raffle Thread " + threadName + " Notify Exception:" + e.getMessage());
		}
	}
	
}
