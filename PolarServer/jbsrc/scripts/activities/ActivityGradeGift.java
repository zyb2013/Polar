package scripts.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.activities.script.AbstractActivityScript;
import com.game.backpack.manager.BackpackManager;
import com.game.backpack.structs.AnalysisItemInfo;
import com.game.data.bean.Q_activitiesBean;
import com.game.data.bean.Q_grade_giftBean;
import com.game.data.manager.DataManager;
import com.game.gradegift.bean.GainGradeGiftInfo;
import com.game.gradegift.bean.GradeGiftInfo;
import com.game.gradegift.manager.GradeGiftManager;
import com.game.gradegift.message.ResGainGradeGiftInfoMessage;
import com.game.gradegift.message.ResGradeGiftInfoMessage;
import com.game.gradegift.message.ResGradeGiftMessage;
import com.game.languageres.manager.ResManager;
import com.game.player.structs.Player;
import com.game.prompt.structs.Notifys;
import com.game.utils.MessageUtil;
import com.game.utils.StringUtil;
import com.game.utils.TimeUtil;

/**
 * 等级礼包活动
 * @author hongxiao.z
 * @date   2014-2-11  下午3:29:49
 */
public class ActivityGradeGift extends AbstractActivityScript
{
	Object lock = new Object();
	
	/**
	 * key 活动子ID
	 * value 道具列表
	 * 		说明:道具列表信息
	 * 			
	 */
	private Map<Integer, List<AnalysisItemInfo>> rewards;
	
	@Override
	public int getId() 
	{
		return 55003;
	}
	
	//活动开始时间
	long startTime;
	//活动结束时间	暂时注释方便测试
	long endTime;

	/**
	 * 获取一个等级对应的货源原型
	 * @param level
	 * @return
	 * @create	hongxiao.z      2014-2-11 下午3:50:02
	 */
	private Q_activitiesBean getBean()
	{
		return DataManager.getInstance().q_activitiesContainer.get(getActivityId());
	}
	
	/**
	 * 初始化实现
	 */
	@Override
	protected boolean initImpl() 
	{
		Q_activitiesBean bean = getBean();
		
		if(bean == null)
		{
			log.error(" --- 活动ID为[" + getActivityId() + "]的原型数据不存在,导致活动无法初始化！");
			return false;
		}
		
		//设置开服时间
		Calendar calendar = TimeUtil.getOpenTime();
		this.startTime = calendar.getTimeInMillis();
		
		rewards = new HashMap<Integer, List<AnalysisItemInfo>>();
		
		//解析道具奖励
		for (Q_grade_giftBean awardBean :  DataManager.getInstance().q_gradegiftContainer.getList()) 
		{
			List<AnalysisItemInfo> infos = StringUtil.analysisItems(awardBean.getQ_reward());
			this.rewards.put(awardBean.getQ_id(), infos);
		}
		
		open();
		
		return true;
	}

	/**
	 * 停止实现
	 */
	@Override
	protected boolean stopImpl() 
	{
		this.close();
		return true;
	}

	/**
	 * 监控实现
	 */
	@Override
	protected void actionImpl() 
	{
		//到时间则停止
	}

	/**
	 * 主动调用
	 */
	@Override
	public void initiative(Player player, Object intention, Object... objs) 
	{
		//时间到了不可操作
		if(!isOpera())
		{
			return;
		}
		
		int iti = (Integer) intention;
		
		switch(iti)
		{
			case AbstractActivityScript.GAIN_REWARD:	//领取奖励
				if(objs.length < 1) return;
				gainReward(player, (Integer)objs[0]);
				break;
			default :		
				break;
		}
	}
	
	/**
	 * 被动调用
	 */
	@Override
	public void passivity(Player player, Object intention, Object... objs) 
	{
		//时间到了不可操作
		if(!isOpera())
		{
			return;
		}
		
		int iti = (Integer) intention;
		
		switch(iti)
		{
			case AbstractActivityScript.SEND_GRADE_GIFT_INFO:	//上线奖励领取信息推送
				sendGradeGiftInfo(player);
				break;
			default :		
				break;
		}
	}
	
