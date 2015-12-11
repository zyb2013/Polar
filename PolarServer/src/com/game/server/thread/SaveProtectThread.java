package com.game.server.thread;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.game.db.bean.ProtectBean;
import com.game.db.dao.ProtectDao;

/**2级密码 异步储存
 * 
 * @author zhangrong
 *
 */
public class SaveProtectThread extends Thread {

	//数据库
	private ProtectDao dao = new ProtectDao();
	//日志
	private Logger log = LogManager.getLogger(SaveProtectThread.class);
	//命令执行队列
	private LinkedBlockingQueue<String> protect_queue = new LinkedBlockingQueue<String>();
	//缓存map(key roleid)
	private HashMap<String, protectBeanInfo> protect_map = new HashMap<String, protectBeanInfo>();
	//运行标志
	private boolean stop;
	//线程名称
	protected String threadName;
	public static int SPIRI_UPDATE = 0;	//更新
	public static int SPIRI_INSERT = 1;	//插入

	private static int MAX_SIZE = 10000;
	
	public SaveProtectThread(String threadName) {
		super(threadName);
		this.threadName = threadName;
	}

	public void run() {
		stop = false;
		while (!stop || protect_queue.size() > 0) {
			protectBeanInfo protect = null;
			synchronized (this) {
				Object o = protect_queue.poll();
				if(o!=null){
					String Userid = (String)o;
					protect = protect_map.remove(Userid);
				}
			}
			
			if (protect == null) {
				try {
					synchronized (this) {
						wait();
					}
				} catch (InterruptedException e) {
					log.error("Save protect Thread " + threadName + " Wait Exception:" + e.getMessage());
				}
			} else {
				try {
					if(protect_queue.size() > MAX_SIZE){
						protect_queue.clear();
						protect_map.clear();
					}
					if (protect.getDeal() == 0) {
						if (dao.update(protect.getProtectBean()) == 0) {
							log.error("updat二级密码保存出错-ID:"+protect.getProtectBean().getUserid()+"，Password:"+protect.getProtectBean().getPassword() + ",mail:"+protect.getProtectBean().getMail());
						}
						
					} else if (protect.getDeal() == 1) {
						if (dao.insert(protect.getProtectBean()) == 0) {
							log.error("insert二级密码保存出错-ID:"+protect.getProtectBean().getUserid()+"，Password:"+protect.getProtectBean().getPassword() + ",mail:"+protect.getProtectBean().getMail());
						}
					}
					
				} catch (Exception e) {
					log.error("Marriage Exception:", e);
					synchronized (this) {
						if(!protect_map.containsKey(protect.getProtectBean().getUserid())){
							this.protect_queue.add(protect.getProtectBean().getUserid());
							this.protect_map.put(protect.getProtectBean().getUserid(), protect);
						}
					}
				}
			}
		}
	}

	public void stop(boolean flag) {
		stop = flag;
		try {
			synchronized (this) {
				notify();
			}
		} catch (Exception e) {
			log.error("protect Thread " + threadName + " Notify Exception:" + e.getMessage());
		}
	}
	
	
	/**
	 * 处理2级密码列表
	 *
	 * @param protectBean 信息
	 * @param deal 0-update 1-insert
	 */
	public void dealprotect(ProtectBean protectBean, int deal) {
		try {
			//this.spiritTree_queue.add(new spirittreeBeanInfo(spirittreeBean, deal));
			synchronized (this) {
				if(!protect_map.containsKey(protectBean.getUserid())){
					this.protect_queue.add(protectBean.getUserid());
				}
				this.protect_map.put(protectBean.getUserid(), new protectBeanInfo(protectBean, deal));
				notify();
			}
		} catch (Exception e) {
			log.error("protect Thread " + threadName + " Notify Exception:" + e.getMessage());
		}
	}


	private class protectBeanInfo {
		private ProtectBean protectBean;
		private int deal;

		public protectBeanInfo(ProtectBean protectBean, int deal) {
			this.protectBean = protectBean;
			this.deal = deal;
		}

		public ProtectBean getProtectBean() {
			return protectBean;
		}

		public int getDeal() {
			return deal;
		}
	}
}
