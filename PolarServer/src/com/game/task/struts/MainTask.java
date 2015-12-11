package com.game.task.struts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.game.backpack.manager.BackpackManager;
import com.game.backpack.structs.Equip;
import com.game.backpack.structs.Item;
import com.game.backpack.structs.ItemTypeConst;
import com.game.collect.manager.CollectManager;
import com.game.config.Config;
import com.game.data.bean.Q_itemBean;
import com.game.data.bean.Q_task_mainBean;
import com.game.data.manager.DataManager;
import com.game.dblog.LogService;
import com.game.horse.manager.HorseManager;
import com.game.horse.struts.Horse;
import com.game.json.JSONserializable;
import com.game.languageres.manager.ResManager;
import com.game.mail.manager.MailServerManager;
import com.game.manager.ManagerPool;
import com.game.pet.manager.PetOptManager;
import com.game.pet.manager.PetScriptManager;
import com.game.player.manager.PlayerAttributeManager;
import com.game.player.manager.PlayerManager;
import com.game.player.structs.AttributeChangeReason;
import com.game.player.structs.Player;
import com.game.player.structs.PlayerAttributeType;
import com.game.prompt.structs.Notifys;
import com.game.script.manager.ScriptManager;
import com.game.script.structs.ScriptEnum;
import com.game.skill.manager.SkillManager;
import com.game.structs.Reasons;
import com.game.task.bean.MainTaskInfo;
import com.game.task.bean.TaskAttribute;
import com.game.task.log.MainTaskLog;
import com.game.task.manager.TaskManager;
import com.game.task.message.ResMainTaskChangeMessage;
import com.game.task.message.ResTaskFinshMessage;
import com.game.task.message.ResTaskRewardInBagMessage;
import com.game.task.script.IMainTaskFinishTaskAction;
import com.game.task.script.ITaskRewardsScript;
import com.game.utils.CollectionUtil;
import com.game.utils.Global;
import com.game.utils.MapUtils;
import com.game.utils.MessageUtil;
import com.game.utils.StringUtil;
import com.game.utils.Symbol;

public class MainTask extends Task {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(MainTask.class);
	
	private int modelid=0;
	
	public int getModelid() {
		return modelid;
	}
	public void setModelid(int modelid) {
		this.modelid = modelid;
	}
	private HashMap<String, Integer> rankActionSaveMap = new HashMap<String, Integer>();
	
	public HashMap<String, Integer> getRankActionSaveMap() {
		return rankActionSaveMap;
	}

	public void setRankActionSaveMap(HashMap<String, Integer> rankActionSaveMap) {
		this.rankActionSaveMap = rankActionSaveMap;
	}
	@Override
	public void finshTask(Player player) {
		finshTask(player,false);
	}

