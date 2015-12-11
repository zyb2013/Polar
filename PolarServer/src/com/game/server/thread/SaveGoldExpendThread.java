package com.game.server.thread;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.game.config.Config;
import com.game.db.bean.GoldExpend;
import com.game.db.dao.GoldExpendDao;
import com.game.player.structs.Player;
import com.game.structs.Reasons;

/**
 * 记录和查询玩家在指定时间段内的钻石消耗(系统消耗)
 * @author Administrator
 *
 */
public class SaveGoldExpendThread extends Thread {

	//日志
	private Logger log = LogManager.getLogger(SaveGoldExpendThread.class);
	
	//日志
	private Logger failedlog = LogManager.getLogger("SAVEGOLDFAILED");
		
	// 保存队列
	private LinkedBlockingQueue<GoldExpend> save_queue = new LinkedBlockingQueue<GoldExpend>();
	
	// 查询队列
	private LinkedBlockingQueue<SearchGoldExpend> search_queue = new LinkedBlockingQueue<SearchGoldExpend>();
	
	//运行标志
	private boolean stop;
	
	//线程名称
	protected String threadName;
	
	boolean insertDB = true;
	
	boolean selectDB = true;
	
	private static int MAX_SIZE = 10000;
	
	private GoldExpendDao dao = new GoldExpendDao();
	
	// 算消耗的理由
//	private HashSet<Reasons> saveReasons = new HashSet<Reasons>();
		
	public SaveGoldExpendThread(String threadName) {
		super(threadName);
		this.threadName = threadName;
		
//		saveReasons.add(Reasons.YBBAGKAIGE);
//		saveReasons.add(Reasons.CHESTBOX_DELGOLD);
//		saveReasons.add(Reasons.DIGONG_YBTASK);
//		saveReasons.add(Reasons.STRENG_QH_YUANBAO);
//		saveReasons.add(Reasons.HIDDENWEAPON_GOLD);
//		saveReasons.add(Reasons.HORSE_CD);
//		saveReasons.add(Reasons.HORSE_LZ);
//		saveReasons.add(Reasons.YBTRANS);
//		saveReasons.add(Reasons.WEDDING_GOLD);
//		saveReasons.add(Reasons.MARRIED_RING_GOLD);
//		saveReasons.add(Reasons.MELTING_GOLD);
//		saveReasons.add(Reasons.YBNPCTRANS);
//		saveReasons.add(Reasons.PET_HETI);
//		saveReasons.add(Reasons.MELTING_BUYITEM);
//		saveReasons.add(Reasons.YBBUY);
//		saveReasons.add(Reasons.MEIREN);
//		saveReasons.add(Reasons.YBSKILLSTUDY);
//		saveReasons.add(Reasons.URGE_YB_RIPE);
//		saveReasons.add(Reasons.SPIRITTREE_RIP_YUANBAO);
//		saveReasons.add(Reasons.YBSTOREKAIGE);
//		saveReasons.add(Reasons.CONQUERTASKDEVOUR);
//		saveReasons.add(Reasons.RANK_DELGOLD);
//		saveReasons.add(Reasons.DAILYTASKREDUCED);
//		saveReasons.add(Reasons.def12);
//		saveReasons.add(Reasons.CONQUERTASKQUICKFINSH);
//		saveReasons.add(Reasons.DAILYTASKQUCKFINSH);
//		saveReasons.add(Reasons.DAILYTASKSUPPERFINSH);
//		saveReasons.add(Reasons.DAILYFINSHCURRENTLOOP);
//		saveReasons.add(Reasons.DAILYTASKUPACHRIVE);
//		saveReasons.add(Reasons.RAID_LIANXU_YB);
//		saveReasons.add(Reasons.RAID_YB);
//		saveReasons.add(Reasons.def6);
//		saveReasons.add(Reasons.def22);
//		saveReasons.add(Reasons.def27);
//		saveReasons.add(Reasons.def16);
//		saveReasons.add(Reasons.HOUSHENGDAN);
//		saveReasons.add(Reasons.SHENGDAN);
		}
	
