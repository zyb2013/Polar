package com.game.task.struts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.game.backpack.manager.BackpackManager;
import com.game.backpack.structs.Item;
import com.game.data.bean.Q_task_daily_condBean;
import com.game.data.bean.Q_task_daily_monsterBean;
import com.game.data.bean.Q_task_daily_rewardsBean;
import com.game.data.bean.Q_task_extra_rewardsBean;
import com.game.data.manager.DataManager;
import com.game.dblog.LogService;
import com.game.dblog.base.Log;
import com.game.json.JSONserializable;
import com.game.languageres.manager.ResManager;
import com.game.liveness.manager.LivenessManager;
import com.game.mail.manager.MailServerManager;
import com.game.pet.manager.PetScriptManager;
import com.game.player.manager.PlayerManager;
import com.game.player.structs.Player;
import com.game.player.structs.AttributeChangeReason;
import com.game.prompt.structs.Notifys;
import com.game.structs.Reasons;
import com.game.task.bean.DailyTaskInfo;
import com.game.task.bean.TaskAttribute;
import com.game.task.log.DailyTaskFinishLog;
import com.game.task.manager.TaskManager;
import com.game.task.message.ResDailyTaskChangeMessage;
import com.game.task.message.ResTaskFinshMessage;
import com.game.utils.CollectionUtil;
import com.game.utils.MessageUtil;
import com.game.utils.RandomUtils;
import com.game.utils.StringUtil;
import com.game.utils.Symbol;

public class DailyTask extends Task {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(DailyTask.class);
	public static final int FINISH_TYPE_COMMON=0;	//普通完成
	public static final int FINISH_TYPE_SUPPERFINISH=1;	//一键完成
	public static final int FINISH_TYPE_OPTIMALSUPPERFINISH=2;//最优一键完成
	public static int maxCount=10;

