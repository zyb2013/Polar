package com.game.drop.manager;

import java.util.List;

import com.game.backpack.manager.BackpackManager;
import com.game.backpack.structs.Item;
import com.game.config.Config;
import com.game.data.bean.Q_itemBean;
import com.game.data.bean.Q_task_mainBean;
import com.game.data.manager.DataManager;
import com.game.drop.structs.DropItem;
import com.game.drop.structs.MapDropInfo;
import com.game.fight.structs.Fighter;
import com.game.languageres.manager.ResManager;
import com.game.map.bean.DropGoodsInfo;
import com.game.map.manager.MapManager;
import com.game.map.structs.Map;
import com.game.monster.manager.MonsterManager;
import com.game.monster.structs.Hatred;
import com.game.monster.structs.Monster;
import com.game.pet.struts.Pet;
import com.game.player.manager.PlayerManager;
import com.game.player.structs.Player;
import com.game.prompt.structs.Notifys;
import com.game.structs.Position;
import com.game.structs.Reasons;
import com.game.summonpet.struts.SummonPet;
import com.game.task.manager.TaskManager;
import com.game.task.struts.Task;
import com.game.utils.MessageUtil;
import com.game.utils.StringUtil;
import com.game.utils.Symbol;
/**
 * 任务掉落
 * @author xiaozhuoming
 * 2013-11-19
 */
public class TaskDrop extends DropItem {
	/**
	 * Logger for this class
	 */
//	private static final Logger logger = Logger.getLogger(TaskDrop.class);

	private int taskId;
	private int taskMaxNum;
	
	
	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public int getTaskMaxNum() {
		return taskMaxNum;
	}

	public void setTaskMaxNum(int taskMaxNum) {
		this.taskMaxNum = taskMaxNum;
	}
	@Override
	protected MapDropInfo buildGoodsInfo(Monster monster) {
//		Hatred maxHatred = monster.getMaxHatred();
		Map map = MapManager.getInstance().getMap(monster);
		Player player = null;
		if(MonsterManager.getInstance().isBoss(monster.getModelId())){
			Fighter killer = monster.getKiller();
			if (isOwner() && killer != null) {
				if (killer instanceof Player) {
					player = (Player) killer;
				}else if (killer instanceof Pet) {
					// 宠物攻击掉落
					Pet pet = (Pet) killer;
					player = PlayerManager.getInstance().getOnLinePlayer(pet.getOwnerId());
				}else if (killer instanceof SummonPet) {
					// 宠物攻击掉落
					SummonPet summonpet = (SummonPet) killer;
					player = PlayerManager.getInstance().getOnLinePlayer(summonpet.getOwnerId());
				}
			}
		}else{		
			Hatred maxHatred = monster.getMaxHatred();
			if (maxHatred == null) {
				return null;
			}
			if (maxHatred.getTarget() instanceof Player) {
				// 如果是宠物攻击 则需要另行处理
				player = (Player) maxHatred.getTarget();
			}else if (maxHatred.getTarget() instanceof Pet) {
				// 宠物攻击掉落
				Pet pet = (Pet) maxHatred.getTarget();
				player = PlayerManager.getInstance().getOnLinePlayer(pet.getOwnerId());
			}else if (maxHatred.getTarget() instanceof SummonPet) {
				// 召唤怪攻击掉落
				SummonPet summonpet = (SummonPet) maxHatred.getTarget();
				player = PlayerManager.getInstance().getOnLinePlayer(summonpet.getOwnerId());
			}	
		}
		
		Task taskByModelId = TaskManager.getInstance().getTaskByModelId(player, taskId);
		if(taskByModelId==null){
			//身上没有相关的任务 
			return null;
		}
		
		Q_task_mainBean taskModel = DataManager.getInstance().q_task_mainContainer.getMap().get(taskId);
		if(taskModel == null ||taskModel.getQ_accept_needmingrade()>player.getLevel()){
			//等级不够
			return null;
		}
		int itemNum = BackpackManager.getInstance().getItemNum(player, getItemModel());
		if(itemNum>=taskMaxNum){
			//达到上限 不再掉落
			return null;
		}
		// 任务掉落
		List<Item> createItems = Item.createItemsForDropItem(getItemModel(), 1, getBind(), 0,getGradeNum(monster,getItemModel(),this.getIntensifyProb()),getAppendCount(monster,getItemModel()));
		if (createItems.size() > 0) {
			Item item = createItems.get(0);
			Q_itemBean itemModel = DropManager.getItemModelByID(item.getItemModelId());
			if (itemModel != null && !StringUtil.isBlank(itemModel.getQ_max_create()) && itemModel.getQ_max_create().contains((Symbol.XIEGANG_REG))) {
				String[] q_max_createStr = itemModel.getQ_max_create().split(Symbol.XIEGANG_REG);
				int count = DropManager.getItemCount(item.getItemModelId());
				if (q_max_createStr.length > 0 && count >= Integer.parseInt(q_max_createStr[0])) {
					return null;
				}
				DropManager.countItemCount(item.getItemModelId());
			}
			
			Position ableDropPoint = getAbleDropPoint(monster.getPosition(), map);
			DropGoodsInfo info = item.buildDropInfo(monster, ableDropPoint);
			info.setOwnerId(player.getId());
			info.setDropTime(System.currentTimeMillis());
			MapDropInfo mapDropInfo = new MapDropInfo(info, item, map,0);
			mapDropInfo.getHideSet().addAll(monster.getHideSet());
			mapDropInfo.getShowSet().addAll(monster.getShowSet());
			mapDropInfo.setShow(monster.isShow());
			return mapDropInfo;
		}
		return null;
	}

	@Override
	public void drop(MapDropInfo info) {
		//任务 直接掉落 到包裹
		long ownerId = info.getDropinfo().getOwnerId();
		Player onLinePlayer = PlayerManager.getInstance()				.getOnLinePlayer(ownerId);
		if (onLinePlayer != null) {
			if(BackpackManager.getInstance().hasAddSpace(onLinePlayer, info.getItem())){
				BackpackManager.getInstance().addItem(onLinePlayer,info.getItem(),Reasons.TASKGOODSADD,Config.getId());	
			}else{
				MessageUtil.notify_player(onLinePlayer, Notifys.ERROR,ResManager.getInstance().getString("包裹空间不足，无法获得任务物品，请先清理包裹预留足够的空间"));
			}
		}
	}
	
}