	public void finshTask(Player player,boolean isDoubleReward) {
		if (logger.isDebugEnabled()) {
			logger.debug("finshTask() - start");
		}
		if (player == null) {
			player = PlayerManager.getInstance().getOnLinePlayer(getOwerId());
		}
		if (player == null) {
			return;
		}
		if (player.getId() != getOwerId()) {
			return;
		}
//		Player player = PlayerManager.getInstance().getOnLinePlayer(getOwerId());
		String beforereceiveAble=JSONserializable.toString(player.getTaskRewardsReceiveAble());
		player.getCurrentMainTasks().remove(this);
		ResTaskFinshMessage msg=new ResTaskFinshMessage();
		msg.setModelId(getModelid());
		msg.setType(Task.MAINTASK);
		MessageUtil.tell_player_message(player, msg);
		try{
			dealResume();
			dealRewards(isDoubleReward);			
		}catch (Exception e) {
			logger.error(e,e);
		}

//		try{ 用于药袋子任务调试
//			if(getModelid()==10180){
//				throw new Exception("玩家(" + player.getId() + ")完成药袋子任务调用！");
//			}
//		}catch (Exception e) {
//			logger.error(e,e);
//		}
		if(logger.isDebugEnabled()){
			logger.debug(getModelid()+"主线任务结束");
		}
		try {
			MainTaskLog log = new MainTaskLog();
			log.setRoleId(player.getId());
			log.setLevel(player.getLevel());
			log.setTaskModelId(getModelid());
			log.setType(2);
			LogService.getInstance().execute(log);
		}catch(Exception e) {
			logger.error("wrong taskId:"+getModelid()+",playerId:"+player.getId(),e);
		}
		Q_task_mainBean model = DataManager.getInstance().q_task_mainContainer.getMap().get(modelid);
		if (model == null) {
			logger.error(getModelid() + "主线任务模型找不着", new NullPointerException());
			return;
		}
		//触发转职任务
		PlayerManager.getInstance().changeJob(player, getModelid());

		//主线任务下一组任务
		String q_next_task = model.getQ_next_task();
		if (!StringUtil.isBlank(q_next_task)) {
			String[] split = q_next_task.split(Symbol.FENHAO_REG);
			for (int i = 0; i < split.length; i++) {
				String[] split2 = split[i].split(Symbol.SHUXIAN_REG);
				if(split2.length == 1 || PlayerManager.checkJob(player.getJob(), split2[1]))
					TaskManager.getInstance().acceptMainTask(player, Integer.parseInt(split2[0]));
			}
		}
		PlayerAttributeManager.getInstance().countPlayerAttribute(player, PlayerAttributeType.TASK_CHATPER);
		/*try {
			MainTask accept = player.getCurrentMainTasks().get(0);
			if(accept!=null){
				log.setAcceptafterReceiveAble(JSONserializable.toString(player.getTaskRewardsReceiveAble()));
				log.setAcceptbeforeReceiveAble(beforereceiveAble);
				log.setAcceptmodelId(accept.getModelid());
				log.setAccepttaskInfo(JSONserializable.toString(accept));
				log.setAcceptlevel(player.getLevel());
				log.setAcceptonlinetime(player.getAccunonlinetime());
			}
			LogService.getInstance().execute(log);
		} catch (Exception e) {
			logger.error(e,e);
		}*/
		try{
			IMainTaskFinishTaskAction finishTask=(IMainTaskFinishTaskAction) ScriptManager.getInstance().getScript(ScriptEnum.TASK_FINISHAFTER);
			if(finishTask==null){
				logger.info("完成主线任务脚本找不到");
			}else{
				finishTask.finishMainTaskAfter(player, this);
			}
		}catch (Exception e) {
			logger.error(e,e);
		}
		try{
			CollectManager.getInstance().sendCollectInfo(player,(byte)0);
		}catch (Exception e) {
			logger.error(e,e);
		}
		if(getModelid()==TaskManager.ACTIVITY_NEED_MAINTASK){
			player.setActivityDailyTask(true);
			TaskManager.getInstance().acceptDailyTask(player);
		}
		PetScriptManager.getInstance().finshTask(player);
	}
	private void init(){
		if (logger.isDebugEnabled()) {
			logger.debug("init() - start"+getModelid());
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("init() - end"+getModelid());
		}
		
	}
	public boolean action(int actionType, int model, int num,Object ... params) {
		if(actionType == TaskEnum.GETITEM) {
			HashMap<Integer, Integer> endNeedGoods = this.endNeedGoods();//收集物品
			HashMap<Integer, Integer> endNeedCondition = endNeedCondition();//物品合成
			if(endNeedGoods.size() == 0 && endNeedCondition.size() == 0) {
				return false;
			}
			Integer composeItemId = endNeedCondition.get(TaskEnum.ITEM_COMPOSE);
			if(composeItemId != null && composeItemId == model) {
				getRankActionSaveMap().put(String.valueOf(TaskEnum.ITEM_COMPOSE), model);
				return true;
			}else if (endNeedGoods.containsKey(model)) {
				return true;
			}
			return false;
		}
		Q_task_mainBean mainBean = DataManager.getInstance().q_task_mainContainer.getMap().get(modelid);
		if(mainBean == null) {
			logger.error(modelid+"主线任务模型找不着");
			return false;
		}
		if(((actionType == TaskEnum.QIANGHUA || actionType == TaskEnum.WEAR_EQUIP) && mainBean.getQ_task_type() == TaskType.WEAR_INTENSIFY_EQUIP12) ||
				(actionType == TaskEnum.QIANGHUA && mainBean.getQ_task_type() == TaskType.INTENSIFY7) ) {
			List<QiangHuaModel> endNeedQiangHua = this.endNeedQiangHua();
			if(CollectionUtil.isBlank(endNeedQiangHua)) {
				return false;
			}
			if(params != null && params.length >0 && params[0] instanceof Equip) {
				Equip equip = (Equip)params[0];
				if(actionType == TaskEnum.QIANGHUA && mainBean.getQ_task_type() == TaskType.WEAR_INTENSIFY_EQUIP12) {
					Player player = PlayerManager.getInstance().getOnLinePlayer(this.getOwerId());
					if(player == null)
						return false;
					if(!TaskManager.getInstance().isWeared(player, equip))
						return false;
				}
				Q_itemBean q_itemBean = DataManager.getInstance().q_itemContainer.getMap().get(equip.getItemModelId());
				Iterator<QiangHuaModel> iterator = endNeedQiangHua.iterator();
				boolean change = false;
				while(iterator.hasNext()) {
					QiangHuaModel next = iterator.next();
					if(next.pos != -1) {
						if(q_itemBean.getQ_kind()-1 == next.pos) {
							this.getRankActionSaveMap().put(String.valueOf(next.pos), equip.getGradeNum());
							change = true;
						}
					}else if(!next.itemMap.isEmpty()) {
						if(next.itemMap.containsKey(equip.getItemModelId())) {
							this.getRankActionSaveMap().put(String.valueOf(equip.getItemModelId()), equip.getGradeNum());
							change = true;
						}
					}else {
						this.getRankActionSaveMap().put(String.valueOf(-1), equip.getGradeNum());
						change = true;
					}
				}
				return change;
			}else {
				return false;
			}
		}
		if(actionType == TaskEnum.WEAR_EQUIP && mainBean.getQ_task_type() == TaskType.WEAR_NORMAL_EQUIP13) {
			HashMap<Integer, Object> endNeedWear = this.endNeedWear();
			if(CollectionUtil.isBlank(endNeedWear)) {
				return false;
			}
			if(params != null && params.length >0 && params[0] instanceof Equip) {
				Equip equip = (Equip)params[0];
				if(!endNeedWear.containsKey(equip.getItemModelId())) {
					return false;
				}
				if(getRankActionSaveMap().containsKey(String.valueOf(equip.getItemModelId()))) {
					return false;
				}
				getRankActionSaveMap().put(String.valueOf(equip.getItemModelId()), 1);
				return true;
			}else {
				return false;
			}
		}
		if(actionType == TaskEnum.UNWEAR_EQUIP && (mainBean.getQ_task_type() == TaskType.WEAR_NORMAL_EQUIP13 || mainBean.getQ_task_type() == TaskType.WEAR_INTENSIFY_EQUIP12)) {
			if(params != null && params.length >0 && params[0] instanceof Equip) {
				Equip equip = (Equip)params[0];
				Integer remove = getRankActionSaveMap().remove(String.valueOf(equip.getItemModelId()));
				if(remove == null && mainBean.getQ_task_type() == TaskType.WEAR_INTENSIFY_EQUIP12) {
					remove = getRankActionSaveMap().remove(String.valueOf(params[1]));
				}
				if(remove != null)
					return true;
				else
					return false;
			}
		}
		try {
			HashMap<Integer, Integer> endNeedCondition = endNeedCondition();
			if (!endNeedCondition.containsKey(actionType)) {
				return false;
			}
			switch(actionType) {
			case TaskEnum.FIRST_PAY:
				getRankActionSaveMap().put(String.valueOf(actionType), 1);
				return true;
			case TaskEnum.USEITEM: {
//				num = Math.abs(num);只能使用一次
				Integer itemId = endNeedCondition.get(actionType);
				if(itemId == model) {
					getRankActionSaveMap().put(String.valueOf(actionType), itemId);
				}else {
					return false;
				}
				return true;
			}
			case TaskEnum.INCENSELIP: {
				Integer neednum = endNeedCondition.get(actionType);
				Integer nownum = getRankActionSaveMap().get(String.valueOf(actionType));
				nownum = nownum == null ? 0 : nownum;
				if (nownum < neednum) {
					nownum = num;
					if (nownum > neednum) {
						nownum = neednum;
					}
					getRankActionSaveMap().put(String.valueOf(actionType), nownum);
				}
				return true;
			}
			case TaskEnum.HORSE_STAGE_UP: {
				if(endNeedCondition.containsKey(actionType)) {
					getRankActionSaveMap().put(String.valueOf(actionType), num);
					return true;
				}
				return false;
			}

			case TaskEnum.ENTER_MISSION:
			case TaskEnum.S_ZONE:
			case TaskEnum.SS_ZONE:
			case TaskEnum.SSS_ZONE:
			case TaskEnum.COMPLETECLASSICBATTLE: {
				Integer zoneId = endNeedCondition.get(actionType);
				if(zoneId == num) {
					getRankActionSaveMap().put(String.valueOf(actionType), zoneId);
					return true;
				}
				return false;
			}
			case TaskEnum.KILL_BOSS:
			case TaskEnum.KILL_ELITE:
			default :
				if (endNeedCondition.containsKey(actionType)) {
					Integer neednum = endNeedCondition.get(actionType);
					Integer nownum = getRankActionSaveMap().get(String.valueOf(actionType));
					nownum = nownum == null ? 0 : nownum;
					if (nownum < neednum) {
						nownum += num;
						if (nownum > neednum) {
							nownum = neednum;
						}
						getRankActionSaveMap().put(String.valueOf(actionType), nownum);
					}
					return true;
				} else {
					return false;
				}
			}
		} catch (Exception e) {
			logger.error(e, e);
			return false;
		}
	}
	private transient HashMap<Integer, Integer> endNeedCondition = null;
	
