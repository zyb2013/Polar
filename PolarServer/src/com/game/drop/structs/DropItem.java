package com.game.drop.structs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.game.backpack.bean.ItemInfo;
import com.game.backpack.manager.BackpackManager;
import com.game.chat.bean.GoodsInfoRes;
import com.game.data.bean.Q_itemBean;
import com.game.data.bean.Q_mapBean;
import com.game.data.bean.Q_monsterBean;
import com.game.data.manager.DataManager;
import com.game.dblog.LogService;
import com.game.drop.log.ItemDropLog;
import com.game.drop.manager.DropManager;
import com.game.fight.structs.Fighter;
import com.game.languageres.manager.ResManager;
import com.game.map.bean.DropGoodsInfo;
import com.game.map.manager.MapManager;
import com.game.map.structs.Area;
import com.game.map.structs.Map;
import com.game.monster.manager.MonsterManager;
import com.game.monster.structs.Hatred;
import com.game.monster.structs.Monster;
import com.game.prompt.structs.Notifys;
import com.game.server.impl.WServer;
import com.game.structs.Grid;
import com.game.structs.Position;
import com.game.utils.Global;
import com.game.utils.MapUtils;
import com.game.utils.MessageUtil;
import com.game.utils.RandomUtils;
import com.game.utils.StringUtil;
import com.game.utils.Symbol;

/**
 * 怪物掉落项基类
 * @author xiaozhuoming   
 * 2013-11-22
 */
public abstract class DropItem {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(DropItem.class);
	
	/**
	 * 掉落范围 （以格子为单位）
	 */
	private static final int DROP_DISTANCE=576;	
	private int probability;//掉落几率
	private int maxNum=-1;
	private boolean gradeLimit=true; //爆率是否受等级衰减限制（真受等级限制 false不受等级限制）
	private boolean isOwner=true; //掉落物品是否有归属
	private boolean bind=false; //掉落物品是否绑定
	private int itemModel;	//掉落的物品模型
	private int groupModel;//组包掉落模型
	private String intensifyProb;//爆出带强化等级装备的几率（格式：强化等级|几率;强化等级|几率;强化等级|几率;)互斥几率，计算规则：所有几率相加，在1-几率之和之间取值，判断该值落于哪个区间段则附加哪个强化等级

	public MapDropInfo buildDropGoods(Monster monster) {
		// 计算机率
		boolean isDrop = true;//RandomUtils.isGenerate(getProbability(),
//				Global.MAX_PROBABILITY);
//		System.out.println(getProbability()+"-"+Global.MAX_PROBABILITY+"-"+isDrop);
		// 如果是计数类型
		if (getMaxNum() > 1) {
			StringBuilder stringBuilder = new StringBuilder();
			if(monster.getKiller() != null) 
			stringBuilder.append(monster.getKiller().getId()).append("_");
			stringBuilder.append(monster.getModelId()).append("_");
			stringBuilder.append(this.getItemModel());
			String key = stringBuilder.toString();
			int count = DropManager.getCount(key);
			if (getMaxNum() <= count + 1) {
				isDrop = true;
				DropManager.resetCount(key);
			} else {
				isDrop = false;
				DropManager.count(key);
			}
		}
		if (isDrop) {
			return buildGoodsInfo(monster);
		} else {
			return null;
		}
	}
	
	public int getProbability() {
		return probability;
	}
	public void setProbability(int probability) {
		this.probability = probability;
	}
	public int getMaxNum() {
		return maxNum;
	}
	public void setMaxNum(int maxNum) {
		this.maxNum = maxNum;
	}
	public boolean isGradeLimit() {
		return gradeLimit;
	}
	public void setGradeLimit(boolean gradeLimit) {
		this.gradeLimit = gradeLimit;
	}
	public boolean isOwner() {
		return isOwner;
	}
	public void setOwner(boolean isOwner) {
		this.isOwner = isOwner;
	}
	public boolean getBind() {
		return bind;
	}
	public void setBind(boolean bind) {
		this.bind = bind;
	}

	public int getItemModel() {
		return itemModel;
	}

	public void setItemModel(int itemModel) {
		this.itemModel = itemModel;
	}
	
	public void setCopperModel(int count) {
		if(count > 0 && count < 1000) {
			this.setItemModel(700036);	
		}else if(count >= 1000 && count < 10000) {
			this.setItemModel(700035);
		}else if(count >= 10000) {
			this.setItemModel(700034);
		}
	}
	
