package com.game.server.thread;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.game.protect.structs.MailBean;
import com.game.utils.MailUtil;

/**密码找回，对外邮件
 * 
 * @author zhangrong
 *
 */
public class SaveProtectMailThread extends Thread {
	//日志
	private Logger log = LogManager.getLogger(SaveProtectMailThread.class);
	//命令执行队列
	private LinkedBlockingQueue<String> mail_queue = new LinkedBlockingQueue<String>();
	//缓存map(key roleid)
	private HashMap<String, mailBeanInfo> mail_map = new HashMap<String, mailBeanInfo>();
	//运行标志
	private boolean stop;
	//线程名称
	protected String threadName;
	public static int SPIRI_UPDATE = 0;	//更新
	public static int SPIRI_INSERT = 1;	//插入

	private static int MAX_SIZE = 10000;
	
	public SaveProtectMailThread(String threadName) {
		super(threadName);
		this.threadName = threadName;
	}

	public void run() {
		stop = false;
		while (!stop || mail_queue.size() > 0) {
			mailBeanInfo mail = null;
			synchronized (this) {
				Object o = mail_queue.poll();
				if(o!=null){
					String Userid = (String)o;
					mail = mail_map.remove(Userid);
				}
			}
			
			if (mail == null) {
				try {
					synchronized (this) {
						wait();
					}
				} catch (InterruptedException e) {
					log.error("Save protectmail Thread " + threadName + " Wait Exception:" + e.getMessage());
				}
			} else {
				try {
					if(mail_queue.size() > MAX_SIZE){
						mail_queue.clear();
						mail_map.clear();
					}
					MailBean bean = mail.getMailBean();
					MailUtil.sendMail(bean.getAddressee(), bean.getTitle(),bean.getContent());

				} catch (Exception e) {
					log.error("protectmail Exception:", e);
					synchronized (this) {
						if(!mail_map.containsKey(mail.getMailBean().getUserid())){
							this.mail_queue.add(mail.getMailBean().getUserid());
							this.mail_map.put(mail.getMailBean().getUserid(), mail);
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
			log.error("protectmail Thread " + threadName + " Notify Exception:" + e.getMessage());
		}
	}
	
	
	/**
	 * 处理2级密码邮件
	 *
	 * @param protectBean 信息
	 * @param deal 0-update 1-insert
	 */
	public void dealmail(MailBean mailBean, int deal) {
		try {
			//this.spiritTree_queue.add(new spirittreeBeanInfo(spirittreeBean, deal));
			synchronized (this) {
				if(!mail_map.containsKey(mailBean.getUserid())){
					this.mail_queue.add(mailBean.getUserid());
				}
				this.mail_map.put(mailBean.getUserid(), new mailBeanInfo(mailBean, deal));
				notify();
			}
		} catch (Exception e) {
			log.error("protectmail Thread " + threadName + " Notify Exception:" + e.getMessage());
		}
	}


	private class mailBeanInfo {
		private MailBean mailBean;
		private int deal;

		public mailBeanInfo(MailBean mailBean, int deal) {
			this.mailBean = mailBean;
			this.deal = deal;
		}

		public MailBean getMailBean() {
			return mailBean;
		}

		public int getDeal() {
			return deal;
		}
	}
}