	public HashMap<Integer, Integer> endNeedCondition() {
		if(endNeedCondition != null) {
			return endNeedCondition;
		}
		HashMap<Integer, Integer> result = new HashMap<Integer, Integer>();
		endNeedCondition = result;
		try {
			Q_task_mainBean q_task_mainBean = DataManager.getInstance().q_task_mainContainer.getMap().get(getModelid());
			if (q_task_mainBean == null) {
				logger.error(getModelid() + "主线任务模型找不着", new NullPointerException());
				return result;
			}
			String q_condition = q_task_mainBean.getQ_condition();
			if (StringUtils.isBlank(q_condition)) {
				return result;
			}
			JSONArray jsonArray = JSONArray.fromObject(q_task_mainBean.getQ_condition());
			if (jsonArray == null || jsonArray.isEmpty()) {
//				logger.error(getModelid() + "主线任务模型条件数据解析出错", new NullPointerException());
				return result;
			}
			Player player = PlayerManager.getInstance().getPlayer(getOwerId());
			Iterator<JSONArray> iterator = jsonArray.iterator();
			while (iterator.hasNext()) {
				JSONArray next = (JSONArray) iterator.next();
				if (next.size() < 2) {
					logger.error(getModelid() + "主线任务模型条件数据解析出错", new NullPointerException());
					return result;
				}
				if (next.getString(1).contains("|")) {
					// 按职业区分
					String[] split = next.getString(1).split(Symbol.SHUXIAN_REG);
					next.set(1, split[player.getJob() - 1]);
				}
				result.put(next.getInt(0), next.getInt(1));
//				if (integers.length == 3) {
//					result.put(integers[1], integers[2]);
//				}
			}
		} catch (Exception e) {
			logger.error(e, e);
		}
		return result;
	}
	@Override
	public void giveUpTask() {
		//主线任务不能放弃 
	}
	
