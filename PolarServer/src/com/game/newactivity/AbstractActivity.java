package com.game.newactivity;

import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.game.backpack.manager.BackpackManager;
import com.game.backpack.structs.Item;
import com.game.config.Config;
import com.game.data.bean.Q_newActivityBean;
import com.game.dblog.LogService;
import com.game.mail.manager.MailServerManager;
import com.game.newactivity.AbstractActivity.GetAwardResult.ResultType;
import com.game.newactivity.log.NewActivityGetAwardLog;
import com.game.newactivity.message.ResAddActivityMessage;
import com.game.newactivity.message.ResGetNewActivityInfo;
import com.game.newactivity.message.ResNewActivityList;
import com.game.newactivity.message.ResRemoveActivityMessage;
import com.game.newactivity.model.DetailActivityInfo;
import com.game.newactivity.model.DetailActivityInfo.Row;
import com.game.newactivity.model.PlayerActivityInfo;
import com.game.newactivity.model.SimpleActivityInfo;
import com.game.player.manager.PlayerManager;
import com.game.player.structs.Player;
import com.game.structs.Reasons;
import com.game.timer.AbstractSchedulerTask;
import com.game.timer.SchedulerParser;
import com.game.utils.CollectionUtil;
import com.game.utils.MessageUtil;
import com.game.utils.StringUtil;
import com.game.utils.Symbol;

/**
 * @author luminghua
 *
 * @date   2014年2月24日 下午4:02:36
 */
public abstract class AbstractActivity extends AbstractSchedulerTask {


	protected DetailActivityInfo detailInfo;
	protected Q_newActivityBean activityBean;
	protected Date startDate;
	protected Date endDate;

	public void initActivity() {
		//解析开始和结束时间
		String q_startAndEnd = activityBean.getQ_startAndEnd();
		if(StringUtil.isNotBlank(q_startAndEnd)) {
			String[] split = q_startAndEnd.split(Symbol.SHUXIAN_REG);
			try {
				this.setStartDate(SchedulerParser.parse2Date(split[0]));
				this.setEndDate(SchedulerParser.parse2Date(split[1]));
			} catch (ParseException e) {
				_logger.error(this.getClass().getName()+":"+q_startAndEnd, e);
			}
		}
		resetDetailInfo();
	}
	
	/**
	 * 定时器默认触发方法
	 */
	public void trigger() {
		_logger.info(this.getClass().getName() +" execute default trigger() method.");
	}
	
	/**
	 * @param str
	 */
	public void start() {
		_logger.info(this.getClass().getName() +" execute default start() method.");
		ResAddActivityMessage msg = new ResAddActivityMessage();
		msg.setInfo(new SimpleActivityInfo(this.activityBean.getQ_id(),0));
		MessageUtil.tell_world_message(msg);
	}
	
	/**
	 * 活动结束默认方法
	 * @param str
	 */
	public void stop() {
		_logger.info(this.getClass().getName() +" execute default end() method.");
		ResRemoveActivityMessage msg = new ResRemoveActivityMessage();
		msg.setActivityId(activityBean.getQ_id());
		MessageUtil.tell_world_message(msg);
	}
	/**
	 * 活动触发
	 * @param player
	 * @param objects
	 */
	public void trigger(Player player,Object...objects) {}

	public void sendDetailActivityInfo(Player player) {
		//发送消息
		ResGetNewActivityInfo msg = new ResGetNewActivityInfo();
		msg.setDetailInfo(detailInfo);
		List<Row> rows = detailInfo.getRows();
		if(CollectionUtil.isNotBlank(rows)) {
			List<Integer> canGet = new LinkedList<Integer>();
			PlayerActivityInfo playerActivityInfo = PlayerActivityInfoManager.getInstance().getPlayerActivityInfo(player.getId(), this.getActivityBean().getQ_id());
			if(playerActivityInfo != null && playerActivityInfo.getAward2JSONArray() !=null) {
				JSONArray award2jsonArray = playerActivityInfo.getAward2JSONArray();
				for(int i =0; i<rows.size(); i++) {
					if(award2jsonArray.contains(String.valueOf(i))) {
						canGet.add(1);
					}else {
						canGet.add(0);
					}
				}
			}else {
				for(int i =0; i<rows.size(); i++) {
					canGet.add(0);
				}
			}
			
			msg.setCanGet(canGet);
		}
		MessageUtil.tell_player_message(player, msg);
	}

