package com.game.newactivity.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.game.backpack.manager.BackpackManager;
import com.game.backpack.structs.Item;
import com.game.config.Config;
import com.game.newactivity.AbstractActivity.GetAwardResult.ResultType;
import com.game.newactivity.AbstractOpenServerActivity;
import com.game.newactivity.message.ResGetNewActivityInfo;
import com.game.newactivity.model.DetailActivityInfo.Row;
import com.game.player.structs.Player;
import com.game.structs.Reasons;
import com.game.utils.CollectionUtil;
import com.game.utils.MessageUtil;
import com.game.utils.Symbol;

/**
 * @author luminghua
 *
 * @date   2014年2月24日 下午3:21:12
 */
public class Exchange extends AbstractOpenServerActivity {

	public void stop() {
		_logger.info(this.getClass().getName() +" execute default end() method.");
	}
	//monsterType-->DropModel
//	private Map<Integer,List<DropModel>> dropMap ;
	private static final Object o = new Object();
	
	private Map<Integer,Object> dropMap = new HashMap<Integer,Object>();
	
	@Override
	public void initActivity() {
		super.initActivity();
		for(Row row :detailInfo.getRows()) {
			String[] split = row.getCond().split(Symbol.SHUXIAN_REG);
			for(String str:split) {
				String[] split2 = str.split(Symbol.XIAHUAXIAN_REG);
				dropMap.put(Integer.parseInt(split2[0]), o);
			}
		}
	}
	/*@Override
	public void initActivity() {
		super.initActivity();
		String q_expend = this.getActivityBean().getQ_expend();
		if(StringUtil.isNotBlank(q_expend)) {
			try {
				JSONObject json = JSONObject.fromObject(q_expend);
				String str = json.getString("dropItem");
				if(StringUtil.isNotBlank(str)) {
					dropMap = new HashMap<Integer,List<DropModel>>();
					JSONObject jsonObject = json.getJSONObject("dropItem");
					if(jsonObject != null) {
						Iterator iterator = jsonObject.keySet().iterator();
						while(iterator.hasNext()) {
							String key = iterator.next().toString();
							JSONArray jsonArray = jsonObject.getJSONArray(key);
							List<DropModel> list = new LinkedList<DropModel>();
							for(Object o :jsonArray) {
								String[] split = o.toString().split(Symbol.XIAHUAXIAN_REG);
								DropModel model = new DropModel();
								model.itemModelId = Integer.parseInt(split[0]);
								model.num = Integer.parseInt(split[1]);
								if(model.num <= 0) {
									model.num = 1;
								}
								model.level = Integer.parseInt(split[2]);
								model.rate = Integer.parseInt(split[3]);
								list.add(model);
							}
							dropMap.put(Integer.parseInt(key), list);
						}
					}
				}
			}catch(Exception e) {
				_logger.error(this.getClass().getName() +"解析关闭活动面板时间出错："+this.getActivityBean().getQ_id(), e);
			}
		}
	}
	
	public void trigger(Player player,Object...objects) {
		if(CollectionUtil.isBlank(dropMap)) {
			return;
		}
		if(objects == null || objects.length < 1) {
			return;
		}
		if(!isBetweenStartAndEnd()) {
			return;
		}
		Monster monster = (Monster)objects[0];
		if(!checkDropAble(monster)) {
			return;
		}
		com.game.map.structs.Map map = MapManager.getInstance().getMap(player);
		if(map == null || map.isCopy()) {
			return;
		}
		Q_monsterBean monsterBean = DataManager.getInstance().q_monsterContainer.getMap().get(monster.getModelId());
		List<DropModel> list = dropMap.get(monsterBean.getQ_monster_type());
		if(CollectionUtil.isBlank(list)) {
			return;
		}
		for(DropModel model:list) {
			if(monsterBean.getQ_grade() >= model.level) {
				if(RandomUtils.isGenerate2(10000, model.rate)) {
					List<Item> createItems = Item.createItems(model.itemModelId, model.num, false, 0);
					if(CollectionUtil.isBlank(createItems)) {
						continue;
					}
					Position ableDropPoint = DropItem.getAbleDropPoint(player.getPosition(), map);
					if(ableDropPoint == null) {
						continue;
					}
					for (Item item : createItems) {
						// 构造掉落物信息
						DropGoodsInfo buildDropInfo = item.buildDropInfo(ableDropPoint);
						buildDropInfo.setOwnerId(player.getId());
						buildDropInfo.setDropTime(System.currentTimeMillis());
						// 构造地图显示掉落物
						MapDropInfo mapDropInfo = new MapDropInfo(buildDropInfo, item, map, 0);
						mapDropInfo.setShow(true);
						CommonDrop commonDrop = new CommonDrop();
						commonDrop.drop(mapDropInfo);
					}
				}
			}
		}
	}
	private class DropModel{
		int itemModelId;
		int num;
		int level;//怪物等级要大于这个值
		int rate;//掉落概率
	}
	private boolean checkDropAble(Monster monster) {
		if(monster.getMaxHatred()==null){
			return false;
		}
		Hatred maxHatred = monster.getMaxHatred();
		Fighter target = maxHatred.getTarget();
		if(MonsterManager.getInstance().isBoss(monster.getModelId())){
			if(target.getLevel()-monster.getLevel()>Global.DROP_BOSS_GRADE_LIMIT){
				return false;
			}
		}else{
			if(target.getLevel()-monster.getLevel()>Global.DROP_COMMON_GRADE_LIMIT){
				return false;
			}
		}	
		return true;
	}*/
	