	@Override
	public byte acqType() {
		return MAINTASK;
	}
	@Override
	public byte deliveryType() {
		Q_task_mainBean q_task_mainBean = DataManager.getInstance().q_task_mainContainer.getMap().get(modelid);
		if(q_task_mainBean==null){
			logger.error(getModelid()+"主线任务模型找不着");
			return (byte)1;
		}
		byte returnbyte = (byte)q_task_mainBean.getQ_finsh_type();
		return returnbyte;
	}
	@Override
	protected void dealResume() {
		Player onLinePlayer = PlayerManager.getInstance().getOnLinePlayer(getOwerId());
		if(onLinePlayer!=null){
			Q_task_mainBean taskModel = DataManager.getInstance().q_task_mainContainer.getMap().get(modelid);
			if(taskModel==null){
				logger.error(getModelid()+"主线任务模型找不着",new NullPointerException());
				return;
			}
			/*
			 * luminghua hide (q_end_need_goods 只检查是否有物品，但不扣除，扣除字段是q_end_resume_goods)
			 * Set<Integer> keySet = endNeedGoods().keySet();
			for (Integer key : keySet) {
				Integer num= endNeedGoods().get(key);
				BackpackManager.getInstance().removeItem(onLinePlayer,key,num,Reasons.TASKRESUME,getId());
			}*/
			String q_end_resume_goods = taskModel.getQ_end_resume_goods();
			StringBuilder builder=new StringBuilder();
			if(!StringUtil.isBlank(q_end_resume_goods)){
				if(!q_end_resume_goods.contains("!")) {
					int indexOf = q_end_resume_goods.indexOf("{");
					if(indexOf != -1) {
						q_end_resume_goods = q_end_resume_goods.substring(0, indexOf);
					}
					String[] split = q_end_resume_goods.split(Symbol.FENHAO_REG);
					for (String string : split) {
						if(!StringUtil.isBlank(string)){
							String[] split2 = string.split(Symbol.XIAHUAXIAN_REG);
							int goodsmodel = Integer.parseInt(split2[0]);
							int goodsnum = Integer.parseInt(split2[1]);
//							Q_itemBean model = DataManager.getInstance().q_itemContainer.getMap().get(goodsmodel);
							String name=BackpackManager.getInstance().getName(goodsmodel);
							builder.append(name).append("(").append(goodsnum).append("),");
							BackpackManager.getInstance().removeItem(onLinePlayer, goodsmodel,goodsnum,Reasons.TASKRESUME,getId());	
						}
					}
				}
			}
			if(builder.length()>0){
				String substring = builder.substring(0, builder.length()-1);
				MessageUtil.notify_player(onLinePlayer,Notifys.CHAT_PERSONAL,ResManager.getInstance().getString("主线任务扣除物品:{1}"),substring);
			}
			if(taskModel.getQ_consume_money()!=0) {
				boolean changeMoney = BackpackManager.getInstance().changeMoney(onLinePlayer, -taskModel.getQ_consume_money(), Reasons.TASKRESUME, Config.getId());
				if(!changeMoney) {
					logger.error("玩家完成任务时扣除金币失败："+onLinePlayer.getId()+","+taskModel.getQ_taskid());
				}
			}
		}
		
	}
	@Override
	protected void dealRewards() {
		dealRewards(false);
	}
	private void dealRewards(boolean isDoubleReward) {
		Q_task_mainBean taskModel = DataManager.getInstance().q_task_mainContainer.getMap().get(modelid);
		if(taskModel==null){
			logger.error(getModelid()+"主线任务模型找不着",new NullPointerException());
			return;
		}		
		String q_rewards_achieve = taskModel.getQ_rewards_achieve();
		Player player = PlayerManager.getInstance().getOnLinePlayer(getOwerId());
		int q_rewards_pet = taskModel.getQ_rewards_pet();
		if(q_rewards_pet!=0){
			PetOptManager.getInstance().addPet(player, q_rewards_pet,"maintask",getId());	
		}
		if (!StringUtil.isBlank(q_rewards_achieve)) {
			String[] split = q_rewards_achieve.split(Symbol.FENHAO_REG);
			for (String string : split) {
				if (StringUtils.isBlank(string)) {
					continue;
				}
				@SuppressWarnings("unused")
				int parseInt = Integer.parseInt(string);
				// TODO 奖励成就
			}
		}
		// StringBuilder builder=new StringBuilder();
		//奖励绑定钻石
		int q_rewards_bindYuanBao = taskModel.getQ_rewards_bindYuanBao();
		if (q_rewards_bindYuanBao > 0) {
			BackpackManager.getInstance().changeBindGold(player, q_rewards_bindYuanBao, Reasons.TASKREWARDS, getId());
			// builder.append(String.format(ResManager.getInstance().getString("%s绑钻,"), q_rewards_bindYuanBao));
		}

		// 奖励金币
		int q_rewards_coin = taskModel.getQ_rewards_coin();
		if(isDoubleReward)
			q_rewards_coin *= 2;
		if (q_rewards_coin > 0) {
			BackpackManager.getInstance().changeMoney(player, q_rewards_coin, Reasons.TASKREWARDS, getId());
			// builder.append(String.format(ResManager.getInstance().getString("%s金币,"), q_rewards_coin));
		}
		
		//奖励 经验
		int q_rewards_exp = taskModel.getQ_rewards_exp();
		if(isDoubleReward)
			q_rewards_exp *= 2;
		if (q_rewards_exp > 0) {
			PlayerManager.getInstance().addExp(player, q_rewards_exp, AttributeChangeReason.TASKREWARDS);
			// builder.append(String.format(ResManager.getInstance().getString("%s经验,"), q_rewards_exp));
		}
		// int q_rewards_exploit = taskModel.getQ_rewards_exploit();
		//
		// if(q_rewards_exploit>0){
		// RankManager.getInstance().addranknum(player, taskModel.getQ_rewards_exploit(),RankType.TASK);
		// builder.append(String.format(ResManager.getInstance().getString("%s军功,"), q_rewards_exploit));
		// }
		// //奖励声望
		// int q_rewards_prestige = taskModel.getQ_rewards_prestige();
		// if(q_rewards_prestige>0){
		// PlayerManager.getInstance().addBattleExp(player, q_rewards_prestige, AttributeChangeReason.TASKREWARDS);
		// builder.append(String.format(ResManager.getInstance().getString("%s声望,"), q_rewards_prestige));
		// }
		// //奖励 真气
		// int q_rewards_zq = taskModel.getQ_rewards_zq();
		// if(q_rewards_zq>0){
		// PlayerManager.getInstance().addZhenqi(player, q_rewards_zq,AttributeChangeReason.TASKREWARDS);
		// builder.append(String.format(ResManager.getInstance().getString("%s真气,"), q_rewards_zq));
		// }
		
//		String q_chapter_buff = taskModel.getQ_chapter_buff();
//		if(q_chapter_buff!=null&&!q_chapter_buff.equals("")){
//			int parseInt = Integer.parseInt(q_chapter_buff);
//			if(parseInt>0){
//				BuffManager.getInstance().addBuff(player, player, parseInt, 0, 0,0);
//			}
//		}
		// 任务奖励物品序列（!(不绑定)物品ID_数量_职业要求_强化等级_附加属性类型1|附加属性比例,附加属性类型2|附加属性比例
		StringBuilder goodsrewards=new StringBuilder();
		ArrayList<Integer> inBagList = new ArrayList<Integer>();
		String q_rewards_goods = taskModel.getQ_rewards_goods();
		if (!StringUtil.isBlank(q_rewards_goods)) {
			String[] goods = q_rewards_goods.split(Symbol.FENHAO_REG);
			for (String goodsExpress : goods) {
				if (goodsExpress != null && !goodsExpress.equals("")) {
					String[] items = goodsExpress.split(Symbol.XIAHUAXIAN_REG);
					if (items.length >= 2) {
						boolean isbind = true;
						if (items[0].startsWith("!")) {
							isbind = false;
							items[0] = items[0].substring(1, items[0].length());
						}
						int modelid = Integer.parseInt(items[0]);
						int num = Integer.parseInt(items[1]);
						int qianghua = 0;
						String jobLimit = null;
						if (items.length > 2) {
							jobLimit = items[2];
						}

						if (items.length > 3) {
							qianghua = Integer.parseInt(items[3]);
						}
						String append = "";
						if (items.length > 4) {
							append = items[4];
						}
						if(!PlayerManager.checkJob(player.getJob(), jobLimit)){
							continue;
						}
							append = append.replace(Symbol.DOUHAO, Symbol.FENHAO);
							// Q_itemBean model =
							// DataManager.getInstance().q_itemContainer.getMap().get(modelid);
							String name = BackpackManager.getInstance().getName(modelid);
							goodsrewards.append(name).append("*").append(num).append("、");
							List<Item> createItems = Item.createItems(modelid, num, isbind, 0, qianghua, append);
							List<Item> spilthGoods = new ArrayList<Item>();
							for (Item item : createItems) {
								if (BackpackManager.getInstance().hasAddSpace(player, item)) {
									BackpackManager.getInstance().addItem(player, item, Reasons.TASKREWARDS, getId());
									inBagList.add(item.getItemModelId());
								} else {
									spilthGoods.add(item);
								}
							}
							// BackpackManager.getInstance().addAbleReceieve(player, spilthGoods);
							// 改为发邮件
							if (spilthGoods.size() > 0) {
								MailServerManager.getInstance().sendSystemMail(player.getId(), player.getName(), "任务奖励", "您的任务 " + taskModel.getQ_name() + " 完成剩余的奖励", (byte) 0, 0,
										spilthGoods);
							}
					}
				}
			}
		}
			MessageUtil.notify_player(player, Notifys.NORMAL, ResManager.getInstance().getString("<font color='#ffffff'>完成任务：</font><font color='#3cff00'>{1}</font>"),
					taskModel.getQ_name());

		if (goodsrewards.length() > 0) {
			String substring = goodsrewards.substring(0, goodsrewards.length() - 1);
			MessageUtil
					.notify_player(player, Notifys.NORMAL, ResManager.getInstance().getString("<font color = '#ffffff'>获得 </font><font color = '#3cff00'>{1}</font>"), substring);
		}
		if(inBagList.size() > 0) {
			//入包消息
			ResTaskRewardInBagMessage msg = new ResTaskRewardInBagMessage();
			msg.setInBagList(inBagList);
			MessageUtil.tell_player_message(player, msg);
		}
		if(taskModel.getQ_rewards_scrpt()!=0){
			try{
				ITaskRewardsScript script=(ITaskRewardsScript) ScriptManager.getInstance().getScript(taskModel.getQ_rewards_scrpt());
				if(script!=null){
					script.rewards(player, this);	
				}else{
					logger.error("任务奖励脚本找不到脚本ID:"+taskModel.getQ_rewards_scrpt());
				}
			}catch (Exception e) {
				logger.error(e,e);
			}
		}
	}
	public void initTask(Player player, int modelId) {
		this.modelid=modelId;
		this.owerId=player.getId();
		Q_task_mainBean model = DataManager.getInstance().q_task_mainContainer.getMap().get(modelId);
		if(model == null) {
			return;
		}
		if(model.getQ_end_needaction()==1){
			isFinshAction=false;
		}
		// // init();
		// player.setCurrentMainTaskId(modelId);
		// player.getCurrentMainTasks().add(this);
		changeTask();
	}
	public MainTaskInfo buildTaskInfo() {
		MainTaskInfo info=new MainTaskInfo();
		info.setModelId(getModelid());
		info.setIsFinshAction((byte)(isFinshAction()?1:0));
		Player player = PlayerManager.getInstance().getPlayer(getOwerId());
		for (Integer achieveModel : endNeedAchieve()) {
			//TODO  任务条件  成就
			info.getPermiseAchieve().add(achieveModel);
		}
		Set<Integer> keySet = endNeedGoods().keySet();
		for (Integer key : keySet) {
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
			if( getKillmonsters().containsKey(String.valueOf(key))){
				Integer integer = getKillmonsters().get(String.valueOf(key));
				TaskAttribute taskAttribute=new TaskAttribute();
				taskAttribute.setModel(key);
				taskAttribute.setNum(integer);
				info.getPermiseMonster().add(taskAttribute);
			}
		}	
		Set<Integer> keySet3 = endNeedKillMonster2().keySet();
		for (Integer key : keySet3) {
			if( getKillmonsters().containsKey(String.valueOf(key))){
				Integer integer = getKillmonsters().get(String.valueOf(key));
				TaskAttribute taskAttribute=new TaskAttribute();
				taskAttribute.setModel(key);
				taskAttribute.setNum(integer);
				info.getPermiseMonster().add(taskAttribute);
			}
		}	
		Iterator<QiangHuaModel> iterator2 = this.endNeedQiangHua().iterator();
		while(iterator2.hasNext()) {
			QiangHuaModel next = iterator2.next();
			int key = -1;
			if(next.pos != -1) {
				key = next.pos;
				Integer value = this.getRankActionSaveMap().get(String.valueOf(key));
				TaskAttribute taskAttribute=new TaskAttribute();
				taskAttribute.setModel(key);
				taskAttribute.setNum(value==null?0:value);
				info.getPermiseQiangHua().add(taskAttribute);
			}else if(!next.itemMap.isEmpty()) {
				Set<Integer> keySet4 = next.itemMap.keySet();
				for(Integer k:keySet4) {
					key = k ;
					Integer value = this.getRankActionSaveMap().get(String.valueOf(key));
					TaskAttribute taskAttribute=new TaskAttribute();
					taskAttribute.setModel(key);
					taskAttribute.setNum(value==null?0:value);
					info.getPermiseQiangHua().add(taskAttribute);
				}
			}else {
				Integer value = this.getRankActionSaveMap().get(String.valueOf(key));
				TaskAttribute taskAttribute=new TaskAttribute();
				taskAttribute.setModel(key);
				taskAttribute.setNum(value==null?0:value);
				info.getPermiseQiangHua().add(taskAttribute);
			}
		}
		HashMap<Integer, Integer> endNeedCondition = endNeedCondition();
		Iterator<Integer> iterator = endNeedCondition.keySet().iterator();
		while (iterator.hasNext()) {
			Integer key = iterator.next();
			String keystr = Integer.toString(key);
			if (getRankActionSaveMap().containsKey(keystr)) {
				int value = getRankActionSaveMap().get(keystr);
				TaskAttribute taskAttribute=new TaskAttribute();
				taskAttribute.setModel(key);
				taskAttribute.setNum(value);
				info.getPermiseOthers().add(taskAttribute);
			}
		}
		Iterator<Integer> iterator3 = this.endNeedWear().keySet().iterator();
		while(iterator3.hasNext()) {
			Integer key = iterator3.next();
			int num = 0;
			if(getRankActionSaveMap().containsKey(String.valueOf(key))) {
				num = 1;
			}
			TaskAttribute taskAttribute=new TaskAttribute();
			taskAttribute.setModel(key);
			taskAttribute.setNum(num);
			info.getPermiseWear().add(taskAttribute);
		}
		return info;
	}
	@Override
	public void changeTask() {
		Player player = PlayerManager.getInstance().getPlayer(getOwerId());
		if(player!=null){
			Q_task_mainBean q_task_mainBean = DataManager.getInstance().q_task_mainContainer.getMap().get(this.getModelid());
			if(q_task_mainBean == null) {
				logger.error(this.getModelid()+"主线任务模型找不着");
				return;
			}
			ResMainTaskChangeMessage msg=new ResMainTaskChangeMessage();
			msg.setTask(buildTaskInfo());
			MessageUtil.tell_player_message(player, msg);	
		}else{
			logger.error("切换主线任务("+this.getModelid()+")时所属角色("+this.getOwerId()+")找不到");
		}
		
	}
	