	public int getGroupModel() {
		return groupModel;
	}

	public void setGroupModel(int groupModel) {
		this.groupModel = groupModel;
	}

	public String getIntensifyProb() {
		return intensifyProb;
	}

	public void setIntensifyProb(String intensifyProb) {
		this.intensifyProb = intensifyProb;
	}

	/**
	 * 获取落点 并排除当前点
	 * //TODO 去除游泳区 并只取与当前点类型相同的点 
	 * @return
	 */
	public static Position getAbleDropPointAndAxclude(Position pos,Map map){
		Grid[][] mapBlocks = MapManager.getInstance().getMapBlocks(map.getMapModelid());
		int max=getLineWedth(DROP_DISTANCE);//从中心点开始遍历
		int index=0;
		HashMap<Position, Integer> pointMap=new HashMap<Position, Integer>();
		while (index<=max) {
			Grid center = MapUtils.getGrid(pos,mapBlocks);
			//遍历一个正方形
			//A角
			Grid a=MapUtils.buidGrid(center.getX()-index,center.getY()-index);
					//.getGrid(center.getX()-index,center.getY()-index,mapBlocks);
			//D角
			Grid d=MapUtils.buidGrid(center.getX()+index,center.getY()+index);
					//.getGrid(center.getX()+index,center.getY()+index,mapBlocks);
			for (int x = a.getX(); x < d.getX(); x++) {
				for (int y = a.getY(); y < d.getY(); y++) {
					Grid grid = MapUtils.getGrid(x,y,mapBlocks);
					if(grid!=null&&!MapUtils.isBlock(grid)&&!grid.equal(center)&&grid.getX()%2!=0&&grid.getY()%2!=0&&!MapManager.getInstance().isSwimGrid(grid)){
						int gridGoodsSum = getDropGoodsSum(grid, map);
						if (gridGoodsSum == 0) {
							// 如果不是阻挡点 且落点没有物品 则直接返回
							return grid.getCenter();
						} else {
							pointMap.put(grid.getCenter(), gridGoodsSum);
						}
					}
				}
			}
			index++;
		}
		Position minGrid=null;
		int minsum=0;
		for (Position grids : pointMap.keySet()) {
			if(minGrid==null){
				minGrid=grids;
				minsum=pointMap.get(grids);
			}else{
				Integer integer = pointMap.get(grids);
				if(integer<minsum){
					minsum=index;
					minGrid=grids;
				}
			}
		}
		return minGrid;
	}
	
