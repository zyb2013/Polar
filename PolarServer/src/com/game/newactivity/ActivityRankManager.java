package com.game.newactivity;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSON;
import com.game.country.structs.CountryData;
import com.game.newactivity.message.ReqGetRankInfo2WorldMessage;
import com.game.newactivity.message.ResGetRankInfoMessage;
import com.game.newactivity.model.SimpleRankInfo;
import com.game.player.structs.Player;
import com.game.server.impl.WServer;
import com.game.toplist.manager.TopListManager;
import com.game.utils.CollectionUtil;
import com.game.utils.MessageUtil;
import com.game.utils.ServerParamUtil;
import com.game.utils.StringUtil;

/**
 * @author luminghua
 *
 * @date   2014年2月27日 下午8:16:03
 * 
 * 活动排名
 */
public class ActivityRankManager {

	private static ActivityRankManager instance;
	private final static Object obj = new Object();
	
	private static final int RANK_LIMIT = 20;//排行榜显示个数
	private static final int RANK_LIMIT_PAYMENT = Short.MAX_VALUE*10;//结算排行榜个数
	
	//activityId - list
	private Map<Integer,SimpleRankInfo[]> rankMap = new ConcurrentHashMap<Integer,SimpleRankInfo[]>();
	
	public static ActivityRankManager getInstance() {
		if(instance == null) {
			synchronized(obj) {
				if(instance == null) {
					instance = new ActivityRankManager();
				}
			}
		}
		return instance;
	}
	
	public void storeRankInfo(int activityId,List<SimpleRankInfo> rankList) {
		if(CollectionUtil.isNotBlank(rankList)) {
			if(rankList.size() > RANK_LIMIT) {
				rankList = rankList.subList(0, RANK_LIMIT);
			}
			SimpleRankInfo[] array = rankList.toArray(new SimpleRankInfo[] {});
		    String jsonString = JSON.toJSONString(array);
			ServerParamUtil.threadSaveImportant(ServerParamUtil.ACTIVITY+activityId, jsonString);
			rankMap.put(activityId, array);
		}
	}
	
	public void playerAskRank(Player player,int activityId) {
		AbstractActivity activityImpl = NewActivityManager.getInstance().getActivityImpl(activityId);
		if(activityImpl == null) {
			return;
		}
		int type = -1;
		NewActivityEnum byActivityId = NewActivityEnum.getByActivityId(activityId);
		switch(byActivityId) {
		case LevelRank:
			type = TopListManager.TOPTYPE_LEVEL;
			break;
		case FightRank:
			type = TopListManager.TOPTYPE_FIGHTPOWER;
			break;
		case MountRank:
			type = TopListManager.TOPTYPE_HORSE;
			break;
		case EliteRank:
			type = TopListManager.TOPTYPE_PATA;
			break;
		case PayRank:
			type = TopListManager.TOPTYPE_RECHARGE;
			break;
		case Siegecraft:
			SimpleRankInfo[] array = rankMap.get(activityId);
			List<SimpleRankInfo> asList = null;
			if(array != null) {
				asList = Arrays.asList(array);
			}else {
				String dataString = ServerParamUtil.getImportantParamMap().get(ServerParamUtil.COUNTRYDATA );
				asList = new LinkedList<SimpleRankInfo>();
				if(StringUtil.isNotBlank(dataString)) {
					CountryData data = JSON.parseObject(dataString,CountryData.class);
					SimpleRankInfo info = new SimpleRankInfo();
					info.setPlayerId(data.getKingId());
					info.setName(data.getKingName());
					info.setData(data.getKingGuildName());
					asList.add(info);
					rankMap.put(activityId, asList.toArray(new SimpleRankInfo[] {}));
				}
			}
			ResGetRankInfoMessage msg = new ResGetRankInfoMessage();
			msg.setActivityId(activityId);
			msg.setInfoList(asList);
			MessageUtil.tell_player_message(player, msg);
			return;
		default:
			break;
		}
		if(type == -1) {
			return;
		}
		if(player !=null && System.currentTimeMillis() > activityImpl.getEndDate().getTime()) {
			//如果活动已经过期，那么取已经保存的数据
			SimpleRankInfo[] array = rankMap.get(activityId);
			if(array == null) {
				String string = ServerParamUtil.getImportantParamMap().get(ServerParamUtil.ACTIVITY+activityId);
				if(StringUtil.isNotBlank(string)) {
					array = JSON.parseObject(string, SimpleRankInfo[].class);
					if(array != null) {
						rankMap.put(activityId, array);
					}
				}
			}
			if(array != null) {
				SimpleRankInfo myself = null;
				for(SimpleRankInfo info : array) {
					if(info.getPlayerId() == player.getId()) {
						myself = info;
						break;
					}
				}
				array = Arrays.copyOf(array, array.length+1);
				if(myself == null) {
					array[array.length-1] = new SimpleRankInfo();
				}else {
					array[array.length-1] = myself;
				}
				List<SimpleRankInfo> asList = Arrays.asList(array);
				ResGetRankInfoMessage msg = new ResGetRankInfoMessage();
				msg.setActivityId(activityId);
				msg.setInfoList(asList);
				MessageUtil.tell_player_message(player, msg);
			}
			return;
		}
		ReqGetRankInfo2WorldMessage msg = new ReqGetRankInfo2WorldMessage();
		msg.setPlayerId(player==null?-1:player.getId());
		msg.setType((byte) type);
		msg.setCount((player ==null?RANK_LIMIT_PAYMENT:RANK_LIMIT));
		int serverId = WServer.getInstance().getServerId();
		msg.setServerId(serverId);
		MessageUtil.send_to_world(msg);
	}
	
	public void worldResRank(long playerId,int type,List<SimpleRankInfo> list) {
//		Player player = PlayerManager.getInstance().getOnLinePlayer(playerId);
//		if(player != null) {
//			ResGetRankInfoMessage msg = new ResGetRankInfoMessage();
//			msg.setActivityId(activityId);
//			msg.setInfoList(list);
//			MessageUtil.tell_player_message(player, msg);
//		}else if(playerId == -1) {
		int activityId = parseType(type);
		AbstractOpenServerActivity activityImpl = (AbstractOpenServerActivity)NewActivityManager.getInstance().getActivityImpl(activityId);
		activityImpl.payMent(list);
//		}
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
	
	public static void main(String[] str) {
	}
}