	/**
	 * 全部都要杀
	 */
	@Override
	public HashMap<Integer, Integer> endNeedKillMonster() {
		return parseKillMonster(";");
	}
	
	/**
	 * 击杀其中一只
	 * @return
	 */
	public HashMap<Integer, Integer> endNeedKillMonster2() {
		return parseKillMonster("|");
	}
	
	private HashMap<Integer, Integer> parseKillMonster(String split_reg) {
		HashMap<Integer, Integer> result=new HashMap<Integer, Integer>();
		Q_task_mainBean model = DataManager.getInstance().q_task_mainContainer.getMap().get(modelid);
		if(model == null){
			logger.error(getModelid()+"主线任务模型找不着",new NullPointerException());
			return result;
		}
		String q_end_need_killmonster = model.getQ_end_need_killmonster();
		if(StringUtil.isNotBlank(q_end_need_killmonster)){
			if(q_end_need_killmonster.contains(";") || q_end_need_killmonster.contains("|")) {
				if(!q_end_need_killmonster.contains(split_reg)) {
					return result;
				}
			}
			if(split_reg.equals("|")) {
				split_reg = "\\|";
			}
			String[] split = q_end_need_killmonster.split(split_reg);
			for (String string : split) {
				if(string.startsWith("@")){
					string=string.replace("@","");
				}
				int indexOf = string.indexOf("{");
				if(indexOf != -1) {
					string = string.substring(0, indexOf);
				}
				String[] split2 = string.split(Symbol.XIAHUAXIAN_REG);
				int parseInt = Integer.parseInt(split2[0]);
				int parseInt2 = Integer.parseInt(split2[1]);
				result.put(parseInt, parseInt2);
			}
		}
		return result;
	}
	
