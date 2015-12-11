package com.game.bank.manger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.game.bank.bean.BankLogInfo;
import com.game.bank.message.ReqQueryBankLogToWorldMessage;
import com.game.bank.message.ReqSendBankLogToWorldMessage;
import com.game.bank.message.ResQueryBankLogMessage;
import com.game.db.bean.BankLogBean;
import com.game.db.dao.BankLogDao;
import com.game.manager.ManagerPool;
import com.game.player.structs.Player;
import com.game.utils.MessageUtil;

public class BankManager {
	private static Object obj = new Object();
	// 后台管理类实例
	private static BankManager manager;

	private BankManager() {
	}

	private Logger log = Logger.getLogger(BankManager.class);

	public static BankManager getInstance() {
		synchronized (obj) {
			if (manager == null) {
				manager = new BankManager();
			}
		}
		return manager;
	}

	/**
	 * 玩家银行
	 */
	private static List<BankLogBean> banklogList = new ArrayList<BankLogBean>();

	private static List<BankLogBean> bankInLogList =new ArrayList<BankLogBean>();

	private static List<BankLogBean> bankOutLogList = new ArrayList<BankLogBean>();

	// 银行数据接口
	private BankLogDao bankLogDao = new BankLogDao();

	public BankLogDao getBankLogDao() {
		return bankLogDao;
	}

	/**
	 * 服务器启动 ，从数据库 读取所有银行存储数据
	 * 
	 */
	public void loadAllBankLogs() {
		try {
			List<BankLogBean> list = getBankLogDao().select();
			Iterator<BankLogBean> iterator = list.iterator();
			while (iterator.hasNext()) {
				BankLogBean bankLogBean = (BankLogBean) iterator.next();
				if (bankLogBean != null) {
					banklogList.add(bankLogBean);

					if (bankLogBean.getOptions() == 0
							|| bankLogBean.getOptions() == 2) {
						bankInLogList.add(bankLogBean);
					}
					if (bankLogBean.getOptions() == 1
							|| bankLogBean.getOptions() == 3) {
						bankOutLogList.add(bankLogBean);
					}

				} else {
					log.error("银行数据读取错误");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void reqSendBankMessage(ReqSendBankLogToWorldMessage msg) {

		BankLogBean bankLogBean = new BankLogBean();
		bankLogBean.setCounts(msg.getCount());
		bankLogBean.setOptions(msg.getOption());
		bankLogBean.setPlayerName(msg.getPlayerName());
		try {
			if (getBankLogDao().insert(bankLogBean) > 0) {
				banklogList.add(bankLogBean);

				if (bankLogBean.getOptions() == 0
						|| bankLogBean.getOptions() == 2) {
					bankInLogList.add(bankLogBean);
				}
				if (bankLogBean.getOptions() == 1
						|| bankLogBean.getOptions() == 3) {
					bankOutLogList.add(bankLogBean);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void ReqQueryBankLogToWorldMessage(ReqQueryBankLogToWorldMessage msg) {
		Player player = ManagerPool.playerManager.getPlayer(msg.getPlayerId());
		// msg.getPlayerId()
		
		ResQueryBankLogMessage cmsg = new ResQueryBankLogMessage();

	
		
		if (msg.getOption() == 0) {
			int size = banklogList.size();

			int start = size - msg.getIndexlittle()-1;

			int end = size - msg.getIndexLarge()-1;
			if (end < 0) {
				end = 0;
			}

		

			if (size > msg.getIndexLarge()) {
				for (int i = start; i > end; i--) {
					cmsg.getBankLogs().add(buildBankLog(banklogList.get(i)));
				}
			} else {
				for (int i = size-1; i > end; i--) {
					if(banklogList.get(i)!=null){
						cmsg.getBankLogs().add(buildBankLog(banklogList.get(i)));
					}
				}
			}
			cmsg.setCount(banklogList.size());
			MessageUtil.tell_player_message(player, cmsg);

		}
		
		if (msg.getOption() == 1) {
		
			int size = bankOutLogList.size();

			int start = size - msg.getIndexlittle()-1;

			int end = size - msg.getIndexLarge()-1;
			if (end < 0) {
				end = 0;
			}
			if (end < 0) {
				end = 0;
			}

			if (size > msg.getIndexLarge()) {
				for (int i = start; i > end; i--) {
					cmsg.getBankLogs().add(buildBankLog(bankOutLogList.get(i)));
				}
			} else {
				for (int i = size-1; i > end; i--) {
					if(bankOutLogList.get(i)!=null){
						cmsg.getBankLogs().add(buildBankLog(bankOutLogList.get(i)));
					}
				}
			}
			cmsg.setCount(bankOutLogList.size());
			MessageUtil.tell_player_message(player, cmsg);

		}
		if (msg.getOption() == 2) {
			int size = bankInLogList.size();

			int start = size - msg.getIndexlittle()-1;

			int end = size - msg.getIndexLarge()-1;
			if (end < 0) {
				end = 0;
			}
			if (size > msg.getIndexLarge()) {
				for (int i = start; i > end; i--) {
					cmsg.getBankLogs().add(buildBankLog(bankInLogList.get(i)));
				}
			} else {
				for (int i = size-1; i > end; i--) {
					if(bankInLogList.get(i)!=null){
						cmsg.getBankLogs().add(buildBankLog(bankInLogList.get(i)));
					}
				}
			}
			cmsg.setCount(bankInLogList.size());
			MessageUtil.tell_player_message(player, cmsg);

		}

	}

	public BankLogInfo buildBankLog(BankLogBean bean) {
			BankLogInfo bankLogInfo = new BankLogInfo();
			bankLogInfo.setCount(bean.getCounts());
			bankLogInfo.setOption(bean.getOptions());
			bankLogInfo.setPlayerName(bean.getPlayerName());
			return bankLogInfo;

	}

}