	/**
	 * 判断是否可以掉落
	 * 
		Exchange exchange = (Exchange)NewActivityManager.getInstance().getActivityImpl(activityId);
		exchange.checkDropAble(itemModelId);
	 * 
	 * @param modelId
	 * @return
	 */
	public boolean checkDropAble(int modelId) {
		return isBetweenStartAndEnd() && dropMap.containsKey(modelId);
	}
	
	public void sendDetailActivityInfo(Player player) {
		//发送消息
		ResGetNewActivityInfo msg = new ResGetNewActivityInfo();
		msg.setDetailInfo(detailInfo);
		List<Row> rows = detailInfo.getRows();
		if(CollectionUtil.isNotBlank(rows)) {
			List<Integer> canGet = new LinkedList<Integer>();
			for(int i=0; i<rows.size(); i++) {
				Row row = rows.get(i);
				String[] split = row.getCond().split(Symbol.SHUXIAN_REG);
				boolean enough = true;
				if(!isBetweenStartAndEnd()) {
					enough = false;
				}else {
					for(String str:split) {
						String[] split2 = str.split(Symbol.XIAHUAXIAN_REG);
						int modelId = Integer.parseInt(split2[0]);
						int num = Integer.parseInt(split2[1]);
						int itemNum = BackpackManager.getInstance().getItemNum(player, modelId);
						if(itemNum < num) {
							enough = false;
							break;
						}
					}
				}
				canGet.add(enough?1:0);
			}
			msg.setCanGet(canGet);
		}
		MessageUtil.tell_player_message(player, msg);
	}

	public GetAwardResult getAward(Player player,int order) {
		if(!isBetweenStartAndEnd()) {
			_logger.error("活动过期咯。p:"+player.getId()+",o:"+order);
			return new GetAwardResult(ResultType.expire);
		}
		//判断是否可领
		Row row = detailInfo.getRows().get(order);
		String[] split = row.getCond().split(Symbol.SHUXIAN_REG);
		boolean enough = true;
		Map<Integer,Integer> itemMap = new HashMap<Integer,Integer>();
		for(String str:split) {
			String[] split2 = str.split(Symbol.XIAHUAXIAN_REG);
			int modelId = Integer.parseInt(split2[0]);
			int num = Integer.parseInt(split2[1]);
			int itemNum = BackpackManager.getInstance().getItemNum(player, modelId);
			if(itemNum < num) {
				enough = false;
				break;
			}
			itemMap.put(modelId, num);
		}
		if(enough) {
			List<Item> parseAward = this.parseAward(player.getJob(), row.getAward());
			if(BackpackManager.getInstance().getEmptyGridNum(player) < parseAward.size()) {
				return new GetAwardResult(ResultType.miss_bag_space);
			}
			long id = Config.getId();
			Iterator<Entry<Integer, Integer>> iterator = itemMap.entrySet().iterator();
			while(iterator.hasNext()) {
				Entry<Integer, Integer> next = iterator.next();
				BackpackManager.getInstance().removeItem(player, next.getKey(), next.getValue(), Reasons.NEW_ACTIVITY_DEDUCT, id);
			}
			BackpackManager.getInstance().addItems(player, parseAward,Reasons.NEW_ACTIVITY_ADD, id);
			GetAwardResult getAwardResult = new GetAwardResult(ResultType.succeed);
			getAwardResult.items =  parseAward.toArray(new Item[] {});
			return getAwardResult;
		}else {
			return new GetAwardResult(ResultType.not_enough_item);
		}
	}
	
	
}