	@Override
	public HashSet<Integer> endNeedAchieve() {
		HashSet<Integer> result=new HashSet<Integer>();
		Q_task_mainBean model = DataManager.getInstance().q_task_mainContainer.getMap().get(modelid);
		if(model==null){
			logger.error(getModelid()+"主线任务模型找不着",new NullPointerException());
			return result;
		}
		String q_end_need_achieve = model.getQ_end_need_achieve();
		if(q_end_need_achieve!=null&&!"".equals(q_end_need_achieve)){
//			if(q_end_need_achieve.startsWith("@")){
//				q_end_need_achieve=q_end_need_achieve.replace("@","");
//			}
			String[] achieves = q_end_need_achieve.split(Symbol.FENHAO);
			for (String achieve : achieves) {
				if(achieve.startsWith("@")){
					achieve=achieve.replace("@","");
				}
				String[] split2 = achieve.split(Symbol.XIAHUAXIAN_REG);
				int parseInt = Integer.parseInt(split2[0]);
				result.add(parseInt);
			}
		}
		return result;
	}
	private transient HashMap<Integer, Integer> endNeedGoods = null;
	@Override
	public HashMap<Integer, Integer> endNeedGoods() {
		if(endNeedGoods != null) {
			return endNeedGoods;
		}
		HashMap<Integer,Integer> result=new HashMap<Integer, Integer>();
		endNeedGoods = result;
		Q_task_mainBean model = DataManager.getInstance().q_task_mainContainer.getMap().get(modelid);
		if(model==null){
			logger.error(getModelid()+"主线任务模型找不着",new NullPointerException());
			return result;
		}
		//Q_end_need_goods和Q_end_resume_goods只能配置其中一个，不能同时两个都配置
		String q_end_need_goods =model.getQ_end_resume_goods();
		if(!StringUtils.isBlank(q_end_need_goods)){
//			if(q_end_need_goods.startsWith("@")){
//				q_end_need_goods=q_end_need_goods.replace("@","");
//			}
			int indexOf = q_end_need_goods.indexOf("{");
			if(indexOf != -1) {
				q_end_need_goods = q_end_need_goods.substring(0, indexOf);
			}
			q_end_need_goods=q_end_need_goods.replace("!","");
			String[] split = q_end_need_goods.split(Symbol.FENHAO_REG);
			for (String string : split) {
				if(string.startsWith("@")){
					string=string.replace("@","");
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
		Q_task_mainBean model = DataManager.getInstance().q_task_mainContainer.getMap().get(modelid);
		if(model==null){
			return 0;
		}
		return model.getQ_end_need_horselevel();
	}
	@Override
	public int endNeedConquerTaskCount() {
		Q_task_mainBean model = DataManager.getInstance().q_task_mainContainer.getMap().get(modelid);
		if(model==null){
			return 0;
		}
		return model.getQ_end_need_conquertaskcount();
	}
	@Override
	public int endNeedDailyTaskCount() {
		Q_task_mainBean model = DataManager.getInstance().q_task_mainContainer.getMap().get(modelid);
		if(model==null){
			return 0;
		}
		return model.getQ_end_need_dailytaskcount();
	}
	
	private transient List<QiangHuaModel> endNeedQiangHua = null;
	/**
	 * 强化任务，部位(0-12)-->强化等级 或者 物品id-->强化等级 
	 * @return
	 */
	public List<QiangHuaModel> endNeedQiangHua(){
		if(endNeedQiangHua != null) {
			return endNeedQiangHua;
		}
		List<QiangHuaModel> result  = new ArrayList<QiangHuaModel>();
		endNeedQiangHua = result;
		Q_task_mainBean model = DataManager.getInstance().q_task_mainContainer.getMap().get(modelid);
		if(model==null){
			logger.error(getModelid()+"主线任务模型找不着",new NullPointerException());
			return result;
		}
		String q_end_need_qianghua = model.getQ_end_need_qianghua();
		if(!StringUtil.isBlank(q_end_need_qianghua)) {
			String[] split = q_end_need_qianghua.split(Symbol.FENHAO_REG);
			for(String sub:split) {
				String[] split2 = sub.split(Symbol.XIAHUAXIAN_REG);
				int buwei = Integer.parseInt(split2[0]);
				int level = Integer.parseInt(split2[2]);
				QiangHuaModel qiangHuaModel = new QiangHuaModel(buwei,0,level);
				result.add(qiangHuaModel);
				String[] split3 = split2[1].split(Symbol.SHUXIAN_REG);
				for(String str:split3) {
					qiangHuaModel.addItemModelId(Integer.parseInt(str));
				}
			}
		}
		return result;
	}
	
	private transient HashMap<Integer,Object> endNeedWear = null;
	public HashMap<Integer,Object> endNeedWear(){
		if(endNeedWear != null) {
			return endNeedWear;
		}
		HashMap<Integer,Object> result = new LinkedHashMap<Integer,Object>();
		endNeedWear = result;
		Q_task_mainBean model = DataManager.getInstance().q_task_mainContainer.getMap().get(modelid);
		if(model==null){
			logger.error(getModelid()+"主线任务模型找不着",new NullPointerException());
			return result;
		}
		String q_end_need_wear = model.getQ_end_need_wear();
		if(StringUtil.isNotBlank(q_end_need_wear)) {
			Player player = PlayerManager.getInstance().getPlayer(getOwerId());
			String[] split = q_end_need_wear.split(Symbol.FENHAO_REG);
			for(String str:split) {
				String[] split2 = str.split(Symbol.SHUXIAN_REG);
				if(split2.length == 2) {
					if(!PlayerManager.checkJob(player.getJob(), split2[1])) {
						continue;
					}
				}
				result.put(Integer.parseInt(split2[0]), Global.REFER_OBJECT);
			}
		}
		return result;
	}
	@Override
	public boolean checkFinsh(boolean isPromp,Player player) {
		if(player==null){
			player= PlayerManager.getInstance().getOnLinePlayer(getOwerId());	
		}
		if(player==null){
			return false;
		}
		if(player.getId()!=getOwerId()){
			return false;
		}
		Q_task_mainBean model = DataManager.getInstance().q_task_mainContainer.getMap().get(modelid);
		if(model==null){
			logger.error(modelid+"主线任务模型找不着",new NullPointerException());
			return true;
		}
		if(player.getTaskRewardsReceiveAble().size()>0){
			if(isPromp)
				MessageUtil.notify_player(player,Notifys.NORMAL,ResManager.getInstance().getString("请先领走领取区域的物品"));
			return false;
		}
		if(player.getLevel()<model.getQ_accept_needmingrade()){
			if(isPromp)
				MessageUtil.notify_player(player,Notifys.NORMAL,ResManager.getInstance().getString("需要{1}级才可开始此任务"),String.valueOf(model.getQ_accept_needmingrade()));
			return false;
		}
		if(player.getMoney() < model.getQ_consume_money()) {
			if(isPromp)
				MessageUtil.notify_player(player,Notifys.NORMAL,ResManager.getInstance().getString("需要{1}金币才能完成此任务"),String.valueOf(model.getQ_consume_money()));
			return false;
		}
		if(player.getMoney() >= Global.BAG_MAX_COPPER) {
			if(isPromp)
				MessageUtil.notify_player(player,Notifys.NORMAL,ResManager.getInstance().getString("金币已达到上限，无法完成任务"));
			return false;
		}
		 if(super.checkFinsh(isPromp,player)) {
			 boolean hold = true;
			 try {
				 if (model.getQ_task_type() == TaskType.WEAR_INTENSIFY_EQUIP12 || model.getQ_task_type() == TaskType.INTENSIFY7) {
					 List<QiangHuaModel> endNeedQiangHua = this.endNeedQiangHua();
						if(CollectionUtil.isNotBlank(endNeedQiangHua)) {
							Iterator<QiangHuaModel> iterator = endNeedQiangHua.iterator();
							while(iterator.hasNext()) {
								QiangHuaModel next = iterator.next();
								int key = -1;
								int level = next.level;
								if(next.pos != -1) {
									key = next.pos;
								}else if(!next.itemMap.isEmpty()) {
									key = player.getEquips().length+1;
								}
								Integer value = this.getRankActionSaveMap().get(String.valueOf(key));
								if( key <0) {
									//不限制物品，但要玩家接受任务后强化
									if(value == null || value < level) {
										if(isPromp)MessageUtil.notify_player(player,Notifys.ERROR,ResManager.getInstance().getString("完成该任务需要将某件装备强化到{1}级"),level+"");
										return false;
									}
								}else if(key <player.getEquips().length) {
									if(value == null || value < level) {
										Equip equip = player.getEquips()[key];
										if(equip==null || equip.getGradeNum()<level) {
											hold = false;
											if(isPromp)MessageUtil.notify_player(player,Notifys.ERROR,ResManager.getInstance().getString("完成该任务需要将这个部位的装备强化到{1}级"),level+"");
											continue;
										}
										this.getRankActionSaveMap().put(String.valueOf(key), level);
									}
								}else {
									if(value == null || value < level) {
										boolean hasEquip = false;
										Iterator<Integer> iterator2 = next.itemMap.keySet().iterator();
										while(iterator2.hasNext()) {
											Integer next2 = iterator2.next();
											if(checkQianghuaEquip(player.getEquips(), next2, level)) {
												hasEquip = true;
												key = next2;
												break;
											}
										}
										if(!hasEquip && model.getQ_task_type() == TaskType.INTENSIFY7) {
											Collection<Item> allItem = ManagerPool.backpackManager.getAllItem(player);
											while(iterator2.hasNext()) {
												Integer next2 = iterator2.next();
												if(checkQianghuaEquip(allItem.toArray(new Item[0]), next2, level)) {
													hasEquip = true;
													key = next2;
													break;
												}
											}	
										}
										if(!hasEquip) {
											hold = false;
											if(isPromp)MessageUtil.notify_player(player,Notifys.ERROR,ResManager.getInstance().getString("完成该任务需要将该装备强化到{1}级"),level+"");
											continue;
										}
										this.getRankActionSaveMap().put(String.valueOf(key), level);
									}
								}
							}
						}
				 }else if (model.getQ_task_type() == TaskType.OTHERS8) {
					HashMap<Integer, Integer> endNeedCondition = endNeedCondition();
					if (endNeedCondition.size() > 0) {
						Set<Integer> keySet = endNeedCondition.keySet();
						for (Integer keyInt : keySet) {
							Integer neednum = endNeedCondition.get(keyInt);
							Integer nownum = getRankActionSaveMap().get(String.valueOf(keyInt));
							if (nownum == null || nownum < neednum) {
								switch(keyInt) {
								case TaskEnum.FIRST_PAY:
									if(player.getRechargeGold() >0 ) {
										getRankActionSaveMap().put(String.valueOf(keyInt), neednum);
										continue;
									}
									break;
								case TaskEnum.USEITEM: {
									Q_itemBean q_itemBean = DataManager.getInstance().q_itemContainer.getMap().get(neednum);
									if (q_itemBean.getQ_type() == ItemTypeConst.SKILLBOOK) {
										// 如果要求使用的是技能书，判断人物是否已经学习了该技能
										if (SkillManager.getInstance().isHaveSkill(player, q_itemBean.getQ_skill())) {
											getRankActionSaveMap().put(String.valueOf(keyInt), neednum);
											continue;
										}
									}
									break;
								}
								case TaskEnum.ITEM_COMPOSE: {
									int itemNum = 0;
									for(int i=0; i<player.getEquips().length; i++) {
										Equip equip = player.getEquips()[i];
										if(equip != null && equip.getItemModelId() == neednum) {
											itemNum = 1;
											getRankActionSaveMap().put(String.valueOf(keyInt), neednum);
											break;
										}
									}
									if(itemNum == 1)
										continue;
									itemNum = BackpackManager.getInstance().getItemNum(player,neednum);
									if(itemNum>0){
										getRankActionSaveMap().put(String.valueOf(keyInt), neednum);
										continue;
									}
									break;
								}
								case TaskEnum.HORSE_STAGE_UP: {
									Horse horse = HorseManager.getInstance().getHorse(player);
									if(horse !=null ){
										getRankActionSaveMap().put(String.valueOf(keyInt), (int)horse.getLayer());
										if(horse.getLayer() >= neednum) {
											getRankActionSaveMap().put(String.valueOf(keyInt),neednum);
											continue;
										}
									}
									break;
								}
								}
								
								if (isPromp) {
									MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉,任务条件未达成"));
								}
								hold = false;
							}
						}
					}
				}else if (model.getQ_task_type() == TaskType.POINT_STORY9) {
					// 判断玩家到坐标的距离
					String q_other_msg = model.getQ_other_msg();
					int start = q_other_msg.indexOf("{") + 1;
					int end = q_other_msg.indexOf("}");
					String[] substring = q_other_msg.substring(start, end).split(Symbol.XIAHUAXIAN_REG);
					if (player.getMapModelId() != Integer.parseInt(substring[0])) {
						if(isPromp)
							MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("玩家不在剧情地图"));
						logger.error("玩家不在剧情地图：" + player.getMapModelId());
						return false;
					}
					double countDistance = MapUtils.countDistance(player.getPosition().getX(), player.getPosition().getY(), Short.parseShort(substring[1]),
							Short.parseShort(substring[2]));
					if (countDistance > 300) {
						if(isPromp)
							MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("玩家离剧情坐标太远了"));
						logger.error("玩家离剧情坐标太远了：" + player.getPosition());
						return false;
					}
				}else if(model.getQ_task_type() == TaskType.WEAR_NORMAL_EQUIP13) {
					HashMap<Integer, Object> endNeedWear = this.endNeedWear();
					if(endNeedWear.size() > 0) {
						Equip[] equips = player.getEquips();
						for(int i=0; i<equips.length; i++) {
							Equip equip = equips[i];
							if(equip != null && endNeedWear.containsKey(equip.getItemModelId())) {
								this.getRankActionSaveMap().put(String.valueOf(equip.getItemModelId()), 1);
							}
						}
						if(endNeedWear.size() != this.getRankActionSaveMap().size()) {
							if (isPromp)
								MessageUtil.notify_player(player, Notifys.ERROR, ResManager.getInstance().getString("很抱歉,任务条件未达成"));
							hold = false;
						}
					}
				}
//					if (!isFinshAction()) {
//						if (isPromp) {
//							MessageUtil.notify_player(player, Notifys.ERROR, "很抱歉,任务条件未达成");
//						}
//						return false;
//					}
				} catch (Exception e) {
					logger.error("配置错误", e);
					return false;
				}
			 	return hold;
		 }
		 return false;
	}

	private boolean checkQianghuaEquip(Item[] items, int modelId, int level) {
		for (Item item : items) {
			if (item instanceof Equip) {
				Equip equip = (Equip) item;
				if (equip != null && equip.getItemModelId() == modelId) {
					if (equip.getGradeNum() >= level) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private static class QiangHuaModel{
		int pos;//部位
		int level;//强化等级
		//某些物品，满足其中一个即可
		LinkedHashMap<Integer,Object> itemMap = new LinkedHashMap<Integer,Object>();
		
		QiangHuaModel(int pos,int itemModelId,int level){
			this.pos = pos;
			if(itemModelId > 0) {
				itemMap.put(itemModelId, Global.REFER_OBJECT);
			}
			this.level = level;
		}

		void addItemModelId(int itemModelId){
			if(itemModelId > 0) {
				itemMap.put(itemModelId, Global.REFER_OBJECT);
			}
		}
	}
	
	public void resetTaskCondition() {
		endNeedGoods = null;
		endNeedQiangHua = null;
		endNeedWear = null;
		endNeedCondition = null;
	}
	
	public static void main(String[] s) {
		String test = "130050001_1_{100016_298_112}|130050003_1_{100016_274_121}|130050005_1_{100016_146_60}|130050007_1_{100016_110_78}|130050009_1_{100016_130_122}";
		String[] split = test.split("//|");
		String[] split2 = test.split("|");
		String[] split3 = test.split("\\|");
		boolean contains = test.contains("\\|");
		System.out.println("");
	}
}
