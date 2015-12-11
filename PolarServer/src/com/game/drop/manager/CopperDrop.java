package com.game.drop.manager;

import java.util.List;

import org.apache.log4j.Logger;

import com.game.backpack.structs.Item;
import com.game.data.bean.Q_itemBean;
import com.game.drop.structs.DropItem;
import com.game.drop.structs.MapDropInfo;
import com.game.fight.structs.Fighter;
import com.game.map.bean.DropGoodsInfo;
import com.game.map.manager.MapManager;
import com.game.map.structs.Map;
import com.game.monster.manager.MonsterManager;
import com.game.monster.structs.Hatred;
import com.game.monster.structs.Monster;
import com.game.pet.struts.Pet;
import com.game.player.structs.Player;
import com.game.structs.Position;
import com.game.summonpet.struts.SummonPet;
import com.game.utils.Global;
import com.game.utils.RandomUtils;
import com.game.utils.StringUtil;
import com.game.utils.Symbol;

/**
 * 金币掉落
 * @author xiaozhuoming
 * 2013-11-19
 */
public class CopperDrop extends DropItem {
	/**
	 * Logger for this class
	 */
	protected static final Logger logger = Logger.getLogger(CopperDrop.class);

	private int count;
	public CopperDrop(int count) {
		this.count=count;
	}
	@Override
	protected MapDropInfo buildGoodsInfo(Monster monster) {
		if (checkGradeAble(monster)) {
//			Item item = Item.createMoney(count);
			List<Item> createItems = Item.createItemsForDropItem(getItemModel(),1,getBind(),0,getGradeNum(monster,getItemModel(),this.getIntensifyProb()),getAppendCount(monster,getItemModel()));
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
				
				Map map = MapManager.getInstance().getMap(monster);
				Position ableDropPoint = getAbleDropPoint(monster.getPosition(), map);
				DropGoodsInfo info = item.buildDropInfo(monster, ableDropPoint);
				Fighter killer = monster.getKiller();
				if(MonsterManager.getInstance().isBoss(monster.getModelId())){
					if(isOwner()&&killer!=null){
						if(killer instanceof Player){
							Player player=(Player)killer;
							info.setOwnerId(player.getId());	
						} else if (killer instanceof Pet) {
							//宠物攻击掉落
							Pet pet= (Pet) killer;
							info.setOwnerId(pet.getOwnerId());
						} else if (killer instanceof SummonPet) {
							// 召唤怪攻击掉落
							SummonPet summonpet = (SummonPet) killer;
							info.setOwnerId(summonpet.getOwnerId());
						}
					}	
				}else{					
					Hatred maxHatred = monster.getMaxHatred();				
					if(isOwner()&&maxHatred!=null){
						if(maxHatred.getTarget() instanceof Player){
							//如果是宠物攻击 则需要另行处理
							Player player=(Player) maxHatred.getTarget();
							info.setOwnerId(player.getId());	
						}else if(maxHatred.getTarget() instanceof Pet){
							//宠物攻击掉落
							Pet pet= (Pet) maxHatred.getTarget();
							info.setOwnerId(pet.getOwnerId());
						}else if(maxHatred.getTarget() instanceof SummonPet){
							// 召唤怪攻击掉落
							SummonPet summonpet= (SummonPet) maxHatred.getTarget();
							info.setOwnerId(summonpet.getOwnerId());
						}
					}	
				}

				// 实际掉落金币 = 掉落金币值（包含表中填写值和各种增加金币掉落影响后的结果）+ 玩家等级 + rand(10)
				if(killer instanceof Player) {
					Player player = (Player) killer;
					
					//游戏币衰减获取金币 = max((1 - 0.036 * max(玩家等级-怪物等级, 0)) * 怪物所带游戏币, 0)
					int count = (int) Math.ceil(Math.max((1 - 0.036 * Math.max((player.getLevel() - monster.getLevel()), 0)) * this.count, 0));
					
					int num = count + player.getLevel() + RandomUtils.random(10);
					// // 杀怪加金币 luminghua
					if (player.getAddmoney_whenkill() > 0 && num > 0) {
						int newNum = num + (num * player.getAddmoney_whenkill() / Global.MAX_PROBABILITY);
						if (newNum > 0) {
							num = newNum;
						}
					}
					item.setNum(num);
					info.setNum(num);
				}

				info.setDropTime(System.currentTimeMillis());
				MapDropInfo mapDropInfo = new MapDropInfo(info, item, map,0);
				mapDropInfo.getHideSet().addAll(monster.getHideSet());
				mapDropInfo.getShowSet().addAll(monster.getShowSet());
				mapDropInfo.setShow(monster.isShow());
				return mapDropInfo;	
			}
			
		}
		return null;
	}
}