	public GetAwardResult getAward(Player player,int order) {
		if(!isCanVisible(player)) {
			_logger.error(this.getClass().getName() +"活动过期咯。p:"+player.getId()+",o:"+order);
			return new GetAwardResult(ResultType.expire);
		}
		//判断是否可领
		PlayerActivityInfo playerActivityInfo = PlayerActivityInfoManager.getInstance().getPlayerActivityInfo(player.getId(), this.getActivityBean().getQ_id());
		if(playerActivityInfo == null) {
			_logger.error(this.getClass().getName() +"连活动信息都没有的战5渣！ player:"+player.getName()+",order:"+order);
			return new GetAwardResult(ResultType.miss_info);
		}
		if(playerActivityInfo.getCanAward() <= 0) {
			_logger.error(this.getClass().getName() +"没有奖励可以领取啊！ player:"+player.getName()+",order:"+order);
			return new GetAwardResult(ResultType.miss_award);
		}
		JSONArray award2jsonArray = playerActivityInfo.getAward2JSONArray();
		if(!award2jsonArray.contains(String.valueOf(order))) {
			_logger.error(this.getClass().getName() +"没有对应的奖励可以领取啊！ player:"+player.getName()+",order:"+order);
			return new GetAwardResult(ResultType.miss_corresponding_award);
		}
		//发送奖励
		Row row = detailInfo.getRows().get(order);
		String award = row.getAward();
		if(StringUtils.isNotBlank(award)) {
			//领取记录
			List<Item> parseAward = parseAward(player.getJob(),award);
			if(BackpackManager.getInstance().getEmptyGridNum(player) < parseAward.size()) {
				return new GetAwardResult(ResultType.miss_bag_space);
			}
			//扣除奖励标记
			playerActivityInfo.reduceAward(order);
			PlayerActivityInfoManager.getInstance().update(playerActivityInfo);
			sendAwardCountMessage(player,playerActivityInfo);
			BackpackManager.getInstance().addItems(player, parseAward,Reasons.NEW_ACTIVITY_ADD, Config.getId());
			this.sendDetailActivityInfo(player);
			GetAwardResult getAwardResult = new GetAwardResult(ResultType.succeed);
			getAwardResult.items = parseAward.toArray(new Item[] {});
			return getAwardResult;
		}else {
			_logger.error(this.getClass().getName() +"纳尼，没有对应的奖励？！ player:"+player.getName()+",order:"+order);
			return new GetAwardResult(ResultType.miss_config);
		}
	}
	
	public static class GetAwardResult{
		public ResultType result;//0成功，1活动过期，2缺少活动信息，3没有奖励，4没有对应的奖励，5背包空间不足，6缺少奖励配置、7奖励入包出错、8缺少兑换道具
		public Item[] items;//成功领奖后的物品列表
		public enum ResultType{
			succeed("领取成功"),expire("活动过期"),miss_info("缺少活动信息"),miss_award("没有奖励"),
			miss_corresponding_award("没有对应的奖励"),miss_bag_space("背包空间不足"),miss_config("缺少奖励配置"),
			error_addItem("奖励入包出错"),not_enough_item("缺少兑换道具");
			public String desc;
			ResultType(String desc){
				this.desc = desc;
			}
		}
		public GetAwardResult(ResultType result){
			this.result = result;
		}
	}
	/**
	 * 活动过期将未领奖励发邮件，并删除活动数据(防止下次重开活动造成数据混乱)
	 */
	public void sendMail() {
		PlayerActivityInfoManager.getInstance().removePlayerActivityInfo(this.getActivityBean().getQ_id());
		List<PlayerActivityInfo> awardList = PlayerActivityInfoManager.getInstance().selectByActivityIdAndAward(this.getActivityBean().getQ_id());
		PlayerActivityInfoManager.getInstance().delete(this.getActivityBean().getQ_id());
		if(CollectionUtil.isNotBlank(awardList)) {
			for(PlayerActivityInfo playerInfo : awardList) {
				try {
					JSONArray award2jsonArray = playerInfo.getAward2JSONArray();
					if(award2jsonArray!=null) {
						for(int i=0;i<award2jsonArray.size();i++) {
							int order = Integer.parseInt(award2jsonArray.getString(i));
							Row row = detailInfo.getRows().get(order);
							String award = row.getAward();
							if(StringUtils.isNotBlank(award)) {
								int job = playerInfo.getJob();
								List<Item> items = parseAward(job,award);
								MailServerManager.getInstance().sendSystemMail(playerInfo.getPlayerId(), null, this.getActivityBean().getQ_desc(), "您有奖励未领取呢，快点击按钮领取吧。", (byte) 0, 0, items);
								//领取记录
								NewActivityGetAwardLog log = new NewActivityGetAwardLog();
								log.setRoleId(playerInfo.getPlayerId());
								log.setActivityId(this.getActivityBean().getQ_id());
								StringBuilder itemStr = new StringBuilder("");
								if(CollectionUtil.isNotBlank(items)) {
									for(Item item:items) {
										itemStr.append(item.getItemModelId()+"_"+item.getNum());
										itemStr.append(",");
									}
								}
								log.setAwards(itemStr.toString());
								LogService.getInstance().execute(log);
							}else {
								_logger.error(this.getClass().getName() +"纳尼，没有对应的奖励？！ player:"+playerInfo.getPlayerId()+",order:"+order);
							}
						}
					}
					Player player = PlayerManager.getInstance().getOnLinePlayer(playerInfo.getPlayerId());
					if(player != null) {
						sendAwardCountMessage(player, 0);
						sendDetailActivityInfo(player);
					}
				}catch(Exception e) {
					_logger.error(this.getClass().getName() +":"+playerInfo.toString(), e);
				}
			}
		}else {
			_logger.error(this.getClass().getName() +"没有人需要发邮件奖励。");
		}
		if(!isCanVisible(null)) {
			ResRemoveActivityMessage msg = new ResRemoveActivityMessage();
			msg.setActivityId(this.getActivityBean().getQ_id());
			MessageUtil.tell_world_message(msg);
		}
	}
	