	/**
	 * 获取落点
	 * @param pos
	 * @param num
	 * @return
	 */
	public static Position getAbleDropPoint(Position pos,Map map){
		Grid[][] mapBlocks = MapManager.getInstance().getMapBlocks(map.getMapModelid());
		int max=getLineWedth(DROP_DISTANCE);//从中心点开始遍历
		int index=0;
		HashMap<Position, Integer> pointMap=new HashMap<Position, Integer>();
		while (index<=max) {
			Grid center = MapUtils.getGrid(pos,mapBlocks);
			//遍历一个正方形
			//A角
			Grid a=MapUtils.buidGrid(center.getX()-index,center.getY()-index);
					//.getGrid(center.getX()-index,center.getY()-index,mapBlocks);
			//D角
			Grid d=MapUtils.buidGrid(center.getX()+index,center.getY()+index);
					//.getGrid(center.getX()+index,center.getY()+index,mapBlocks);
			for (int x = a.getX(); x < d.getX(); x = x + 3) {
				for (int y = a.getY(); y < d.getY(); y= y + 3) {
					Grid grid = MapUtils.getGrid(x,y,mapBlocks);
//					if(grid!=null&&!MapUtils.isBlock(grid)&&grid.getX()%2!=0&&grid.getY()%2!=0&&!MapManager.getInstance().isSwimGrid(grid)){
					if(grid!=null&&!MapUtils.isBlock(grid)&&!MapManager.getInstance().isSwimGrid(grid)){
						int gridGoodsSum = getDropGoodsSum(grid, map);
						if (gridGoodsSum == 0) {
							// 如果不是阻挡点 且落点没有物品 则直接返回
							return grid.getCenter();
						} else {
							pointMap.put(grid.getCenter(), gridGoodsSum);
						}
					}
				}
			}
			index = index + 3;
		}
		Position minGrid=null;
		int minsum=0;
		for (Position grids : pointMap.keySet()) {
			if(minGrid==null){
				minGrid=grids;
				minsum=pointMap.get(grids);
			}else{
				Integer integer = pointMap.get(grids);
				if(integer<minsum){
					minsum=index;
					minGrid=grids;
				}
			}
		}
		return minGrid==null?pos:minGrid;
	}
	/**
	 * 获取单个点含有物品的数量
	 * @param position
	 * @param map
	 * @return
	 */
	protected static int getDropGoodsSum(Grid grid,Map map){
		Grid[][] mapBlocks = MapManager.getInstance().getMapBlocks(map.getMapModelid());
		int areaId = MapManager.getInstance().getAreaId(grid.getCenter());
		Area area = map.getAreas().get(areaId);
		int sum=0;
		Iterator<Entry<Long, MapDropInfo>> iterator = area.getDropGoods().entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<Long, MapDropInfo> next = iterator.next();
			MapDropInfo value = next.getValue();
			DropGoodsInfo dropinfo = value.getDropinfo();
			Grid grid2 = MapUtils.getGrid(MapUtils.buildPosition((short) dropinfo.getX(), (short) dropinfo.getY()), mapBlocks);
			if (grid.equal(grid2)) {
				// 格子里存在物品了
				sum++;
			}			
		}
		return sum;
	}
	
	/**
	 * 获取半径
	 * @param num
	 * @return
	 */
	protected static int getLineWedth(int num){
		int sqrt = (int) Math.sqrt(num);
		int a=sqrt*sqrt;
		while (a<num) {
			sqrt++;
			a=sqrt*sqrt;
		}
		return sqrt/2;
	}
	
	/**
	 * 级差检查
	 * @return
	 */
	protected boolean checkGradeAble(Monster monster) {
		if(monster.getMaxHatred()==null){
			if (logger.isInfoEnabled()) {
				logger.error("怪物不明死亡");
			}
			return false;
		}
		if(isGradeLimit()){
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
		}
		return true;
	}
	protected abstract MapDropInfo buildGoodsInfo(Monster monster);
	
	public void drop(MapDropInfo info) {
		if(info==null)
			return;
//			logger.debug(info);
		Q_itemBean model = DataManager.getInstance().q_itemContainer.getMap().get(info.getItem().getItemModelId());
		if (model != null) {
			if (model.getQ_notice() == 3 || model.getQ_notice() == 4) {
				int mapModelid = info.getMapModelid();
				Q_mapBean mapModel = DataManager.getInstance().q_mapContainer.getMap().get(mapModelid);
				short x = info.getDropinfo().getX();
				short y = info.getDropinfo().getY();
				int lineId = info.getLine();
				int serverId = info.getServerId();
				String name = WServer.getGameConfig().getCountryNameByServer(serverId);
				
				ItemInfo itemInfo = info.getItem().buildItemInfo();
				List<GoodsInfoRes> goodsInfos = new ArrayList<GoodsInfoRes>();
				GoodsInfoRes goodsInfo = new GoodsInfoRes();
				goodsInfo.setItemInfo(itemInfo);
				goodsInfos.add(goodsInfo);
				
				MessageUtil.notify_All_player(Notifys.CUTOUT,ResManager.getInstance().getString("{$}掉落在{1}{2}{3}的[{4},{5}]"), goodsInfos, 0, BackpackManager.getInstance().getName(model.getQ_id()), name, mapModel.getQ_map_name(), lineId + ResManager.getInstance().getString("线"), String.valueOf(x/25),
						String.valueOf(y/25));
//				MessageUtil.boardcastWorld("{1}掉落在{2}{3}{4}的[{5},{6}]",  model.getQ_name(), name, mapModel.getQ_map_name(), lineId + "线", String.valueOf(x/25),
//				String.valueOf(y/25));
			}			
			MapManager.getInstance().enterMap(info);
			try{
				if(BackpackManager.getInstance().isLog(model.getQ_id())){
					ItemDropLog log=new ItemDropLog(info);
					LogService.getInstance().execute(log);
				}	
			}catch(Exception ex){
				logger.error(ex, ex);
			}
		}
	}
	
	/**
	 * 获取扩展数量
	 * @param monster
	 * @param itemModelId
	 * @return
	 */
	public static int getAppendCount(Monster monster, int itemModelId){
		int num=0;
		Q_monsterBean model = DataManager.getInstance().q_monsterContainer.getMap().get(monster.getModelId());
		String q_addition_prob = model.getQ_addition_prob();
		if(!StringUtils.isBlank(q_addition_prob)){
			List<Integer> counts=new ArrayList<Integer>();
			List<Integer> probs=new ArrayList<Integer>();
			String[] split = q_addition_prob.split(Symbol.XIAHUAXIAN_REG);
			if(split.length >= 2) {
				int isNeed = Integer.parseInt(split[1]);
				Q_itemBean q_itemBean = DataManager.getInstance().q_itemContainer.getMap().get(itemModelId);
				if(isNeed == 0 && q_itemBean != null && q_itemBean.getQ_remarkable() == 1) {
					return num;
				}
			}
			String[] data = split[0].split(Symbol.FENHAO_REG);
			for (String express : data) {
				if(StringUtils.isBlank(express)){
					continue;
				}
				String[] exp = express.split(Symbol.SHUXIAN_REG);
				int count=Integer.parseInt(exp[0]);
				counts.add(count);
				int prob=Integer.parseInt(exp[1]);
				probs.add(prob);
			}
			int randomIndexByProb = RandomUtils.randomIndexByProb(probs);
			if(randomIndexByProb!=-1){
				num=counts.get(randomIndexByProb);	
			}
		}
		return num;
	}
	/**
	 * 获取强化等级
	 * @param monster
	 * @param intensifyProb
	 * @return
	 */
	public static int getGradeNum(Monster monster, int itemModelId, String intensifyProb) {
		String q_intensify_prob = intensifyProb; 
		if(StringUtil.isBlank(q_intensify_prob)) {
			Q_monsterBean model = DataManager.getInstance().q_monsterContainer.getMap().get(monster.getModelId());
			q_intensify_prob = model.getQ_intensify_prob(); 
		}
		int num=0;
		if(!StringUtils.isBlank(q_intensify_prob)){
			List<Integer> counts=new ArrayList<Integer>();
			List<Integer> probs=new ArrayList<Integer>();
			String[] split = q_intensify_prob.split(Symbol.XIAHUAXIAN_REG);
			if(split.length >= 2) {
				int isNeed = Integer.parseInt(split[1]);
				Q_itemBean q_itemBean = DataManager.getInstance().q_itemContainer.getMap().get(itemModelId);
				if(isNeed == 0 && q_itemBean != null && q_itemBean.getQ_remarkable() == 1) {
					return num;
				}
			}
			String[] data = split[0].split(Symbol.FENHAO_REG);
			for (String express : data) {
				String[] exp = express.split(Symbol.SHUXIAN_REG);
				int count=Integer.parseInt(exp[0]);
				counts.add(count);
				int prob=Integer.parseInt(exp[1]);
				probs.add(prob);
			}
			int randomIndexByProb = RandomUtils.randomIndexByProb(probs);
			if(randomIndexByProb>=0){
				num=counts.get(randomIndexByProb);	
			}
		}
		return num;
	}