	/**
	 * 上线等级礼包数量信息推送
	 * @param player
	 * @create	hongxiao.z      2014-2-15 下午8:00:42
	 */
	private void sendGradeGiftInfo(Player player)
	{
		long nowTime = System.currentTimeMillis();
		//时间未到或者结束(延迟一天)
		if(startTime > nowTime || (endTime + 1000 * 60 * 60 * 24)  <= nowTime)
		{
			return;
		}
		
		ResGradeGiftMessage msg = new ResGradeGiftMessage();
		msg.setStartTime(startTime);
		
		msg.setEndTime(endTime);
		msg.setSurplusTime((int)((endTime - nowTime) / 1000));
		
//		System.out.println("活动开始时间 --- " + new Date(startTime) + " : 结束时间 --- " + new Date(endTime));
		
		List<GradeGiftInfo> list = new ArrayList<GradeGiftInfo>();
		for (Q_grade_giftBean bean : DataManager.getInstance().q_gradegiftContainer.getList()) 
		{
			GradeGiftInfo info = new GradeGiftInfo();
			info.setGiftId(bean.getQ_id());
			Integer record = GradeGiftManager.getRecord(bean.getQ_id());
			
			int surplusCount = -1;
			
			if(bean.getQ_max_gain() > 0)
			{
				surplusCount = bean.getQ_max_gain() - (record == null ? 0 : record);
			}
			
			info.setReamain(surplusCount);
//			System.out.println("活动数量 --- " + bean.getQ_id() + " : " + (bean.getQ_max_gain() - (record == null ? 0 : record)));
			list.add(info);
		}
		
		msg.setAwardInfo(list);
		
		List<GainGradeGiftInfo> gainList = new ArrayList<GainGradeGiftInfo>();
		
		
		for (Q_grade_giftBean bean : DataManager.getInstance().q_gradegiftContainer.getList()) 
		{
			GainGradeGiftInfo info = new GainGradeGiftInfo();
			info.setGiftId(bean.getQ_id());
			String value = player.getActivitiesReward().get(GAIN_GRADE_GIFT + bean.getQ_id());
			
			info.setIsGain(value == null ? 0 : 1);
			
//			System.out.println("领取状态 --- " + bean.getQ_id() + " : " + (value == null ? 0 : 1));
			gainList.add(info);
		}
		
		msg.setGainAwardInfo(gainList);
		
		MessageUtil.tell_player_message(player, msg);
	}
	
	//角色领取奖励次数的保存索引
	private final String GAIN_GRADE_GIFT = "GAIN_GRADE_GIFT";
	
	/**
	 * 领取活动奖励
	 * @param player			角色实体
	 * @param objs				参数列表
	 * @create	hongxiao.z      2014-2-12 上午10:07:40
	 */
	private void gainReward(Player player, int giftId)
	{
		synchronized (lock) 
		{
			String value = player.getActivitiesReward().get(GAIN_GRADE_GIFT + giftId);
			if(value != null)	//已领过
			{
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您已经领过这个礼包啦！"));	
				return;
			}
			
			//获取礼包数据
			Q_grade_giftBean bean = DataManager.getInstance().q_gradegiftContainer.get(giftId);
			
			if(bean == null)
			{
				log.error("领取等级礼包 --- 礼包ID[" + giftId + "]的礼包数据不存在！");
				return;
			}
			
			//等级判断
			if(player.getLevel() < bean.getQ_need_grade())
			{
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("领取等级礼包 --- 请先升级后再领取！"));
				log.error("领取等级礼包 --- 角色[" + player.getName() + "],请先升级后再领取！");
				return;
			}
			
			//是否已经领完
			Integer record = GradeGiftManager.getRecord(giftId);
			
			if(record == null) record = 0;
			
			//不是无限
			if(record >= bean.getQ_max_gain() && bean.getQ_max_gain() > 0)
			{
				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您的动作慢了点哦，这个礼包已经被领取完啦！"));	
				return;
			}
			
			//背包数量是否足够
			List<AnalysisItemInfo> infos = this.rewards.get(giftId); 
//			int emptynum = BackpackManager.getInstance().getEmptyGridNum(player);
//			if(infos.getToBackpackSize() > emptynum)
//			{
//				MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("您的背包空间不足，请整理后再来领取！"));	
//				return;
//			}
			
			//领取礼包
			BackpackManager.getInstance().toBackpack(player, infos, "等级奖励");
			
			//保存纪录
			player.getActivitiesReward().put(GAIN_GRADE_GIFT + giftId, Integer.toString(1));
			record++;
			GradeGiftManager.saveRecord(giftId, record);
			
			//推送给全F
			ResGradeGiftInfoMessage msg = new ResGradeGiftInfoMessage();
			GradeGiftInfo giftInfo = new GradeGiftInfo();
			giftInfo.setGiftId(giftId);
			giftInfo.setReamain(bean.getQ_max_gain() - record);
			msg.setInfo(giftInfo);

			log.error("活动等级礼包领取 --- 角色[" + player.getName() + "] - 礼包ID[" + giftId + "]");
//			System.out.println("活动数量 --- " + giftId + " : " + giftInfo.getReamain());
			
			MessageUtil.tell_world_message(msg);
			
			//推送给玩家
			ResGainGradeGiftInfoMessage mssg = new ResGainGradeGiftInfoMessage();
			GainGradeGiftInfo info = new GainGradeGiftInfo();
			info.setGiftId(giftId);
			info.setIsGain(1);
			mssg.setAwardInfo(info);
			
//			System.out.println("领取状态 --- " + giftId + " : " + info.getIsGain());
			
			MessageUtil.tell_player_message(player, mssg);
		}
	}
	
	

	@Override
	public int getActivityId() 
	{
		return 1001;
	}
	
	/**
	 * 是否可以进行活动操作
	 * @return
	 * @create	hongxiao.z      2014-2-17 上午9:56:33
	 */
	private boolean isOpera()
	{
//		Q_activitiesBean bean = getBean();
//		return TimeUtil.checkRangeTime(bean.getQ_duration());
		
		if(endTime == 0)
		{
			Q_activitiesBean bean = getBean();
			this.endTime = TimeUtil.getRangeTimeBeforeOrAfter(bean.getQ_duration(), false, true);
		}
		
//		时间到了不可操作
		return System.currentTimeMillis() < this.endTime;
	}
}