	protected List<Item> parseAward(int job,String award){
		//id_num_jobLimit_intensifyLevel_appendLevel_attribute;
		String[] split = award.split(Symbol.DOUHAO_REG);
		List<Item> items = new LinkedList<Item>();
		for(String str:split) {
			String[] split2 = str.split(Symbol.XIAHUAXIAN_REG);
			boolean bind = false;
			if(split2[0].contains("!")) {                                                                       
				split2[0] = split2[0].substring(1);
				bind = true;
			}
			int id = Integer.parseInt(split2[0]);
			int num = Integer.parseInt(split2[1]);
			String jobLimit = split2.length > 2? split2[2]:"0";
			if(!PlayerManager.checkJob(job, jobLimit)) {
				continue;
			}
			int intensifyLevel = split2.length > 3? Integer.parseInt(split2[3]):0;
			int appendLevel = split2.length > 4? Integer.parseInt(split2[4]):0;
			String attributes = split2.length > 5? split2[5]:"";
			List<Item> createItems = Item.createItems(id, num, bind, 0, intensifyLevel, appendLevel, attributes);
			items.addAll(createItems);
		}
		return items;
	}
	public Q_newActivityBean getActivityBean() {
		return activityBean;
	}

	public void setActivityBean(Q_newActivityBean activityBean) {
		this.activityBean = activityBean;
	}
	
	protected Date getStartDate() {
		return startDate;
	}
	protected void setStartDate(Date startDate) {
		this.startDate = startDate;
		_logger.info(this.getClass().getName()  +" set Start Date:"+startDate);
	}
	protected Date getEndDate() {
		return endDate;
	}
	protected void setEndDate(Date endDate) {
		this.endDate = endDate;
		_logger.info(this.getClass().getName()  +" set End Date:"+endDate);
	}
	
	/**
	 * 是否在活动期间内
	 * 
	 * @return
	 */
	public boolean isBetweenStartAndEnd() {
		long now = System.currentTimeMillis();
		return (startDate == null?true:now>startDate.getTime()) && (endDate == null?true:now<endDate.getTime());
	}
	
	/**
	 * 是否可以显示活动
	 * @return
	 */
	public boolean isCanVisible(Player player) {
		return isBetweenStartAndEnd();
	}
	
	/**
	 * 活动是否进行中
	 * 
	 * @return
	 */
	public boolean isRunning() {
		return false;
	}
	
	protected void resetDetailInfo() {
		//if(detailInfo == null) {//重载配置时更新
			DetailActivityInfo detailInfo = new DetailActivityInfo();
			if(activityBean == null) {
				detailInfo = null;
				_logger.warn(this.getClass().getName() +" wrong id:"+activityBean.getQ_id());
				return;
			}
			detailInfo.setId(activityBean.getQ_id());
			Date startDate = getStartDate();
			Date endDate = getEndDate();
			if(startDate != null) {
				detailInfo.setStartTime((int) (startDate.getTime()/1000));
			}
			if(endDate != null) {
				detailInfo.setEndTime((int) (endDate.getTime()/1000));
			}
			String logic = activityBean.getQ_logic();
			if(StringUtil.isNotBlank(logic)) {
				JSONArray array = JSONArray.fromObject(logic);
				List<Row> rows = new LinkedList<Row>();
				for(Object o:array) {
					JSONObject json = (JSONObject)o;
					String condStr = json.getString("cond");
					String rewardStr = json.getString("reward");
					rows.add(new Row(condStr,rewardStr));
				}
				detailInfo.setRows(rows);
			}
			detailInfo.setCondDesc(activityBean.getQ_condDesc());
			this.detailInfo = detailInfo;
	//	}
	}
	
	public void sendAwardCountMessage(Player player,PlayerActivityInfo info) {
		sendAwardCountMessage(player,info.getCanAward());
	}
	public void sendAwardCountMessage(Player player,int canAward) {
		ResNewActivityList msg = new ResNewActivityList();
		List<SimpleActivityInfo> activities = new LinkedList<SimpleActivityInfo>();
		activities.add(new SimpleActivityInfo(this.getActivityBean().getQ_id(),canAward));
		msg.setActivities(activities);
		MessageUtil.tell_player_message(player, msg);
	}
}
