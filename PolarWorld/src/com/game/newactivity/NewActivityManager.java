package com.game.newactivity;

import java.util.Collection;
import java.util.List;

import com.game.data.bean.Q_newActivityBean;
import com.game.data.dao.Q_new_activityDao;
import com.game.newactivity.message.ReqUpdateActivity2WorldMessage;
import com.game.newactivity.message.ResGetRankInfo2WorldMessage;
import com.game.newactivity.message.ResGetRankInfoMessage;
import com.game.newactivity.model.SimpleRankInfo;
import com.game.player.manager.PlayerManager;
import com.game.player.structs.Player;
import com.game.toplist.manager.TopListManager;
import com.game.utils.MessageUtil;

/**
 * @author luminghua
 *
 * @date   2014年2月27日 下午8:16:03
 * 
 * 活动排名
 */
public class NewActivityManager {

	private static NewActivityManager instance;
	private final static Object obj = new Object();
	private Q_new_activityDao dao = new Q_new_activityDao();
	
	//serverId--activity list
	private List<Q_newActivityBean> activities;
	
	public static NewActivityManager getInstance() {
		if(instance == null) {
			synchronized(obj) {
				if(instance == null) {
					instance = new NewActivityManager();
				}
			}
		}
		return instance;
	}
	
	public void init() {
		activities = dao.select();
	}
	public void serverAskRank(int serverId,long playerId,int type,int count) {
		switch(type) {
		case TopListManager.TOPTYPE_FIGHTPOWER:
		case TopListManager.TOPTYPE_LEVEL:
		case TopListManager.TOPTYPE_HORSE:
		case TopListManager.TOPTYPE_PATA:
		case TopListManager.TOPTYPE_RECHARGE:
			List<SimpleRankInfo> simpleRankInfoByActivity = TopListManager.getInstance().getSimpleRankInfoByActivity(playerId, type, count);
			Player player = PlayerManager.getInstance().getPlayer(playerId);
			if(player != null) {
				ResGetRankInfoMessage msg = new ResGetRankInfoMessage();
				msg.setActivityId(parseType(type));
				msg.setInfoList(simpleRankInfoByActivity);
				MessageUtil.tell_player_message(player, msg);
			}else if(playerId == -1) {
				ResGetRankInfo2WorldMessage msg = new ResGetRankInfo2WorldMessage();
				msg.setInfoList(simpleRankInfoByActivity);
				msg.setPlayerId(playerId);
				msg.setType((byte) type);
				MessageUtil.send_to_game(serverId,msg);
			}
			break;
		}
	}
	
	private int parseType(int type) {
		switch(type) {
		case TopListManager.TOPTYPE_LEVEL:
			return NewActivityEnum.LevelRank.getId();
		case TopListManager.TOPTYPE_FIGHTPOWER:
			return NewActivityEnum.FightRank.getId();
		case TopListManager.TOPTYPE_HORSE:
			return NewActivityEnum.MountRank.getId();
		case TopListManager.TOPTYPE_PATA:
			return NewActivityEnum.EliteRank.getId();
		case TopListManager.TOPTYPE_RECHARGE:
			return NewActivityEnum.PayRank.getId();
		default:
			return 0;
		}
	}
	
	/**
	 * 请求获取某服的所有活动列表
	 * @param serverId
	 */
	public List<Q_newActivityBean> requestAllActivity() {
//		ReqGetActivityList2WorldMessage msg = new ReqGetActivityList2WorldMessage();
//		MessageUtil.send_to_game(serverId, msg);
		return activities;
	}
	
	/**
	 * 返回某服的所有活动列表
	 * @param beans
	 */
//	public void responseAllActivity(Collection<Q_newActivityBean> beans) {
//		
//	}
	
	/**
	 * 请求修改某个活动
	 * @param serverId
	 * @param bean
	 */
	public void requestUpdateActivity(int serverId,Q_newActivityBean bean) {
		ReqUpdateActivity2WorldMessage msg = new ReqUpdateActivity2WorldMessage();
		msg.setBean(bean);
		MessageUtil.send_to_game(serverId, msg);
	}
	
	/**
	 * 返回修改结果
	 * @param bean
	 */
	public void responseUpdateActivity(Q_newActivityBean bean) {
		for(int i=0; i<activities.size(); i++) {
			if(activities.get(i).getQ_id() == bean.getQ_id()) {
				activities.set(i, bean);
				break;
			}
		}
	}
}