//	/**
//	 * 
//	 * @param player
//	 * @param mapmodelId
//	 * @return
//	 */
//	public DropGoodsInfo buildGoodsInfo(Monster monster){
//		
//		if(this instanceof GropuDrop){
//			Q_monster_dropgroupBean groupConfig = DataManager.getInstance().q_monster_dropgroupContainer.getMap().get( getGroupModel());
//			if(groupConfig==null){
//				return null;
//			}
//			setItemModel(DropManager.getNowGroupAndToNext(groupConfig).getQ_id());
//			DropManager.groupToNext(groupConfig);
//		}
//		List<Item> createItems = Item.createItems(itemModel,1, bind,0);
//		Item item=null;
//		if(createItems.size()>0){
//			if (this instanceof CopperDrop) {
//				//FXIME 需要普通物品的类
//				item=new Book();
//			}else{
//				item=createItems.get(0);
//			}
//		}
//		Fighter target = monster.getTarget();
//		Map map = MapManager.getInstance().getMap(monster);
//		Position ableDropPoint = getAbleDropPoint(monster.getPosition(), map);
//		DropGoodsInfo info=item.buildDropInfo(monster,ableDropPoint);
//		if(isOwner()&&target!=null){
//			if(target instanceof Player){
//				//如果是宠物攻击 则需要另行处理
//				Player player=(Player) target;
//				info.setOwnerId(player.getId());	
//			}			
//		}
//		DropManager.addDropGoods(monster.getServerId(),monster.getLine(), monster.getMap(), item);
//		return info;
//	}
	
}