	public void run() {
		stop = false;
		while (!stop) {
			save();
			search();
		}
	}
	
	private void save() {
		while (save_queue.size() > 0) {
			GoldExpend expend = save_queue.poll();
			if(expend == null) {
				try {
					synchronized (this) { wait(); }
				} catch (InterruptedException e) {
					log.error("Save Gold Expend Thread " + threadName + " Wait Exception:" + e.getMessage());
				}
			}else {
				try {
					if(save_queue.size() > MAX_SIZE) {
						insertDB = false;
					}
					if(insertDB) {
						dao.insert(expend);
					}
				} catch (Exception e) {
					failedlog.error("Gold Exception:" + expend.getRoleid() + "[" + expend.getGoldnum() + "]", e);
					save_queue.add(expend);
				}
			}
		}
	}
	
	private void search() {
		while (search_queue.size() > 0) {
			SearchGoldExpend search = search_queue.poll();
			if(search == null) {
				try {
					synchronized (this) { wait(); }
				} catch (InterruptedException e) {
					log.error("Save Gold Expend Thread " + threadName + " Wait Exception:" + e.getMessage());
				}
			}else {
				try {
					if(search_queue.size() > MAX_SIZE) {
						selectDB = false;
					}
					if(selectDB) {
						int expend = dao.select(search.getRoleid(), search.getStartTime(), search.getEndTime());
						search.getCb().doWhenGetExpendGold(expend);
					}
				} catch (Exception e) {
					failedlog.error("Gold Exception:" + search.getRoleid() + "[" + search.getStartTime() + "," + search.getEndTime() + "]", e);
					search.getCb().doWhenGetExpendGold(0);
				}
			}
		}
	}
	
	public void stop(boolean flag) {
		stop = flag;
		try {
			synchronized (this) { notify(); }
		} catch (Exception e) {
			log.error("Gold Expend Thread " + threadName + " Notify Exception:" + e.getMessage());
		}
	}

	/**
	 * 添加一个保存任务
	 * @param player
	 * @param goldnum
	 * @param reason
	 */
	public void addSaveTask(Player player, int goldnum, Reasons reason) {
		GoldExpend expend = new GoldExpend();
		expend.setTime(System.currentTimeMillis());
		expend.setGoldnum(goldnum);
		expend.setRoleid(player.getId());
		expend.setReason(reason.ordinal());
		expend.setUnuse_index(Config.getId());
		save_queue.add(expend);
	}
	
	/**
	 * 获取玩家在指定时间段内消耗的钻石数量(系统消耗)
	 * @param roleid
	 * @param startTime 格式 yyyymmddhhmmss
	 * @param endTime 格式 yyyymmddhhmmss
	 * @return
	 * @throws ParseException 
	 */
	public void addSearchTask(long roleid, String startTime, String endTime, GetExpendGoldCallBack cb) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		SearchGoldExpend task = new SearchGoldExpend();
		task.setCb(cb);
		task.setEndTime(sdf.parse(endTime).getTime());
		task.setStartTime(sdf.parse(startTime).getTime());
		task.setRoleid(roleid);
		search_queue.add(task);
	}
	
	private class SearchGoldExpend {
		private long roleid;
		private long startTime;
		private long endTime;
		GetExpendGoldCallBack cb;
		public long getRoleid() {
			return roleid;
		}
		public void setRoleid(long roleid) {
			this.roleid = roleid;
		}
		public long getStartTime() {
			return startTime;
		}
		public void setStartTime(long startTime) {
			this.startTime = startTime;
		}
		public long getEndTime() {
			return endTime;
		}
		public void setEndTime(long endTime) {
			this.endTime = endTime;
		}
		public GetExpendGoldCallBack getCb() {
			return cb;
		}
		public void setCb(GetExpendGoldCallBack cb) {
			this.cb = cb;
		}
	}
	
	public interface GetExpendGoldCallBack {
		public void doWhenGetExpendGold(int expendGold);
	}

	public boolean checkReason(Reasons reason) {
		return true;
	}
}
