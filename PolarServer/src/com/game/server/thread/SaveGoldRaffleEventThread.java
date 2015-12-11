package com.game.server.thread;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.game.db.dao.GoldRaffleEventDao;
import com.game.goldraffle.structs.GoldRaffleEventData;

/**
 * 保存钻石抽奖日志线程
 */
public class SaveGoldRaffleEventThread extends Thread {

	//日志
	private Logger log = LogManager.getLogger(SaveGoldRaffleEventThread.class);
	
	//日志
	private Logger failedlog = LogManager.getLogger("SAVEGOLDRAFFLEEVENTFAILED");
		
	//命令执行队列
	private LinkedBlockingQueue<GoldRaffleEventDataInfo> gold_raffle_event_queue = new LinkedBlockingQueue<GoldRaffleEventDataInfo>();
	
	//运行标志
	private boolean stop;
	
	//线程名称
	protected String threadName;
	
	boolean insertDB = true;
	
	private static int MAX_SIZE = 10000;
	
	public static int MAIL_INSERT = 0;
	
	public static int MAIL_DELETE = 1;
	
	private GoldRaffleEventDao dao = new GoldRaffleEventDao();
		
	public SaveGoldRaffleEventThread(String threadName){
		super(threadName);
		this.threadName = threadName;
	}
	
	public void run(){
		stop = false;
		while(!stop || gold_raffle_event_queue.size() > 0){
			GoldRaffleEventDataInfo goldRaffleEventDataInfo = gold_raffle_event_queue.poll();
			if(goldRaffleEventDataInfo == null){
				try{
					synchronized (this) {
						wait();
					}
				}catch (InterruptedException e) {
					log.error("Save Gold Raffle Thread " + threadName + " Wait Exception:" + e.getMessage());
				}
			}else{
				try {
					if(gold_raffle_event_queue.size() > MAX_SIZE){
						insertDB = false;
					}
					if(insertDB){
						if (goldRaffleEventDataInfo.getDeal() == MAIL_INSERT) {
							if (dao.insert(goldRaffleEventDataInfo.getGoldRaffleEventData()) == -1) {
								log.error(goldRaffleEventDataInfo.getGoldRaffleEventData());
							}
						} else if (goldRaffleEventDataInfo.getDeal() == MAIL_DELETE) {
							if (dao.delete(goldRaffleEventDataInfo.getGoldRaffleEventData().getEventId()) == -1) {
								log.error(goldRaffleEventDataInfo.getGoldRaffleEventData());
							}
						}
					}else{
						failedlog.error(goldRaffleEventDataInfo.getGoldRaffleEventData());
					}
				} catch (Exception e) {
					log.error("Deal GoldRaffleEventData Exception:", e);
					gold_raffle_event_queue.add(goldRaffleEventDataInfo);
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
			log.error("Gold Raffle Event Thread " + threadName + " Notify Exception:" + e.getMessage());
		}
	}
	
	/**
	 * 处理玩家钻石抽奖事件
	 * @param goldRaffleEventData
	 * @param deal 0-insert 1-delete
	 */
	public void dealGoldRaffleEventData(GoldRaffleEventData goldRaffleEventData, int deal){
		try{
			this.gold_raffle_event_queue.add(new GoldRaffleEventDataInfo(goldRaffleEventData, deal));
			synchronized (this) {
				notify();
			}
		}catch (Exception e) {
			log.error("Gold Raffle Event Thread " + threadName + " Notify Exception:" + e.getMessage());
		}
	}

	private class GoldRaffleEventDataInfo {

		private GoldRaffleEventData goldRaffleEventData;
		
		private int deal;

		public GoldRaffleEventDataInfo(GoldRaffleEventData goldRaffleEventData, int deal) {
			this.goldRaffleEventData = goldRaffleEventData;
			this.deal = deal;
		}

		public GoldRaffleEventData getGoldRaffleEventData() {
			return goldRaffleEventData;
		}

		public int getDeal() {
			return deal;
		}
		
	}
	
}