	//奖励物品 需要在接受任务时就将物品随机属性计算出
	private List<Item> rewardsGoods=new ArrayList<Item>();
	private List<Item> extraRewardsGoods=new ArrayList<Item>();
	private int condid;//条件ID
	private int monsterId;//怪物表id
	private int rewardId;//奖励ID
	private int extraRewardId;//额外奖励ID
	private int sid;
	@Log(logField="sid",fieldType="int")
	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}
	
	@Override
	public void finshTask(Player player) {
		if (player == null) {
			player = PlayerManager.getInstance().getPlayer(getOwerId());
		}
		if (player == null) {
			return;
		}
		if (player.getId() != getOwerId()) {
			return;
		}
		player.getCurrentDailyTasks().remove(this);	
		try{
			dealResume();	
		}catch (Exception e) {
			logger.error(e,e);
		}
		try{
			noResumeFinshTask(FINISH_TYPE_COMMON);
		}catch (Exception e) {
			logger.error(e,e);
		}
		try {
			TaskManager.getInstance().acceptDailyTask(player);
		} catch (Exception e) {
			logger.error(e, e);
		}
		PetScriptManager.getInstance().finshTask(player);
		
	}

	public void noResumeFinshTask(int finishType){
		Player player = PlayerManager.getInstance().getPlayer(getOwerId());
		ResTaskFinshMessage msg=new ResTaskFinshMessage();
		msg.setModelId(getCondid());
		msg.setType(Task.DAILYTASK);
		msg.setFinshType(finishType);
		MessageUtil.tell_player_message(player, msg);
		dealRewards();
		dealExtraRewards();
		player.getSupperFinshSerial().clear();
		player.getSupperOptimalFinshSerial().clear();
		TaskManager.getInstance().action(player, Task.ACTION_TYPE_RANK, TaskEnum.COMPLETEDAYTASK, 1);
		if(logger.isDebugEnabled()){
			logger.debug("任务结束");
		}
		try {
			DailyTaskFinishLog log=new DailyTaskFinishLog();
			log.setCondmodelId(getCondid());
			log.setExtrarewardsId(getExtraRewardId());
			log.setLoopcount(player.getDailyTaskCount());
			log.setRewardsId(getRewardId());
			log.setRoleId(player.getId());
			log.setTaskId(getId());
			log.setTaskInfo(JSONserializable.toString(this));
			log.setFinshtype(finishType);
			log.setSid(player.getCreateServerId());
			LogService.getInstance().execute(log);
		} catch (Exception e) {
			logger.error(e,e);
		}
		LivenessManager.getInstance().completeRc(player);
	}
	
	
	
	private void buildRewards(Q_task_daily_rewardsBean reward, Player player){
		//初始化奖励的物品
		if (logger.isDebugEnabled()) {
			logger.debug("buildRewards(String) - start");
		}
		if(player.getId()!=getOwerId())return;
		rewardsGoods.clear();
//		Player player = PlayerManager.getInstance().getPlayer(getOwerId());
		String replace=reward.getQ_rewards_goods();
//		String replace = rewardsExpress.replace("@","");
		String[] split = replace.split(Symbol.FENHAO_REG);
		List<Item> result=new ArrayList<Item>();
		for (String string : split) {
			if(StringUtils.isBlank(string)){
				continue;
			}
			String[] split2 = string.split(Symbol.XIAHUAXIAN_REG);
			boolean isbind=true;
			if(split2[0].startsWith("!")){
				isbind=false;
				split2[0]=split2[0].substring(1,split2[0].length());
			}
			int model= Integer.parseInt(split2[0]);
			int num= Integer.parseInt(split2[1]);
			String jobLimit= null;
			if(split2.length> 2){
				jobLimit = split2[2];	
			}
			int qianghua=0;
			if(split2.length> 3){
				qianghua=Integer.parseInt(split2[3]);	
			}
			String append="";
			if(split2.length> 4){
				append=split2[4];
			}
			if(!PlayerManager.checkJob(player.getJob(), jobLimit)) {
				continue;
			}
			append = append.replace(Symbol.DOUHAO,Symbol.FENHAO);
			List<Item> createItems = Item.createItems(model, num, isbind,0,qianghua, append);
			result.addAll(createItems);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("buildRewards(String) - end");
		}
		rewardsGoods.addAll(result);
		rewardId=reward.getQ_id();
	}
	
	/**
	 * 提升奖励 
	 */
	public void upAchrive(Q_task_daily_rewardsBean targetModel,Player player){
		if (logger.isDebugEnabled()) {
			logger.debug("upAchrive() - start");
		}
		if(targetModel!=null){
			rewardId=targetModel.getQ_id();
			buildRewards(targetModel,player);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("upAchrive() - end");
		}
		changeTask();
	}
	/**
	 * 降低难度
	 * @param targetModel 
	 */
	public void reducedDifficulty(Q_task_daily_condBean targetModel){
		if (logger.isDebugEnabled()) {
			logger.debug("reducedDifficulty() - start");
		}
		if(targetModel!=null){
			condid=targetModel.getQ_id();
			endNeedKillMonster = null;
//			buildCondition(targetModel);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("reducedDifficulty() - end");
		}
		changeTask();
//		if(checkFinsh(false,null)){
//			finshTask(null);
//		}
	}

	@Override
	public void giveUpTask() {
		
	}

	@Override
	public byte acqType() {
		return DAILYTASK;
	}

	@Override
	public byte deliveryType() {
		return COMMIT_NPC;
	}


	public List<Item> getRewardsGoods() {
		return rewardsGoods;
	}


	public void setRewardsGoods(List<Item> rewardsGoods) {
		this.rewardsGoods = rewardsGoods;
	}


	public int getCondid() {
		return condid;
	}


	public void setCondid(int condid) {
		this.condid = condid;
	}


	public int getRewardId() {
		return rewardId;
	}

	public int getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(int monsterId) {
		this.monsterId = monsterId;
	}

	public void setRewardId(int rewardId) {
		this.rewardId = rewardId;
	}


	public List<Item> getExtraRewardsGoods() {
		return extraRewardsGoods;
	}

	public void setExtraRewardsGoods(List<Item> extraRewardsGoods) {
		this.extraRewardsGoods = extraRewardsGoods;
	}

	public int getExtraRewardId() {
		return extraRewardId;
	}

	public void setExtraRewardId(int extraRewardId) {
		this.extraRewardId = extraRewardId;
	}

	@Override
	protected void dealResume() {
		Q_task_daily_condBean q_task_daily_condBean = DataManager.getInstance().q_task_daily_condContainer.getMap().get(getCondid());
		if(q_task_daily_condBean==null){
			logger.error(getCondid()+"日常任务条件模型找不到",new Exception());
			return;
		}
		Player player = PlayerManager.getInstance().getPlayer(getOwerId());
//		String q_end_needgoods = q_task_daily_condBean.getQ_end_needgoods();
//		String[] split = q_end_needgoods.split(Symbol.FENHAO_REG);
//		for (String string : split) {
//			String[] express = string.split(Symbol.XIAHUAXIAN_REG);
//			int modelId = Integer.parseInt(express[0]);
//			int num = Integer.parseInt(express[1]);
//			BackpackManager.getInstance().removeItem(player,modelId, num);
//		}
		String q_resume_goods = q_task_daily_condBean.getQ_resume_goods();
		String[] split = q_resume_goods.split(Symbol.FENHAO_REG);
		StringBuilder builder=new StringBuilder();
		for (String string : split) {
			if(StringUtils.isBlank(string)){
				continue;
			}
			String[] express = string.split(Symbol.XIAHUAXIAN_REG);
			int modelId = Integer.parseInt(express[0]);
			int num = Integer.parseInt(express[1]);
//			Q_itemBean model = DataManager.getInstance().q_itemContainer.getMap().get(modelId);
			String name=BackpackManager.getInstance().getName(modelId);
			builder.append(name).append("(").append(num).append("),");
			BackpackManager.getInstance().removeItem(player,modelId, num,Reasons.TASKRESUME,getId());
		}
		if(builder.length()>0){
			String substring = builder.substring(0, builder.length()-1);
			MessageUtil.notify_player(player,Notifys.CHAT_SYSTEM,ResManager.getInstance().getString("日常任务扣除物品:{1}"),substring);
		}
	}


	@Override
	protected void dealRewards() {
		// StringBuilder builder=new StringBuilder();
		StringBuilder goodsrewards=new StringBuilder();
		Player player = PlayerManager.getInstance().getPlayer(getOwerId());
		Q_task_daily_rewardsBean model = DataManager.getInstance().q_task_daily_rewardsContainer.getMap().get(getRewardId());
		if(model==null){
			logger.error(player.getId()+":"+getCondid()+" 日常任务奖励模型找不到",new Exception());
			return;
		}else{
			String q_rewards_achieve = model.getQ_rewards_achieve();
			if(!StringUtils.isBlank(q_rewards_achieve)) {
				String[] split = q_rewards_achieve.split(Symbol.FENHAO_REG);
				for (String string : split) {
					if(StringUtils.isBlank(string)){
						continue;
					}
					@SuppressWarnings("unused")
					int parseInt = Integer.parseInt(string);
					//TODO 奖励成就
				}
			}
			
			//奖励绑定钻石
			int q_rewards_bindYuanBao = model.getQ_rewards_bindyuanbao();
			if(q_rewards_bindYuanBao>0){
				BackpackManager.getInstance().changeBindGold(player,q_rewards_bindYuanBao,Reasons.TASKREWARDS,getId());
				// builder.append(String.format(ResManager.getInstance().getString("绑钻(%s),"), q_rewards_bindYuanBao));
			}
			
			//奖励金币
			int q_rewards_coin = model.getQ_rewards_coin();
			if(q_rewards_coin>0){
				BackpackManager.getInstance().changeMoney(player, q_rewards_coin,Reasons.TASKREWARDS,getId());
				// builder.append(String.format(ResManager.getInstance().getString("金币(%s),"), q_rewards_coin));
			}
			
			//奖励 经验
			int q_rewards_exp = model.getQ_rewards_exp();
			if(q_rewards_exp>0){
				PlayerManager.getInstance().addExp(player, q_rewards_exp, AttributeChangeReason.TASKREWARDS);
				// builder.append(String.format(ResManager.getInstance().getString("经验(%s),"), q_rewards_exp));
			}
			// 奖励声望
			// int q_rewards_prestige = model.getQ_rewards_prestige();
			// if (q_rewards_prestige > 0) {
			// PlayerManager.getInstance().addBattleExp(player, q_rewards_prestige, AttributeChangeReason.TASKREWARDS);
			// builder.append(String.format(ResManager.getInstance().getString("声望(%s),"), q_rewards_prestige));
			// }
			// // 奖励 真气
			// int q_rewards_zq = model.getQ_rewards_zq();
			// if (q_rewards_zq > 0) {
			// PlayerManager.getInstance().addZhenqi(player, q_rewards_zq,AttributeChangeReason.TASKREWARDS);
			// builder.append(String.format(ResManager.getInstance().getString("真气(%s),"), q_rewards_zq));
			// }
		}
		//任务奖励物品序列（物品ID_数量_强化等级_附加属性类型|附加属性比例;物品ID_数量;物品ID_数量）
		List<Item> rewardsGoods = getRewardsGoods();
		List<Item> spilthGoods=new ArrayList<Item>();
		for (Item item : rewardsGoods) {
//			Q_itemBean itemmodel = DataManager.getInstance().q_itemContainer.getMap().get(item.getItemModelId());
			String name=BackpackManager.getInstance().getName(item.getItemModelId());
			goodsrewards.append(name).append("(").append(item.getNum()).append("),");	
			if(BackpackManager.getInstance().hasAddSpace(player,item)){
				BackpackManager.getInstance().addItem(player, item,Reasons.TASKREWARDS,getId());
			}else{
				spilthGoods.add(item);
			}
		}
		if (CollectionUtil.isNotBlank(spilthGoods)) {
			// BackpackManager.getInstance().addAbleReceieve(player, spilthGoods);
			MailServerManager.getInstance().sendSystemMail(player.getId(), player.getName(), "日常任务奖励", "完成日常任务剩余的奖励", (byte) 0, 0, spilthGoods);
		}
		// if(builder.length()>0){
		// String substring = builder.substring(0, builder.length()-1);
		// MessageUtil.notify_player(player, Notifys.CHAT_SYSTEM,ResManager.getInstance().getString("完成日常任务获得奖励:{1}"),substring);
		// }
		if(goodsrewards.length()>0){
			String substring = goodsrewards.substring(0, goodsrewards.length()-1);
			MessageUtil.notify_player(player, Notifys.NORMAL, ResManager.getInstance().getString("完成日常任务获得物品:{1}"), substring);
		}
		
		
	}
	private void dealExtraRewards() {
		
		
		Player player = PlayerManager.getInstance().getPlayer(getOwerId());
//		int dailyTaskCount = player.getDailyTaskCount();
		if(player.getDailyTaskCount()!=DailyTask.maxCount){
			return;
		}
//		if(dailyTaskCount<maxCount){
//			//只有
//			return;
//		}
	/*	
	 * luminghua hide (repeat)
	 * if(player.getDailyTaskCount()!=DailyTask.maxCount){
			return;
		}*/
		
//		StringBuilder builder=new StringBuilder();
		
		
		Q_task_extra_rewardsBean model = DataManager.getInstance().q_task_extra_rewardsContainer.getMap().get(getExtraRewardId());
		if(model==null){
			logger.error(getExtraRewardId()+"日常任务额外奖励模型找不到",new Exception());
		}else{
			String q_rewards_achieve = model.getQ_rewards_achieve();
			if(!StringUtils.isBlank(q_rewards_achieve)) {
				String[] split = q_rewards_achieve.split(Symbol.FENHAO_REG);
				for (String string : split) {
					if(StringUtils.isBlank(string)){
						continue;
					}
					@SuppressWarnings("unused")
					int parseInt = Integer.parseInt(string);
					//TODO 奖励成就
				}
			}
			//奖励绑定钻石
			int q_rewards_bindYuanBao = model.getQ_rewards_bindyuanbao();
			if(q_rewards_bindYuanBao>0){
				BackpackManager.getInstance().changeBindGold(player,q_rewards_bindYuanBao,Reasons.TASKREWARDS,getId());	
//				builder.append(String.format(ResManager.getInstance().getString("绑钻(%s),"), q_rewards_bindYuanBao));
			}
			//奖励金币
			int q_rewards_coin = model.getQ_rewards_coin();
			if(q_rewards_coin>0){
				BackpackManager.getInstance().changeMoney(player, q_rewards_coin,Reasons.TASKREWARDS,getId());
//				builder.append(String.format(ResManager.getInstance().getString("金币(%s),"), q_rewards_coin));
			}
			//奖励 经验
			int q_rewards_exp = model.getQ_rewards_exp();
			if(q_rewards_exp>0){
				PlayerManager.getInstance().addExp(player, q_rewards_exp, AttributeChangeReason.TASKREWARDS);
//				builder.append(String.format(ResManager.getInstance().getString("经验(%s),"), q_rewards_exp));
			}
			//奖励声望
			/*
			 * luminghua hide
			 * int q_rewards_prestige = model.getQ_rewards_prestige();
			if(q_rewards_prestige>0){
				PlayerManager.getInstance().addBattleExp(player, q_rewards_prestige, AttributeChangeReason.TASKREWARDS);
				builder.append(String.format(ResManager.getInstance().getString("声望(%s),"), q_rewards_prestige));
			}
			//奖励 真气
			int q_rewards_zq = model.getQ_rewards_zq();
			if(q_rewards_zq>0){
				PlayerManager.getInstance().addZhenqi(player, q_rewards_zq,AttributeChangeReason.TASKREWARDS);
				builder.append(String.format(ResManager.getInstance().getString("真气(%s),"), q_rewards_zq));
			}*/
		}
		//任务奖励物品序列（物品ID_数量_强化等级_附加属性类型|附加属性比例;物品ID_数量;物品ID_数量）
		List<Item> rewardsGoods =getExtraRewardsGoods(); 
		List<Item> spilthGoods=new ArrayList<Item>();
		StringBuilder goodsrewards=new StringBuilder();
		for (Item item : rewardsGoods) {
			String name=BackpackManager.getInstance().getName(item.getItemModelId());
			goodsrewards.append(name).append("(").append(item.getNum()).append("),");	
			if(BackpackManager.getInstance().hasAddSpace(player,item)){
				BackpackManager.getInstance().addItem(player, item,Reasons.TASKREWARDS,getId());
			}else{
				spilthGoods.add(item);
			}
		}
		if (CollectionUtil.isNotBlank(spilthGoods)) {
			// BackpackManager.getInstance().addAbleReceieve(player, spilthGoods);
			MailServerManager.getInstance().sendSystemMail(player.getId(), player.getName(), "日常任务奖励", "完成10环日常任务剩余的奖励", (byte) 0, 0, spilthGoods);
		}
		/*
		 * luminghua hide
		 * if(builder.length()>0){
			String substring = builder.substring(0, builder.length()-1);
			MessageUtil.notify_player(player, Notifys.CHAT_SYSTEM,ResManager.getInstance().getString("完成所有日常任务获得额外奖励:{1}"),substring);
		}*/
		if(goodsrewards.length()>0){
			String substring = goodsrewards.substring(0, goodsrewards.length()-1);
			MessageUtil.notify_player(player, Notifys.CHAT_SYSTEM,ResManager.getInstance().getString("完成所有日常任务获得额外奖励:{1}"),substring);
		}
	}

	public void initTask(Player player) {
		setOwerId(player.getId());
		List<Q_task_daily_condBean> conds = new ArrayList<Q_task_daily_condBean>();
		List<Q_task_daily_monsterBean> monsters = new ArrayList<Q_task_daily_monsterBean>();
		List<Q_task_daily_rewardsBean> rewards = new ArrayList<Q_task_daily_rewardsBean>();
		List<Q_task_extra_rewardsBean> extraRewards=new ArrayList<Q_task_extra_rewardsBean>();
		List<Q_task_daily_condBean> condlist = DataManager.getInstance().q_task_daily_condContainer.getList();
		List<Q_task_daily_monsterBean> monsterByLevel = DataManager.getInstance().q_task_daily_monsterContainer.getList();
		List<Q_task_daily_rewardsBean> rewardslist = DataManager.getInstance().q_task_daily_rewardsContainer.getList();
		List<Q_task_extra_rewardsBean> rewardsExtraList = DataManager.getInstance().q_task_extra_rewardsContainer.getList();		
		int level = player.getLevel();

		for (Q_task_daily_condBean cond : condlist) {
			if (cond.getQ_mingrade() <= level && level <= cond.getQ_maxgrade()) {
				conds.add(cond);
			}
		}
		for(Q_task_daily_monsterBean monster:monsterByLevel) {
			int diff = player.getLevel()-monster.getQ_level();
			if(diff < 0) {
				break;
			}
			if(diff <= 20) {
				monsters.add(monster);
			}
		}

		int random = RandomUtils.random(conds.size());
		Q_task_daily_condBean model = conds.remove(random);
		condid=model.getQ_id();
		while(true) {
			if(monsters.size() == 0) {
				monsterId = monsterByLevel.get(0).getQ_id();
				break;
			}
			//上一次的日常任务目标，不能连续两次杀同一只怪或者收集同一个物品
			random = RandomUtils.random(monsters.size());
			Q_task_daily_monsterBean q_task_daily_monsterBean = monsters.remove(random);
			int q_monster = q_task_daily_monsterBean.getQ_monster();
			if(!StringUtil.isBlank(player.getLastDailyTaskTarget())) {
				if(monsters.size() > 0 && player.getLastDailyTaskTarget().contains(String.valueOf(q_monster))) {
					logger.error(player.getName()+" level:"+level+",dailyTask repeat monster:"+player.getLastDailyTaskTarget()+",need:"+model.getQ_id());
					continue;
				}
			}
			player.setLastDailyTaskTarget(String.valueOf(q_monster));
			/*String q_end_needgoods = model.getQ_end_needgoods();
			if(!StringUtil.isBlank(q_end_needgoods) && !StringUtil.isBlank(player.getLastDailyTaskTarget())) {
				if(q_end_needgoods.contains(player.getLastDailyTaskTarget())) {
					continue;
				}else {
					player.setLastDailyTaskTarget(q_end_needgoods.split(Symbol.XIAHUAXIAN_REG)[0]);
				}
			}*/
			monsterId = q_task_daily_monsterBean.getQ_id();
			break;
		}
		
		// 初始化奖励
		for (Q_task_daily_rewardsBean reward : rewardslist) {
			if (reward.getQ_mingrade() <= level && level <= reward.getQ_maxgrade()) {
				rewards.add(reward);
			}
		}
		if(rewards.size()>0){
			Q_task_daily_rewardsBean reward = rewards.get(RandomUtils.random(rewards.size()));
			buildRewards(reward,player);	
		}
		for (Q_task_extra_rewardsBean extraReward : rewardsExtraList) {
			if(extraReward.getQ_mingrade()<=level&&level<=extraReward.getQ_maxgrade()){
				extraRewards.add(extraReward);
			}
		}
		if(extraRewards.size()>0){
			Q_task_extra_rewardsBean extraReward = extraRewards.get(RandomUtils.random(extraRewards.size()));
			buildExttraRewards(extraReward,player);	
		}
		logger.error(player.getId()+" level "+player.getLevel()+" init dailyTask:"+condid+","+monsterId+","+rewardId);
	}

	private void buildExttraRewards(Q_task_extra_rewardsBean extraReward, Player player) {
		// 初始化奖励的物品
		if (logger.isDebugEnabled()) {
			logger.debug("buildRewards(String) - start");
		}
		if(player.getId()!=getOwerId())return;
		extraRewardsGoods.clear();
		String replace = extraReward.getQ_rewards_goods();
		String[] split = replace.split(Symbol.FENHAO_REG);
		List<Item> result = new ArrayList<Item>();
		for (String string : split) {
			if(StringUtils.isBlank(string)){
				continue;
			}
			String[] split2 = string.split(Symbol.XIAHUAXIAN_REG);
			boolean isbind=true;
			if(split2[0].startsWith("!")){
				isbind=false;
				split2[0]=split2[0].substring(1,split2[0].length());
			}
			int model = Integer.parseInt(split2[0]);
			int num = Integer.parseInt(split2[1]);
			String jobLimit = null;
			if (split2.length > 2) {
				jobLimit = split2[2];
			}
			int qianghua = 0;
			if (split2.length > 3) {
				qianghua = Integer.parseInt(split2[3]);
			}
			String append = "";
			if (split2.length > 4) {
				append = split2[4];
			}
			if(!PlayerManager.checkJob(player.getJob(), jobLimit)){
				continue;
			}
			append = append.replace(Symbol.DOUHAO, Symbol.FENHAO);
			List<Item> createItems = Item.createItems(model, num, isbind, 0, qianghua, append);
			result.addAll(createItems);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("buildRewards(String) - end");
		}
		extraRewardsGoods.addAll(result);
		setExtraRewardId(extraReward.getQ_id());
	}

	@SuppressWarnings("unused")
	@Override
	public DailyTaskInfo buildTaskInfo() {
		Player player = PlayerManager.getInstance().getPlayer(getOwerId());
		
		DailyTaskInfo info=new DailyTaskInfo();
		info.setJlId(getRewardId());
		info.setPremiseId(getCondid());
		info.setOtherjlId(getExtraRewardId());
		info.setMonsterId(monsterId);
		for (Integer achieveModel : endNeedAchieve()) {
			//TODO  任务条件  成就
		}
		Set<Integer> keySet = endNeedGoods().keySet();
		for (Integer key : keySet) {
//			int num = BackpackManager.getInstance().getItemNum(player, Integer.parseInt(key));
			Integer need = endNeedGoods().get(key);
			int num = BackpackManager.getInstance().getItemNum(player,key);
			if(num>need){
				num=need;
			}
			TaskAttribute taskAttribute = new TaskAttribute();
			taskAttribute.setModel(key);
			taskAttribute.setNum(num);
			info.getPermiseGoods().add(taskAttribute);
		}
		Set<Integer> keySet2 = endNeedKillMonster().keySet();
		for (Integer key : keySet2) {
			Integer integer = killmonsters.get(String.valueOf(key));
			TaskAttribute taskAttribute = new TaskAttribute();
			taskAttribute.setModel(key);
			taskAttribute.setNum(integer == null ? 0 : integer);
			info.getPermiseMonster().add(taskAttribute);
		}
		for (Item item : rewardsGoods) {
			info.getRewards().add(item.buildItemInfo());
		}
		return info;
	}

	@Override
	public void changeTask() {
		Player player = PlayerManager.getInstance().getPlayer(getOwerId());
		ResDailyTaskChangeMessage msg=new ResDailyTaskChangeMessage();
		msg.setDaylyTaskacceptcount(player.getDailyTaskCount());
		msg.setTask(buildTaskInfo());
		MessageUtil.tell_player_message(player, msg);
	}



	private transient HashMap<Integer,Integer> endNeedKillMonster = null;
	@Override
	public HashMap<Integer, Integer> endNeedKillMonster() {
		if(endNeedKillMonster != null) {
			return endNeedKillMonster;
		}
		Q_task_daily_condBean model = DataManager.getInstance().q_task_daily_condContainer.getMap().get(getCondid());
		Q_task_daily_monsterBean monsterById = DataManager.getInstance().q_task_daily_monsterContainer.getBeanById(monsterId);
		HashMap<Integer,Integer> result=new HashMap<Integer, Integer>();
		if(model == null || monsterById == null){
			logger.error(getCondid()+","+monsterId+" 日常任务条件模型找不到",new Exception());
			return result;
		}
		int monsterNum = 1;
		String monsterStr = model.getQ_end_needkillmonster();
		
		if(StringUtil.isNotBlank(monsterStr)){
			monsterNum = Integer.parseInt(monsterStr);
		}
		result.put(monsterById.getQ_monster(), monsterNum);
		endNeedKillMonster = result;
		return result;
	}



	@Override
	public HashSet<Integer> endNeedAchieve() {
		HashSet<Integer> set=new HashSet<Integer>();
		Q_task_daily_condBean model = DataManager.getInstance().q_task_daily_condContainer.getMap().get(getCondid());
		if(model==null){
			logger.error(getCondid()+"日常任务条件模型找不到",new Exception());
			return set;
		}
		String q_end_need_achieve = model.getQ_end_needachieve();
		if(q_end_need_achieve!=null&&!"".equals(q_end_need_achieve)){
			if(q_end_need_achieve.startsWith("@")){
				q_end_need_achieve=q_end_need_achieve.replace("@","");
			}
			//TODO 需要成就系统
//			String substring = q_end_need_achieve;
//			String[] achieves = substring.split(Symbol.FENHAO);
//			for (String achieve : achieves) {
//				int parseInt = Integer.parseInt(achieve);
//				endNeedAchieve.add(parseInt);
//			}
		}
		return set;
	}



	@Override
	public HashMap<Integer, Integer> endNeedGoods() {
		Q_task_daily_condBean model = DataManager.getInstance().q_task_daily_condContainer.getMap().get(getCondid());
		HashMap<Integer,Integer> result=new HashMap<Integer, Integer>();
		if(model==null){
			logger.error(getCondid()+"日常任务条件模型找不到",new Exception());
			return result;
		}
		String q_end_need_goods = model.getQ_end_needgoods();
		if(q_end_need_goods!=null&&!"".equals(q_end_need_goods)){
			String substring = q_end_need_goods;
			if(substring.startsWith("@")){
				substring=substring.replace("@","");
			}
			String[] split = substring.split(Symbol.FENHAO_REG);
			for (String string : split) {
				if(StringUtils.isBlank(string)){
					continue;
				}
				String[] split2 = string.split(Symbol.XIAHUAXIAN_REG);
				int parseInt = Integer.parseInt(split2[0]);
				int parseInt2 = Integer.parseInt(split2[1]);
				result.put(parseInt,parseInt2);
			}
		}
		return result;
	}

	@Override
	public int endNeedHorseLevel() {
		return 0;
	}

	@Override
	public int endNeedConquerTaskCount() {
		return 0;
	}

	@Override
	public int endNeedDailyTaskCount() {
		return 0;
	}
}
